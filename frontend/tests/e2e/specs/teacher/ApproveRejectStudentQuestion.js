describe('Administration walkthrough', () => {

  it('login approves with justification', () => {
    cy.demoStudentLogin()
    cy.visitSubmittedQuestionsPage()
    cy.submitQuestion('Question Title 1', 'Content', 'Options')
    cy.contains('Logout').click()
    cy.demoTeacherLogin()
    cy.visitApproveRejectPage()

    cy.approveQuestion('Question Title 1', 'justification')
  });

  it('login approves without justification', () => {
    cy.demoStudentLogin()
    cy.visitSubmittedQuestionsPage()
    cy.submitQuestion('Question Title 2', 'Content', 'Options')
    cy.contains('Logout').click()
    cy.demoTeacherLogin()
    cy.visitApproveRejectPage()

    cy.approveQuestionNoJust('Question Title 2')
  });

  it('login rejects with justification', () => {
    cy.demoStudentLogin()
    cy.visitSubmittedQuestionsPage()
    cy.submitQuestion('Question Title 3', 'Content', 'Options')
    cy.contains('Logout').click()
    cy.demoTeacherLogin()
    cy.visitApproveRejectPage()

    cy.rejectQuestion('Question Title 3', 'justification 1')

    cy.contains('Logout').click()
  });


  it('login rejects approved question', () => {
    cy.demoStudentLogin()
    cy.visitSubmittedQuestionsPage()
    cy.submitQuestion('Question Title 4', 'Content', 'Options')
    cy.contains('Logout').click()
    cy.demoTeacherLogin()
    cy.visitApproveRejectPage()

    cy.approveQuestionNoJust('Question Title 4')
    cy.rejectQuestion('Question Title 4',  'justification')
    cy.closeErrorMessage()
    cy.get('[data-cy="closeQuestionButton"]').click()
    cy.contains('Logout').click()

  });
});
