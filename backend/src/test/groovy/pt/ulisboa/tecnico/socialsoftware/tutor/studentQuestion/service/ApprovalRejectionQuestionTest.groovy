package pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@DataJpaTest
class ApprovalRejectionQuestionTest extends Specification {
    public static final String USERNAME = "user"
    public static final String PERSON_NAME = "Name"
    public static final String QUESTION_TITLE = 'question title'
    public static final String QUESTION_CONTENT = 'question content'
    public static final String OPTION_CONTENT = "optionId content"
    public static final String JUSTIFICATION_CONTENT = "justification content"

    @Autowired
    StudentQuestionService studentQuestionService

    @Autowired
    QuestionService questionService

    @Autowired
    OptionRepository optionRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    StudentQuestionRepository studentQuestionRepository

    def question
    def option
    def optionF
    def user

    def setup() {
        user = new User(PERSON_NAME, USERNAME, 1, User.Role.STUDENT)
        userRepository.save(user)

        question = new StudentQuestion()
        question.setKey(1)
        question.setTitle(QUESTION_TITLE)
        question.setContent(QUESTION_CONTENT)
        question.setStatus(StudentQuestion.Status.PENDING)

        option = new Option()
        option.setContent(OPTION_CONTENT)
        option.setCorrect(true)
        option.setStudentQuestion(question)
        option.setSequence(1)
        optionRepository.save(option)

        optionF = new Option()
        optionF.setContent(OPTION_CONTENT)
        optionF.setCorrect(false)
        optionF.setStudentQuestion(question)
        optionF.setSequence(2)
        optionRepository.save(optionF)

        question.addOption(option)
        question.addOption(optionF)
        question.setSubmittingUser(user)
        studentQuestionRepository.save(question)
    }

    def "approve a valid question"() {
        when:
        studentQuestionService.approveQuestion(question.getId(), null)

        // verify if person approving is teacher
        then: "the question is approved successfully"
        studentQuestionRepository.count() == 1L
        def result = studentQuestionRepository.findAll().get(0)
        result.getId() != null
        result.getStatus() == StudentQuestion.Status.APPROVED
        result.getTitle() == QUESTION_TITLE
        result.getContent() == QUESTION_CONTENT
        result.getOptions().size() == 2
        result.getSubmittingUser().getUsername() == USERNAME
    }

    def "approve a valid question with justification"() {
        when:
        studentQuestionService.approveQuestion(question.getId(), JUSTIFICATION_CONTENT)

        // verify if person approving is teacher
        then: "the question is approved successfully"
        studentQuestionRepository.count() == 1L
        def result = studentQuestionRepository.findAll().get(0)
        result.getId() != null
        result.getStatus() == StudentQuestion.Status.APPROVED
        result.getTitle() == QUESTION_TITLE
        result.getContent() == QUESTION_CONTENT
        result.getOptions().size() == 2
        result.getSubmittingUser().getUsername() == USERNAME
    }

    def "reject a question with justification"() {
        when:
        studentQuestionService.rejectQuestion(question.getId(), JUSTIFICATION_CONTENT)

        then: "the question is rejected successfully"
        studentQuestionRepository.count() == 1L
        def result = studentQuestionRepository.findAll().get(0)
        result.getId() != null
        result.getStatus() == StudentQuestion.Status.REJECTED
        result.getTitle() == QUESTION_TITLE
        result.getContent() == QUESTION_CONTENT
        result.getJustification() == JUSTIFICATION_CONTENT
        result.getOptions().size() == 2
        result.getSubmittingUser().getUsername() == USERNAME
    }

    def "reject a question without justification"() {
        when:
        studentQuestionService.rejectQuestion(question.getId(), null)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.QUESTION_MISSING_JUSTIFICATION
        def result = studentQuestionRepository.findAll().get(0)
        result.getId() != null
        result.getStatus() == StudentQuestion.Status.PENDING
        result.getTitle() == QUESTION_TITLE
        result.getContent() == QUESTION_CONTENT
        result.getJustification() == null
        studentQuestionRepository.count() == 1L
    }

    def "reject a question with blank justification"() {
        when:
        studentQuestionService.rejectQuestion(question.getId(), '         ')

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.QUESTION_MISSING_JUSTIFICATION
        def result = studentQuestionRepository.findAll().get(0)
        result.getId() != null
        result.getStatus() == StudentQuestion.Status.PENDING
        result.getTitle() == QUESTION_TITLE
        result.getContent() == QUESTION_CONTENT
        result.getJustification() == null
        studentQuestionRepository.count() == 1L
    }

    def "approve a question not in pending status"() {
        given: "a question in rejected status"
        question.setStatus(StudentQuestion.Status.REJECTED)

        when:
        studentQuestionService.approveQuestion(question.getId(), null)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.QUESTION_NOT_PENDING
        def result = studentQuestionRepository.findAll().get(0)
        result.getId() != null
        result.getStatus() == StudentQuestion.Status.REJECTED
        result.getTitle() == QUESTION_TITLE
        result.getContent() == QUESTION_CONTENT
        result.getJustification() == null
        studentQuestionRepository.count() == 1L
    }

    @TestConfiguration
    static class StudentQuestionServiceImplTestContextConfiguration {

        @Bean
        StudentQuestionService studentQuestionService() {
            return new StudentQuestionService()
        }

        @Bean
        QuestionService questionService() {
            return new QuestionService()
        }
    }
}