package pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class StudentQuestionService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentQuestionRepository studentQuestionRepository;

    @PersistenceContext
    EntityManager entityManager;

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
            question.getSubmittingUser().increaseNumberOfApprovedQuestions();
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
            question.getSubmittingUser().increaseNumberOfRejectedQuestions();
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
    public List<StudentQuestionDto> getSubmittedQuestionsStats(String username) {
        User user = userRepository.findByUsername(username);
        checkUserFound(user);
        checkSubmittedQuestions(user);
        List<StudentQuestion> questions = user.getSubmittedQuestions();

        return questions.stream().map(StudentQuestionDto::new).collect(Collectors.toList());
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

    private void checkQuestionKey(StudentQuestionDto questionDto) {
        if (questionDto.getKey() == null) {
            int maxQuestionNumber = studentQuestionRepository.getMaxQuestionNumber() != null ?
                    studentQuestionRepository.getMaxQuestionNumber() : 0;
            questionDto.setKey(maxQuestionNumber + 1);
        }
    }
}
