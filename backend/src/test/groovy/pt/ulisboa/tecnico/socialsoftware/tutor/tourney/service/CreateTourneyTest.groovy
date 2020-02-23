package pt.ulisboa.tecnico.socialsoftware.tutor.tourney.service

import pt.ulisboa.tecnico.socialsoftware.tutor.tourney.Tourney
import spock.lang.Specification


class CreateTourneyTest extends Specification{

    def tourney

    def setup(){
        tourney = new Tourney()
    }

    /* In case we want to implement a name feature for a tourney
    def "tourney with the same name already exists"(){
        expect: false
    }

    def "tourney name is blank"(){
        expect: false
    }

    def "tourney name is empty"(){
        expect: false
    }*/

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

    def "tourney topic is blank"(){
        // an exception is thrown
        expect: false
    }

    def "number of questions is empty"(){
        // an exception is thrown
        expect: false
    }

}
