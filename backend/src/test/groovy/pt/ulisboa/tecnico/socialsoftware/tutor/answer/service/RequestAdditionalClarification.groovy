package pt.ulisboa.tecnico.socialsoftware.tutor.answer.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.Discussion
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.DiscussionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.MessageDto
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.DiscussionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Shared
import spock.lang.Specification

@DataJpaTest
class RequestAdditionalClarification extends  Specification{
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final String QUESTION_TITLE = 'question title'
    public static final String QUESTION_CONTENT = 'question content'
    public static final String OPTION_CONTENT = 'optionId content'
    public static final String STUDENTANSWER= 'Posso tirar uma dúvida?'
    public static final String TEACHERANSWER= 'Vai ja!!'
    public static final String OPTIONCONTENT= 'Option content'

    @Autowired
    AnswerService answerService

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    DiscussionRepository discussionRepository

    @Autowired
    QuizAnswerRepository quizAnswerRepository

    @Autowired
    QuizRepository quizRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    QuizQuestionRepository quizQuestionRepository

    @Autowired
    OptionRepository optionRepository;

    @Autowired
    QuestionAnswerRepository questionAnswerRepository;

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





        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        user_student = new User("Rodrigo","rcosta1944",1,User.Role.STUDENT)
        user_teacher = new User("Rito Silva","Ocarina",2,User.Role.TEACHER)
        user_student.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(user_student)

        quiz = new Quiz()
        quiz.setKey(1)
        quiz.setType(Quiz.QuizType.GENERATED as String)
        quiz.setCourseExecution(courseExecution)
        courseExecution.addQuiz(quiz)
        quizRepository.save(quiz)

        quizAnswer = new QuizAnswer(user_student, quiz)
        quizAnswerRepository.save(quizAnswer)

        question = new Question()
        question.setTitle(QUESTION_TITLE)
        question.setKey(1)
        question.setCourse(course)

        questionRepository.save(question)

        course.addQuestion(question)

        quizQuestion = new QuizQuestion(quiz, question, 1)
        quizQuestionRepository.save(quizQuestion)
        question.addQuizQuestion(quizQuestion)

        option1 = new Option()
        option1.setCorrect(true)
        option1.setContent(OPTION_CONTENT)
        option1.setSequence(1)
        question.addOption(option1)

        questionAnswer = new QuestionAnswer(quizAnswer, quizQuestion, 1, option1, 1)

        userRepository.save(user_student)
        userRepository.save(user_teacher)
        optionRepository.save(option1)
        questionAnswerRepository.save(questionAnswer)


        discussion = new Discussion()
        discussion.setQuizAnswer(quizAnswer)
        discussion.setCourse(course)
        discussion.setId(1)
        discussion.setCreatorStudent(user_student)

        discussionRepository.save(discussion)









        /*

        quiz = new Quiz()
        quiz.setKey(1)

        user_student = new User("Rodrigo","rcosta1944",1,User.Role.STUDENT)
        user_teacher = new User("Rito Silva","Ocarina",2,User.Role.TEACHER)

        userRepository.save(user_student)
        userRepository.save(user_teacher)

        user_student.addQuizAnswer(quizAnswer)


        quizAnswer = new QuizAnswer(user_student, quiz)
        quizAnswer.id = 1
        quizAnswerRepository.save(quizAnswer)

        course = new Course(COURSE_NAME, Course.Type.TECNICO)


        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)
        quiz.setCourseExecution(courseExecution)


        user_student.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(user_student)
        courseExecution.getUsers().add(user_teacher)

        courseExecution.addQuiz(quiz)


        discussion = new Discussion()
        discussion.id = 1
        discussion.setCourse(course)
        discussion.setQuizAnswer(quizAnswer)
        discussion.setCreatorStudent(user_student)
        discussionRepository.save(discussion)

        discussionDto = new DiscussionDto(discussion)
        discussionDto.id = 1
*/
    }

    def 'Request Aditional Clarification'(){
        given: "new discussion"
        def messageDto6 = new MessageDto()
        def messageDto5 = new MessageDto()
        def messageDto7 = new MessageDto()


        messageDto6.setSentence(STUDENTANSWER)
        messageDto5.setSentence(TEACHERANSWER)
        messageDto7.setSentence("ADITIONAL CLARIFICATION")
        discussionDto = new DiscussionDto(discussion)

        when:
        answerService.submitMessage(messageDto6, discussionDto.getId(), "rcosta1944")
        answerService.submitMessage(messageDto5, discussionDto.getId(), "Ocarina")
        answerService.submitMessage(messageDto7, discussionDto.getId(), "rcosta1944")

        then:
        def result = answerService.seeMessages(discussionDto.getId())
        result.size() == 3
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
