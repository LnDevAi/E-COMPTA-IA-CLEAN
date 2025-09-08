// Audit module for E-COMPTA-IA
const Audit = {
    
    /**
     * Initialize audit module
     */
    async init(subModule = '') {
        UI.createBreadcrumb([
            { label: I18n.t('nav.home'), href: '#dashboard' },
            { label: 'Audit' }
        ]);
        
        UI.updateContent(`
            <div class="row">
                <div class="col-12">
                    <h1 class="h3 mb-4">Audit</h1>
                    <div class="alert alert-info">
                        <i class="bi bi-info-circle me-2"></i>
                        Cette fonctionnalité sera bientôt disponible.
                    </div>
                </div>
            </div>
        `);
    }
};

// Export Audit module
window.Audit = Audit;
