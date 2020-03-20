package pt.ulisboa.tecnico.socialsoftware.tutor.answer.service

import org.apache.tomcat.jni.Local
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.DiscussionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.DiscussionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport
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
import java.time.format.DateTimeFormatter

@DataJpaTest
class VisualizeDiscussionQuizAnswerTest extends Specification {
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final String QUESTION_TITLE = 'question title'
    public static final String QUESTION_CONTENT = 'question content'
    public static final String OPTION_CONTENT = "optionId content"
    public static final String STUDENTANSWER="Posso tirar uma dúvida?"
    public static final String TEACHERANSWER="Vai ja!!"

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

    @Shared formatter
    @Shared availableDate

    @Shared def questionAnswer
    @Shared def course
    @Shared def question
    @Shared def quizAnswer
    @Shared def quizQuestion
    @Shared def user_student
    @Shared def user_teacher
    @Shared def quiz
    @Shared def option1


    def setup() {

        quiz = new Quiz()
        quiz.setKey(3)
        quiz.setType(Quiz.QuizType.GENERATED)

        user_student = new User("Rodrigo","rcosta1944",1,User.Role.STUDENT)
        user_teacher = new User('Rito Silva','Ocarina',2,User.Role.TEACHER)
        user_student.setId(1)
        user_teacher.setId(1)
        quizAnswer = new QuizAnswer(user_student, quiz)

        user_student.addQuizAnswer(quizAnswer)

        course = new Course(COURSE_NAME, Course.Type.TECNICO)

        question = new Question()
        question.setId(1)
        question.setTitle(QUESTION_TITLE)


        questionDto = new QuestionDto(question)

        quizQuestion = new QuizQuestion(quiz, question, 1)

        question.addQuizQuestion(quizQuestion)

        option1 = new Option()
        option1.setContent(OPTION_CONTENT)

        questionAnswer = new QuestionAnswer(quizAnswer , quizQuestion, 1)
        questionAnswer.setId(1)

        userRepository.save(user_student)
        userRepository.save(user_teacher)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)
        quiz.setCourseExecution(courseExecution)


        user_student.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(user_student)
        courseExecution.getUsers().add(user_teacher)

        courseExecution.addQuiz(quiz)


        discussionDto = new DiscussionDto()

        discussionDto.setStudent(user_student)


        def qAId3 = questionAnswer.getId()
        discussionDto.setId(1)
        answerService.createDiscussion(qAId3, discussionDto)



        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        availableDate = LocalDateTime.now()

    }




    def 'The teacher didnt reply yet' () {
        given:
        def messageDto4 = new MessageDto()


        //discussionDto.setId(1)
        messageDto4.setUser(user_student)
        messageDto4.setId(user_student.getId())
        messageDto4.setSentence(STUDENTANSWER)


        def qAId = questionAnswer.getId()
        def date1 = LocalDateTime.now()
        messageDto4.setMessageDate(date1)


        when:
        answerService.submitMessage(qAId, user_student.getId(),discussionDto, messageDto4)
        answerService.displayDiscussion(user_student.getId(),discussionDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TEACHER_DID_NOT_CLARIFIED
    }


    def 'Student visualizes the message' () {
        given:
        def messageDto6 = new MessageDto()
        def messageDto5 = new MessageDto()

        discussionDto.setStudent(user_student)
        discussionDto.setTeacher(user_teacher)

        messageDto6.setUser(user_student)
        messageDto5.setUser(user_teacher)
        messageDto6.setId(user_student.getId())
        messageDto5.setId(user_teacher.getId())
        messageDto6.setSentence(STUDENTANSWER)
        messageDto5.setSentence(TEACHERANSWER)


        def qAId1 = questionAnswer.getId()
        def date1 = LocalDateTime.now()
        def data2 = LocalDateTime.now().plusHours(1)
        messageDto6.setMessageDate(date1)
        messageDto5.setMessageDate(data2)


        when:
        answerService.submitMessage(qAId1, user_student.getId(), discussionDto, messageDto6)
        answerService.submitMessage(qAId1, user_teacher.getId(), discussionDto, messageDto5)
        def result = answerService.displayDiscussion(user_student.getId(),discussionDto)

        then:
        //def result = discussionRepository.findAll().get(0)
        result.size() == 1
    }


    @TestConfiguration
    static class AnswerServiceImplTestContextConfiguration{

        @Bean
        AnswerService answerService(){
            return new AnswerService()
        }

        @Bean
        AnswersXmlImport answersXmlImport() {
            return new AnswersXmlImport()
        }

    }
}
