package pt.ulisboa.tecnico.socialsoftware.tutor.tourney.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService
import pt.ulisboa.tecnico.socialsoftware.tutor.tourney.Tourney
import pt.ulisboa.tecnico.socialsoftware.tutor.tourney.TourneyRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tourney.TourneyService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@DataJpaTest
class UpdateTourneyPerformanceTest extends Specification {

    static final String TOURNEY_TITLE = "Tourney 1"
    static final Integer TOURNEY_ONE_NUMBER_QUESTIONS = 1
    static final String TOURNEY_AVAILABLE_DATE = "2020-01-01 21:12"
    static final String TOURNEY_CONCLUSION_DATE = "2020-01-06 21:12"

    static final String NAME = "name"
    static final String USERNAME = "username"

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

    def "performance testing to cancel 10000 tourneys"() {
        given: "one course execution"
        def course = new Course()
        courseRepository.save(course)
        def courseExecution = new CourseExecution(course, "AC", "1", Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        and: "one user"

        def user = new User(NAME, USERNAME, 1, User.Role.STUDENT)
        user.addCourse(courseExecution)
        userRepository.save(user)

        and: "a 10000 tourneys"
        1.upto(1, {
            def tourney = new Tourney(TOURNEY_TITLE, TOURNEY_ONE_NUMBER_QUESTIONS, TOURNEY_AVAILABLE_DATE, TOURNEY_CONCLUSION_DATE, user)
            tourney.setCourseExecution(courseExecution)
            tourneyRepository.save(tourney)
        })

        when:
        0.upto(0, {
            tourneyService.cancelTournament(tourneyRepository.findAll().get(it).getId())
        })

        then:
        true
    }


    @TestConfiguration
    static class TourneyServiceImplTestContextConfiguration {

        @Bean
        QuizService quizService() {
            return new QuizService()
        }

        @Bean
        TourneyService tourneyService() {
            return new TourneyService()
        }
    }
}
