// Main application script for E-COMPTA-IA
class EcomptaiaApp {
    
    constructor() {
        this.currentRoute = '';
        this.init();
    }
    
    /**
     * Initialize application
     */
    async init() {
        try {
            // Initialize services
            await this.initializeServices();
            
            // Set up event listeners
            this.setupEventListeners();
            
            // Handle initial route
            this.handleRouteChange();
            
            // Set up theme
            this.initializeTheme();
            
            console.log('E-COMPTA-IA Application initialized successfully');
        } catch (error) {
            console.error('Application initialization failed:', error);
            this.showInitializationError(error);
        }
    }
    
    /**
     * Initialize services
     */
    async initializeServices() {
        // Initialize internationalization
        await I18n.init();
        
        // Initialize API
        API.init();
        
        console.log('Services initialized');
    }
    
    /**
     * Set up event listeners
     */
    setupEventListeners() {
        // Route change listener
        window.addEventListener('hashchange', () => {
            this.handleRouteChange();
        });
        
        // Language change listener
        window.addEventListener('languageChange', (event) => {
            console.log('Language changed to:', event.detail.language);
        });
        
        // Click handler for navigation links
        document.addEventListener('click', (event) => {
            const link = event.target.closest('a[href^="#"]');
            if (link) {
                event.preventDefault();
                window.location.hash = link.getAttribute('href');
            }
        });
        
        // Global error handler
        window.addEventListener('error', (event) => {
            console.error('Global error:', event.error);
            UI.showToast('Une erreur inattendue s\'est produite', 'error');
        });
        
        // Unhandled promise rejection handler
        window.addEventListener('unhandledrejection', (event) => {
            console.error('Unhandled promise rejection:', event.reason);
            UI.showToast('Erreur de connexion ou de données', 'error');
        });
    }
    
    /**
     * Handle route changes
     */
    handleRouteChange() {
        const hash = window.location.hash || '#dashboard';
        const route = hash.substring(1); // Remove #
        
        if (route === this.currentRoute) return;
        
        this.currentRoute = route;
        this.navigateToRoute(route);
        
        // Save last route
        Utils.Storage.set(CONFIG.STORAGE_KEYS.LAST_ROUTE, route);
    }
    
    /**
     * Navigate to specific route
     */
    async navigateToRoute(route) {
        try {
            // Update active nav link
            this.updateActiveNavLink(route);
            
            // Parse route parts
            const routeParts = route.split('/');
            const mainModule = routeParts[0];
            const subModule = routeParts[1];
            
            // Route to appropriate module
            switch (mainModule) {
                case 'dashboard':
                case '':
                    await Dashboard.init();
                    break;
                    
                case 'accounting':
                    await Accounting.init(subModule);
                    break;
                    
                case 'reporting':
                    await Reporting.init(subModule);
                    break;
                    
                case 'third-parties':
                    await ThirdParties.init(subModule);
                    break;
                    
                case 'hr':
                    await Hr.init(subModule);
                    break;
                    
                case 'assets':
                    await Assets.init(subModule);
                    break;
                    
                case 'ai':
                    await Ai.init(subModule);
                    break;
                    
                case 'international':
                    await International.init(subModule);
                    break;
                    
                case 'tax':
                    await Tax.init(subModule);
                    break;
                    
                case 'documents':
                    await Documents.init(subModule);
                    break;
                    
                case 'workflow':
                    await Workflow.init(subModule);
                    break;
                    
                case 'audit':
                    await Audit.init(subModule);
                    break;
                    
                case 'backup':
                    await Backup.init(subModule);
                    break;
                    
                case 'monitoring':
                    await Monitoring.init(subModule);
                    break;
                    
                case 'system':
                    await System.init(subModule);
                    break;
                    
                default:
                    await this.show404();
                    break;
            }
        } catch (error) {
            console.error('Navigation error:', error);
            UI.updateContent(UI.createErrorState(
                'Erreur lors du chargement de la page',
                'App.handleRouteChange'
            ));
        }
    }
    
    /**
     * Update active navigation link
     */
    updateActiveNavLink(route) {
        // Remove active class from all nav links
        document.querySelectorAll('.navbar-nav .nav-link').forEach(link => {
            link.classList.remove('active');
        });
        
        // Add active class to current route
        const currentLink = document.querySelector(`a[href="#${route}"]`);
        if (currentLink) {
            currentLink.classList.add('active');
        }
    }
    
    /**
     * Show 404 page
     */
    async show404() {
        UI.createBreadcrumb([
            { label: I18n.t('nav.home'), href: '#dashboard' },
            { label: 'Page non trouvée' }
        ]);
        
        UI.updateContent(`
            <div class="row">
                <div class="col-12">
                    <div class="text-center py-5">
                        <i class="bi bi-exclamation-triangle text-warning" style="font-size: 4rem;"></i>
                        <h1 class="h3 mt-3">Page non trouvée</h1>
                        <p class="text-muted">La page que vous recherchez n'existe pas.</p>
                        <a href="#dashboard" class="btn btn-primary">
                            <i class="bi bi-house"></i> Retour à l'accueil
                        </a>
                    </div>
                </div>
            </div>
        `);
    }
    
    /**
     * Initialize theme
     */
    initializeTheme() {
        const savedTheme = Utils.Storage.get(CONFIG.STORAGE_KEYS.THEME, CONFIG.THEMES.LIGHT);
        this.setTheme(savedTheme);
    }
    
    /**
     * Set theme
     */
    setTheme(theme) {
        document.documentElement.setAttribute('data-bs-theme', theme);
        
        // Update theme icon
        const themeIcon = document.getElementById('themeIcon');
        if (themeIcon) {
            themeIcon.className = theme === CONFIG.THEMES.DARK ? 'bi bi-moon-fill' : 'bi bi-sun-fill';
        }
        
        // Save theme preference
        Utils.Storage.set(CONFIG.STORAGE_KEYS.THEME, theme);
    }
    
    /**
     * Toggle theme
     */
    toggleTheme() {
        const currentTheme = document.documentElement.getAttribute('data-bs-theme');
        const newTheme = currentTheme === CONFIG.THEMES.DARK ? CONFIG.THEMES.LIGHT : CONFIG.THEMES.DARK;
        this.setTheme(newTheme);
    }
    
    /**
     * Show initialization error
     */
    showInitializationError(error) {
        document.body.innerHTML = `
            <div class="container mt-5">
                <div class="row justify-content-center">
                    <div class="col-md-6">
                        <div class="card">
                            <div class="card-body text-center">
                                <i class="bi bi-exclamation-triangle text-danger" style="font-size: 3rem;"></i>
                                <h4 class="mt-3">Erreur d'initialisation</h4>
                                <p class="text-muted">
                                    L'application n'a pas pu s'initialiser correctement.
                                </p>
                                <button class="btn btn-primary" onclick="window.location.reload()">
                                    <i class="bi bi-arrow-clockwise"></i> Recharger la page
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        `;
    }
}

/**
 * Global theme toggle function
 */
window.toggleTheme = function() {
    if (window.app) {
        window.app.toggleTheme();
    }
};

/**
 * Global navigation function
 */
window.navigateTo = function(route) {
    window.location.hash = route;
};

// Initialize application when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    window.app = new EcomptaiaApp();
});

// Handle page visibility change
document.addEventListener('visibilitychange', () => {
    if (!document.hidden) {
        // Page became visible, refresh data if needed
        console.log('Page became visible');
    }
});

// Export app class
window.EcomptaiaApp = EcomptaiaApp;