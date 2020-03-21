package pt.ulisboa.tecnico.socialsoftware.tutor.tourney.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.TopicService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.tourney.Tourney
import pt.ulisboa.tecnico.socialsoftware.tutor.tourney.TourneyRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tourney.TourneyService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@DataJpaTest
class UpdateTourneyTest extends Specification {

    public static final Integer TOURNEY_ONE_NUMBER_QUESTIONS = 1
    public static final Integer TOURNEY_TWO_NUMBER_QUESTIONS = 2
    public static final String TOURNEY_AVAILABLE_DATE = "2020-01-01 21:12"
    public static final String TOURNEY_CONCLUSION_DATE = "2020-01-06 21:12"

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
        def course = new Course()
        courseRepository.save(course)
        def courseExecution = new CourseExecution(course, "AC", "1", Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)
        def courseExecution2 = new CourseExecution(course, "AC", "2", Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution2)

        def user1 = new User(NAME1, USERNAME1, 1, User.Role.STUDENT)
        user1.addCourse(courseExecution)
        userRepository.save(user1)

        def user2 = new User(NAME2, USERNAME2, 2, User.Role.STUDENT)
        user1.addCourse(courseExecution)
        userRepository.save(user2)

        def tourney = new Tourney(TOURNEY_ONE_NUMBER_QUESTIONS, TOURNEY_AVAILABLE_DATE, TOURNEY_CONCLUSION_DATE, user1)
        tourney.setCourseExecution(courseExecution)
        tourneyRepository.save(tourney)
    }

    def "creator cancels tourney"(){
        given: "a user"
        def userId = userRepository.findAll().get(0).getId()

        and: "a tourney"
        def tourneyId = tourneyRepository.findAll().get(0).getId()

        when:
        def result = tourneyService.cancelTournament(tourneyId, userId)

        then:
        result.getTourneyStatus() == Tourney.Status.CANCELED
    }

    def "user is not tourney's creator"(){
        given: "a user"
        def userId = userRepository.findAll().get(1).getId()

        and: "a tourney"
        def tourneyId = tourneyRepository.findAll().get(0).getId()

        when:
        tourneyService.cancelTournament(tourneyId, userId)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.STUDENT_IS_NOT_TOURNEY_CREATOR
    }

    @TestConfiguration
    static class TourneyServiceImplTestContextConfiguration{

        @Bean
        TourneyService tourneyService(){
            return new TourneyService()
        }

    }

}
