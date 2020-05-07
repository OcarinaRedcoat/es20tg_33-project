package pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@DataJpaTest
class MakeAvailablePerformanceTest extends Specification {
    public static final String USERNAME = "user"
    public static final String PERSON_NAME = "Name"
    public static final String COURSE_NAME = "Test Course"
    public static final String QUESTION_TITLE = 'question title'
    public static final String QUESTION_CONTENT = 'question content'
    public static final String OPTION_CONTENT = "optionId content"

    @Autowired
    StudentQuestionService studentQuestionService

    @Autowired
    QuestionService questionService

    @Autowired
    OptionRepository optionRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    StudentQuestionRepository studentQuestionRepository

    @Autowired
    QuestionRepository questionRepository

    def question
    def course
    def user

    def setup() {
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        user = new User(PERSON_NAME, USERNAME, 1, User.Role.STUDENT)
        userRepository.save(user)

        question = new StudentQuestion()
        question.setKey(1)
        question.setTitle(QUESTION_TITLE)
        question.setContent(QUESTION_CONTENT)
        question.setSubmittingUser(user)
        question.setCourse(course)

        def option = new Option()
        option.setContent(OPTION_CONTENT)
        option.setCorrect(true)
        option.setSequence(0)
        question.addOption(option)
        option = new Option()
        option.setContent(OPTION_CONTENT)
        option.setCorrect(false)
        option.setSequence(1)
        question.addOption(option)
        option = new Option()
        option.setContent(OPTION_CONTENT)
        option.setCorrect(false)
        option.setSequence(2)
        question.addOption(option)
        option = new Option()
        option.setContent(OPTION_CONTENT)
        option.setCorrect(false)
        option.setSequence(3)
        question.addOption(option)

        studentQuestionRepository.save(question)

        course.addStudentQuestion(question)
        user.addSubmittedQuestion(question)
    }

    def "performance test to make 2000 available questions"() {
        given: "an approved student question"
        question.setStatus(StudentQuestion.Status.APPROVED)

        when:
        1.upto(1, {
            studentQuestionService.makeStudentQuestionAvailable(question.getId())
            question.setStatus(StudentQuestion.Status.APPROVED)
        })

        then: "all the questions where successfully created"
        questionRepository.count() == 1L
    }


    @TestConfiguration
    static class StudentQuestionServiceImplTestContextConfiguration {

        @Bean
        StudentQuestionService studentQuestionService() {
            return new StudentQuestionService()
        }

        @Bean
        QuestionService questionService() {
            return new QuestionService()
        }
    }
}
