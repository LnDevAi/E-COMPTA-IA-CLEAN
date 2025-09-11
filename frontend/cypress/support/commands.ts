// ***********************************************
// This example commands.ts shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************

Cypress.Commands.add('login', (username = 'testuser', password = 'password123') => {
  cy.intercept('POST', '/api/auth/login', {
    statusCode: 200,
    body: {
      accessToken: 'mock-token',
      refreshToken: 'mock-refresh-token',
      user: {
        id: 1,
        username: username,
        email: `${username}@example.com`,
        firstName: 'Test',
        lastName: 'User',
        roles: ['USER'],
        isActive: true,
        createdAt: new Date(),
        updatedAt: new Date()
      }
    }
  }).as('loginRequest');

  cy.visit('/login');
  cy.get('input[formControlName="username"]').type(username);
  cy.get('input[formControlName="password"]').type(password);
  cy.get('button[type="submit"]').click();
  cy.wait('@loginRequest');
});

Cypress.Commands.add('logout', () => {
  cy.intercept('POST', '/api/auth/logout', {
    statusCode: 200,
    body: {}
  }).as('logoutRequest');

  cy.get('button[mat-icon-button]').contains('account_circle').click();
  cy.get('button').contains('Déconnexion').click();
  cy.wait('@logoutRequest');
});

Cypress.Commands.add('mockApiResponse', (method: string, url: string, response: any) => {
  cy.intercept(method, url, {
    statusCode: 200,
    body: response
  });
});

// Custom command to wait for Angular to be ready
Cypress.Commands.add('waitForAngular', () => {
  cy.window().then((win) => {
    return new Cypress.Promise((resolve) => {
      if (win.ng) {
        win.ng.getTestability(win.document.body).whenStable(() => {
          resolve();
        });
      } else {
        resolve();
      }
    });
  });
});

// Custom command to clear all cookies and localStorage
Cypress.Commands.add('clearAppState', () => {
  cy.clearCookies();
  cy.clearLocalStorage();
  cy.window().then((win) => {
    win.sessionStorage.clear();
  });
});

// Custom command to mock file upload
Cypress.Commands.add('uploadFile', (selector: string, filePath: string) => {
  cy.get(selector).selectFile(filePath, { force: true });
});

// Custom command to check if element is in viewport
Cypress.Commands.add('isInViewport', (selector: string) => {
  cy.get(selector).then(($el) => {
    const rect = $el[0].getBoundingClientRect();
    expect(rect.top).to.be.at.least(0);
    expect(rect.bottom).to.be.at.most(Cypress.config('viewportHeight'));
    expect(rect.left).to.be.at.least(0);
    expect(rect.right).to.be.at.most(Cypress.config('viewportWidth'));
  });
});

// Custom command to wait for network to be idle
Cypress.Commands.add('waitForNetworkIdle', (timeout = 2000) => {
  cy.window().then((win) => {
    return new Cypress.Promise((resolve) => {
      let idleTimer: number;
      const resetTimer = () => {
        clearTimeout(idleTimer);
        idleTimer = setTimeout(resolve, timeout);
      };
      
      resetTimer();
      
      // Listen for network requests
      const originalFetch = win.fetch;
      win.fetch = (...args) => {
        resetTimer();
        return originalFetch.apply(win, args);
      };
    });
  });
});








