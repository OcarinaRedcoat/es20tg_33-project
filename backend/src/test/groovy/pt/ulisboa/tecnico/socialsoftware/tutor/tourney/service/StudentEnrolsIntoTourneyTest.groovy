package pt.ulisboa.tecnico.socialsoftware.tutor.tourney.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.tourney.Tourney
import pt.ulisboa.tecnico.socialsoftware.tutor.tourney.TourneyDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tourney.TourneyRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tourney.TourneyService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@DataJpaTest
class StudentEnrolsIntoTourneyTest extends Specification{

    static final int NUMBER_QUESTIONS = 5
    static final int CREATOR_KEY = 1
    static final int STUDENT_KEY = 2
    static final int NOT_STUDENT_KEY = 3

    def tourney
    def topicDto

    @Autowired
    TourneyService tourneyService

    @Autowired
    TourneyRepository tourneyRepository

    @Autowired
    UserRepository userRepository

    def setup() {
        tourney = new TourneyDto()
        def formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        def availableDate = LocalDateTime.now()
        def conclusionDate = LocalDateTime.now().plusDays(1)

        tourney.setTourneyStatus(Tourney.Status.CLOSED)
        tourney.setTourneyAvailableDate(availableDate.format(formatter))
        tourney.setTourneyConclusionDate(conclusionDate.format(formatter))
        tourney.setTourneyNumberOfQuestions(NUMBER_QUESTIONS)

        def topics = new ArrayList()
        topics.add(topicDto)
        tourney.setTourneyTopics(topics)

        def user = new User("name", "username",CREATOR_KEY, User.Role.STUDENT)
        userRepository.save(user)

        user = new User("name", "username1",STUDENT_KEY, User.Role.STUDENT)
        userRepository.save(user)

        user = new User("name", "username2",NOT_STUDENT_KEY, User.Role.TEACHER)
        userRepository.save(user)

        tourneyService.createTourney(tourney, userRepository.findAll().get(0).getId())
    }

    def "a student enrols into a tourney"() {
        given: "a studentId and a tourneyId"
        def userId = userRepository.findAll().get(1).getId()
        def tourneyId = tourneyRepository.findAll().get(0).getId()

        when:
        tourneyService.enrollStudent(tourneyId, userId)

        then:
        def tourney = tourneyRepository.findAll().get(0)
        def enrolledStudents = tourney.getEnrolledStudents()
        enrolledStudents.get(0).getKey() == STUDENT_KEY
        enrolledStudents.size() == 1
    }

    def "tourney doesn't exist"() {
        given:
        def userId = userRepository.findAll().get(1).getId()
        def tourneyId = -1

        when:
        tourneyService.enrollStudent(tourneyId, userId)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNEY_NOT_FOUND
        def tourney = tourneyRepository.findAll().get(0)
        tourney.getEnrolledStudents().size() == 0
    }

    def "tourney is closed"() {
        given:
        def userId = userRepository.findAll().get(1).getId()
        def repoTourney = tourneyRepository.findAll().get(0)
        repoTourney.closeTourney()
        def tourneyId = repoTourney.getId()

        when:
        tourneyService.enrollStudent(tourneyId, userId)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNEY_CLOSED
        def tourney = tourneyRepository.findAll().get(0)
        tourney.getEnrolledStudents().size() == 0
    }

    def "user doesn't exist"() {
        given:
        def userId = -1
        def tourneyId = tourneyRepository.findAll().get(0).getId()

        when:
        tourneyService.enrollStudent(tourneyId, userId)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.USER_NOT_FOUND
        def tourney = tourneyRepository.findAll().get(0)
        tourney.getEnrolledStudents().size() == 0
    }

    def "user is not a student"() {
        given:
        def notStudentId = userRepository.findAll().get(2).getId()
        def tourneyId = tourneyRepository.findAll().get(0).getId()

        when:
        tourneyService.enrollStudent(tourneyId, notStudentId)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.USER_NOT_STUDENT
        def tourney = tourneyRepository.findAll().get(0)
        tourney.getEnrolledStudents().size() == 0
    }

    @TestConfiguration
    static class TourneyServiceImplTestContextConfiguration{

        @Bean
        TourneyService tourneyService(){
            return new TourneyService()
        }
    }

}
