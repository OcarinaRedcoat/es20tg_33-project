package pt.ulisboa.tecnico.socialsoftware.tutor.answer.service

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
class DiscussionQuizAnswerTest extends Specification {
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

    /*
    def setup() {


        quiz = new Quiz()
        quiz.setKey(1)
        quiz.setType(Quiz.QuizType.GENERATED)

        user_student = new User("Rodrigo","rcosta1944",1,User.Role.STUDENT)
        user_teacher = new User('Rito Silva','Ocarina',2,User.Role.TEACHER)

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


        discussion = new Discussion()
        discussion.setId(1)

        discussionDto = new DiscussionDto()
        discussionDto.setId(1)
        discussionDto.setStudent(user_student)
        discussionDto.setTeacher(user_teacher)
        answerService.createDiscussion(questionAnswer.getId(), discussionDto)

        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        availableDate = LocalDateTime.now()
    }

    def 'Create a new discussion' () {
        given: "new discussion"
        def qAId = questionAnswer.getId()
        def discussionDto1 = new DiscussionDto()
        def user_student1 = new User("Rodrigo","st",1,User.Role.STUDENT)
        user_student1.setId(1)

        user_student1.addQuizAnswer(quizAnswer)

        discussionDto1.setStudent(user_student1)
        discussionDto1.setId(1)


        when:
        def result = answerService.createDiscussion(qAId, discussionDto1)

        then:
        result.getId() == 1
    }
    /*

    def 'Send a null student message' () {
        given:
        def messageDto = new MessageDto()
        messageDto.setUser(user_student)
        messageDto.setId(user_student.getId())
        messageDto.setSentence(null)

        def qAId = questionAnswer.getId()
        def date = LocalDateTime.now()
        messageDto.setMessageDate(date)


        when:
        answerService.submitMessage(user_student.getId(),discussionDto, messageDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.MESSAGE_NULL
    }


    def 'Send a empty student message' () {
        given:
        def messageDto2 = new MessageDto()

        messageDto2.setUser(user_student)
        messageDto2.setId(user_student.getId())
        messageDto2.setSentence("    ")

        def qAId = questionAnswer.getId()
        def date = LocalDateTime.now()
        messageDto2.setMessageDate(date)


        when:
        answerService.submitMessage(user_student.getId(),discussionDto, messageDto2)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.MESSAGE_EMPTY
    }


    def 'Student did not answered the question'(){
        given:
        def messageDto3 = new MessageDto()
        def user_student1 = new User("Rodrigo","gaylord",3,User.Role.STUDENT)
        user_student1.setId(3)
        messageDto3.setUser(user_student1)
        messageDto3.setId(user_student1.getId())
        messageDto3.setSentence(STUDENTANSWER)
        def qAId = questionAnswer.getId()
        def date = LocalDateTime.now()
        messageDto3.setMessageDate(date)

        when:
        answerService.submitMessage(user_student1.getId(),discussionDto, messageDto3)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.USER_NOT_FOUND
    }


    def 'Student answered with null date'(){
        given:
        def messageDto4 = new MessageDto()

        messageDto4.setUser(user_student)
        messageDto4.setId(user_student.getId())
        messageDto4.setSentence(STUDENTANSWER)

        def qAId = questionAnswer.getId()
        def date = null
        messageDto4.setMessageDate(date)


        when:
        answerService.submitMessage(user_student.getId(),discussionDto, messageDto4)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.MESSAGE_DATE_NULL
    }

    def 'Professor sends a null message'(){
        given:
        def messageDto2 = new MessageDto()
        def messageDto3 = new MessageDto()

        messageDto3.setUser(user_student)
        messageDto3.setId(user_student.getId())
        messageDto3.setSentence(STUDENTANSWER)

        messageDto2.setUser(user_teacher)
        messageDto2.setId(user_teacher.getId())
        messageDto2.setSentence("  ")

        def qAId = questionAnswer.getId()
        def date = LocalDateTime.now()
        def date1 = LocalDateTime.now().plusHours(2)
        messageDto3.setMessageDate(date)
        messageDto2.setMessageDate(date1)

        when:
        answerService.submitMessage(user_student.getId(),discussionDto, messageDto3)
        answerService.submitMessage(user_teacher.getId(),discussionDto,messageDto2)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.MESSAGE_EMPTY
    }

    def 'Professor message with null date'(){
        given:
        def messageDto4 = new MessageDto()
        def messageDto5 = new MessageDto()

        messageDto4.setUser(user_student)
        messageDto5.setUser(user_teacher)
        messageDto4.setId(user_student.getId())
        messageDto5.setId(user_teacher.getId())
        messageDto4.setSentence(STUDENTANSWER)
        messageDto5.setSentence(TEACHERANSWER)


        def qAId = questionAnswer.getId()
        def date1 = LocalDateTime.now()
        def date2 = null
        messageDto4.setMessageDate(date1)
        messageDto5.setMessageDate(date2)


        when:
        answerService.submitMessage(user_student.getId(),discussionDto, messageDto4)
        answerService.submitMessage(user_teacher.getId(),discussionDto,messageDto5)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.MESSAGE_DATE_NULL
    }


    def 'Send a null professor message'(){
        given:
        def messageDto2 = new MessageDto()
        def messageDto3 = new MessageDto()

        messageDto3.setUser(user_student)
        messageDto3.setId(user_student.getId())
        messageDto3.setSentence(STUDENTANSWER)

        messageDto2.setUser(user_teacher)
        messageDto2.setId(user_teacher.getId())
        messageDto2.setSentence(null)

        def qAId = questionAnswer.getId()
        def date = LocalDateTime.now()
        def date1 = LocalDateTime.now().plusHours(2)
        messageDto3.setMessageDate(date)
        messageDto2.setMessageDate(date1)

        when:
        answerService.submitMessage(user_student.getId(),discussionDto, messageDto3)
        answerService.submitMessage(user_teacher.getId(),discussionDto,messageDto2)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.MESSAGE_NULL
    }

     */

    

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

