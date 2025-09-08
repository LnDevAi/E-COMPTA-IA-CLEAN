// Dashboard module for E-COMPTA-IA
const Dashboard = {
    
    /**
     * Initialize dashboard
     */
    async init() {
        UI.createBreadcrumb([
            { label: I18n.t('nav.home'), href: '#dashboard' },
            { label: I18n.t('nav.dashboard') }
        ]);
        
        try {
            UI.showLoading();
            await this.loadDashboard();
        } catch (error) {
            console.error('Dashboard loading error:', error);
            UI.updateContent(UI.createErrorState(
                I18n.t('message.loadingError'),
                'Dashboard.init'
            ));
        } finally {
            UI.hideLoading();
        }
    },
    
    /**
     * Load dashboard content
     */
    async loadDashboard() {
        // Load dashboard data
        const [testData, analytics, performance, financial] = await Promise.allSettled([
            API.Dashboard.getTestData(),
            API.Dashboard.getAnalytics().catch(() => null),
            API.Dashboard.getPerformance().catch(() => null),
            API.Dashboard.getFinancial().catch(() => null)
        ]);
        
        const dashboardData = testData.status === 'fulfilled' ? testData.value : null;
        
        const content = `
            <div class="row">
                <div class="col-12">
                    <div class="d-flex justify-content-between align-items-center mb-4">
                        <div>
                            <h1 class="h3 mb-1">${I18n.t('dashboard.title')}</h1>
                            <p class="text-muted mb-0">${I18n.t('dashboard.welcome')}</p>
                        </div>
                        <div>
                            ${UI.createActionButtons([
                                {
                                    label: I18n.t('common.refresh'),
                                    icon: 'arrow-clockwise',
                                    className: 'btn btn-outline-primary',
                                    onclick: 'Dashboard.init()'
                                }
                            ])}
                        </div>
                    </div>
                </div>
            </div>
            
            ${this.createKPICards()}
            
            <div class="row">
                <div class="col-lg-8">
                    ${this.createRecentActivities()}
                </div>
                <div class="col-lg-4">
                    ${this.createQuickActions()}
                </div>
            </div>
            
            <div class="row">
                <div class="col-12">
                    ${this.createSystemStatus(dashboardData)}
                </div>
            </div>
        `;
        
        UI.updateContent(content);
    },
    
    /**
     * Create KPI cards
     */
    createKPICards() {
        const kpis = [
            {
                title: 'Écritures du mois',
                value: '1,247',
                icon: 'journal-text',
                color: 'primary'
            },
            {
                title: 'Solde de trésorerie',
                value: '2,450,000 CFA',
                icon: 'wallet2',
                color: 'success'
            },
            {
                title: 'Tiers actifs',
                value: '156',
                icon: 'people',
                color: 'info'
            },
            {
                title: 'Tâches en attente',
                value: '8',
                icon: 'list-check',
                color: 'warning'
            }
        ];
        
        return `
            <div class="row mb-4">
                ${kpis.map(kpi => `
                    <div class="col-lg-3 col-md-6 mb-3">
                        ${UI.createStatsCard(kpi.title, kpi.value, kpi.icon, kpi.color)}
                    </div>
                `).join('')}
            </div>
        `;
    },
    
    /**
     * Create recent activities section
     */
    createRecentActivities() {
        const activities = [
            {
                time: '10:30',
                title: 'Écriture validée',
                description: 'Vente de marchandises - 150,000 CFA',
                icon: 'check-circle',
                color: 'success'
            },
            {
                time: '09:15',
                title: 'Nouveau tiers créé',
                description: 'SARL TECHNO-SERVICES',
                icon: 'person-plus',
                color: 'info'
            },
            {
                time: '08:45',
                title: 'Rapport généré',
                description: 'Balance générale - Janvier 2025',
                icon: 'file-earmark-pdf',
                color: 'primary'
            },
            {
                time: 'Hier',
                title: 'Sauvegarde effectuée',
                description: 'Sauvegarde automatique des données',
                icon: 'cloud-check',
                color: 'secondary'
            }
        ];
        
        const activitiesHtml = activities.map(activity => `
            <div class="d-flex align-items-start mb-3">
                <div class="flex-shrink-0">
                    <div class="rounded-circle bg-${activity.color} bg-opacity-10 p-2">
                        <i class="bi bi-${activity.icon} text-${activity.color}"></i>
                    </div>
                </div>
                <div class="flex-grow-1 ms-3">
                    <div class="d-flex justify-content-between">
                        <h6 class="mb-1">${activity.title}</h6>
                        <small class="text-muted">${activity.time}</small>
                    </div>
                    <p class="mb-0 text-muted small">${activity.description}</p>
                </div>
            </div>
        `).join('');
        
        return UI.createCard(
            I18n.t('dashboard.recentActivities'),
            activitiesHtml
        );
    },
    
    /**
     * Create quick actions section
     */
    createQuickActions() {
        const actions = [
            {
                title: 'Nouvelle écriture',
                description: 'Créer une écriture comptable',
                icon: 'plus-circle',
                color: 'primary',
                href: '#accounting/journal-entries/new'
            },
            {
                title: 'Ajouter un tiers',
                description: 'Créer un nouveau tiers',
                icon: 'person-plus',
                color: 'success',
                href: '#third-parties/new'
            },
            {
                title: 'Générer un rapport',
                description: 'Créer un rapport financier',
                icon: 'file-earmark-text',
                color: 'info',
                href: '#reporting'
            },
            {
                title: 'Assistant IA',
                description: 'Obtenir de l\'aide de l\'IA',
                icon: 'robot',
                color: 'warning',
                href: '#ai/assistant'
            }
        ];
        
        const actionsHtml = actions.map(action => `
            <a href="${action.href}" class="text-decoration-none">
                <div class="d-flex align-items-center p-3 mb-2 rounded bg-light hover-bg-${action.color} hover-bg-opacity-10 transition">
                    <div class="flex-shrink-0">
                        <i class="bi bi-${action.icon} text-${action.color} fs-4"></i>
                    </div>
                    <div class="flex-grow-1 ms-3">
                        <h6 class="mb-1">${action.title}</h6>
                        <p class="mb-0 text-muted small">${action.description}</p>
                    </div>
                    <div class="flex-shrink-0">
                        <i class="bi bi-chevron-right text-muted"></i>
                    </div>
                </div>
            </a>
        `).join('');
        
        return UI.createCard(
            I18n.t('dashboard.quickActions'),
            actionsHtml
        );
    },
    
    /**
     * Create system status section
     */
    createSystemStatus(dashboardData) {
        let statusContent = '';
        
        if (dashboardData && dashboardData.success) {
            const data = dashboardData.data;
            
            statusContent = `
                <div class="row">
                    <div class="col-md-6">
                        <h6 class="text-primary mb-3">
                            <i class="bi bi-gear"></i> Fonctionnalités disponibles
                        </h6>
                        <div class="row">
                            ${data.features ? data.features.map(feature => `
                                <div class="col-12 mb-2">
                                    <i class="bi bi-check-circle text-success me-2"></i>
                                    <span>${feature}</span>
                                </div>
                            `).join('') : ''}
                        </div>
                    </div>
                    <div class="col-md-6">
                        <h6 class="text-primary mb-3">
                            <i class="bi bi-api"></i> Points d'accès API
                        </h6>
                        <div class="row">
                            ${data.endpoints ? Object.entries(data.endpoints).map(([key, endpoint]) => `
                                <div class="col-12 mb-2">
                                    <code class="small">${endpoint}</code>
                                </div>
                            `).join('') : ''}
                        </div>
                    </div>
                </div>
                <div class="row mt-3">
                    <div class="col-12">
                        <div class="alert alert-success">
                            <i class="bi bi-check-circle me-2"></i>
                            <strong>Système opérationnel</strong> - 
                            Dernière mise à jour: ${data.timestamp ? Utils.Date.format(data.timestamp, 'DD/MM/YYYY HH:mm') : 'N/A'}
                        </div>
                    </div>
                </div>
            `;
        } else {
            statusContent = `
                <div class="alert alert-warning">
                    <i class="bi bi-exclamation-triangle me-2"></i>
                    <strong>Informations système non disponibles</strong> - 
                    Impossible de récupérer le statut du système.
                </div>
            `;
        }
        
        return UI.createCard(
            'Statut du système',
            statusContent
        );
    },
    
    /**
     * Navigate to specific section
     */
    navigateTo(section) {
        window.location.hash = section;
    }
};

// Export Dashboard module
window.Dashboard = Dashboard;