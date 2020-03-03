package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import spock.lang.Specification

class SubmitQuestionTest extends Specification {

    def questionService

    def setup() {
        questionService = new QuestionService()
    }

    def "submit a question"() {
        //A student submits a question
        expect: false
    }

    def "question submitted without a title"() {
        //The student tries to submit a question without a title for it, an exception is thrown
        expect: false
    }

    def "question with less than four options"() {
        //The student doesn't give four options for the question, as exception is thrown
        expect: false
    }

    def "question with more than one correct answer"() {
        //The student tries to submit a question with more than one option as the correct one, an exception is thrown
        expect: false
    }
}