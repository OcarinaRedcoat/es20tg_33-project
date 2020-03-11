package pt.ulisboa.tecnico.socialsoftware.tutor.tourney.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tourney.Tourney
import pt.ulisboa.tecnico.socialsoftware.tutor.tourney.TourneyDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tourney.TourneyRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tourney.TourneyService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.COURSE_EXECUTION_ACADEMIC_TERM_IS_EMPTY
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.COURSE_EXECUTION_ACADEMIC_TERM_IS_EMPTY
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.COURSE_EXECUTION_ACRONYM_IS_EMPTY
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.COURSE_EXECUTION_ACRONYM_IS_EMPTY
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.COURSE_NAME_IS_EMPTY
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.COURSE_NAME_IS_EMPTY
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.COURSE_TYPE_NOT_DEFINED
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.USER_NOT_FOUND
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.USER_NOT_STUDENT

@DataJpaTest

class StudentEnrolsIntoTourneyTest extends Specification{

    static final int QUESTION_NUMBER = 5
    static final int CREATOR_KEY = 1
    static final int STUDENT_KEY = 2
    static final int NOT_STUDENT_KEY = 3

    def tourney

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

        def topicDto = new TopicDto()
        def topics = new ArrayList()
        topics.add(topicDto)
        tourney.setTourneyTopics(topics)

        def user = new User("name", "username",CREATOR_KEY, User.Role.STUDENT)
        userRepository.save(user)

        user = new User("name", "username",STUDENT_KEY, User.Role.STUDENT)
        userRepository.save(user)

        user = new User("name", "username",NOT_STUDENT_KEY, User.Role.TEACHER)
        userRepository.save(user)
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

    @Unroll
    def "enroll student"() {
        given:
        def USER_ID = userRepository.findAll().get(1).getId()
        def NOT_USER_ID = userRepository.findAll().get(2).getId()
        def TOURNEY_ID = tourneyRepository.findAll().get(0).getId()

        when:
        tourneyService.enrollStudent(tourneyId, userId)

        then:
        def error = thrown(TutorException)
        error.errorMessage == errorMessage
        def tourney = tourneyRepository.findAll().get(0)
        tourney.getEnrolledStudents().size() == 0

        where:
        userId      | tourneyId  || errorMessage
        null        | TOURNEY_ID || USER_NOT_FOUND
        NOT_USER_ID | TOURNEY_ID || USER_NOT_STUDENT
        USER_ID     | null       || COURSE_NAME_IS_EMPTY
    }

    @TestConfiguration
    static class TourneyServiceImplTestContextConfiguration{

        @Bean
        TourneyService tourneyService(){
            return new TourneyService()
        }
    }

}
