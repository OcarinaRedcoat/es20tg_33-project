package pt.ulisboa.tecnico.socialsoftware.tutor.tourney.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.tourney.Tourney
import pt.ulisboa.tecnico.socialsoftware.tutor.tourney.TourneyDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tourney.TourneyRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tourney.TourneyService
import spock.lang.Specification
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


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
        tourney.setTopics(topics)

    }

    def "create a tourney"(){
        given: "new tourney creation"
        tourney.setTourneyNumberOfQuestions(QUESTION_NUMBER)

        when:
        tourneyService.createTourney(tourney)

        then:
        tourneyRepository.count() == 1L
        def result = tourneyRepository.findAll().get(0)
        result.getId() != null
        result.getAvailableDate().format(formatter) == availableDate.format(formatter)
        result.getConclusionDate().format(formatter) == conclusionDate.format(formatter)
        result.getTopics().size() == 1
        result.getStatus() == Tourney.Status.CLOSED
        result.getNumberOfQuestions() == QUESTION_NUMBER
    }

    def "tourney with no start date"(){
        given: "a tourney with no start date"
        tourney.setTourneyNumberOfQuestions(QUESTION_NUMBER)
        tourney.setTourneyAvailableDate(null)

        when:
        tourneyService.createTourney(tourney)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNEY_NOT_CONSISTENT
        tourneyRepository.count() == 0L
    }

    def "tourney with no end date"(){
        given: "a tourney with no end date"
        tourney.setTourneyNumberOfQuestions(QUESTION_NUMBER)
        tourney.setTourneyConclusionDate(null)

        when:
        tourneyService.createTourney(tourney)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNEY_NOT_CONSISTENT
        tourneyRepository.count() == 0L
    }

    def "tourney with start date bigger than end date"(){
        given: "a tourney with start date bigger than the end date"
        tourney.setTourneyNumberOfQuestions(QUESTION_NUMBER)
        tourney.setTourneyConclusionDate(getAvailableDate().minusDays(1).format(formatter))

        when:
        tourneyService.createTourney(tourney)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNEY_NOT_CONSISTENT
        tourneyRepository.count() == 0L
    }

    def "tourney topic is empty"(){
        given: "a tourney with no topics"
        tourney.setTourneyNumberOfQuestions(QUESTION_NUMBER)
        tourney.setTopics(null)

        when:
        tourneyService.createTourney(tourney)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNEY_NOT_CONSISTENT
        tourneyRepository.count() == 0L
    }

    def "number of questions is empty"(){
        when: "creating a tourney without a given number of questions"
        tourneyService.createTourney(tourney)

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
