package pt.ulisboa.tecnico.socialsoftware.tutor.tourney.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tourney.Tourney
import pt.ulisboa.tecnico.socialsoftware.tutor.tourney.TourneyRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tourney.TourneyService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@DataJpaTest
class GetOpenTourneysTest extends Specification {

    public static final Integer TOURNEY_ONE_NUMBER_QUESTIONS = 1
    public static final Integer TOURNEY_TWO_NUMBER_QUESTIONS = 2
    public static final String TOURNEY_AVAILABLE_DATE = "2020-01-01 21:12"
    public static final String TOURNEY_CONCLUSION_DATE = "2020-01-06 21:12"

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


    def "no tourney open"(){
        def user = new User("name", "username", 1, User.Role.STUDENT)
        userRepository.save(user)
        def userId = userRepository.findAll().get(0).getId()
        when:
        def result = tourneyService.getOpenTourneys(userId)

        then:
        result.size() == 0
    }

    def "two tourneys - open one the user can access the not"(){
        given: "two tourneys"
        def course = new Course()
        courseRepository.save(course)
        def courseExecution = new CourseExecution(course, "AC", "1", Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        def user = new User("name", "username", 1, User.Role.STUDENT)
        user.addCourse(courseExecution)
        userRepository.save(user)
        def userId = userRepository.findAll().get(0).getId()

        def tourney = new Tourney(TOURNEY_ONE_NUMBER_QUESTIONS, TOURNEY_AVAILABLE_DATE, TOURNEY_CONCLUSION_DATE, user)
        tourney.setCourseExecution(courseExecution)
        tourneyRepository.save(tourney)

        tourney = new Tourney(TOURNEY_TWO_NUMBER_QUESTIONS, TOURNEY_AVAILABLE_DATE, TOURNEY_CONCLUSION_DATE, user)
        tourneyRepository.save(tourney)

        when:
        def result = tourneyService.getOpenTourneys(userId)

        then:
        result.size() == 1
        and:
        result.get(0).getTourneyNumberOfQuestions() == 1
    }

    @TestConfiguration
    static class TourneyServiceImplTestContextConfiguration {

        @Bean
        TourneyService tourneyService() {
            return new TourneyService()
        }
    }

}

