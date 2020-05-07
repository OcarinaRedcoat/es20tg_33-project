describe('Student create a Tourney', () => {
  const TITLE = 'Demo Tourney';
  const NUMBER_OF_QUESTIONS = 2;
  const AVAILABLE_DATE = '2020-04-10 00:00';
  const CONCLUSION_DATE = '2021-04-10 00:00';
  const AVAILABLE_DATE_FORMATED = '10/04/2020, 12:00';
  const CONCLUSION_DATE_FORMATED = '10/04/2021, 12:00';
  const TOPICS = ['Availability', 'GitHub'];

  beforeEach(() => {
    cy.demoStudentLogin();
  });

  afterEach(() => {
    cy.contains('Logout').click();
  });

  it('student creates a Tourney, gets the list, and deletes it', () => {
    cy.visitCreateTourneyPage();

    cy.createTourney(
      TITLE,
      NUMBER_OF_QUESTIONS,
      AVAILABLE_DATE,
      CONCLUSION_DATE,
      TOPICS
    );

    cy.visitOpenTourneysPage();
    cy.getOpenTourney(
      TITLE,
      NUMBER_OF_QUESTIONS,
      AVAILABLE_DATE_FORMATED,
      CONCLUSION_DATE_FORMATED,
      TOPICS
    );

    cy.enrollInTourney(
        TITLE
    );

    cy.cancelTourney(
        TITLE
    );
  });
});
