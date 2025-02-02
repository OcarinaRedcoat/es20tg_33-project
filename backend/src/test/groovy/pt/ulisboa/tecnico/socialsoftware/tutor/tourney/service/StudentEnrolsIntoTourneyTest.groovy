package pt.ulisboa.tecnico.socialsoftware.tutor.tourney.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.TopicService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService
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

    public static final String TOURNEY_TITLE = "Tourney 1"
    public static final int NUMBER_QUESTIONS = 5
    public static final int CREATOR_KEY = 1
    public static final int STUDENT_KEY = 2
    public static final int NOT_STUDENT_KEY = 3
    public static final int OTHER_STUDENT_KEY = 4
    public static final String COURSE_NAME = "Arquitetura de Software"

    def tourney
    def topicDto
    def course
    def courseExecution

    @Autowired
    TourneyService tourneyService

    @Autowired
    TopicService topicService

    @Autowired
    TourneyRepository tourneyRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository  courseExecutionRepository

    def setup() {
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)
        def courseId = courseRepository.findAll().get(0).getId()
        courseExecution = new CourseExecution(course, "AC", "1", Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        tourney = new TourneyDto()
        def availableDate = DateHandler.now()
        def conclusionDate = DateHandler.now().plusDays(1)

        tourney.setTourneyTitle(TOURNEY_TITLE)
        tourney.setTourneyAvailableDate(DateHandler.toISOString(availableDate))
        tourney.setTourneyConclusionDate(DateHandler.toISOString(conclusionDate))
        tourney.setTourneyNumberOfQuestions(NUMBER_QUESTIONS)
        tourney.setTourneyCourseExecution(new CourseDto(courseExecution))
        tourney.setTourneyStatus(Tourney.Status.OPEN)

        topicDto = new TopicDto()
        topicDto.setName("topic")
        topicService.createTopic(courseId, topicDto)
        def topics = new ArrayList()
        topics.add(topicDto)
        tourney.setTourneyTopics(topics)

        def user = new User("name1", "username1",CREATOR_KEY, User.Role.STUDENT)
        user.addCourse(courseExecution)
        userRepository.save(user)

        user = new User("name2", "username2",STUDENT_KEY, User.Role.STUDENT)
        userRepository.save(user)

        user = new User("name3", "username3",NOT_STUDENT_KEY, User.Role.TEACHER)
        userRepository.save(user)

        tourneyService.createTourney(tourney, userRepository.findAll().get(0).getId())
    }

    def "a student enrols into a tourney"() {
        given: "a studentId and a tourneyId"
        def userId = userRepository.findAll().get(0).getId()
        def tourneyId = tourneyRepository.findAll().get(0).getId()

        when:
        tourneyService.enrollStudent(tourneyId, userId)

        then:
        def tourney = tourneyRepository.findAll().get(0)
        def enrolledStudents = tourney.getEnrolledStudents()
        enrolledStudents.get(0).getKey() == CREATOR_KEY
        enrolledStudents.size() == 1
    }

    def "a student enrols into a tourney twice"() {
        given: "a studentId and a tourneyId"
        def userId = userRepository.findAll().get(0).getId()
        def tourneyId = tourneyRepository.findAll().get(0).getId()
        tourneyService.enrollStudent(tourneyId, userId)

        when:
        tourneyService.enrollStudent(tourneyId, userId)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.STUDENT_ALREADY_ENROLLED
        def tourney = tourneyRepository.findAll().get(0)
        tourney.getEnrolledStudents().size() == 1
    }

    def "two students enroll in a tourney"() {
        given: "two students"
        def user1Id = userRepository.findAll().get(0).getId()
        def user = new User("name4", "username4", OTHER_STUDENT_KEY, User.Role.STUDENT)
        user.addCourse(courseExecution)
        userRepository.save(user)
        def user2Id = userRepository.findAll().get(3).getId()

        and: "a tourney"
        def tourneyId = tourneyRepository.findAll().get(0).getId()
        tourneyService.enrollStudent(tourneyId, user1Id)

        when:
        tourneyService.enrollStudent(tourneyId, user2Id)

        then:
        def tourney = tourneyRepository.findAll().get(0)
        tourney.getQuiz().getTourney().getId() == tourneyId
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

    def "student is not enrolled on tourney course execution"() {
        given:
        def userId = userRepository.findAll().get(1).getId()
        def tourneyId = tourneyRepository.findAll().get(0).getId()

        when:
        tourneyService.enrollStudent(tourneyId, userId)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.STUDENT_CANT_ACCESS_COURSE_EXECUTION
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

    def "tourney is closed"() {
        given:
        def userId = userRepository.findAll().get(1).getId()
        def newTourney = tourneyRepository.findAll().get(0)
        newTourney.closeTourney()
        def tourneyId = newTourney.getId()

        when:
        tourneyService.enrollStudent(tourneyId, userId)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNEY_CLOSED
        def tourney = tourneyRepository.findAll().get(0)
        tourney.getEnrolledStudents().size() == 0
    }

    def "enrolled student gets the quiz answer"() {
        given: "two students"
        def user1Id = userRepository.findAll().get(0).getId()
        def user = new User("name4", "username4", OTHER_STUDENT_KEY, User.Role.STUDENT)
        user.addCourse(courseExecution)
        userRepository.save(user)
        def user2Id = userRepository.findAll().get(3).getId()

        and: "a tourney"
        def tourneyId = tourneyRepository.findAll().get(0).getId()
        tourneyService.enrollStudent(tourneyId, user1Id)
        tourneyService.enrollStudent(tourneyId, user2Id)

        when:
        def response = tourneyService.getTourneyQuizAnswer(tourneyId, user2Id)

        then:
        response.getId() != null
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
        TourneyService tourneyService(){
            return new TourneyService()
        }

        @Bean
        QuestionService questionService() {
            return new QuestionService()
        }

        @Bean
        TopicService topicService() {
            return new TopicService()
        }
    }

}
