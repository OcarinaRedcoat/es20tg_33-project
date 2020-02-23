package pt.ulisboa.tecnico.socialsoftware.tutor.tourney.service

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.TopicConjunction
import pt.ulisboa.tecnico.socialsoftware.tutor.tourney.Tourney
import pt.ulisboa.tecnico.socialsoftware.tutor.tourney.TourneyConjunction
import spock.lang.Specification
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class CreateTourneyTest extends Specification{
    static final int QUESTION_NUMBER = 5

    def tourney
    def tourneyConj
    def formatter
    def availableDate
    def conclusionDate
    def topicConjunction

    def setup(){
        tourney = new Tourney()
        tourneyConj = new TourneyConjunction()
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

        availableDate = LocalDateTime.now()
        conclusionDate = LocalDateTime.now().plusDays(1)

        topicConjunction = new TopicConjunction()
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
