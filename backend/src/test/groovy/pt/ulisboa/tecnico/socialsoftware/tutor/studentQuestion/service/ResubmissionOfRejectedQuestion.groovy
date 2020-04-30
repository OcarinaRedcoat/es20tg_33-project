package pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
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
class ResubmissionOfRejectedQuestion extends Specification{
    public static final String USERNAME = "user"
    public static final String PERSON_NAME = "Name"
    public static final String QUESTION_TITLE = 'question title'
    public static final String CHANGED_QUESTION_TITLE = 'changed question title'
    public static final String QUESTION_CONTENT = 'question content'
    public static final String OPTION_CONTENT = "optionId content"

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

    def question
    def option
    def optionF
    def user

    def setup() {
        user = new User(PERSON_NAME, USERNAME, 1, User.Role.STUDENT)
        userRepository.save(user)

        question = new StudentQuestion()
        question.setKey(1)
        question.setTitle(QUESTION_TITLE)
        question.setContent(QUESTION_CONTENT)
        question.setStatus(StudentQuestion.Status.REJECTED)

        option = new Option()
        option.setContent(OPTION_CONTENT)
        option.setCorrect(true)
        option.setStudentQuestion(question)
        option.setSequence(1)
        optionRepository.save(option)

        optionF = new Option()
        optionF.setContent(OPTION_CONTENT)
        optionF.setCorrect(false)
        optionF.setStudentQuestion(question)
        optionF.setSequence(2)
        optionRepository.save(optionF)

        optionF = new Option()
        optionF.setContent(OPTION_CONTENT)
        optionF.setCorrect(false)
        optionF.setStudentQuestion(question)
        optionF.setSequence(3)
        optionRepository.save(optionF)

        optionF = new Option()
        optionF.setContent(OPTION_CONTENT)
        optionF.setCorrect(false)
        optionF.setStudentQuestion(question)
        optionF.setSequence(4)
        optionRepository.save(optionF)


        question.addOption(option)
        question.addOption(optionF)
        question.setSubmittingUser(user)
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
        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_CONTENT)
        optionDto.setCorrect(true)
        options.add(optionDto)
        optionDto = new OptionDto()
        optionDto.setContent(OPTION_CONTENT)
        optionDto.setCorrect(false)
        options.add(optionDto)
        optionDto = new OptionDto()
        optionDto.setContent(OPTION_CONTENT)
        optionDto.setCorrect(false)
        options.add(optionDto)
        optionDto = new OptionDto()
        optionDto.setContent(OPTION_CONTENT)
        optionDto.setCorrect(false)
        options.add(optionDto)
        questionDto.setOptions(options)

        when:
        studentQuestionService.resubmitQuestion(question.getId(), questionDto, USERNAME);

        then: "the question is submitted successfully"
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

    def "resubmit question not in REJECTED status"() {
        expect: false
    }

    def "Resubmits without changing question"() {
        expect: false
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
