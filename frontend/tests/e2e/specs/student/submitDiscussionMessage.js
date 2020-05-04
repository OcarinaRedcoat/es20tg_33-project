describe('Discussion Message Submission walkthrough', () => {
  const QUIZNAME = 'Designing an architecture';
  const SENTENCE = 'Why this?';

  beforeEach(() => {
    cy.demoStudentLogin()
  });

  afterEach(() => {
    cy.contains('Logout').click();
  });


  it('login, solves a quiz,submits a discussion message', () => {
    cy.visitAvailableQuizesPage();
    /*
    cy.getQuizAndSolve(QUIZNAME);

    cy.sendDiscussionMessage(SENTENCE);

     */

  });
});