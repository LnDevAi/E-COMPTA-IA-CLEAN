describe('CRM Module', () => {
  beforeEach(() => {
    // Mock login and navigate to CRM
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

  describe('Clients Management', () => {
    beforeEach(() => {
      cy.visit('/crm/clients');
    });

    it('should display clients list', () => {
      cy.get('h1').should('contain', 'Gestion des clients');
      cy.get('mat-table').should('be.visible');
      cy.get('mat-header-row').should('be.visible');
    });

    it('should add new client', () => {
      cy.intercept('POST', '/api/crm/customers', {
        statusCode: 201,
        body: {
          id: 1,
          name: 'New Client',
          email: 'client@example.com',
          phone: '0123456789',
          leadStatus: 'CLIENT',
          customerSegment: 'PREMIUM'
        }
      }).as('createClient');

      cy.get('button').contains('Nouveau client').click();
      
      cy.get('input[formControlName="name"]').type('New Client');
      cy.get('input[formControlName="email"]').type('client@example.com');
      cy.get('input[formControlName="phone"]').type('0123456789');
      cy.get('mat-select[formControlName="leadStatus"]').click();
      cy.get('mat-option').contains('Client').click();
      cy.get('mat-select[formControlName="customerSegment"]').click();
      cy.get('mat-option').contains('Premium').click();
      
      cy.get('button[type="submit"]').click();
      cy.wait('@createClient');
      cy.get('mat-snack-bar-container').should('be.visible');
    });

    it('should edit existing client', () => {
      cy.intercept('PUT', '/api/crm/customers/1', {
        statusCode: 200,
        body: {
          id: 1,
          name: 'Updated Client',
          email: 'updated@example.com',
          phone: '0987654321',
          leadStatus: 'CLIENT',
          customerSegment: 'STANDARD'
        }
      }).as('updateClient');

      cy.get('button[mat-icon-button]').contains('edit').first().click();
      
      cy.get('input[formControlName="name"]').clear().type('Updated Client');
      cy.get('input[formControlName="email"]').clear().type('updated@example.com');
      
      cy.get('button[type="submit"]').click();
      cy.wait('@updateClient');
      cy.get('mat-snack-bar-container').should('be.visible');
    });

    it('should delete client', () => {
      cy.intercept('DELETE', '/api/crm/customers/1', {
        statusCode: 200,
        body: {}
      }).as('deleteClient');

      cy.get('button[mat-icon-button]').contains('delete').first().click();
      cy.get('button').contains('Confirmer').click();
      cy.wait('@deleteClient');
      cy.get('mat-snack-bar-container').should('be.visible');
    });

    it('should filter clients', () => {
      cy.get('input[placeholder*="Rechercher"]').type('test');
      cy.get('mat-table tbody tr').should('have.length.at.least', 1);
    });

    it('should sort clients table', () => {
      cy.get('mat-header-cell').contains('Nom').click();
      cy.get('mat-table tbody tr').should('be.visible');
    });
  });

  describe('Prospects Management', () => {
    beforeEach(() => {
      cy.visit('/crm/prospects');
    });

    it('should display prospects list', () => {
      cy.get('h1').should('contain', 'Gestion des prospects');
      cy.get('mat-table').should('be.visible');
    });

    it('should convert prospect to client', () => {
      cy.intercept('PUT', '/api/crm/customers/1', {
        statusCode: 200,
        body: {
          id: 1,
          leadStatus: 'CLIENT'
        }
      }).as('convertProspect');

      cy.get('button').contains('Convertir').first().click();
      cy.wait('@convertProspect');
      cy.get('mat-snack-bar-container').should('be.visible');
    });
  });

  describe('Opportunities Management', () => {
    beforeEach(() => {
      cy.visit('/crm/opportunities');
    });

    it('should display opportunities list', () => {
      cy.get('h1').should('contain', 'Gestion des opportunités');
      cy.get('mat-table').should('be.visible');
    });

    it('should create new opportunity', () => {
      cy.intercept('POST', '/api/crm/customers', {
        statusCode: 201,
        body: {
          id: 1,
          name: 'New Opportunity',
          email: 'opportunity@example.com',
          leadStatus: 'OPPORTUNITY',
          estimatedValue: 50000
        }
      }).as('createOpportunity');

      cy.get('button').contains('Nouvelle opportunité').click();
      
      cy.get('input[formControlName="name"]').type('New Opportunity');
      cy.get('input[formControlName="email"]').type('opportunity@example.com');
      cy.get('input[formControlName="estimatedValue"]').type('50000');
      
      cy.get('button[type="submit"]').click();
      cy.wait('@createOpportunity');
      cy.get('mat-snack-bar-container').should('be.visible');
    });
  });
});








