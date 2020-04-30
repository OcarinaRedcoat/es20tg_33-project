package pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class StudentQuestionService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentQuestionRepository studentQuestionRepository;

    @Autowired
    private QuestionService questionService;

    @PersistenceContext
    EntityManager entityManager;

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public CourseDto findQuestionCourse(Integer questionId) {
        return studentQuestionRepository.findById(questionId)
                .map(StudentQuestion::getCourse)
                .map(CourseDto::new)
                .orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, questionId));
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public StudentQuestionDto submitQuestion(int courseId, StudentQuestionDto questionDto, String username) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new TutorException(COURSE_NOT_FOUND, courseId));
        User user = userRepository.findByUsername(username);
        if (user == null)
            throw new TutorException(USERNAME_NOT_FOUND);

        checkQuestionKey(questionDto);

        StudentQuestion question = new StudentQuestion(course, questionDto);
        checkIfPending(question);
        question.setSubmittingUser(user);
        user.addSubmittedQuestion(question);
        entityManager.persist(question);
        return new StudentQuestionDto(question);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public StudentQuestionDto approveQuestion(int questionId, String justification) {
        StudentQuestion question = studentQuestionRepository.findById(questionId).orElseThrow(() -> new TutorException(STUDENT_QUESTION_NOT_FOUND, questionId));
        if(question.getStatus() == StudentQuestion.Status.PENDING) {
            question.setStatus(StudentQuestion.Status.APPROVED);
            question.setJustification(justification);
            entityManager.persist(question);
            return new StudentQuestionDto(question);
        }
        else {
            throw new TutorException(QUESTION_NOT_PENDING);
        }
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public StudentQuestionDto rejectQuestion(int questionId, String justification) {
        if(justification == null || justification.trim().isEmpty()) {
            throw new TutorException(QUESTION_MISSING_JUSTIFICATION);
        }
        StudentQuestion question = studentQuestionRepository.findById(questionId).orElseThrow(() -> new TutorException(STUDENT_QUESTION_NOT_FOUND, questionId));
        if(question.getStatus() == StudentQuestion.Status.PENDING) {
            question.setStatus(StudentQuestion.Status.REJECTED);
            question.setJustification(justification);
            entityManager.persist(question);
            return new StudentQuestionDto(question);
        }
        else {
            throw new TutorException(QUESTION_NOT_PENDING);
        }
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<StudentQuestionDto> getUserSubmittedQuestions(String username) {
        User user = userRepository.findByUsername(username);
        checkUserFound(user);
        List<StudentQuestion> questions = user.getSubmittedQuestions();

        return questions.stream().map(StudentQuestionDto::new).collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<StudentQuestionDto> getUserSubmittedQuestionsStats(String username) {
        User user = userRepository.findByUsername(username);
        checkSubmittedQuestions(user);
        checkUserFound(user);
        List<StudentQuestion> questions = user.getSubmittedQuestions();

        return questions.stream().map(StudentQuestionDto::new).collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<StudentQuestionDto> getSubmittedQuestions(int courseId) {
        return studentQuestionRepository.findQuestions(courseId).stream().map(StudentQuestionDto::new).collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void removeStudentQuestion(Integer questionId) {
        StudentQuestion question = studentQuestionRepository.findById(questionId).orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, questionId));
        question.remove();
        studentQuestionRepository.delete(question);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public QuestionDto makeStudentQuestionAvailable(Integer questionId) {
        StudentQuestion studentQuestion = studentQuestionRepository.findById(questionId).orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, questionId));
        checkIfApproved(studentQuestion);
        QuestionDto questionDto = setupQuestionDto(studentQuestion);

        return questionService.createQuestion(studentQuestion.getCourse().getId(), questionDto);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public StudentQuestionDto resubmitQuestion(int questionId, StudentQuestionDto questionDto, String username) {
        Integer index = 0;
        StudentQuestion question = studentQuestionRepository.findById(questionId).orElseThrow(() -> new TutorException(STUDENT_QUESTION_NOT_FOUND, questionId));
        User user = userRepository.findByUsername(username);
        if (user == null)
            throw new TutorException(USERNAME_NOT_FOUND);
        checkIfRejected(question);
        //checkIfChanged(question, questionDto);
        question.setTitle(questionDto.getTitle());
        question.setContent(questionDto.getContent());
        question.setJustification("");
        question.setStatus(StudentQuestion.Status.PENDING);

        List<Option> options = new ArrayList<>();
        for (OptionDto optionDto : questionDto.getOptions()) {
            optionDto.setSequence(index++);
            Option option = new Option(optionDto);
            options.add(option);
            option.setStudentQuestion(question);
        }
        question.setOptions(options);
        entityManager.persist(question);
        return new StudentQuestionDto(question);
    }

    private void checkSubmittedQuestions(User user) {
        if (user.getSubmittedQuestions().isEmpty())
            throw new TutorException(USER_WITHOUT_SUBMITTED_QUESTIONS);
    }

    private void checkUserFound(User user) {
        if (user == null)
            throw new TutorException(USERNAME_NOT_FOUND);
    }

    private void checkIfPending(StudentQuestion question) {
        if (question.getStatus() != StudentQuestion.Status.PENDING)
            question.setStatus(StudentQuestion.Status.PENDING);
    }

    private void checkIfRejected(StudentQuestion question) {
        if (question.getStatus() != StudentQuestion.Status.REJECTED)
            throw new TutorException(QUESTION_NOT_REJECTED);
    }

    private void checkIfApproved(StudentQuestion question) {
        if (question.getStatus() != StudentQuestion.Status.APPROVED)
            throw new TutorException(QUESTION_NOT_APPROVED);
    }

    private void checkQuestionKey(StudentQuestionDto questionDto) {
        if (questionDto.getKey() == null) {
            int maxQuestionNumber = studentQuestionRepository.getMaxQuestionNumber() != null ?
                    studentQuestionRepository.getMaxQuestionNumber() : 0;
            questionDto.setKey(maxQuestionNumber + 1);
        }
    }

    private QuestionDto setupQuestionDto(StudentQuestion studentQuestion) {
        QuestionDto questionDto = new QuestionDto();
        questionDto.setTitle(studentQuestion.getTitle());
        questionDto.setContent(studentQuestion.getContent());
        questionDto.setKey(null);
        questionDto.setStatus(Question.Status.AVAILABLE.name());
        List<OptionDto> options = studentQuestion.getOptions().stream().map(OptionDto::new).collect(Collectors.toList());
        questionDto.setOptions(options);
        return questionDto;
    }
}
