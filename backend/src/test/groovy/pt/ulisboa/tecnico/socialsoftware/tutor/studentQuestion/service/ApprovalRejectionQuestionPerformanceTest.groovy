package pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestionService
import spock.lang.Specification

@DataJpaTest
class ApprovalRejectionQuestionPerformanceTest extends Specification {
    public static final String QUESTION_TITLE = "qTitle"
    public static final String QUESTION_CONTENT = "qContent"
    public static final String OPTION_CONTENT = "optContent"
    public static final String JUSTIFICATION_CONTENT = "justification content"

    @Autowired
    StudentQuestionService studentQuestionService

    @Autowired
    StudentQuestionRepository studentQuestionRepository

//TODO refactor
    def "performance test to approve 2500 questions"() {
        def question
        when:
        1.upto(1, {
            question = new StudentQuestion()
            question.setId(it)
            question.setKey(it)
            question.setTitle(QUESTION_TITLE)
            question.setContent(QUESTION_CONTENT)
            question.setStatus(StudentQuestion.Status.PENDING)
            studentQuestionRepository.save(question)

            studentQuestionService.approveQuestion(it, JUSTIFICATION_CONTENT)})

        then:
        true
    }
//TODO refactor
    def "performance test to reject 2500 questions"() {
        when:
        2.upto(2, {
            def question
            question = new StudentQuestion()
            question.setId(it)
            question.setKey(it)
            question.setTitle(QUESTION_TITLE)
            question.setContent(QUESTION_CONTENT)
            question.setStatus(StudentQuestion.Status.PENDING)
            studentQuestionRepository.save(question)

            studentQuestionService.rejectQuestion(it, JUSTIFICATION_CONTENT)})



        then:
        true
    }

    @TestConfiguration
    static class StudentQuestionServiceImplTestContextConfiguration {

        @Bean
        StudentQuestionService studentQuestionService() {
            return new StudentQuestionService()
        }
    }
}
