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

@DataJpaTest
class CreateTourneyTest extends Specification{

    public static final int QUESTION_NUMBER = 5
    public static final String TOURNEY_TITLE = "Tourney 1"
    public static final String COURSE_NAME = "Arquitetura de Software"

    def tourney
    def formatter
    def availableDate
    def conclusionDate
    def topicDto
    def course

    @Autowired
    TourneyService tourneyService

    @Autowired
    TopicService topicService

    @Autowired
    TourneyRepository tourneyRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    CourseRepository courseRepository

    def setup(){
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)
        def courseId = courseRepository.findAll().get(0).getId()
        def courseExecution = new CourseExecution(course, "AC", "1", Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        tourney = new TourneyDto()
        availableDate = DateHandler.toISOString(DateHandler.now())
        conclusionDate = DateHandler.toISOString(DateHandler.now().plusDays(1))

        tourney.setTourneyTitle(TOURNEY_TITLE)
        tourney.setTourneyStatus(Tourney.Status.CLOSED)
        tourney.setTourneyAvailableDate(availableDate)
        tourney.setTourneyConclusionDate(conclusionDate)
        tourney.setTourneyCourseExecution(new CourseDto(courseExecution))

        topicDto = new TopicDto()
        topicDto.setName("topic")
        topicService.createTopic(courseId, topicDto)

        def topics = new ArrayList()
        topics.add(topicDto)
        tourney.setTourneyTopics(topics)

        def user = new User("name", "username",1, User.Role.STUDENT)
        user.addCourse(courseExecution)
        userRepository.save(user)
    }

    def "create a tourney"(){
        given: "new tourney creation"
        def userId = userRepository.findAll().get(0).getId()
        tourney.setTourneyNumberOfQuestions(QUESTION_NUMBER)

        when:
        tourneyService.createTourney(tourney, userId)

        then:
        tourneyRepository.count() == 1L
        def result = tourneyRepository.findAll().get(0)
        result.getId() != null
        result.getTitle() == TOURNEY_TITLE
        result.getAvailableDate() == availableDate
        result.getConclusionDate() == conclusionDate
        result.getTopics().size() == 1
        result.getStatus() == Tourney.Status.CLOSED
        result.getNumberOfQuestions() == QUESTION_NUMBER
        result.getCreator().getKey() == userId
    }

    def "tourney title is null"(){
        given: "a tourney with no title"
        def userId = userRepository.findAll().get(0).getId()
        tourney.setTourneyNumberOfQuestions(QUESTION_NUMBER)
        tourney.setTourneyTitle(null)

        when:
        tourneyService.createTourney(tourney, userId)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNEY_NOT_CONSISTENT
        tourneyRepository.count() == 0L
    }

    def "tourney title is empty"(){
        given: "a tourney with an empty title"
        def userId = userRepository.findAll().get(0).getId()
        tourney.setTourneyNumberOfQuestions(QUESTION_NUMBER)
        tourney.setTourneyTitle(" ")

        when:
        tourneyService.createTourney(tourney, userId)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNEY_NOT_CONSISTENT
        tourneyRepository.count() == 0L
    }

    def "tourney with no start date"(){
        given: "a tourney with no start date"
        def userId = userRepository.findAll().get(0).getId()
        tourney.setTourneyNumberOfQuestions(QUESTION_NUMBER)
        tourney.setTourneyAvailableDate(null)

        when:
        tourneyService.createTourney(tourney, userId)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNEY_NOT_CONSISTENT
        tourneyRepository.count() == 0L
    }

    def "tourney with no end date"(){
        given: "a tourney with no end date"
        def userId = userRepository.findAll().get(0).getId()
        tourney.setTourneyNumberOfQuestions(QUESTION_NUMBER)
        tourney.setTourneyConclusionDate(null)

        when:
        tourneyService.createTourney(tourney, userId)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNEY_NOT_CONSISTENT
        tourneyRepository.count() == 0L
    }

