package pt.ulisboa.tecnico.socialsoftware.tutor.tourney.service

import pt.ulisboa.tecnico.socialsoftware.tutor.tourney.Tourney
import pt.ulisboa.tecnico.socialsoftware.tutor.tourney.TourneyConjunction
import spock.lang.Specification

public class GetOpenTourneysTest extends Specification {

    def tourneyConj

    def setup() {
        tourneyConj = TourneyConjunction()
    }

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
        tourneyConj.addTourney(tourney)
        tourney = new Tourney()
        tourney.setId(2)
        tourneyConj.addTourney(tourney)

        when:
        def result = tourneyConj.getOpenTourneys()

        then:
        result.size() == 2
        and:
        result.get(0).getId() == 1
        result.get(1).getId() == 2
    }

}

