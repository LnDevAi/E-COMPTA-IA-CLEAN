describe('Dashboard', () => {
  beforeEach(() => {
    // Mock login and navigate to dashboard
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

    cy.visit('/login');
    cy.get('input[formControlName="username"]').type('testuser');
    cy.get('input[formControlName="password"]').type('password123');
    cy.get('button[type="submit"]').click();
    cy.wait('@loginRequest');
  });

  it('should display dashboard with all sections', () => {
    cy.url().should('include', '/dashboard');
    
    // Check header
    cy.get('h1').should('contain', 'Tableau de bord');
    
    // Check KPIs section
    cy.get('.kpis-section').should('be.visible');
    cy.get('.kpi-card').should('have.length.at.least', 4);
    
    // Check widgets section
    cy.get('.widgets-section').should('be.visible');
    cy.get('.widget-card').should('have.length.at.least', 3);
    
    // Check quick actions
    cy.get('.quick-actions').should('be.visible');
    cy.get('.quick-action-item').should('have.length.at.least', 4);
  });

  it('should display KPI cards with correct data', () => {
    cy.get('.kpi-card').first().within(() => {
      cy.get('.kpi-title').should('be.visible');
      cy.get('.kpi-value').should('be.visible');
      cy.get('.kpi-change').should('be.visible');
    });
  });

  it('should display widgets with charts and tables', () => {
    cy.get('.widget-card').first().within(() => {
      cy.get('mat-card-title').should('be.visible');
      cy.get('mat-card-content').should('be.visible');
    });
  });

  it('should handle quick actions', () => {
    cy.get('.quick-action-item').first().click();
    // Should navigate to appropriate page or show modal
    cy.url().should('not.include', '/dashboard');
  });

  it('should refresh dashboard data', () => {
    cy.intercept('GET', '/api/dashboard/**', {
      statusCode: 200,
      body: {
        kpis: [],
        widgets: [],
        activities: []
      }
    }).as('refreshData');

    cy.get('button').contains('Actualiser').click();
    cy.wait('@refreshData');
  });

  it('should export data', () => {
    cy.get('button').contains('Exporter').click();
    cy.get('mat-snack-bar-container').should('be.visible');
  });

  it('should display recent activities', () => {
    cy.get('.activities-section').should('be.visible');
    cy.get('.activity-item').should('have.length.at.least', 1);
  });

  it('should handle responsive design', () => {
    // Test mobile view
    cy.viewport(375, 667);
    cy.get('.kpis-grid').should('be.visible');
    cy.get('.widgets-grid').should('be.visible');
    
    // Test tablet view
    cy.viewport(768, 1024);
    cy.get('.kpis-grid').should('be.visible');
    cy.get('.widgets-grid').should('be.visible');
    
    // Test desktop view
    cy.viewport(1200, 800);
    cy.get('.kpis-grid').should('be.visible');
    cy.get('.widgets-grid').should('be.visible');
  });

  it('should show loading state', () => {
    cy.intercept('GET', '/api/dashboard/**', {
      statusCode: 200,
      body: {},
      delay: 2000
    }).as('slowRequest');

    cy.visit('/dashboard');
    cy.get('mat-spinner').should('be.visible');
    cy.wait('@slowRequest');
    cy.get('mat-spinner').should('not.exist');
  });

  it('should handle error state', () => {
    cy.intercept('GET', '/api/dashboard/**', {
      statusCode: 500,
      body: { message: 'Internal server error' }
    }).as('errorRequest');

    cy.visit('/dashboard');
    cy.wait('@errorRequest');
    cy.get('mat-snack-bar-container').should('be.visible');
  });
});








