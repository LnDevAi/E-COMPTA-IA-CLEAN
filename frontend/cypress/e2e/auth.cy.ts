describe('Authentication Flow', () => {
  beforeEach(() => {
    cy.visit('/login');
  });

  it('should display login form', () => {
    cy.get('mat-card-title').should('contain', 'Connexion');
    cy.get('input[formControlName="username"]').should('be.visible');
    cy.get('input[formControlName="password"]').should('be.visible');
    cy.get('button[type="submit"]').should('be.visible');
  });

  it('should show validation errors for empty fields', () => {
    cy.get('button[type="submit"]').click();
    cy.get('mat-error').should('be.visible');
  });

  it('should login successfully with valid credentials', () => {
    cy.intercept('POST', '/api/auth/login', {
      statusCode: 200,
      body: {
        accessToken: 'mock-token',
        refreshToken: 'mock-refresh-token',
        user: {
          id: 1,
          username: 'testuser',
          email: 'test@example.com',
          firstName: 'Test',
          lastName: 'User',
          roles: ['USER'],
          isActive: true
        }
      }
    }).as('loginRequest');

    cy.get('input[formControlName="username"]').type('testuser');
    cy.get('input[formControlName="password"]').type('password123');
    cy.get('button[type="submit"]').click();

    cy.wait('@loginRequest');
    cy.url().should('include', '/dashboard');
  });

  it('should show error for invalid credentials', () => {
    cy.intercept('POST', '/api/auth/login', {
      statusCode: 401,
      body: { message: 'Invalid credentials' }
    }).as('loginError');

    cy.get('input[formControlName="username"]').type('wronguser');
    cy.get('input[formControlName="password"]').type('wrongpassword');
    cy.get('button[type="submit"]').click();

    cy.wait('@loginError');
    cy.get('mat-snack-bar-container').should('be.visible');
  });

  it('should navigate to register page', () => {
    cy.get('a[routerLink="/register"]').click();
    cy.url().should('include', '/register');
  });

  it('should register new user successfully', () => {
    cy.visit('/register');
    
    cy.intercept('POST', '/api/auth/register', {
      statusCode: 201,
      body: {
        id: 2,
        username: 'newuser',
        email: 'new@example.com',
        firstName: 'New',
        lastName: 'User',
        roles: ['USER'],
        isActive: true
      }
    }).as('registerRequest');

    cy.get('input[formControlName="username"]').type('newuser');
    cy.get('input[formControlName="email"]').type('new@example.com');
    cy.get('input[formControlName="password"]').type('password123');
    cy.get('input[formControlName="confirmPassword"]').type('password123');
    cy.get('input[formControlName="firstName"]').type('New');
    cy.get('input[formControlName="lastName"]').type('User');
    cy.get('button[type="submit"]').click();

    cy.wait('@registerRequest');
    cy.url().should('include', '/login');
  });

  it('should logout successfully', () => {
    // First login
    cy.intercept('POST', '/api/auth/login', {
      statusCode: 200,
      body: {
        accessToken: 'mock-token',
        refreshToken: 'mock-refresh-token',
        user: {
          id: 1,
          username: 'testuser',
          email: 'test@example.com',
          firstName: 'Test',
          lastName: 'User',
          roles: ['USER'],
          isActive: true
        }
      }
    }).as('loginRequest');

    cy.get('input[formControlName="username"]').type('testuser');
    cy.get('input[formControlName="password"]').type('password123');
    cy.get('button[type="submit"]').click();

    cy.wait('@loginRequest');
    cy.url().should('include', '/dashboard');

    // Then logout
    cy.intercept('POST', '/api/auth/logout', {
      statusCode: 200,
      body: {}
    }).as('logoutRequest');

    cy.get('button[mat-icon-button]').contains('account_circle').click();
    cy.get('button').contains('Déconnexion').click();

    cy.wait('@logoutRequest');
    cy.url().should('include', '/login');
  });
});








