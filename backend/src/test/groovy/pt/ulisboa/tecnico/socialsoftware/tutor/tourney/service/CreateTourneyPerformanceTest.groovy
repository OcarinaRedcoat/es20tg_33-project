package pt.ulisboa.tecnico.socialsoftware.tutor.tourney.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
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

@DataJpaTest
class CreateTourneyPerformanceTest extends Specification{
    public static final String TOURNEY_TITLE = "Tourney 1"
    public static final Integer TOURNEY_ONE_NUMBER_QUESTIONS = 1
    public static final String TOURNEY_AVAILABLE_DATE = "2020-01-01 21:12"
    public static final String TOURNEY_CONCLUSION_DATE = "2020-01-06 21:12"

    public static final String NAME = "name"
    public static final String USERNAME = "username"

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

    @Autowired
    TopicService topicService

    def "performance testing to create 10000 tourneys"(){
        given: "one course execution"
        def course = new Course()
        courseRepository.save(course)
        def courseId = courseRepository.findAll().get(0).getId()
        def courseExecution = new CourseExecution(course, "AC", "1", Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        and: "one user"
        def user = new User(NAME, USERNAME, 1, User.Role.STUDENT)
        user.addCourse(courseExecution)
        userRepository.save(user)
        def userId = userRepository.findAll().get(0).getId()

        and: "one tourney"
        def tourney = new TourneyDto()
        tourney.setTourneyTitle(TOURNEY_TITLE)
        tourney.setTourneyNumberOfQuestions(TOURNEY_ONE_NUMBER_QUESTIONS)
        tourney.setTourneyStatus(Tourney.Status.CLOSED)
        tourney.setTourneyAvailableDate(TOURNEY_AVAILABLE_DATE)
        tourney.setTourneyConclusionDate(TOURNEY_CONCLUSION_DATE)
        tourney.setTourneyCourseExecution(new CourseDto(courseExecution))

        and: "one topic"
        def topicDto = new TopicDto()
        topicDto.setName("topic")
        course.addTopic(new Topic(topicDto))
        topicService.createTopic(courseId, topicDto)
        def topics = new ArrayList()
        topics.add(topicDto)
        tourney.setTourneyTopics(topics)

        when:
        1.upto(1, { tourneyService.createTourney(tourney, userId)})

        then:
        true
    }

    @TestConfiguration
    static class TourneyServiceImplTestContextConfiguration {

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
        TourneyService tourneyService() {
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
