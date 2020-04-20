describe('Question Submission walkthrough', () => {
    beforeEach(() => {
        cy.demoStudentLogin()
        cy.visitSubmittedQuestionsPage()
    })

    afterEach(() => {
        cy.contains('Logout').click()
    })

    it('login, submits and deletes a question', () => {
        cy.submitQuestion('FE Test Question Title', 'FE Test Question Content', 'FE Test Question Option Content')

        cy.deleteSubmittedQuestion('FE Test Question Title')
    });

    it('login, try to submit a question without a title and cancel the submission', () => {
        cy.log('try to submit without a title')
        cy.submitQuestion('   ', 'FE Test Question Content', 'FE Test Question Option Content')

        cy.closeErrorMessage()

        cy.log('close dialog')
        cy.get('[data-cy="cancelButton"]').click()
    });

    it('login, submits two questions and deletes them', () => {
        cy.submitQuestion('FE Test Question Title 1', 'FE Test Question Content', 'FE Test Question Option Content')
        cy.submitQuestion('FE Test Question Title 2', 'FE Test Question Content', 'FE Test Question Option Content')

        cy.deleteSubmittedQuestion('FE Test Question Title 1')
        cy.deleteSubmittedQuestion('FE Test Question Title 2')
    });
});