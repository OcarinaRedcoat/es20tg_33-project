package pt.ulisboa.tecnico.socialsoftware.tutor.tourney.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import spock.lang.Specification

@DataJpaTest

class StudentEnrolsIntoTourneyTest extends Specification{

    def "a student enrols into a tourney"() {
        expect: false
    }

    def "no open tourney with the given ID"() {
        expect: false
    }

    def "no user with the given ID"() {
        expect: false
    }

    def "user is not a student"() {
        expect: false
    }

}
