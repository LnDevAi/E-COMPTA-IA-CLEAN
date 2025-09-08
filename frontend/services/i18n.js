// Internationalization service for E-COMPTA-IA
const I18n = {
    
    // Current language
    currentLanguage: 'fr',
    
    // Translation cache
    translations: {},
    
    /**
     * Initialize i18n service
     */
    async init() {
        // Get saved language or use default
        this.currentLanguage = Utils.Storage.get(CONFIG.STORAGE_KEYS.LANGUAGE, CONFIG.APP.DEFAULT_LANGUAGE);
        
        // Load translations
        await this.loadTranslations(this.currentLanguage);
        
        // Apply translations to the page
        this.applyTranslations();
        
        // Update language indicator
        this.updateLanguageIndicator();
    },
    
    /**
     * Load translations for a language
     */
    async loadTranslations(lang) {
        try {
            // In a real app, this would load from the server
            // For now, we'll use hardcoded translations
            this.translations = this.getTranslations(lang);
        } catch (error) {
            console.error('Failed to load translations:', error);
            // Fallback to default language
            if (lang !== 'fr') {
                this.translations = this.getTranslations('fr');
            }
        }
    },
    
    /**
     * Get translations object for a language
     */
    getTranslations(lang) {
        const translations = {
            fr: {
                // Navigation
                'nav.dashboard': 'Tableau de Bord',
                'nav.accounting': 'Comptabilité',
                'nav.chartOfAccounts': 'Plan Comptable',
                'nav.journalEntries': 'Écritures',
                'nav.generalLedger': 'Grand Livre',
                'nav.trialBalance': 'Balance',
                'nav.validation': 'Validation',
                'nav.reporting': 'Reporting',
                'nav.balanceSheet': 'Bilan',
                'nav.incomeStatement': 'Compte de Résultat',
                'nav.financialDashboard': 'Dashboard Financier',
                'nav.allReports': 'Tous les Rapports',
                'nav.thirdParties': 'Tiers',
                'nav.thirdPartiesList': 'Liste des Tiers',
                'nav.numbering': 'Numérotation',
                'nav.thirdPartyReporting': 'Reporting Tiers',
                'nav.hr': 'RH',
                'nav.employees': 'Employés',
                'nav.leaves': 'Congés',
                'nav.hrManagement': 'Gestion RH',
                'nav.assets': 'Assets',
                'nav.inventory': 'Inventaire',
                'nav.advancedAssets': 'Assets Avancés',
                'nav.assetReports': 'Rapports Assets',
                'nav.ai': 'IA',
                'nav.aiAssistant': 'Assistant IA',
                'nav.documentAnalysis': 'Analyse Documents',
                'nav.predictions': 'Prédictions',
                'nav.advancedAI': 'IA Avancée',
                'nav.international': 'International',
                'nav.localization': 'Localisation',
                'nav.currencies': 'Devises',
                'nav.exchangeRates': 'Taux de Change',
                'nav.intlAccounting': 'Comptabilité Internationale',
                'nav.more': 'Plus',
                'nav.tax': 'Fiscalité',
                'nav.documents': 'Documents',
                'nav.workflow': 'Workflow',
                'nav.audit': 'Audit',
                'nav.backup': 'Sauvegarde',
                'nav.monitoring': 'Monitoring',
                'nav.system': 'Système',
                'nav.account': 'Compte',
                'nav.profile': 'Profil',
                'nav.subscription': 'Abonnement',
                'nav.logout': 'Déconnexion',
                'nav.home': 'Accueil',
                
                // Footer
                'footer.help': 'Aide',
                'footer.support': 'Support',
                
                // Common
                'common.loading': 'Chargement...',
                'common.save': 'Enregistrer',
                'common.cancel': 'Annuler',
                'common.delete': 'Supprimer',
                'common.edit': 'Modifier',
                'common.view': 'Voir',
                'common.add': 'Ajouter',
                'common.search': 'Rechercher',
                'common.filter': 'Filtrer',
                'common.export': 'Exporter',
                'common.import': 'Importer',
                'common.print': 'Imprimer',
                'common.refresh': 'Actualiser',
                'common.yes': 'Oui',
                'common.no': 'Non',
                'common.ok': 'OK',
                'common.close': 'Fermer',
                'common.confirm': 'Confirmer',
                'common.success': 'Succès',
                'common.error': 'Erreur',
                'common.warning': 'Attention',
                'common.info': 'Information',
                'common.date': 'Date',
                'common.amount': 'Montant',
                'common.description': 'Description',
                'common.status': 'Statut',
                'common.actions': 'Actions',
                'common.total': 'Total',
                'common.subtotal': 'Sous-total',
                'common.name': 'Nom',
                'common.code': 'Code',
                'common.type': 'Type',
                'common.currency': 'Devise',
                'common.country': 'Pays',
                'common.language': 'Langue',
                
                // Dashboard
                'dashboard.title': 'Tableau de Bord',
                'dashboard.welcome': 'Bienvenue sur E-COMPTA-IA INTERNATIONAL',
                'dashboard.overview': 'Vue d\'ensemble',
                'dashboard.recentActivities': 'Activités récentes',
                'dashboard.quickActions': 'Actions rapides',
                'dashboard.kpis': 'Indicateurs clés',
                
                // Accounting
                'accounting.title': 'Comptabilité',
                'accounting.journalEntries': 'Écritures comptables',
                'accounting.createEntry': 'Créer une écriture',
                'accounting.entryNumber': 'Numéro d\'écriture',
                'accounting.entryDate': 'Date d\'écriture',
                'accounting.debit': 'Débit',
                'accounting.credit': 'Crédit',
                'accounting.account': 'Compte',
                'accounting.accountCode': 'Code compte',
                'accounting.accountName': 'Nom du compte',
                
                // Statuses
                'status.draft': 'Brouillon',
                'status.pending': 'En attente',
                'status.validated': 'Validé',
                'status.rejected': 'Rejeté',
                
                // Messages
                'message.saved': 'Enregistré avec succès',
                'message.deleted': 'Supprimé avec succès',
                'message.updated': 'Mis à jour avec succès',
                'message.error': 'Une erreur s\'est produite',
                'message.confirmDelete': 'Êtes-vous sûr de vouloir supprimer cet élément ?',
                'message.noData': 'Aucune donnée disponible',
                'message.loadingError': 'Erreur lors du chargement des données'
            },
            
            en: {
                // Navigation
                'nav.dashboard': 'Dashboard',
                'nav.accounting': 'Accounting',
                'nav.chartOfAccounts': 'Chart of Accounts',
                'nav.journalEntries': 'Journal Entries',
                'nav.generalLedger': 'General Ledger',
                'nav.trialBalance': 'Trial Balance',
                'nav.validation': 'Validation',
                'nav.reporting': 'Reporting',
                'nav.balanceSheet': 'Balance Sheet',
                'nav.incomeStatement': 'Income Statement',
                'nav.financialDashboard': 'Financial Dashboard',
                'nav.allReports': 'All Reports',
                'nav.thirdParties': 'Third Parties',
                'nav.thirdPartiesList': 'Third Parties List',
                'nav.numbering': 'Numbering',
                'nav.thirdPartyReporting': 'Third Party Reporting',
                'nav.hr': 'HR',
                'nav.employees': 'Employees',
                'nav.leaves': 'Leaves',
                'nav.hrManagement': 'HR Management',
                'nav.assets': 'Assets',
                'nav.inventory': 'Inventory',
                'nav.advancedAssets': 'Advanced Assets',
                'nav.assetReports': 'Asset Reports',
                'nav.ai': 'AI',
                'nav.aiAssistant': 'AI Assistant',
                'nav.documentAnalysis': 'Document Analysis',
                'nav.predictions': 'Predictions',
                'nav.advancedAI': 'Advanced AI',
                'nav.international': 'International',
                'nav.localization': 'Localization',
                'nav.currencies': 'Currencies',
                'nav.exchangeRates': 'Exchange Rates',
                'nav.intlAccounting': 'International Accounting',
                'nav.more': 'More',
                'nav.tax': 'Tax',
                'nav.documents': 'Documents',
                'nav.workflow': 'Workflow',
                'nav.audit': 'Audit',
                'nav.backup': 'Backup',
                'nav.monitoring': 'Monitoring',
                'nav.system': 'System',
                'nav.account': 'Account',
                'nav.profile': 'Profile',
                'nav.subscription': 'Subscription',
                'nav.logout': 'Logout',
                'nav.home': 'Home',
                
                // Common
                'common.loading': 'Loading...',
                'common.save': 'Save',
                'common.cancel': 'Cancel',
                'common.delete': 'Delete',
                'common.edit': 'Edit',
                'common.view': 'View',
                'common.add': 'Add',
                'common.search': 'Search',
                'common.filter': 'Filter',
                'common.export': 'Export',
                'common.import': 'Import',
                'common.print': 'Print',
                'common.refresh': 'Refresh',
                'common.yes': 'Yes',
                'common.no': 'No',
                'common.ok': 'OK',
                'common.close': 'Close',
                'common.confirm': 'Confirm',
                'common.success': 'Success',
                'common.error': 'Error',
                'common.warning': 'Warning',
                'common.info': 'Information',
                
                // Dashboard
                'dashboard.title': 'Dashboard',
                'dashboard.welcome': 'Welcome to E-COMPTA-IA INTERNATIONAL'
                
                // Add more English translations as needed
            },
            
            es: {
                // Navigation
                'nav.dashboard': 'Panel de Control',
                'nav.accounting': 'Contabilidad',
                'nav.chartOfAccounts': 'Plan de Cuentas',
                'nav.journalEntries': 'Asientos',
                'nav.generalLedger': 'Libro Mayor',
                'nav.trialBalance': 'Balance de Comprobación',
                'nav.validation': 'Validación',
                'nav.reporting': 'Informes',
                'nav.balanceSheet': 'Balance',
                'nav.incomeStatement': 'Estado de Resultados',
                'nav.financialDashboard': 'Panel Financiero',
                'nav.allReports': 'Todos los Informes',
                
                // Common
                'common.loading': 'Cargando...',
                'common.save': 'Guardar',
                'common.cancel': 'Cancelar',
                'common.delete': 'Eliminar',
                'common.edit': 'Editar',
                'common.view': 'Ver',
                'common.add': 'Agregar',
                
                // Dashboard
                'dashboard.title': 'Panel de Control',
                'dashboard.welcome': 'Bienvenido a E-COMPTA-IA INTERNATIONAL'
                
                // Add more Spanish translations as needed
            }
        };
        
        return translations[lang] || translations['fr'];
    },
    
    /**
     * Change language
     */
    async changeLanguage(lang) {
        if (this.currentLanguage === lang) return;
        
        this.currentLanguage = lang;
        Utils.Storage.set(CONFIG.STORAGE_KEYS.LANGUAGE, lang);
        
        await this.loadTranslations(lang);
        this.applyTranslations();
        this.updateLanguageIndicator();
        
        // Trigger language change event
        window.dispatchEvent(new CustomEvent('languageChange', { detail: { language: lang } }));
    },
    
    /**
     * Get translation for a key
     */
    t(key, defaultValue = key) {
        return this.translations[key] || defaultValue;
    },
    
    /**
     * Apply translations to DOM elements
     */
    applyTranslations() {
        const elements = document.querySelectorAll('[data-i18n]');
        
        elements.forEach(element => {
            const key = element.getAttribute('data-i18n');
            const translation = this.t(key);
            
            if (element.tagName === 'INPUT' && element.type === 'submit') {
                element.value = translation;
            } else if (element.tagName === 'INPUT' || element.tagName === 'TEXTAREA') {
                element.placeholder = translation;
            } else {
                element.textContent = translation;
            }
        });
    },
    
    /**
     * Update language indicator in UI
     */
    updateLanguageIndicator() {
        const indicator = document.getElementById('currentLang');
        if (indicator) {
            const lang = CONFIG.LANGUAGES.find(l => l.code === this.currentLanguage);
            indicator.textContent = lang ? lang.code.toUpperCase() : 'FR';
        }
    },
    
    /**
     * Format number with localization
     */
    formatNumber(number, options = {}) {
        const locale = this.getLocale();
        return new Intl.NumberFormat(locale, options).format(number);
    },
    
    /**
     * Format currency with localization
     */
    formatCurrency(number, currency = 'XOF') {
        const locale = this.getLocale();
        return new Intl.NumberFormat(locale, {
            style: 'currency',
            currency: currency
        }).format(number);
    },
    
    /**
     * Format date with localization
     */
    formatDate(date, options = {}) {
        const locale = this.getLocale();
        return new Intl.DateTimeFormat(locale, options).format(new Date(date));
    },
    
    /**
     * Get locale for current language
     */
    getLocale() {
        const locales = {
            'fr': 'fr-FR',
            'en': 'en-US',
            'es': 'es-ES'
        };
        return locales[this.currentLanguage] || 'fr-FR';
    }
};

/**
 * Global function to change language
 */
window.changeLanguage = function(lang) {
    I18n.changeLanguage(lang);
};

// Export I18n service
window.I18n = I18n;