export const environment = {
  production: true,
  apiUrl: 'https://api.ecomptaia.com',
  appName: 'E-COMPTA-IA',
  version: '1.0.0',
  features: {
    ai: true,
    inventory: true,
    payroll: true,
    documentManagement: true,
    externalIntegrations: true,
    governmentPlatforms: true,
    advancedSecurity: true
  },
  countries: {
    supported: ['BF', 'CI', 'SN', 'CMR', 'TG', 'NE', 'ML', 'GN', 'GW', 'CV', 'MR', 'TD', 'CF', 'CG', 'CD', 'GA', 'GQ', 'ST'],
    default: 'BF'
  },
  currencies: {
    supported: ['XOF', 'XAF', 'EUR', 'USD'],
    default: 'XOF'
  },
  accountingStandards: {
    supported: ['SYSCOHADA', 'IFRS', 'GAAP', 'PCG'],
    default: 'SYSCOHADA'
  }
};