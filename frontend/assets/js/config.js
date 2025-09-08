// Configuration settings for E-COMPTA-IA
const CONFIG = {
    // API configuration
    API: {
        BASE_URL: window.location.hostname === 'localhost' ? 'http://localhost:8080' : '',
        TIMEOUT: 30000,
        RETRY_ATTEMPTS: 3
    },
    
    // Application settings
    APP: {
        NAME: 'E-COMPTA-IA INTERNATIONAL',
        VERSION: '1.0.0',
        DEFAULT_LANGUAGE: 'fr',
        DEFAULT_CURRENCY: 'XOF',
        DEFAULT_ACCOUNTING_STANDARD: 'SYSCOHADA'
    },
    
    // UI settings
    UI: {
        ITEMS_PER_PAGE: 25,
        DEBOUNCE_DELAY: 300,
        ANIMATION_DURATION: 300,
        TOAST_DURATION: 4000
    },
    
    // Supported languages
    LANGUAGES: [
        { code: 'fr', name: 'Français', flag: '🇫🇷' },
        { code: 'en', name: 'English', flag: '🇬🇧' },
        { code: 'es', name: 'Español', flag: '🇪🇸' }
    ],
    
    // Supported currencies
    CURRENCIES: [
        { code: 'XOF', name: 'Franc CFA BCEAO', symbol: 'CFA' },
        { code: 'XAF', name: 'Franc CFA BEAC', symbol: 'FCFA' },
        { code: 'EUR', name: 'Euro', symbol: '€' },
        { code: 'USD', name: 'US Dollar', symbol: '$' },
        { code: 'GBP', name: 'British Pound', symbol: '£' }
    ],
    
    // Accounting standards
    ACCOUNTING_STANDARDS: [
        { code: 'SYSCOHADA', name: 'SYSCOHADA', description: 'Système Comptable OHADA' },
        { code: 'IFRS', name: 'IFRS', description: 'International Financial Reporting Standards' },
        { code: 'GAAP', name: 'US GAAP', description: 'Generally Accepted Accounting Principles' },
        { code: 'PCG_FR', name: 'PCG France', description: 'Plan Comptable Général Français' }
    ],
    
    // Countries
    COUNTRIES: [
        { code: 'SN', name: 'Sénégal', flag: '🇸🇳', currency: 'XOF', standard: 'SYSCOHADA' },
        { code: 'CI', name: 'Côte d\'Ivoire', flag: '🇨🇮', currency: 'XOF', standard: 'SYSCOHADA' },
        { code: 'BF', name: 'Burkina Faso', flag: '🇧🇫', currency: 'XOF', standard: 'SYSCOHADA' },
        { code: 'ML', name: 'Mali', flag: '🇲🇱', currency: 'XOF', standard: 'SYSCOHADA' },
        { code: 'NE', name: 'Niger', flag: '🇳🇪', currency: 'XOF', standard: 'SYSCOHADA' },
        { code: 'TG', name: 'Togo', flag: '🇹🇬', currency: 'XOF', standard: 'SYSCOHADA' },
        { code: 'BJ', name: 'Bénin', flag: '🇧🇯', currency: 'XOF', standard: 'SYSCOHADA' },
        { code: 'CM', name: 'Cameroun', flag: '🇨🇲', currency: 'XAF', standard: 'SYSCOHADA' },
        { code: 'GA', name: 'Gabon', flag: '🇬🇦', currency: 'XAF', standard: 'SYSCOHADA' },
        { code: 'FR', name: 'France', flag: '🇫🇷', currency: 'EUR', standard: 'PCG_FR' },
        { code: 'US', name: 'United States', flag: '🇺🇸', currency: 'USD', standard: 'GAAP' },
        { code: 'GB', name: 'United Kingdom', flag: '🇬🇧', currency: 'GBP', standard: 'IFRS' }
    ],
    
    // Module configurations
    MODULES: {
        dashboard: { name: 'Dashboard', icon: 'speedometer2', enabled: true },
        accounting: { name: 'Accounting', icon: 'calculator', enabled: true },
        reporting: { name: 'Reporting', icon: 'graph-up', enabled: true },
        thirdParties: { name: 'Third Parties', icon: 'people', enabled: true },
        hr: { name: 'Human Resources', icon: 'person-workspace', enabled: true },
        assets: { name: 'Assets', icon: 'building', enabled: true },
        ai: { name: 'Artificial Intelligence', icon: 'robot', enabled: true },
        international: { name: 'International', icon: 'globe', enabled: true },
        tax: { name: 'Tax & Fiscal', icon: 'receipt', enabled: true },
        documents: { name: 'Document Management', icon: 'file-earmark', enabled: true },
        workflow: { name: 'Workflow', icon: 'diagram-3', enabled: true },
        audit: { name: 'Audit & Compliance', icon: 'shield-check', enabled: true },
        backup: { name: 'Backup & Recovery', icon: 'cloud-download', enabled: true },
        monitoring: { name: 'Monitoring', icon: 'activity', enabled: true },
        system: { name: 'System Administration', icon: 'gear', enabled: true }
    },
    
    // Local storage keys
    STORAGE_KEYS: {
        LANGUAGE: 'ecompta_language',
        THEME: 'ecompta_theme',
        USER_PREFERENCES: 'ecompta_user_preferences',
        AUTH_TOKEN: 'ecompta_auth_token',
        LAST_ROUTE: 'ecompta_last_route'
    },
    
    // Theme options
    THEMES: {
        LIGHT: 'light',
        DARK: 'dark'
    },
    
    // Date and number formats
    FORMATS: {
        DATE: 'DD/MM/YYYY',
        DATETIME: 'DD/MM/YYYY HH:mm',
        NUMBER: '0,0.00',
        CURRENCY: '0,0.00'
    },
    
    // Status types
    STATUS_TYPES: {
        SUCCESS: 'success',
        ERROR: 'danger',
        WARNING: 'warning',
        INFO: 'info'
    },
    
    // Entry statuses
    ENTRY_STATUSES: [
        { value: 'DRAFT', label: 'Brouillon', color: 'secondary' },
        { value: 'PENDING', label: 'En attente', color: 'warning' },
        { value: 'VALIDATED', label: 'Validé', color: 'success' },
        { value: 'REJECTED', label: 'Rejeté', color: 'danger' }
    ]
};

// Export configuration for use in other modules
window.CONFIG = CONFIG;