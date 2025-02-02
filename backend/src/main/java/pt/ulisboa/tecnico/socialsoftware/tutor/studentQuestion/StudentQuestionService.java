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
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository;
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

    @Autowired
    private OptionRepository optionRepository;

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
        User user = question.getSubmittingUser();
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
        studentQuestion.setStatus(StudentQuestion.Status.AVAILABLE);

        return questionService.createQuestion(studentQuestion.getCourse().getId(), questionDto);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public StudentQuestionDto resubmitQuestion(int questionId, StudentQuestionDto questionDto) {
        StudentQuestion question = studentQuestionRepository.findById(questionId).orElseThrow(() -> new TutorException(STUDENT_QUESTION_NOT_FOUND, questionId));
        checkIfRejected(question);
        question.editStudentQuestion(questionDto);
        question.setStatus(StudentQuestion.Status.PENDING);
        return new StudentQuestionDto(question);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public StudentQuestionDto editApprovedQuestion(int questionId, StudentQuestionDto questionDto) {
        StudentQuestion question = studentQuestionRepository.findById(questionId).orElseThrow(() -> new TutorException(STUDENT_QUESTION_NOT_FOUND, questionId));
        checkIfApproved(question);
        question.editStudentQuestion(questionDto);
        return new StudentQuestionDto(question);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public StudentQuestionStatsDto getStudentQuestionStats(String username) {
        int approved = 0;
        int submitted = 0;
        User user = userRepository.findByUsername(username);
        checkUserFound(user);
        List<StudentQuestion> questions = user.getSubmittedQuestions();
        for(StudentQuestion question : questions){
            if (question.getStatus() == StudentQuestion.Status.APPROVED) {
                approved += 1;
            }
            submitted += 1;
        }
        StudentQuestionStatsDto stats = new StudentQuestionStatsDto();
        stats.setSubmitted(submitted);
        stats.setApproved(approved);
        return stats;
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public boolean toggleStudentQuestionPrivacy(String studentQuestionPrivacy, Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        if(!studentQuestionPrivacy.equals("private") && !studentQuestionPrivacy.equals("public")){
            throw new TutorException(ErrorMessage.PRIVACY_NOT_DEFINED, userId);
        }

        user.setStudentQuestionPrivacy(studentQuestionPrivacy.equals("private"));

        return user.getStudentQuestionPrivacy();
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
        options.forEach(optionDto -> optionDto.setStudentQuestionOption(true));
        questionDto.setOptions(options);
        return questionDto;
    }
}
