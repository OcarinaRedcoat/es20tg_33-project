package pt.ulisboa.tecnico.socialsoftware.tutor.tourney.service

import org.springframework.beans.factory.annotation.Autowired
import pt.ulisboa.tecnico.socialsoftware.tutor.tourney.Tourney
import pt.ulisboa.tecnico.socialsoftware.tutor.tourney.TourneyRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tourney.TourneyService
import spock.lang.Specification

public class GetOpenTourneysTest extends Specification {


    @Autowired
    TourneyService tourneyService

    @Autowired
    TourneyRepository tourneyRepository

    def "no tourney open"(){
        when:
        def result = tourneyConj.getOpenTourneys()

        then:
        result.size() == 0
    }

    def "two tourneys open"(){
        given: "two tourneys"
        def tourney = new Tourney()
        tourney.setId(1)
        tourneyRepository.save(tourney)
        tourney = new Tourney()
        tourney.setId(2)
        tourneyRepository.save(tourney)

        when:
        def result = tourneyService.getOpenTourneys()

        then:
        result.size() == 2
        and:
        result.get(0).getId() == 1
        result.get(1).getId() == 2
    }

}

