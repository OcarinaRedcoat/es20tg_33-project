package pt.ulisboa.tecnico.socialsoftware.tutor.tourney.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService
import pt.ulisboa.tecnico.socialsoftware.tutor.tourney.Tourney
import pt.ulisboa.tecnico.socialsoftware.tutor.tourney.TourneyRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tourney.TourneyService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@DataJpaTest
class UpdateTourneyTest extends Specification {

    public static final String TOURNEY_TITLE = "Tourney 1"
    public static final Integer TOURNEY_ONE_NUMBER_QUESTIONS = 1
    public static final String COURSE_NAME = "Arquitetura de Software"

    public static final String NAME1 = "name1"
    public static final String USERNAME1 = "username1"
    public static final String NAME2 = "name2"
    public static final String USERNAME2 = "username2"

    @Autowired
    TourneyService tourneyService

    @Autowired
    TourneyRepository tourneyRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    CourseRepository courseRepository

    def  setup() {
        def course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)
        def courseExecution = new CourseExecution(course, "AC", "1", Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        def user1 = new User(NAME1, USERNAME1, 1, User.Role.STUDENT)
        user1.addCourse(courseExecution)
        userRepository.save(user1)

        def user2 = new User(NAME2, USERNAME2, 2, User.Role.STUDENT)
        user2.addCourse(courseExecution)
        userRepository.save(user2)

        def availableDate = DateHandler.toISOString(DateHandler.now())
        def conclusionDate = DateHandler.toISOString(DateHandler.now().plusDays(1))

        def tourney = new Tourney(TOURNEY_TITLE, TOURNEY_ONE_NUMBER_QUESTIONS, availableDate, conclusionDate, user1)
        tourney.setCourseExecution(courseExecution)
        tourneyRepository.save(tourney)
    }

    def "creator cancels tourney"(){
        given: "a tourney"
        def tourneyId = tourneyRepository.findAll().get(0).getId()

        when:
        def result = tourneyService.cancelTournament(tourneyId)

        then:
        result.getTourneyStatus() == Tourney.Status.CANCELED
    }


    @TestConfiguration
    static class TourneyServiceImplTestContextConfiguration{

        @Bean
        AnswerService answerService() {
            return new AnswerService()
        }

        @Bean
        AnswersXmlImport answersXmlImport() {
            return new AnswersXmlImport()
        }

        @Bean
        QuizService quizService() {
            return new QuizService()
        }

        @Bean
        QuestionService questionService() {
            return new QuestionService()
        }

        @Bean
        TourneyService tourneyService(){
            return new TourneyService()
        }

    }

}
