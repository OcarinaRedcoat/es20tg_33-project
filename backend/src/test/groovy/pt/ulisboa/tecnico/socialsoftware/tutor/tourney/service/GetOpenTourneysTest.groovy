package pt.ulisboa.tecnico.socialsoftware.tutor.tourney.service

import pt.ulisboa.tecnico.socialsoftware.tutor.tourney.TourneyConjunction;
import spock.lang.Specification;

public class GetOpenTourneysTest extends Specification {

    def tourneyConj

    def setup() {
        tourneyConj = TourneyConjunction()
    }

    def "no tourney open"(){
        // an exception is thrown
        expect: false
    }

    def "two tourneys open"(){
        expect: false
    }

}

