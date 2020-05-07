package pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@DataJpaTest
class EditApprovedQuestionPerformanceTest extends Specification {
    public static final String COURSE_NAME = "TestCourse"
    public static final String USERNAME = "user"
    public static final String PERSON_NAME = "Name"
    public static final String QUESTION_TITLE = 'question title'
    public static final String QUESTION_CONTENT = 'question content'
    public static final String OPTION_CONTENT = "optionId content"
    public static final String CHANGED_QUESTION_TITLE = 'changed question title'
    public static final String CHANGED_OPTION_CONTENT = "changed optionId content"

    @Autowired
    StudentQuestionService studentQuestionService

    @Autowired
    QuestionService questionService

    @Autowired
    OptionRepository optionRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    StudentQuestionRepository studentQuestionRepository

    @Autowired
    CourseRepository courseRepository

    def question
    def option
    def user
    def course

    def setup() {
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        user = new User(PERSON_NAME, USERNAME, 1, User.Role.STUDENT)
        userRepository.save(user)

        question = new StudentQuestion()
        question.setKey(1)
        question.setTitle(QUESTION_TITLE)
        question.setContent(QUESTION_CONTENT)
        question.setStatus(StudentQuestion.Status.APPROVED)
        question.setSubmittingUser(user)
        question.setCourse(course)

        option = new Option()
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

    def "Change an approved question 2000 times"() {
        given: "a changed student question dto"
        def questionDto = new StudentQuestionDto()
        questionDto.setTitle(CHANGED_QUESTION_TITLE)
        questionDto.setContent(QUESTION_CONTENT)
        and: "four options with a changed one"
        def options = new ArrayList<OptionDto>()
        def optionDto = new OptionDto(option)
        optionDto.setContent(CHANGED_OPTION_CONTENT)
        optionDto.setSequence(0)
        optionDto.setCorrect(true)
        options.add(optionDto)
        optionDto = new OptionDto(option)
        optionDto.setContent(OPTION_CONTENT)
        optionDto.setSequence(1)
        optionDto.setCorrect(false)
        options.add(optionDto)
        optionDto = new OptionDto(option)
        optionDto.setContent(OPTION_CONTENT)
        optionDto.setSequence(2)
        optionDto.setCorrect(false)
        options.add(optionDto)
        optionDto = new OptionDto(option)
        optionDto.setContent(OPTION_CONTENT)
        optionDto.setSequence(3)
        optionDto.setCorrect(false)
        options.add(optionDto)
        questionDto.setOptions(options)

        when:
        1.upto(5, {
            studentQuestionService.editApprovedQuestion(question.getId(), questionDto)
            question.setTitle(QUESTION_TITLE)
        })

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
