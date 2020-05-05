package pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@DataJpaTest
class DeleteSubmittedQuestionTest extends Specification {
    public static final String USERNAME = "user"
    public static final String PERSON_NAME = "Name"
    public static final String TEST_COURSE = "TestCourse"
    public static final String ACRONYM = "TC"
    public static final String ACADEMIC_TERM = "1S"
    public static final String QUESTION_TITLE = 'question title'
    public static final String QUESTION_CONTENT = 'question content'
    public static final String OPTION_CONTENT = "optionId content"

    @Autowired
    StudentQuestionService studentQuestionService

    @Autowired
    CourseRepository courseRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    StudentQuestionRepository studentQuestionRepository

    def question
    def user
    def course

    def setup() {
        course = new Course(TEST_COURSE, Course.Type.TECNICO)
        courseRepository.save(course)

        user = new User(PERSON_NAME, USERNAME, 1, User.Role.STUDENT)
        userRepository.save(user)

        given: "create a question"
        question = new StudentQuestion()
        question.setKey(1)
        question.setTitle(QUESTION_TITLE)
        question.setContent(QUESTION_CONTENT)
        question.setStatus(StudentQuestion.Status.PENDING)
        question.setSubmittingUser(user)
        question.setCourse(course)

        and: 'four options'
        def option = new Option()
        option.setContent(OPTION_CONTENT)
        option.setSequence(0)
        option.setCorrect(true)
        question.addOption(option)
        option = new Option()
        option.setContent(OPTION_CONTENT)
        option.setSequence(1)
        option.setCorrect(false)
        question.addOption(option)
        option = new Option()
        option.setContent(OPTION_CONTENT)
        option.setSequence(2)
        option.setCorrect(false)
        question.addOption(option)
        option = new Option()
        option.setContent(OPTION_CONTENT)
        option.setSequence(3)
        option.setCorrect(false)
        question.addOption(option)

        studentQuestionRepository.save(question)

        course.addStudentQuestion(question)
        user.addSubmittedQuestion(question)
    }

    def "delete a pending question"() {
        when:
        studentQuestionService.removeStudentQuestion(question.getId())

        then: "the question is removed successfully"
        studentQuestionRepository.count() == 0L
    }

    def "delete a rejected question"() {
        question.setStatus(StudentQuestion.Status.REJECTED)

        when:
        studentQuestionService.removeStudentQuestion(question.getId())

        then: "the question is removed successfully"
        studentQuestionRepository.count() == 0L
    }

    def "delete an approved question"() {
        question.setStatus(StudentQuestion.Status.APPROVED)

        when:
        studentQuestionService.removeStudentQuestion(question.getId())

        then: "the question can't be removed"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.QUESTION_ALREADY_APPROVED
        studentQuestionRepository.count() == 1L
    }

    @TestConfiguration
    static class StudentQuestionServiceImplTestContextConfiguration {

        @Bean
        StudentQuestionService studentQuestionService() {
            return new StudentQuestionService()
        }
    }

}