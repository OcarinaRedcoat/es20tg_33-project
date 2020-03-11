package pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.user

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import spock.lang.Specification

@DataJpaTest
class ShowSubmittedQuestionsStatus extends Specification {

    def setup() {
    }

    def "a student visualizes the state of his submitted questions"() {
        expect false
    }

    def "the student doesn't have submitted questions"() {
        expect false
    }
}
