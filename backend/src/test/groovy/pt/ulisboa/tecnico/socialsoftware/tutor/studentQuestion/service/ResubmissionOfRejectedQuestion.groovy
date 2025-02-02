package pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
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
class ResubmissionOfRejectedQuestion extends Specification {
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

    def setup() {
        user = new User(PERSON_NAME, USERNAME, 1, User.Role.STUDENT)
        userRepository.save(user)
        course = new Course(TEST_COURSE, Course.Type.TECNICO)
        courseRepository.save(course)

        question = new StudentQuestion()
        question.setKey(1)
        question.setTitle(QUESTION_TITLE)
        question.setContent(QUESTION_CONTENT)
        question.setCourse(course)
        question.setStatus(StudentQuestion.Status.REJECTED)

        option = new Option()
        option.setContent(OPTION_CONTENT)
        option.setCorrect(true)
        option.setStudentQuestion(question)
        option.setSequence(0)
        optionRepository.save(option)
        question.addOption(option)

        optionB = new Option()
        optionB.setContent(OPTION_CONTENT)
        optionB.setCorrect(false)
        optionB.setStudentQuestion(question)
        optionB.setSequence(1)
        question.addOption(optionB)
        optionRepository.save(optionB)

        optionC = new Option()
        optionC.setContent(OPTION_CONTENT)
        optionC.setCorrect(false)
        optionC.setStudentQuestion(question)
        optionC.setSequence(2)
        question.addOption(optionC)
        optionRepository.save(optionC)


        optionD = new Option()
        optionD.setContent(OPTION_CONTENT)
        optionD.setCorrect(false)
        optionD.setStudentQuestion(question)
        optionD.setSequence(3)
        question.addOption(optionD)
        optionRepository.save(optionD)

        question.setSubmittingUser(user)
        user.addSubmittedQuestion(question)
        course.addStudentQuestion(question);
        studentQuestionRepository.save(question)
    }

    def "Resubmit rejected question"() {
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
        studentQuestionService.resubmitQuestion(question.getId(), questionDto);

        then: "the question is resubmitted successfully"
        studentQuestionRepository.count() == 1L
        def result = studentQuestionRepository.findAll().get(0)
        result.getId() != null
        result.getStatus() == StudentQuestion.Status.PENDING
        result.getTitle() == CHANGED_QUESTION_TITLE
        result.getContent() == QUESTION_CONTENT
        result.getOptions().size() == 4
        result.getSubmittingUser().getUsername() == USERNAME
        user.getSubmittedQuestions().get(0).getSubmittingUser().getUsername() == USERNAME
    }

    def "resubmit question not in rejected status"() {
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
        and:
        question.setStatus(StudentQuestion.Status.PENDING)
        studentQuestionRepository.save(question)

        when:
        studentQuestionService.resubmitQuestion(question.getId(), questionDto);

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.QUESTION_NOT_REJECTED
        studentQuestionRepository.count() == 1L
        def result = studentQuestionRepository.findAll().get(0)
        result.getId() != null
        result.getStatus() == StudentQuestion.Status.PENDING
        result.getTitle() == QUESTION_TITLE
        result.getContent() == QUESTION_CONTENT
        result.getOptions().size() == 4
        result.getSubmittingUser().getUsername() == USERNAME
        user.getSubmittedQuestions().get(0).getSubmittingUser().getUsername() == USERNAME
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
