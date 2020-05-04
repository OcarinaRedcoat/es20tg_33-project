describe('Question Submission walkthrough', () => {
  beforeEach(() => {
    cy.demoStudentLogin()
    cy.visitSubmittedQuestionsPage()
    cy.submitQuestion('Question Title', 'Content', 'Options')
    cy.contains('Logout').click()
    cy.demoTeacherLogin()
    cy.visitApproveRejectPage()
    cy.rejectQuestion('Question Title', 'justification')
    cy.contains('Logout').click()
    cy.demoStudentLogin()
    cy.visitSubmittedQuestionsPage()
  })

  afterEach(() => {
    cy.contains('Logout').click()
  })

  it('login, resubmits a question', () => {
    cy.resubmitQuestion(' Correct', ' Missing', ' Content')

  });

});