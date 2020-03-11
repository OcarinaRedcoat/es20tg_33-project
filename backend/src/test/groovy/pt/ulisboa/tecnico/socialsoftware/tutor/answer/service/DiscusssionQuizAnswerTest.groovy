package pt.ulisboa.tecnico.socialsoftware.tutor.answer.service

import org.postgresql.copy.CopyOut
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.DiscussionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.DiscussionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.Message
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.Discussion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Shared
import spock.lang.Specification

import java.time.LocalDateTime

@DataJpaTest
class DiscussionQuizAnswer extends Specification {
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
    QuestionService questionService

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

        questionAnswer = new QuestionAnswer(quizAnswer , quizQuestion, 1, option1, 1)

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

        discussionRepository.save(discussionDto)
    }

    def 'create a question discussion' () {
        given: "new discussion"




        then:




    /*
        given: "new discussion"
        discussion.setId(1)
        discussion.setStudent(user1)

        when:
        questionService.createDiscussion() //FIXME

        then:
        quizAnswerRepository.findAll().size() == 1
        def quizAnswer = quizAnswerRepository.findAll().get(0)
        quizAnswer.getId() != null
        !quizAnswer.isCompleted()
        quizAnswer.getUser().getId() == userId
        quizAnswer.getUser().getQuizAnswers().contains(quizAnswer)
        quizAnswer.getQuiz().getId() == quizId
        quizAnswer.getQuiz().getQuizAnswers().contains(quizAnswer)*/

        expect:false;
    }


    def 'Send a null student message' () {
        expect:false;
    }


    def 'Send a empty student message' () {
        expect:false;
    }


    def 'student did not answered the question'(){
        expect:false;
    }

    def 'student answered with null date'(){
        expect:false;
    }
}

