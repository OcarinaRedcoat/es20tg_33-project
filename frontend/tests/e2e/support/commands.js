// ***********************************************
// This example commands.js shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************
//
//
// -- This is a parent command --
// Cypress.Commands.add("login", (email, password) => { ... })
//
//
// -- This is a child command --
// Cypress.Commands.add("drag", { prevSubject: 'element'}, (subject, options) => { ... })
//
//
// -- This is a dual command --
// Cypress.Commands.add("dismiss", { prevSubject: 'optional'}, (subject, options) => { ... })
//
//
// -- This will overwrite an existing command --
// Cypress.Commands.overwrite("visit", (originalFn, url, options) => { ... })
/// <reference types="Cypress" />

Cypress.Commands.add('demoStudentLogin', () => {
  cy.visit('/');
  cy.get('[data-cy="demoStudentLoginButton"]').click();
});

Cypress.Commands.add('demoTeacherLogin', () => {
  cy.visit('/');
  cy.get('[data-cy="demoTeacherLoginButton"]').click();
});

Cypress.Commands.add('demoStudentLogin', () => {
  cy.visit('/');
  cy.get('[data-cy="demoStudentLoginButton"]').click();
});

Cypress.Commands.add('createCourseExecution', (name, acronym, academicTerm) => {
  cy.get('[data-cy="createButton"]').click();
  cy.get('[data-cy="courseExecutionNameInput"]').type(name);
  cy.get('[data-cy="courseExecutionAcronymInput"]').type(acronym);
  cy.get('[data-cy="courseExecutionAcademicTermInput"]').type(academicTerm);
  cy.get('[data-cy="saveButton"]').click();
});

Cypress.Commands.add('closeErrorMessage', (name, acronym, academicTerm) => {
  cy.contains('Error')
    .parent()
    .find('button')
    .click();
});

Cypress.Commands.add('deleteCourseExecution', acronym => {
  cy.contains(acronym)
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 7)
    .find('[data-cy="deleteCourse"]')
    .click();
});

Cypress.Commands.add(
  'createFromCourseExecution',
  (name, acronym, academicTerm) => {
    cy.contains(name)
      .parent()
      .should('have.length', 1)
      .children()
      .should('have.length', 7)
      .find('[data-cy="createFromCourse"]')
      .click();
    cy.get('[data-cy="courseExecutionAcronymInput"]').type(acronym);
    cy.get('[data-cy="courseExecutionAcademicTermInput"]').type(academicTerm);
    cy.get('[data-cy="saveButton"]').click();
  }
);

// Date Picker

function getMonthName(monthNumber) {
  const monthNames = [
    'January',
    'February',
    'March',
    'April',
    'May',
    'June',
    'July',
    'August',
    'September',
    'October',
    'November',
    'December'
  ];
  return monthNames[monthNumber - 1];
}

Cypress.Commands.add('selectDate', (datePicker, dateTime) => {
  const dateTimeArr = dateTime.split(' ');
  const dateArr = dateTimeArr[0].split('-');
  const year = dateArr[0];
  const month = dateArr[1];
  const day = dateArr[2];
  const timeArr = dateTimeArr[1].split(':');
  const hour = timeArr[0];
  const minute = timeArr[1];

  cy.get(`[data-cy="${datePicker}"]`).click();
  cy.get(`[data-cy="${datePicker}"]`)
    .parent()
    .parent()
    .within(() => {
      cy.get(
        '#undefined-picker-container-DatePicker > div > div.datepicker-controls.flex.align-center.justify-content-center > div.datepicker-container-label.flex-1.flex.justify-content-center > span:nth-child(2) > button'
      )
        .click();
      cy.get('.year-month-selector')
        .contains(year)
        .click();
      cy.get(
        '#undefined-picker-container-DatePicker > div > div.datepicker-controls.flex.align-center.justify-content-center > div.datepicker-container-label.flex-1.flex.justify-content-center > span.h-100.flex.align-center.flex-1.flex.justify-content-right > button'
      )
        .click();
      cy.get('.year-month-selector')
        .contains(getMonthName(Number.parseInt(month)).slice(0, 3))
        .click();
      cy.get('.month-container .datepicker-days')
        .last()
        .children()
        .children()
        .contains(day)
        .click();
      cy.get('.time-picker-column-hours')
        .contains((Number.parseInt(hour) % 12) + 12)
        .click();
      cy.get('.time-picker-column-minutes')
        .contains(minute)
        .click();
      cy.get('.datepicker-button.validate').click();
    });
});

// Student commands

Cypress.Commands.add('visitAvailableQuizesPage', () => {
  cy.get('[data-cy="top-bar-quizzes"]').click();
  cy.get('[data-cy="top-bar-available"]').click();
});

Cypress.Commands.add('visitCreateTourneyPage', () => {
  cy.get('[data-cy="top-bar-tourneys"]').click();
  cy.get('[data-cy="top-bar-create-tourney"]').click();
});

