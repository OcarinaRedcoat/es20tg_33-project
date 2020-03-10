package pt.ulisboa.tecnico.socialsoftware.tutor.answer.service

import org.postgresql.copy.CopyOut
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Message
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Discussion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Shared
import spock.lang.Specification

import java.time.LocalDateTime

@DataJpaTest
class ThreadQuizAnswer extends Specification {
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

    @Shared def course
    @Shared def courseExecution
    @Shared def date
    @Shared def user1
    @Shared def user2
    @Shared def message1
    @Shared def message2
    @Shared def thread

    def setup() {

        user1 = new User("Rodrigo","gaylord",1,User.Role.STUDENT)
        user2 = new User("Caetano","gaylordfucker",2,User.Role.TEACHER)


        course = new Course()
        course.setId(1)
        course.()
        /*
        date = LocalDateTime.now()

        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        user1 = new User("Rodrigo","gaylord",1,User.Role.STUDENT)
        user2 = new User("Caetano","gaylordfucker",2,User.Role.TEACHER)

        user1.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(user1)

        user2.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(user2)

        message1= new Message(user1,STUDENTANSWER,date)
        message2=new Message(user2,TEACHERANSWER,date)

        thread = new Thread(1,message1,message2)

        def quiz= new Quiz()
        quiz.setKey(1)
        quiz.setType(Quiz.QuizType.GENERATED)
        quiz.setCourseExecution(courseExecution)
        courseExecution.addQuiz(quiz)

        def question = new Question()
        question.setKey(1)
        question.setCourse(course)
        question.setThread(thread)
        course.addQuestion(question)


        userRepository.save(user1)
        userRepository.save(user2)
        questionRepository.save(question)
        
         */

    }

    def 'create a question thread' () {
       expect:false
    }


    def 'Send a null student message' () {
        expect:false;
    }

    def 'Send a null professor message' () {
        expect:false;

    }

    def 'Send a empty student message' () {
        expect:false;
    }

    def 'Send a empty teacher message' () {
        expect:false;
    }

    def 'student answered the question'(){
        expect:false;
    }
}

