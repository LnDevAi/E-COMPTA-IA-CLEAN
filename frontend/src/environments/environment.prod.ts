export const environment = {
  production: true,
  apiUrl: 'http://localhost:8080/api',
  appName: 'E-COMPTA-IA',
  appVersion: '1.0.0',
  defaultLanguage: 'fr',
  supportedLanguages: ['fr', 'en', 'es', 'ar'],
  dateFormat: 'dd/MM/yyyy',
  timeFormat: 'HH:mm',
  currency: 'XOF',
  timezone: 'Africa/Abidjan',
  pagination: {
    defaultPageSize: 20,
    pageSizeOptions: [10, 20, 50, 100]
  },
  upload: {
    maxFileSize: 10 * 1024 * 1024, // 10MB
    allowedTypes: ['.pdf', '.doc', '.docx', '.xls', '.xlsx', '.jpg', '.jpeg', '.png']
  },
  features: {
    ai: true,
    multiLanguage: true,
    darkMode: true,
    notifications: true,
    export: true,
    import: true
  },
  security: {
    tokenExpirationWarning: 5 * 60 * 1000, // 5 minutes
    sessionTimeout: 30 * 60 * 1000, // 30 minutes
    maxLoginAttempts: 5,
    lockoutDuration: 15 * 60 * 1000 // 15 minutes
  }
};


