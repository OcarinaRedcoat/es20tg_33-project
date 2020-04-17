describe('Administration walkthrough', () => {
  beforeEach(() => {
    cy.demoStudentLogin()
    cy.submitQuestion('Question Title', 'Content', 'Options')
    cy.contains('Logout').click()
    cy.demoTeacherLogin()
  })

  afterEach(() => {
    cy.contains('Logout').click()
    cy.demoStudentLogin()
    cy.deleteSubmittedQuestion('Question Title')
    cy.contains('Logout').click()
  })

  it('login approves with justification', () => {
    cy.approveQuestion('Question Title', 'justification')
  });

  it('login approves without justification', () => {
    cy.approveQuestion('Question Title')
  });

  it('login rejects with justification', () => {
    cy.rejectQuestion('Question Title', 'justification')
  });

  it('login rejects approved question', () => {
    cy.rejectQuestion('Question Title')
    cy.closeErrorMessage()
    cy.get('[data-cy="closeQuestionButton"]').click()
  });

});
