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
class ResubmissionOfRejectedQuestionPerformaceTest extends Specification {
    public static final String USERNAME = "user"
    public static final String PERSON_NAME = "Name"
    public static final String QUESTION_TITLE = 'question title'
    public static final String CHANGED_QUESTION_TITLE = 'changed question title'
    public static final String QUESTION_CONTENT = 'question content'
    public static final String OPTION_CONTENT = "optionId content"
    public static final String CHANGED_OPTION_CONTENT = "changed optionId content"
    public static final String TEST_COURSE = "TestCourse"

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
    def optionB
    def optionC
    def optionD
    def user
    def course

    def setup(){
        user = new User(PERSON_NAME, USERNAME, 1, User.Role.STUDENT)
        userRepository.save(user)
        course = new Course(TEST_COURSE, Course.Type.TECNICO)
        courseRepository.save(course)

        option = new Option()
        option.setContent(OPTION_CONTENT)
        option.setCorrect(true)
        option.setStudentQuestion(question)
        option.setSequence(0)
        optionRepository.save(option)

        optionB = new Option()
        optionB.setContent(OPTION_CONTENT)
        optionB.setCorrect(false)
        optionB.setStudentQuestion(question)
        optionB.setSequence(1)
        optionRepository.save(optionB)

        optionC = new Option()
        optionC.setContent(OPTION_CONTENT)
        optionC.setCorrect(false)
        optionC.setStudentQuestion(question)
        optionC.setSequence(2)
        optionRepository.save(optionC)


        optionD = new Option()
        optionD.setContent(OPTION_CONTENT)
        optionD.setCorrect(false)
        optionD.setStudentQuestion(question)
        optionD.setSequence(3)
        optionRepository.save(optionD)
    }

    def "performance test to resubmit 2500 times a question"() {
        given: "a student question dto"
        def questionDto = new StudentQuestionDto()
        questionDto.setTitle(CHANGED_QUESTION_TITLE)
        questionDto.setContent(QUESTION_CONTENT)
        questionDto.setStatus(StudentQuestion.Status.PENDING.name())
        and: "four options"
        def options = new ArrayList<OptionDto>()
        def optionDto = new OptionDto(option)
        optionDto.setContent(CHANGED_OPTION_CONTENT)
        optionDto.setSequence(0)
        optionDto.setCorrect(true)
        options.add(optionDto)
        optionDto = new OptionDto(optionB)
        optionDto.setContent(OPTION_CONTENT)
        optionDto.setSequence(1)
        optionDto.setCorrect(false)
        options.add(optionDto)
        optionDto = new OptionDto(optionC)
        optionDto.setContent(OPTION_CONTENT)
        optionDto.setSequence(2)
        optionDto.setCorrect(false)
        options.add(optionDto)
        optionDto = new OptionDto(optionD)
        optionDto.setContent(OPTION_CONTENT)
        optionDto.setSequence(3)
        optionDto.setCorrect(false)
        options.add(optionDto)
        questionDto.setOptions(options)

        when:
        1.upto(1, {
            question = new StudentQuestion()
            question.setId(it)
            question.setKey(it)
            question.setTitle(QUESTION_TITLE)
            question.setContent(QUESTION_CONTENT)
            question.setStatus(StudentQuestion.Status.REJECTED)
            question.setSubmittingUser(user)
            question.addOption(option)
            question.addOption(optionB)
            question.addOption(optionC)
            question.addOption(optionD)
            question.setCourse(course)
            option.setStudentQuestion(question)
            studentQuestionRepository.save(question)

            studentQuestionService.resubmitQuestion(it, questionDto, USERNAME);
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
