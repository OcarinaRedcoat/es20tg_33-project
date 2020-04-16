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
    public static final String OPTION_CONTENT = "optionId content"

    @Autowired
    UserRepository userRepository

    @Autowired
    StudentQuestionService studentQuestionService

    @Autowired
    StudentQuestionRepository studentQuestionRepository

    @Autowired
    OptionRepository optionRepository

    def userWithQuestions
    def userWithoutQuestions
    def option

    def setup() {
        userWithQuestions = new User(PERSON_NAME, USERNAME_1, 1, User.Role.STUDENT)
        userRepository.save(userWithQuestions)

        userWithoutQuestions = new User(PERSON_NAME, USERNAME_2, 2, User.Role.STUDENT)
        userRepository.save(userWithoutQuestions)

        option = new Option()
        option.setContent(OPTION_CONTENT)
        option.setCorrect(true)
        option.setSequence(1)
        optionRepository.save(option)
    }

    def "a student tries to get the state of his submitted questions"() {
        given:
        def question = new StudentQuestion()
        question.setKey(key)
        question.setTitle(title)
        question.setContent(content)
        question.setStatus(status)
        question.setSubmittingUser(userWithQuestions)
        question.addOption(option)
        option.setStudentQuestion(question)
        userWithQuestions.addSubmittedQuestion(question)
        studentQuestionRepository.save(question)

        when: "the student tries to see the stats about his submitted questions"
        studentQuestionService.getUserSubmittedQuestions(USERNAME_1)

        then: "the correct information is stored"
        def result = userRepository.findByUsername(USERNAME_1)
        def questions = result.getSubmittedQuestions()
        questions.size() == 1
        def resQuestion = questions.get(0)
        resQuestion.getKey() == resKey
        resQuestion.getTitle() == resTitle
        resQuestion.getContent() == resContent
        resQuestion.getStatus() == resStatus
        resQuestion.getSubmittingUser().getUsername() == resUsername

        where:
        key     | title           | content          | status                           | resKey    | resTitle        | resContent       | resStatus                        | resUsername
        1       | QUESTION_TITLE1 | QUESTION_CONTENT | StudentQuestion.Status.PENDING   | 1         | QUESTION_TITLE1 | QUESTION_CONTENT | StudentQuestion.Status.PENDING   | USERNAME_1
        2       | QUESTION_TITLE2 | QUESTION_CONTENT | StudentQuestion.Status.PENDING   | 2         | QUESTION_TITLE2 | QUESTION_CONTENT | StudentQuestion.Status.PENDING   | USERNAME_1
        3       | QUESTION_TITLE3 | QUESTION_CONTENT | StudentQuestion.Status.APPROVED  | 3         | QUESTION_TITLE3 | QUESTION_CONTENT | StudentQuestion.Status.APPROVED  | USERNAME_1
    }

    def "the student doesn't have submitted questions"() {
        when: "the student tries to see the stats about his submitted questions"
        studentQuestionService.getUserSubmittedQuestionsStats(USERNAME_2)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.USER_WITHOUT_SUBMITTED_QUESTIONS
    }

    def "the student doesn't exist"() {
        when: "the student tries to see the stats about his submitted questions"
        studentQuestionService.getUserSubmittedQuestions(USERNAME_3)

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
