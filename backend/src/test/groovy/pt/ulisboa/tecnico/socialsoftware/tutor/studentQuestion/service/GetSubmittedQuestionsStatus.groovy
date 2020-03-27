package pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@DataJpaTest
class GetSubmittedQuestionsStatus extends Specification {
    public static final String USERNAME_1 = "user1"
    public static final String USERNAME_2 = "user2"
    public static final String USERNAME_3 = "user3"
    public static final String PERSON_NAME = "Name"
    public static final String QUESTION_TITLE1 = "qTitle1"
    public static final String QUESTION_TITLE2 = "qTitle2"
    public static final String QUESTION_TITLE3 = "qTitle3"
    public static final String QUESTION_CONTENT = "qContent"

    @Autowired
    UserRepository userRepository

    @Autowired
    StudentQuestionService studentQuestionService

    @Autowired
    StudentQuestionRepository studentQuestionRepository

    def userWithQuestions
    def userWithoutQuestions

    def setup() {
        userWithQuestions = new User(PERSON_NAME, USERNAME_1, 1, User.Role.STUDENT)
        userRepository.save(userWithQuestions)

        userWithoutQuestions = new User(PERSON_NAME, USERNAME_2, 2, User.Role.STUDENT)
        userRepository.save(userWithoutQuestions)
    }

    def "a student tries to get the state of his submitted questions"() {
        given:
        def question = new StudentQuestion()
        question.setKey(key)
        question.setTitle(title)
        question.setContent(content)
        question.setStatus(status)
        userWithQuestions.addSubmittedQuestion(question)
        studentQuestionRepository.save(question)

        when: "the student tries to see the stats about his submitted questions"
        studentQuestionService.getSubmittedQuestionsStats(USERNAME_1)

        then: "the correct information is stored"
        def result = userRepository.findByUsername(USERNAME_1)
        result.numberOfSubmittedQuestions == 1
        result.numberOfApprovedQuestions == nrApprQ
        def questions = result.getSubmittedQuestions()
        questions.size() == 1
        def resQuestion = questions.get(0)
        resQuestion.getKey() == resKey
        resQuestion.getTitle() == resTitle
        resQuestion.getContent() == resContent
        resQuestion.getStatus() == resStatus

        where:
        key     | title           | content          | status                           | resKey    | resTitle        | resContent       | resStatus                        | nrApprQ
        1       | QUESTION_TITLE1 | QUESTION_CONTENT | StudentQuestion.Status.PENDING   | 1         | QUESTION_TITLE1 | QUESTION_CONTENT | StudentQuestion.Status.PENDING   | 0
        2       | QUESTION_TITLE2 | QUESTION_CONTENT | StudentQuestion.Status.PENDING   | 2         | QUESTION_TITLE2 | QUESTION_CONTENT | StudentQuestion.Status.PENDING   | 0
        3       | QUESTION_TITLE3 | QUESTION_CONTENT | StudentQuestion.Status.APPROVED  | 3         | QUESTION_TITLE3 | QUESTION_CONTENT | StudentQuestion.Status.APPROVED  | 1
    }

    def "the student doesn't have submitted questions"() {
        when: "the student tries to see the stats about his submitted questions"
        studentQuestionService.getSubmittedQuestionsStats(USERNAME_2)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.USER_WITHOUT_SUBMITTED_QUESTIONS
    }

    def "the student doesn't exist"() {
        when: "the student tries to see the stats about his submitted questions"
        studentQuestionService.getSubmittedQuestionsStats(USERNAME_3)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.USERNAME_NOT_FOUND
    }

    @TestConfiguration
    static class StudentQuestionServiceImplTestContextConfiguration {

        @Bean
        StudentQuestionService studentQuestionService() {
            return new StudentQuestionService()
        }
    }

}
