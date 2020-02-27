package pt.ulisboa.tecnico.socialsoftware.tutor.tourney.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.TopicConjunction
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.tourney.Tourney
import pt.ulisboa.tecnico.socialsoftware.tutor.tourney.TourneyConjunction
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
        given: "creating a new tourney"
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
        // an exception is thrown
        expect: false
    }

    def "tourney with no end date"(){
        // an exception is thrown
        expect: false
    }

    def "tourney with start date bigger than end date"(){
        // an exception is thrown
        expect: false
    }

    def "tourney topic is empty"(){
        // an exception is thrown
        expect: false
    }

    def "number of questions is empty"(){
        // an exception is thrown
        expect: false
    }

    @TestConfiguration
    static class TourneyServiceImplTestContextConfiguration{

        @Bean
        TourneyService tourneyService(){
            return new TourneyService()
        }
    }

}
