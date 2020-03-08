package pt.ulisboa.tecnico.socialsoftware.tutor.tourney.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.tourney.Tourney
import pt.ulisboa.tecnico.socialsoftware.tutor.tourney.TourneyDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tourney.TourneyRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tourney.TourneyService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@DataJpaTest
class CreateTourneyTest extends Specification{
    static final int QUESTION_NUMBER = 5

    def tourney
    def formatter
    def availableDate
    def conclusionDate
    def topicDto

    @Autowired
    TourneyService tourneyService

    @Autowired
    TourneyRepository tourneyRepository

    @Autowired
    UserRepository userRepository


    def setup(){
        tourney = new TourneyDto()
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        availableDate = LocalDateTime.now()
        conclusionDate = LocalDateTime.now().plusDays(1)

        tourney.setTourneyStatus(Tourney.Status.CLOSED)
        tourney.setTourneyAvailableDate(availableDate.format(formatter))
        tourney.setTourneyConclusionDate(conclusionDate.format(formatter))

        def topics = new ArrayList()
        topics.add(topicDto)
        tourney.setTourneyTopics(topics)

        def user = new User("name", "username",1, User.Role.STUDENT)
        userRepository.save(user)
    }

    def "create a tourney"(){
        given: "new tourney creation"
        def userId = userRepository.findAll().get(0).getId()
        tourney.setTourneyNumberOfQuestions(QUESTION_NUMBER)

        when:
        tourneyService.createTourney(tourney, userId)

        then:
        tourneyRepository.count() == 1L
        def result = tourneyRepository.findAll().get(0)
        result.getId() != null
        result.getAvailableDate().equals(availableDate.format(formatter))
        result.getConclusionDate().equals(conclusionDate.format(formatter))
        result.getTopics().size() == 1
        result.getStatus() == Tourney.Status.CLOSED
        result.getNumberOfQuestions() == QUESTION_NUMBER
        result.getCreator().getKey() == userId
    }

    def "tourney with no start date"(){
        given: "a tourney with no start date"
        def userId = userRepository.findAll().get(0).getId()
        tourney.setTourneyNumberOfQuestions(QUESTION_NUMBER)
        tourney.setTourneyAvailableDate(null)

        when:
        tourneyService.createTourney(tourney, userId)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNEY_NOT_CONSISTENT
        tourneyRepository.count() == 0L
    }

    def "tourney with no end date"(){
        given: "a tourney with no end date"
        def userId = userRepository.findAll().get(0).getId()
        tourney.setTourneyNumberOfQuestions(QUESTION_NUMBER)
        tourney.setTourneyConclusionDate(null)

        when:
        tourneyService.createTourney(tourney, userId)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNEY_NOT_CONSISTENT
        tourneyRepository.count() == 0L
    }

    def "tourney with start date bigger than end date"(){
        given: "a tourney with start date bigger than the end date"
        def userId = userRepository.findAll().get(0).getId()
        tourney.setTourneyNumberOfQuestions(QUESTION_NUMBER)
        tourney.setTourneyConclusionDate(getAvailableDate().minusDays(1).format(formatter))

        when:
        tourneyService.createTourney(tourney, userId)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNEY_AVAILABLEDATE_BIGGER_THAN_CONCLUSIONDATE
        tourneyRepository.count() == 0L
    }

    def "tourney topics is empty"(){
        given: "a tourney with no topics"
        def userId = userRepository.findAll().get(0).getId()
        tourney.setTourneyNumberOfQuestions(QUESTION_NUMBER)
        tourney.setTourneyTopics(new ArrayList<Topic>())

        when:
        tourneyService.createTourney(tourney, userId)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNEY_NOT_CONSISTENT
        tourneyRepository.count() == 0L
    }

    def "number of questions is empty"(){
        given: "a tourney without a given number of questions"
        def userId = userRepository.findAll().get(0).getId()

        when:
        tourneyService.createTourney(tourney, userId)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNEY_NOT_CONSISTENT
        tourneyRepository.count() == 0L
    }

    @TestConfiguration
    static class TourneyServiceImplTestContextConfiguration{

        @Bean
        TourneyService tourneyService(){
            return new TourneyService()
        }
    }

}
