package pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestionService
import spock.lang.Specification

@DataJpaTest
class ApprovalRejectionQuestionTest extends Specification {
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final String QUESTION_TITLE = 'question title'
    public static final String QUESTION_CONTENT = 'question content'
    public static final String OPTION_CONTENT = "optionId content"
    public static final String JUSTIFICATION_CONTENT = "justification content"

    @Autowired
    StudentQuestionService studentQuestionService

    @Autowired
    OptionRepository optionRepository

    @Autowired
    StudentQuestionRepository studentQuestionRepository

    def question
    def option
    def optionF

    def setup() {
        given: "create a question"
        question = new StudentQuestion()
        question.setTitle(QUESTION_TITLE)
        question.setContent(QUESTION_CONTENT)
        question.setStatus(StudentQuestion.Status.PENDING)

        and: 'two options'
        option = new Option()
        option.setContent(OPTION_CONTENT)
        option.setCorrect(true)
        option.setStudentQuestion(question)
        optionRepository.save(option)

        optionF = new Option()
        optionF.setContent(OPTION_CONTENT)
        optionF.setCorrect(false)
        optionF.setStudentQuestion(question)
        optionRepository.save(optionF)

        question.addOption(option)
        question.addOption(optionF)
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
        question.setStatus(Question.Status.REJECTED)

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
    }
}