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
Cypress.Commands.add('demoAdminLogin', () => {
  cy.visit('/');
  cy.get('[data-cy="adminButton"]').click();
  cy.contains('Administration').click();
  cy.contains('Manage Courses').click();
});

Cypress.Commands.add('demoStudentLogin', () => {
  cy.visit('/');
  cy.get('[data-cy="studentButton"]').click();
});

Cypress.Commands.add('createCourseExecution', (name, acronym, academicTerm) => {
  cy.get('[data-cy="createButton"]').click();
  cy.get('[data-cy="Name"]').type(name);
  cy.get('[data-cy="Acronym"]').type(acronym);
  cy.get('[data-cy="AcademicTerm"]').type(academicTerm);
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
    cy.get('[div="v-date-picker-table--date"]')
      .contains()
      .type(acronym);
    cy.get('[data-cy="AcademicTerm"]').type(academicTerm);
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

Cypress.Commands.add('selectDate', (dialog, datePicker, dateTime) => {
  const dateTimeArr = dateTime.split(' ');
  const dateArr = dateTimeArr[0].split('-');
  const year = dateArr[0];
  const month = dateArr[1];
  const day = dateArr[2];
  const timeArr = dateTimeArr[1].split(':');
  const hour = timeArr[0];
  const minute = timeArr[1];

  cy.get('[data-cy="form"]')
    .contains(datePicker)
    .parent()
    .children('input')
    .click();
  cy.get('.v-picker__title__btn.v-date-picker-title__year').last().click();
  cy.get('.v-date-picker-years')
    .contains(year)
    .click();
  cy.get('.v-date-picker-table').last()
    .contains(getMonthName(Number.parseInt(month)).slice(0, 3))
    .click();
  cy.get('.v-date-picker-table--date').last()
    .contains(day)
    .click();
  cy.get('.v-time-picker-clock__inner').last()
    .contains((Number.parseInt(hour) % 12) + 12)
    .click();
  cy.get('.v-time-picker-clock__inner').last()
    .contains(minute)
    .click();
  cy.get('.v-card__actions').last()
    .contains('OK')
    .click();
});

// Student commands

Cypress.Commands.add('visitCreateTourneyPage', () => {
  cy.get('[data-cy="top-bar-tourneys"]').click();
  cy.get('[data-cy="top-bar-create-tourney"]').click();
});

// Tourney commands

Cypress.Commands.add(
  'createTourney',
  (name, numberOfQuestions, availableDate, conclusionDate, topics) => {
    cy.get('[data-cy="title"]').type(name);
    cy.get('[data-cy="numberOfQuestions"]').type(numberOfQuestions);
    cy.selectDate('[data-cy="availableDate"]', 'Available Date', availableDate);
    cy.selectDate('[data-cy="conclusionDate"]', 'Conclusion Date', conclusionDate);
    for (let topic of topics) {
      cy.get('[data-cy="topics"]')
        .contains(topic)
        .click();
    }
    cy.get('[data-cy="saveTourney"]').click();
  }
);