Cypress.Commands.add('visitSubmittedQuestionsPage', () => {
  cy.get('[data-cy="top-bar-submissions"]').click();
  cy.get('[data-cy="top-bar-submitted-questions"]').click();
});

Cypress.Commands.add('visitOpenTourneysPage', () => {
  cy.get('[data-cy="top-bar-tourneys"]').click();
  cy.get('[data-cy="top-bar-open-tourneys"]').click();
  cy.get('.scrollbar').click();
});

Cypress.Commands.add('visitTourneysDashboard', () => {
  cy.get('[data-cy="top-bar-tourneys"]').click();
  cy.get('[data-cy="top-bar-tourneys-dashboard"]').click();
  cy.get('.scrollbar').click();
});

// Teacher commands

Cypress.Commands.add('visitApproveRejectPage', () => {
  cy.get('[data-cy="top-bar-management"]').click();
  cy.get('[data-cy="top-bar-approve-reject"]').click();
});

//Discussion commands

Cypress.Commands.add('getQuizAndSolve', quizName => {
  for (let quizTitle of quizName) {
    cy.get('[data-cy="quizTitle"]')
      .contains(quizTitle)
      .click();
  }
  cy.get('[data-cy="endQuiz"]').click();
});

Cypress.Commands.add('sendDiscussionMessage', sentence => {
  cy.get('[data-cy="submitDiscussionMessage"]').click();
  cy.get('[data-cy="message"').type(sentence);
  cy.get('[data-cy="submitMessage"}').click();
});

Cypress.Commands.add('teacherResponse', sentence => {
  cy.get('[data-cy="submitDiscussionMessage"]').click();
  cy.get('[data-cy="searchDiscussion"').click();
  cy.get('[data-cy="submitMessage"').type(sentence);
  cy.get('[data-cy="submitResponse"').click();
});

// Tourney commands

Cypress.Commands.add(
  'createTourney',
  (name, numberOfQuestions, availableDate, conclusionDate, topics) => {
    cy.get('[data-cy="title"]').type(name);
    cy.get('[data-cy="numberOfQuestions"]').type(numberOfQuestions);
    cy.selectDate('availableDate', availableDate);
    cy.selectDate('conclusionDate', conclusionDate);
    for (let topic of topics) {
      cy.get('[data-cy="topics"]')
        .contains(topic)
        .click();
    }
    cy.get('[data-cy="saveTourney"]').click();
  }
);

Cypress.Commands.add(
  'getOpenTourney',
  (name, numberOfQuestions, availableDate, conclusionDate, topics) => {
    return cy
      .get('[data-cy="tourneysList"] tbody tr')
      .last()
      .children()
      .should('contain', name)
      .and('contain', numberOfQuestions)
      .and('contain', availableDate)
      .and('contain', conclusionDate);
  }
);

Cypress.Commands.add('enrollInTourney', name => {
  cy.get('[data-cy="tourneysList"] tbody tr')
    .last()
    .children().contains(name)
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 5)
    .find('[data-cy="enrollInTourney"]')
    .click();
});

Cypress.Commands.add('cancelTourney', name => {
  cy.get('[data-cy="tourneysList"] tbody tr')
    .last()
    .children()
    .contains(name)
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 5)
    .find('[data-cy="cancelTourney"]')
    .click();
});

//Student Question commands

Cypress.Commands.add('submitQuestion', (title, content, optionContent) => {
  cy.get('[data-cy="createSubmissionButton"]').click();
  cy.get('[data-cy="Title"]').type(title, { force: true });
  cy.get('[data-cy="QuestionContent"]').type(content);
  cy.get('[data-cy="OptionContent"]')
    .eq(0)
    .type(optionContent);
  cy.get('[data-cy="OptionContent"]')
    .eq(1)
    .type(optionContent);
  cy.get('[data-cy="OptionContent"]')
    .eq(2)
    .type(optionContent);
  cy.get('[data-cy="OptionContent"]')
    .eq(3)
    .type(optionContent);
  cy.get('[data-cy="OptionCorrect"]:first').click({ force: true });
  cy.get('[data-cy="submitButton"]').click();
});

Cypress.Commands.add('deleteSubmittedQuestion', title => {
  cy.contains(title)
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 4)
    .find('[data-cy="deleteSubmittedQuestion"]')
    .click();
});

Cypress.Commands.add('approveQuestion', title => {
  cy.contains(title)
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 5)
    .find('[data-cy="reviewQuestionButton"]')
    .click();
  cy.get('[data-cy="approveButton"]').click();
});

Cypress.Commands.add('rejectQuestion', (title, justification) => {
  cy.contains(title)
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 5)
    .find('[data-cy="reviewQuestionButton"]')
    .click();
  cy.get('[data-cy="justification"]').type(justification, { force: true });
  cy.get('[data-cy="rejectButton"]').click();
});

Cypress.Commands.add('rejectQuestion', title => {
  cy.contains(title)
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 5)
    .find('[data-cy="reviewQuestionButton"]')
    .click();
  cy.get('[data-cy="rejectButton"]').click();
});
