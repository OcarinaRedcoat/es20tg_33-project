package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import spock.lang.Specification

@DataJpaTest
class ApprovalRejectionQuestionPerformanceTest extends Specification {
    public static final String QUESTION_TITLE = "qTitle"
    public static final String QUESTION_CONTENT = "qContent"
    public static final String OPTION_CONTENT = "optContent"
    public static final String JUSTIFICATION_CONTENT = "justification content"

    @Autowired
    QuestionService questionService

    @Autowired
    QuestionRepository questionRepository

//TODO refactor
    def "performance test to approve 5000 questions"() {
        when:
        1.upto(2500, {
            def question
            question = new Question()
            question.setId(it)
            question.setKey(it)
            question.setTitle(QUESTION_TITLE)
            question.setContent(QUESTION_CONTENT)
            question.setStatus(Question.Status.PENDING)
            questionRepository.save(question)

            questionService.approveQuestion(it, JUSTIFICATION_CONTENT)})

        then:
        true
    }
//TODO refactor
    def "performance test to reject 5000 questions"() {
        when:
        2501.upto(5000, {
            def question
            question = new Question()
            question.setId(it)
            question.setKey(it)
            question.setTitle(QUESTION_TITLE)
            question.setContent(QUESTION_CONTENT)
            question.setStatus(Question.Status.PENDING)
            questionRepository.save(question)

            questionService.rejectQuestion(it, JUSTIFICATION_CONTENT)})



        then:
        true
    }

    @TestConfiguration
    static class QuestionServiceImplTestContextConfiguration {

        @Bean
        QuestionService questionService() {
            return new QuestionService()
        }
    }
}
