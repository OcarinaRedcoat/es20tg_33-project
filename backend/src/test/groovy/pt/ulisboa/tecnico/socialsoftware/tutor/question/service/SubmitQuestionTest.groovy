package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import org.springframework.beans.factory.annotation.Autowired
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException

import spock.lang.Specification

class SubmitQuestionTest extends Specification {
    public static final String TEST_COURSE = "TestCourse"
    public static final String ACRONYM = "TC"
    public static final String ACADEMIC_TERM = "1S"
    public static final String QUESTION_TITLE = "qTitle"
    public static final String QUESTION_CONTENT = "qContent"
    public static final String OPTION_CONTENT = "optContent"

    @Autowired
    QuestionService questionService

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    QuestionRepository questionRepository

    def course
    def courseExecution

    def setup() {
        course = new Course(TEST_COURSE, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)
    }

    def "submit a question with title, content and four options, with one being correct"() {
        given: "a question"
        def question = new Question()
        question.setKey(1)
        question.setTitle(QUESTION_TITLE)
        question.setContent(QUESTION_CONTENT)
        and: "four options"
        def option = new Option()
        option.setContent(OPTION_CONTENT)
        option.setCorrect(true)
        question.addOption(option)
        option = new Option()
        option.setContent(OPTION_CONTENT)
        option.setCorrect(false)
        question.addOption(option)
        option = new Option()
        option.setContent(OPTION_CONTENT)
        option.setCorrect(false)
        question.addOption(option)
        option = new Option()
        option.setContent(OPTION_CONTENT)
        option.setCorrect(false)
        question.addOption(option)

        when:
        questionService.submitQuestion(question)

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
        course.getQuestions().contains(result)
    }

    def "question submitted without a title or content"() {
        given: "a question without title"
        def question = new Question()
        question.setKey(1)
        question.setTitle(null)
        question.setContent(null)

        when:
        questionService.submitQuestion(question)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.QUESTION_MISSING_TITLE_OR_CONTENT //TODO add to ErrorMessage
        questionRepository.count() == 0L
    }

    def "question with two options"() {
        given: "a question"
        def question = new Question()
        question.setKey(1)
        question.setTitle(QUESTION_TITLE)
        question.setContent(QUESTION_CONTENT)
        and: "two options"
        def option = new Option()
        option.setContent(OPTION_CONTENT)
        option.setCorrect(true)
        question.addOption(option)
        option = new Option()
        option.setContent(OPTION_CONTENT)
        option.setCorrect(false)
        question.addOption(option)

        when:
        questionService.submitQuestion(question)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.QUESTION_MISSING_OPTIONS//TODO add to ErrorMessage
        questionRepository.count() == 0L
    }

    def "question with two correct answers"() {
        given: "a question"
        def question = new Question()
        question.setKey(1)
        question.setTitle(QUESTION_TITLE)
        question.setContent(QUESTION_CONTENT)
        and: "four options with two correct ones"
        def option = new Option()
        option.setContent(OPTION_CONTENT)
        option.setCorrect(true)
        question.addOption(option)
        option = new Option()
        option.setContent(OPTION_CONTENT)
        option.setCorrect(true)
        question.addOption(option)
        option = new Option()
        option.setContent(OPTION_CONTENT)
        option.setCorrect(false)
        question.addOption(option)
        option = new Option()
        option.setContent(OPTION_CONTENT)
        option.setCorrect(false)
        question.addOption(option)

        when:
        questionService.submitQuestion(question)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.QUESTION_MULTIPLE_CORRECT_OPTIONS
        questionRepository.count() == 0L
    }

    def "question without a correct answer"() {
        given: "a question"
        def question = new Question()
        question.setKey(1)
        question.setTitle(QUESTION_TITLE)
        question.setContent(QUESTION_CONTENT)
        and: "four options with none being correct"
        def option = new Option()
        option.setContent(OPTION_CONTENT)
        option.setCorrect(true)
        question.addOption(option)
        option = new Option()
        option.setContent(OPTION_CONTENT)
        option.setCorrect(true)
        question.addOption(option)
        option = new Option()
        option.setContent(OPTION_CONTENT)
        option.setCorrect(false)
        question.addOption(option)
        option = new Option()
        option.setContent(OPTION_CONTENT)
        option.setCorrect(false)
        question.addOption(option)

        when:
        questionService.submitQuestion(question)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.QUESTION_MISSING_CORRECT_OPTION //TODO add to ErrorMessage
        questionRepository.count() == 0L
    }

}