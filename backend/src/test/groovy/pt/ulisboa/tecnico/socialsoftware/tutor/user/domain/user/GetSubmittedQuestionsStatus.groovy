package pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.user

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService
import spock.lang.Specification

@DataJpaTest
class GetSubmittedQuestionsStatus extends Specification {
    public static final String USERNAME_1 = "user1"
    public static final String USERNAME_2 = "user2"
    public static final String PERSON_NAME = "Name"
    public static final String QUESTION_TITLE1 = "qTitle1"
    public static final String QUESTION_TITLE2 = "qTitle2"
    public static final String QUESTION_TITLE3 = "qTitle3"
    public static final String QUESTION_CONTENT = "qContent"

    @Autowired
    UserRepository userRepository

    @Autowired
    UserRepository userService

    @Autowired
    QuestionRepository questionRepository

    def userWithQuestions
    def userWithoutQuestions
    def question1
    def question2
    def question3

    def setup() {
        userWithQuestions = new User()
        userWithQuestions.setKey(1)
        userWithQuestions.setUsername(USERNAME_1)
        userWithQuestions.setName(PERSON_NAME)
        userWithQuestions.setRole(User.Role.STUDENT)
        userRepository.save(userWithQuestions)

        userWithoutQuestions = new User()
        userWithoutQuestions.setKey(2)
        userWithoutQuestions.setUsername(USERNAME_2)
        userWithoutQuestions.setName(PERSON_NAME)
        userWithoutQuestions.setRole(User.Role.STUDENT)
        userRepository.save(userWithoutQuestions)

        question1 = new Question()
        question1.setKey(1)
        question1.setTitle(QUESTION_TITLE1)
        question1.setContent(QUESTION_CONTENT)
        question1.setStatus(Question.Status.PENDING)
        question1.setSubmittingUser(userWithQuestions)
        userWithQuestions.addSubmittedQuestion(question1)
        questionRepository.save(question1)

        question2 = new Question()
        question2.setKey(2)
        question2.setTitle(QUESTION_TITLE2)
        question2.setContent(QUESTION_CONTENT)
        question2.setStatus(Question.Status.PENDING)
        question2.setSubmittingUser(userWithQuestions)
        userWithQuestions.addSubmittedQuestion(question2)
        questionRepository.save(question2)

        question3 = new Question()
        question3.setKey(3)
        question3.setTitle(QUESTION_TITLE3)
        question3.setContent(QUESTION_CONTENT)
        question3.setStatus(Question.Status.AVAILABLE)
        question3.setSubmittingUser(userWithQuestions)
        userWithQuestions.addSubmittedQuestion(question3)
        questionRepository.save(question3)
    }

    def "a student tries to get the state of his submitted questions"() {
        when: "the student tries to see the stats about his submitted questions"
        userService.getSubmittedQuestionsStats(userWithQuestions)

        then: "the correct information is stored"
        def result = userRepository.findByUsername(userWithQuestions)
        result.numberOfSubmittedQuestions == 3
        result.numberOfApprovedQuestions == 1
        def questions = result.getSubmittedQuestions()
        questions.size() == 3
        def resQuestion1 = questions.get(0)
        resQuestion1.getKey() == 1
        resQuestion1.getTitle() == QUESTION_TITLE1
        resQuestion1.getContent() == QUESTION_CONTENT
        resQuestion1.getStatus() == Question.Status.PENDING
        resQuestion1.getSubmittingUser().getUsername() == USERNAME_1
        def resQuestion2 = questions.get(1)
        resQuestion2.getKey() == 2
        resQuestion2.getTitle() == QUESTION_TITLE2
        resQuestion2.getContent() == QUESTION_CONTENT
        resQuestion2.getStatus() == Question.Status.PENDING
        resQuestion2.getSubmittingUser().getUsername() == USERNAME_1
        def resQuestion3 = questions.get(2)
        resQuestion3.getKey() == 3
        resQuestion3.getTitle() == QUESTION_TITLE3
        resQuestion3.getContent() == QUESTION_CONTENT
        resQuestion3.getStatus() == Question.Status.AVAILABLE
        resQuestion3.getSubmittingUser().getUsername() == USERNAME_1
    }

    def "the student doesn't have submitted questions"() {
        when: "the student tries to see the stats about his submitted questions"
        userService.getSubmittedQuestionsStats(userWithoutQuestions)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.USER_WITHOUT_SUBMITTED_QUESTIONS
    }

    @TestConfiguration
    static class UserServiceImplTestContextConfiguration {

        @Bean
        UserService userService() {
            return new UserService()
        }
    }

}
