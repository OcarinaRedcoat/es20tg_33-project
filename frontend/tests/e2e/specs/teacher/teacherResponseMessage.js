describe('Teacher Resconses a Student Discussion walkthrough', () => {
  const DISCUSSIONID = '1';
  const SENTENCE = 'Why this?';

  beforeEach(() => {
    cy.demoTeacherLogin()

  });

  afterEach(() => {
    cy.contains('Logout').click();
  });


  it('login, search fot a discussion submit response', () => {
    /*

    cy.getDiscussionLis();

    cy.searchDiscussion(DISCUSSIONID);

    cy.teacherResponse();

    cy.Response(SENTENCE);

    cy.saveResponse();
     */

  });
});