    def "student not enrolled at course execution"(){
        given: "a tourney with no end date"
        def userId = userRepository.findAll().get(0).getId()
        def courseExecution = new CourseExecution(course, "AC", "2", Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)
        def courseExecutionDto = new CourseDto(courseExecution)
        courseExecutionDto.setCourseExecutionId(courseExecutionRepository.findAll().get(1).getId())
        tourney.setTourneyCourseExecution(courseExecutionDto)
        tourney.setTourneyNumberOfQuestions(QUESTION_NUMBER)

        when:
        tourneyService.createTourney(tourney, userId)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.STUDENT_CANT_ACCESS_COURSE_EXECUTION
        tourneyRepository.count() == 0L
    }

    def "tourney doesn't have a course execution"(){
        given: "a tourney with no end date"
        def userId = userRepository.findAll().get(0).getId()
        tourney.setTourneyCourseExecution(null)
        tourney.setTourneyNumberOfQuestions(QUESTION_NUMBER)

        when:
        tourneyService.createTourney(tourney, userId)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNEY_NOT_CONSISTENT
        tourneyRepository.count() == 0L
    }

    def "course execution doestn't exist"(){
        given: "a tourney with no end date"
        def userId = userRepository.findAll().get(0).getId()
        def courseExecutionDto = new CourseDto()
        courseExecutionDto.setCourseExecutionId(1)
        tourney.setTourneyCourseExecution(courseExecutionDto)
        tourney.setTourneyNumberOfQuestions(QUESTION_NUMBER)

        when:
        tourneyService.createTourney(tourney, userId)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.COURSE_EXECUTION_NOT_FOUND
        tourneyRepository.count() == 0L
    }

    def "topics are not from the same course"(){
        given: "a tourney with no end date"
        def userId = userRepository.findAll().get(0).getId()
        def newCourse = new Course("Other Course", Course.Type.TECNICO)
        courseRepository.save(newCourse)
        def courseId = courseRepository.findAll().get(1).getId()
        topicDto = new TopicDto()
        topicDto.setName("topic2")
        topicService.createTopic(courseId, topicDto)

        def topics = new ArrayList(tourney.getTourneyTopics())
        topics.add(topicDto)
        tourney.setTourneyTopics(topics)
        tourney.setTourneyNumberOfQuestions(QUESTION_NUMBER)

        when:
        tourneyService.createTourney(tourney, userId)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOPICS_NOT_FROM_SAME_COURSE
        tourneyRepository.count() == 0L
    }

    def "tourney with start date bigger than end date"(){
        given: "a tourney with start date bigger than the end date"
        def userId = userRepository.findAll().get(0).getId()
        tourney.setTourneyNumberOfQuestions(QUESTION_NUMBER)
        tourney.setTourneyConclusionDate(DateHandler.toISOString(DateHandler.now().minusDays(1)))

        when:
        tourneyService.createTourney(tourney, userId)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNEY_AVAILABLEDATE_BIGGER_THAN_CONCLUSIONDATE
        tourneyRepository.count() == 0L
    }

    def "tourney topics is empty"(){
        given: "a tourney with no topics"
        def userId = userRepository.findAll().get(0).getId()
        tourney.setTourneyNumberOfQuestions(QUESTION_NUMBER)
        tourney.setTourneyTopics(new ArrayList<Topic>())

        when:
        tourneyService.createTourney(tourney, userId)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNEY_NOT_CONSISTENT
        tourneyRepository.count() == 0L
    }

    def "number of questions is empty"(){
        given: "a tourney without a given number of questions"
        def userId = userRepository.findAll().get(0).getId()
        tourney.setTourneyNumberOfQuestions(0)

        when:
        tourneyService.createTourney(tourney, userId)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNEY_NOT_CONSISTENT
        tourneyRepository.count() == 0L
    }

    def "number of questions is null"(){
        given: "a tourney without a given number of questions"
        def userId = userRepository.findAll().get(0).getId()

        when:
        tourneyService.createTourney(tourney, userId)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNEY_NOT_CONSISTENT
        tourneyRepository.count() == 0L
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
