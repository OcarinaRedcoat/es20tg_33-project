package pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@DataJpaTest
class SubmitQuestionPerformanceTest extends Specification {
    public static final String USERNAME = "ist190637"
    public static final String PERSON_NAME = "Name"
    public static final String QUESTION_TITLE = "qTitle"
    public static final String QUESTION_CONTENT = "qContent"
    public static final String OPTION_CONTENT = "optContent"
    public static final String COURSE_NAME = "Arquitetura de Software"

    @Autowired
    StudentQuestionService studentQuestionService

    @Autowired
    QuestionService questionService

    @Autowired
    CourseRepository courseRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    StudentQuestionRepository studentQuestionRepository

    def "performance test to submit 2000 questions"(){
        given: "a course"
        def course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)
        def courseId = courseRepository.findAll().get(0).getId()

        and: "one student"
        def user = new User(PERSON_NAME, USERNAME, 1, User.Role.STUDENT)
        userRepository.save(user)

        when:
        1.upto(1, {
            def questionDto = new StudentQuestionDto()
            questionDto.setTitle(QUESTION_TITLE)
            questionDto.setContent(QUESTION_CONTENT)
            questionDto.setStatus(StudentQuestion.Status.PENDING.name())
            def options = new ArrayList<OptionDto>()
            def optionDto = new OptionDto()
            optionDto.setContent(OPTION_CONTENT)
            optionDto.setSequence(0)
            optionDto.setCorrect(true)
            options.add(optionDto)
            optionDto = new OptionDto()
            optionDto.setContent(OPTION_CONTENT)
            optionDto.setSequence(1)
            optionDto.setCorrect(false)
            options.add(optionDto)
            optionDto = new OptionDto()
            optionDto.setContent(OPTION_CONTENT)
            optionDto.setSequence(2)
            optionDto.setCorrect(false)
            options.add(optionDto)
            optionDto = new OptionDto()
            optionDto.setContent(OPTION_CONTENT)
            optionDto.setSequence(3)
            optionDto.setCorrect(false)
            options.add(optionDto)
            questionDto.setOptions(options)

            studentQuestionService.submitQuestion(courseId, questionDto, USERNAME)})

        then:
        true
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
