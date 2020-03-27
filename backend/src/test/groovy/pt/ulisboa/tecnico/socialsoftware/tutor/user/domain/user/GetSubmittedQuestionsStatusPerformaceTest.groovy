package pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService
import spock.lang.Specification

@DataJpaTest
class GetSubmittedQuestionsStatusPerformaceTest extends Specification {
    public static final String USERNAME = "ist100000"
    public static final String PERSON_NAME = "Name"
    public static final String QUESTION_TITLE = "qTitle"
    public static final String QUESTION_CONTENT = "qContent"
    public static final String OPTION_CONTENT = "optContent"
    public static final String TEST_COURSE = "Course"

    @Autowired
    UserRepository userRepository

    @Autowired
    UserService userService

    @Autowired
    QuestionRepository questionRepository
    @Autowired
    QuestionService questionService

    @Autowired
    CourseRepository courseRepository


    def "a student tries to get the state of his submitted questions"() {
        given: "a course"
        def course = new Course(TEST_COURSE, Course.Type.TECNICO)
        courseRepository.save(course)
        and: "one student"
        def user = new User(PERSON_NAME, USERNAME, 1, User.Role.STUDENT)
        userRepository.save(user)
        and: "a 100 question dto"
        1.upto(1, {
        def questionDto = new QuestionDto()
        questionDto.setKey(it)
        questionDto.setTitle(QUESTION_TITLE)
        questionDto.setContent(QUESTION_CONTENT)
        questionDto.setStatus(Question.Status.PENDING.name())
        def options = new ArrayList<OptionDto>()
        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_CONTENT)
        optionDto.setCorrect(true)
        options.add(optionDto)
        optionDto = new OptionDto()
        optionDto.setContent(OPTION_CONTENT)
        optionDto.setCorrect(false)
        options.add(optionDto)
        optionDto = new OptionDto()
        optionDto.setContent(OPTION_CONTENT)
        optionDto.setCorrect(false)
        options.add(optionDto)
        optionDto = new OptionDto()
        optionDto.setContent(OPTION_CONTENT)
        optionDto.setCorrect(false)
        options.add(optionDto)
        questionDto.setOptions(options)
        questionService.submitQuestion(course.getId(), questionDto, user.getUsername())})


        when: "get 5000"
        1.upto(1, {
            userService.getSubmittedQuestionsStats(USERNAME)})
        then:
        true
    }

    @TestConfiguration
    static class UserServiceImplTestContextConfiguration {

        @Bean
        UserService userService() {
            return new UserService()
        }
        @Bean
        QuestionService questionService() {
            return new QuestionService()
        }
    }

}
