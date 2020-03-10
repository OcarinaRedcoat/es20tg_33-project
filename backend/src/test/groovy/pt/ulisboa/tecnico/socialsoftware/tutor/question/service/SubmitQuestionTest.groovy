package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

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
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@DataJpaTest
class SubmitQuestionTest extends Specification {
    public static final String TEST_COURSE = "TestCourse"
    public static final String ACRONYM = "TC"
    public static final String ACADEMIC_TERM = "1S"
    public static final String QUESTION_TITLE = "qTitle"
    public static final String QUESTION_CONTENT = "qContent"
    public static final String OPTION_CONTENT = "optContent"
    public static final String USERNAME = "ist190637"
    public static final String PERSON_NAME = "Name"

    @Autowired
    QuestionService questionService

    @Autowired
    CourseRepository courseRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    QuestionRepository questionRepository

    def course
    def user

    def setup() {
        course = new Course(TEST_COURSE, Course.Type.TECNICO)
        courseRepository.save(course)

        user = new User()
        user.setKey(1)
        user.setUsername(USERNAME)
        user.setName(PERSON_NAME)
        user.setRole(User.Role.STUDENT)
        userRepository.save(user)
    }

    def "submit a question with title, content and four options, with one being correct"() {
        given: "a question dto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_TITLE)
        questionDto.setContent(QUESTION_CONTENT)
        questionDto.setStatus(Question.Status.PENDING.name())
        and: "four options"
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

        when:
        questionService.submitQuestion(course.getId(), questionDto, user.getUsername())

        then: "the question is submitted successfully"
        questionRepository.count() == 1L
        def result = questionRepository.findAll().get(0)
        result.getId() != null
        result.getKey() == 1
        result.getStatus() == Question.Status.PENDING
        result.getTitle() == QUESTION_TITLE
        result.getContent() == QUESTION_CONTENT
        result.getOptions().size() == 4
        result.getCourse().getName() == TEST_COURSE
        result.getSubmittingUser().getUsername() == USERNAME
        course.getQuestions().contains(result)
        user.getSubmittedQuestions().get(0).getKey() == 1
    }

    def "question submitted without a title or content"() {
        given: "a question dto without title"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(null)
        questionDto.setContent(null)
        questionDto.setStatus(Question.Status.PENDING.name())

        when:
        questionService.submitQuestion(course.getId(), questionDto, user.getUsername())

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.QUESTION_MISSING_TITLE_OR_CONTENT
        questionRepository.count() == 0L
    }

    def "question with two options"() {
        given: "a question dto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_TITLE)
        questionDto.setContent(QUESTION_CONTENT)
        questionDto.setStatus(Question.Status.PENDING.name())
        and: "two options"
        def options = new ArrayList<OptionDto>()
        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_CONTENT)
        optionDto.setCorrect(true)
        options.add(optionDto)
        optionDto = new OptionDto()
        optionDto.setContent(OPTION_CONTENT)
        optionDto.setCorrect(false)
        options.add(optionDto)
        questionDto.setOptions(options)

        when:
        questionService.submitQuestion(course.getId(), questionDto, user.getUsername())

        then: "the question is submitted successfully"
        questionRepository.count() == 1L
        def result = questionRepository.findAll().get(0)
        result.getId() != null
        result.getKey() == 1
        result.getStatus() == Question.Status.PENDING
        result.getTitle() == QUESTION_TITLE
        result.getContent() == QUESTION_CONTENT
        result.getOptions().size() == 2
        result.getCourse().getName() == TEST_COURSE
        result.getSubmittingUser().getUsername() == USERNAME
        course.getQuestions().contains(result)
        user.getSubmittedQuestions().get(0).getKey() == 1
    }

    def "question with two correct answers"() {
        given: "a question dto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_TITLE)
        questionDto.setContent(QUESTION_CONTENT)
        questionDto.setStatus(Question.Status.PENDING.name())
        and: "four options with two correct ones"
        def options = new ArrayList<OptionDto>()
        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_CONTENT)
        optionDto.setCorrect(true)
        options.add(optionDto)
        optionDto = new OptionDto()
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
        questionDto.setOptions(options)

        when:
        questionService.submitQuestion(course.getId(), questionDto, user.getUsername())

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.QUESTION_MULTIPLE_CORRECT_OPTIONS
        questionRepository.count() == 0L
    }

    def "question without a correct answer"() {
        given: "a question dto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_TITLE)
        questionDto.setContent(QUESTION_CONTENT)
        questionDto.setStatus(Question.Status.PENDING.name())
        and: "four options with none being correct"
        def options = new ArrayList<OptionDto>()
        def optionDto = new OptionDto()
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
        optionDto = new OptionDto()
        optionDto.setContent(OPTION_CONTENT)
        optionDto.setCorrect(false)
        options.add(optionDto)
        questionDto.setOptions(options)

        when:
        questionService.submitQuestion(course.getId(), questionDto, user.getUsername())

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.QUESTION_MISSING_CORRECT_OPTION
        questionRepository.count() == 0L
    }

    @TestConfiguration
    static class QuestionServiceImplTestContextConfiguration {

        @Bean
        QuestionService questionService() {
            return new QuestionService()
        }
    }

}