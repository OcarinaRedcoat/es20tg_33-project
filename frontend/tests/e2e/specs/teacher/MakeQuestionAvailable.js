describe('Walkthrough on making approved questions available', () => {
  it('login approves and makes a question available', () => {
    cy.demoStudentLogin();
    cy.visitSubmittedQuestionsPage();
    cy.submitQuestion(
      'FE Test Question Title',
      'FE Test Question Content',
      'FE Test Question Option Content'
    );
    cy.contains('Logout').click();

    cy.demoTeacherLogin();
    cy.visitApproveRejectPage();
    cy.approveQuestion('FE Test Question Title', 'justification');
    cy.makeQuestionAvailable('FE Test Question Title');

    cy.visitQuestionsPage();
    cy.filterQuestionsMostRecent();
    cy.deleteAvailableQuestion('FE Test Question Title');
    cy.contains('Logout').click();
  });
});
