describe('Discussion Message Submission walkthrough', () => {
  const QUIZNAME = 'Designing an architecture';
  const SENTENCE = 'Why this?';

  afterEach(() => {
    cy.contains('Logout').click();
  });

  /*
  it('login, solves a quiz,submits a discussion message', () => {
    cy.visitAvailableQuizesPage();

    cy.getQuizAndSolve(QUIZNAME);

    cy.sendDiscussionMessage(SENTENCE);

  });
  
   */

  it('login as student, goes to discussions,makes extra clarification', () => {
    cy.demoStudentLogin();
    cy.visitDiscussionQuizesPage();
  });

  it('login as teacher, goes to discussions,makes clarification public', () => {
    cy.demoTeacherLogin();
    cy.visitDiscussionQuizesPageTeacher();
  });

  it('login as student, goes to discussions,sees the clarifications of a question', () => {
    cy.demoStudentLogin();
    cy.visitAllDiscussionsPage();
  });
});
