package pt.ulisboa.tecnico.socialsoftware.tutor.answer.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.DiscussionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.DiscussionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.Discussion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.MessageDto
import spock.lang.Shared
import spock.lang.Specification

import java.time.LocalDateTime

@DataJpaTest
class DiscussionQuizAnswerTest extends Specification {
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final String QUESTION_TITLE = 'question title'
    public static final String QUESTION_CONTENT = 'question content'
    public static final String OPTION_CONTENT = "optionId content"
    public static final String URL = 'URL'
    public static final String STUDENTANSWER="Posso tirar uma dúvida?"
    public static final String TEACHERANSWER="Vai po caralho!"

    @Autowired
    AnswerService answerService

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    DiscussionRepository discussionRepository

    @Shared def courseExecution

    @Shared def discussionDto
    @Shared def discussion
    @Shared def questionDto

    @Shared def questionAnswer
    @Shared def course
    @Shared def question
    @Shared def quizAnswer
    @Shared def quizQuestion
    @Shared def user_student
    @Shared def quiz
    @Shared def option1


    def setup() {


        quiz = new Quiz()
        quiz.setKey(1)
        quiz.setType(Quiz.QuizType.GENERATED)

        user_student = new User("Rodrigo","gaylord",1,User.Role.STUDENT)

        quizAnswer = new QuizAnswer(user_student, quiz)

        course = new Course(COURSE_NAME, Course.Type.TECNICO)

        questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_TITLE)

        question = new Question(course, questionDto)


        quizQuestion = new QuizQuestion(quiz, question, 1)

        question.addQuizQuestion(quizQuestion)

        option1 = new Option()
        option1.setContent(OPTION_CONTENT)

        questionAnswer = new QuestionAnswer(quizAnswer , quizQuestion, 1)

        userRepository.save(user_student)
        questionRepository.save(question)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)
        quiz.setCourseExecution(courseExecution)


        user_student.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(user_student)

        courseExecution.addQuiz(quiz)

        questionService.createQuestion(course.getId(), questionDto)

        discussion = new Discussion()
        discussion.setId(1)

        discussionDto = new DiscussionDto()

        //discussionRepository.save(discussionDto)
    }

    def 'create a question discussion' () {
        given: "new discussion"
        //def user_student1 = new User("Rodrigo","gaylord",1,User.Role.STUDENT)
        def userId = userRepository.findAll().get(0).getId()

        when:
        answerService.createDiscussion(userId, discussionDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.STUDENT_DID_NOT_ANSWER_QUESTION

    }


    def 'Send a null student message' () {
        given:
        def messageDto = new MessageDto()
        def user_student1 = new User("Rodrigo","gaylord",1,User.Role.STUDENT)
        messageDto.setUser(user_student1)
        messageDto.setId(user_student1.getId())
        messageDto.setSentence(STUDENTANSWER)
        def qAId = questionAnswer.getId()
        def date = new LocalDateTime()
        messageDto.setMessageDate(date)

        when:
        answerService.submitMessage(qAId, user_student1.getId(),discussionDto, messageDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.MESSAGE_NULL
    }


    def 'Send a empty student message' () {
        given:
        def messageDto = new MessageDto()
        def user_student1 = new User("Rodrigo","gaylord",1,User.Role.STUDENT)
        messageDto.setUser(user_student1)
        messageDto.setId(user_student1.getId())
        messageDto.setSentence(STUDENTANSWER)
        def qAId = questionAnswer.getId()
        def date = new LocalDateTime()
        messageDto.setMessageDate(date)

        when:
        answerService.submitMessage(qAId, user_student1.getId(),discussionDto, messageDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.MESSAGE_EMPTY
    }


    def 'student did not answered the question'(){
        given:
        def messageDto = new MessageDto()
        def user_student1 = new User("Rodrigo","gaylord",1,User.Role.STUDENT)
        messageDto.setUser(user_student1)
        messageDto.setId(user_student1.getId())
        messageDto.setSentence(STUDENTANSWER)
        def qAId = questionAnswer.getId()
        def date = new LocalDateTime()
        messageDto.setMessageDate(date)

        when:
        answerService.submitMessage(qAId, user_student1.getId(),discussionDto, messageDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.STUDENT_DID_NOT_ANSWER_QUESTION
    }

    def 'student answered with null date'(){
        given:
        def messageDto = new MessageDto()
        def user_student1 = new User("Rodrigo","gaylord",1,User.Role.STUDENT)
        messageDto.setUser(user_student1)
        messageDto.setId(user_student1.getId())
        messageDto.setSentence(STUDENTANSWER)
        def qAId = questionAnswer.getId()
        def date = new LocalDateTime()
        messageDto.setMessageDate(date)

        when:
        answerService.submitMessage(qAId, user_student1.getId(),discussionDto, messageDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.MESSAGE_DATE_NULL
    }
}

