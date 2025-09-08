// Reporting module for E-COMPTA-IA
const Reporting = {
    
    /**
     * Initialize reporting module
     */
    async init(subModule = 'balance-sheet') {
        switch (subModule) {
            case 'balance-sheet':
                await this.initBalanceSheet();
                break;
            case 'income-statement':
                await this.initIncomeStatement();
                break;
            case 'financial-dashboard':
                await this.initFinancialDashboard();
                break;
            case 'all-reports':
                await this.initAllReports();
                break;
            default:
                await this.initAllReports();
        }
    },
    
    /**
     * Initialize all reports overview
     */
    async initAllReports() {
        UI.createBreadcrumb([
            { label: I18n.t('nav.home'), href: '#dashboard' },
            { label: I18n.t('nav.reporting') }
        ]);
        
        try {
            UI.showLoading();
            const data = await API.Reporting.getTestData();
            
            const content = `
                <div class="row">
                    <div class="col-12">
                        <div class="d-flex justify-content-between align-items-center mb-4">
                            <div>
                                <h1 class="h3 mb-1">${I18n.t('nav.reporting')}</h1>
                                <p class="text-muted mb-0">Génération et consultation des rapports financiers</p>
                            </div>
                        </div>
                    </div>
                </div>
                
                ${this.createReportsGrid()}
                
                <div class="row">
                    <div class="col-12">
                        ${this.createReportsList(data)}
                    </div>
                </div>
            `;
            
            UI.updateContent(content);
        } catch (error) {
            console.error('Reporting loading error:', error);
            UI.updateContent(UI.createErrorState(
                I18n.t('message.loadingError'),
                'Reporting.initAllReports'
            ));
        } finally {
            UI.hideLoading();
        }
    },
    
    /**
     * Create reports grid
     */
    createReportsGrid() {
        const reports = [
            {
                title: 'Bilan',
                description: 'État de la situation financière',
                icon: 'bar-chart',
                color: 'primary',
                href: '#reporting/balance-sheet'
            },
            {
                title: 'Compte de Résultat',
                description: 'Performance financière sur la période',
                icon: 'graph-up',
                color: 'success',
                href: '#reporting/income-statement'
            },
            {
                title: 'Balance Générale',
                description: 'Soldes de tous les comptes',
                icon: 'list-columns',
                color: 'info',
                href: '#reporting/trial-balance'
            },
            {
                title: 'Grand Livre',
                description: 'Détail des mouvements par compte',
                icon: 'journal-text',
                color: 'warning',
                href: '#reporting/general-ledger'
            }
        ];
        
        return `
            <div class="row mb-4">
                ${reports.map(report => `
                    <div class="col-lg-3 col-md-6 mb-3">
                        <a href="${report.href}" class="text-decoration-none">
                            <div class="card h-100 hover-shadow">
                                <div class="card-body text-center">
                                    <div class="mb-3">
                                        <i class="bi bi-${report.icon} text-${report.color}" style="font-size: 2.5rem;"></i>
                                    </div>
                                    <h5 class="card-title text-${report.color}">${report.title}</h5>
                                    <p class="card-text text-muted small">${report.description}</p>
                                </div>
                            </div>
                        </a>
                    </div>
                `).join('')}
            </div>
        `;
    },
    
    /**
     * Create reports list
     */
    createReportsList(data) {
        let content = '';
        
        if (data && data.endpoints) {
            content = `
                <h6 class="text-primary mb-3">
                    <i class="bi bi-list-check"></i> Rapports disponibles
                </h6>
                <div class="list-group">
                    ${Object.entries(data.endpoints).map(([key, endpoint]) => `
                        <div class="list-group-item">
                            <div class="d-flex w-100 justify-content-between">
                                <h6 class="mb-1">${key.charAt(0).toUpperCase() + key.slice(1)}</h6>
                                <small class="text-muted">API</small>
                            </div>
                            <p class="mb-1"><code>${endpoint}</code></p>
                        </div>
                    `).join('')}
                </div>
            `;
        } else {
            content = UI.createEmptyState(
                'Aucun rapport disponible',
                'Les rapports seront disponibles prochainement.',
                'file-earmark-text'
            );
        }
        
        return UI.createCard('Rapports disponibles', content);
    },
    
    /**
     * Initialize balance sheet
     */
    async initBalanceSheet() {
        UI.createBreadcrumb([
            { label: I18n.t('nav.home'), href: '#dashboard' },
            { label: I18n.t('nav.reporting'), href: '#reporting' },
            { label: I18n.t('nav.balanceSheet') }
        ]);
        
        UI.updateContent(`
            <div class="row">
                <div class="col-12">
                    <h1 class="h3 mb-4">${I18n.t('nav.balanceSheet')}</h1>
                    <div class="alert alert-info">
                        <i class="bi bi-info-circle me-2"></i>
                        Cette fonctionnalité sera bientôt disponible.
                    </div>
                </div>
            </div>
        `);
    },
    
    /**
     * Initialize income statement
     */
    async initIncomeStatement() {
        UI.createBreadcrumb([
            { label: I18n.t('nav.home'), href: '#dashboard' },
            { label: I18n.t('nav.reporting'), href: '#reporting' },
            { label: I18n.t('nav.incomeStatement') }
        ]);
        
        UI.updateContent(`
            <div class="row">
                <div class="col-12">
                    <h1 class="h3 mb-4">${I18n.t('nav.incomeStatement')}</h1>
                    <div class="alert alert-info">
                        <i class="bi bi-info-circle me-2"></i>
                        Cette fonctionnalité sera bientôt disponible.
                    </div>
                </div>
            </div>
        `);
    },
    
    /**
     * Initialize financial dashboard
     */
    async initFinancialDashboard() {
        UI.createBreadcrumb([
            { label: I18n.t('nav.home'), href: '#dashboard' },
            { label: I18n.t('nav.reporting'), href: '#reporting' },
            { label: I18n.t('nav.financialDashboard') }
        ]);
        
        UI.updateContent(`
            <div class="row">
                <div class="col-12">
                    <h1 class="h3 mb-4">${I18n.t('nav.financialDashboard')}</h1>
                    <div class="alert alert-info">
                        <i class="bi bi-info-circle me-2"></i>
                        Cette fonctionnalité sera bientôt disponible.
                    </div>
                </div>
            </div>
        `);
    }
};

// Export Reporting module
window.Reporting = Reporting;