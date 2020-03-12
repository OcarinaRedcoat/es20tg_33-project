package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
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
    QuestionService questionService

    @Autowired
    OptionRepository optionRepository

    @Autowired
    QuestionRepository questionRepository

    def question
    def option
    def optionF

    def setup() {
        given: "create a question"
        question = new Question()
        question.setKey(1)
        question.setTitle(QUESTION_TITLE)
        question.setContent(QUESTION_CONTENT)
        question.setStatus(Question.Status.PENDING)
        and: 'two options'

        option = new Option()
        option.setContent(OPTION_CONTENT)
        option.setCorrect(true)
        option.setQuestion(question)
        optionRepository.save(option)

        optionF = new Option()
        optionF.setContent(OPTION_CONTENT)
        optionF.setCorrect(false)
        optionF.setQuestion(question)
        question.addOption(option)
        question.addOption(optionF)
        optionRepository.save(optionF)
        questionRepository.save(question)
    }

    def "approve a valid question"() {
        when:
        questionService.approveQuestion(question.getId(), null)
        // verify if person approving is teacher
        then: "the question is approved successfully"
        questionRepository.count() == 1L
        def result = questionRepository.findAll().get(0)
        result.getId() != null
        result.getKey() == 1
        result.getStatus() == Question.Status.AVAILABLE
        result.getTitle() == QUESTION_TITLE
        result.getContent() == QUESTION_CONTENT
        result.getOptions().size() == 2
    }

    def "approve a valid question with justification"() {
        when:
        questionService.approveQuestion(question.getId(), JUSTIFICATION_CONTENT)
        // verify if person approving is teacher
        then: "the question is approved successfully"
        questionRepository.count() == 1L
        def result = questionRepository.findAll().get(0)
        result.getId() != null
        result.getKey() == 1
        result.getStatus() == Question.Status.AVAILABLE
        result.getTitle() == QUESTION_TITLE
        result.getContent() == QUESTION_CONTENT
        result.getOptions().size() == 2
    }

    def "reject a question with justification"() {
        when:
        questionService.rejectQuestion(question.getId(), JUSTIFICATION_CONTENT)

        then: "the question is rejected successfully"
        questionRepository.count() == 1L
        def result = questionRepository.findAll().get(0)
        result.getId() != null
        result.getKey() == 1
        result.getStatus() == Question.Status.REJECTED
        result.getTitle() == QUESTION_TITLE
        result.getContent() == QUESTION_CONTENT
        result.getJustification() == JUSTIFICATION_CONTENT
        result.getOptions().size() == 2
    }

    def "reject a question without justification"() {
        when:
        questionService.rejectQuestion(question.getId(), null)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.QUESTION_MISSING_JUSTIFICATION
        def result = questionRepository.findAll().get(0)
        result.getId() != null
        result.getKey() == 1
        result.getStatus() == Question.Status.PENDING
        result.getTitle() == QUESTION_TITLE
        result.getContent() == QUESTION_CONTENT
        result.getJustification() == null
        questionRepository.count() == 1L
    }

    def "reject a question with blank justification"() {
        when:
        questionService.rejectQuestion(question.getId(), '         ')

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.QUESTION_MISSING_JUSTIFICATION
        def result = questionRepository.findAll().get(0)
        result.getId() != null
        result.getKey() == 1
        result.getStatus() == Question.Status.PENDING
        result.getTitle() == QUESTION_TITLE
        result.getContent() == QUESTION_CONTENT
        result.getJustification() == null
        questionRepository.count() == 1L
    }

    def "approve a question not in pending status"() {
        given: "a question in rejected status"
        question.setStatus(Question.Status.REJECTED)

        when:
        questionService.approveQuestion(question.getId(), null)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.QUESTION_NOT_PENDING
        def result = questionRepository.findAll().get(0)
        result.getId() != null
        result.getKey() == 1
        result.getStatus() == Question.Status.REJECTED
        result.getTitle() == QUESTION_TITLE
        result.getContent() == QUESTION_CONTENT
        result.getJustification() == null
        questionRepository.count() == 1L
    }

    @TestConfiguration
    static class QuestionServiceImplTestContextConfiguration {

        @Bean
        QuestionService questionService() {
            return new QuestionService()
        }
    }
}