describe('Student Question Dashboard', () => {

  it('login, goes to dashboard and changes privacy', () => {
    cy.demoStudentLogin();
    cy.visitSubmittedQuestionsDashboardPage();
    cy.get('[data-cy="dashboardPrivacy"]:first').click({ force: true });
    cy.contains('Logout').click();
  });
});
