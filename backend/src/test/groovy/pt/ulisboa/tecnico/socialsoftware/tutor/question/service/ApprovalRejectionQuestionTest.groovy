package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import spock.lang.Specification

class ApprovalRejectionQuestionTest extends Specification {

    def questionService

    def setup() {
        questionService = new QuestionService()
    }

    def "approve a question"() {
        //A teacher approves a valid question
        expect: false
    }

    def "an approved question is in the repository"() {
        //An approved question is in the repository of available questions
        expect: false
    }

    def "reject a question with justification"() {
        //A teacher rejects a valid question
        expect: false
    }

    def "a rejected question is in the repository"() {
        //The question rejected by the teacher is in the repository waiting for the students review and resubmit
        expect: false
    }

    def "reject a question without justification"() {
        //The teacher rejects a question without justifying it, an exception is thrown
        expect: false
    }

    def "question approved without a title"() {
        //The teacher tries to approve a question without a title, an exception is thrown
        expect: false
    }

    def "question approved with less than four options"() {
        //The teacher tries to approve a question without four options for the question, as exception is thrown
        expect: false
    }

    def "question approved with more than one correct answer"() {
        //The teacher tries to approve a question with more than one option as the correct one, an exception is thrown
        expect: false
    }
}