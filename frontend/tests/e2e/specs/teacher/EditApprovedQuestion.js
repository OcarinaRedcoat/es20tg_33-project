describe('Walkthrough on editing approved questions', () => {
  it('login approves and edits an approved question', () => {
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

    cy.editApprovedQuestion(
      'FE Test Question Title',
      'Changed Title',
      'Changed Content',
      'Changed Option'
    );
    cy.wait(1000);
    cy.contains('Logout').click();
  });
});
