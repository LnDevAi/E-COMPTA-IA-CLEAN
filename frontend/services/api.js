// API Service for E-COMPTA-IA
const API = {
    
    // Base configuration
    baseURL: CONFIG.API.BASE_URL,
    timeout: CONFIG.API.TIMEOUT,
    
    // Authentication token
    authToken: null,
    
    /**
     * Initialize API service
     */
    init() {
        this.authToken = Utils.Storage.get(CONFIG.STORAGE_KEYS.AUTH_TOKEN);
    },
    
    /**
     * Set authentication token
     */
    setAuthToken(token) {
        this.authToken = token;
        Utils.Storage.set(CONFIG.STORAGE_KEYS.AUTH_TOKEN, token);
    },
    
    /**
     * Clear authentication token
     */
    clearAuthToken() {
        this.authToken = null;
        Utils.Storage.remove(CONFIG.STORAGE_KEYS.AUTH_TOKEN);
    },
    
    /**
     * Make HTTP request
     */
    async request(path, options = {}) {
        const url = `${this.baseURL}/api${path}`;
        
        const defaultOptions = {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        };
        
        // Add authentication header if token exists
        if (this.authToken) {
            defaultOptions.headers.Authorization = `Bearer ${this.authToken}`;
        }
        
        const config = { ...defaultOptions, ...options };
        
        // Merge headers
        if (options.headers) {
            config.headers = { ...defaultOptions.headers, ...options.headers };
        }
        
        try {
            const response = await fetch(url, config);
            
            // Handle non-JSON responses
            const contentType = response.headers.get('content-type');
            let data;
            
            if (contentType && contentType.includes('application/json')) {
                data = await response.json();
            } else {
                data = await response.text();
            }
            
            if (!response.ok) {
                throw new Error(data.message || `HTTP ${response.status}: ${response.statusText}`);
            }
            
            return data;
        } catch (error) {
            console.error('API Request failed:', error);
            throw error;
        }
    },
    
    /**
     * GET request
     */
    async get(path, params = {}) {
        const searchParams = new URLSearchParams(params);
        const url = searchParams.toString() ? `${path}?${searchParams}` : path;
        return this.request(url);
    },
    
    /**
     * POST request
     */
    async post(path, data = {}) {
        return this.request(path, {
            method: 'POST',
            body: JSON.stringify(data)
        });
    },
    
    /**
     * PUT request
     */
    async put(path, data = {}) {
        return this.request(path, {
            method: 'PUT',
            body: JSON.stringify(data)
        });
    },
    
    /**
     * DELETE request
     */
    async delete(path) {
        return this.request(path, {
            method: 'DELETE'
        });
    },
    
    // =====================================================
    // DASHBOARD ENDPOINTS
    // =====================================================
    
    Dashboard: {
        async getTestData() {
            return API.get('/dashboard/test');
        },
        
        async getAnalytics(companyId = 1) {
            return API.get('/dashboard/analytics', { companyId });
        },
        
        async getPerformance(companyId = 1) {
            return API.get('/dashboard/performance', { companyId });
        },
        
        async getFinancial(companyId = 1) {
            return API.get('/dashboard/financial', { companyId });
        }
    },
    
    // =====================================================
    // ACCOUNTING ENDPOINTS
    // =====================================================
    
    Accounting: {
        // Journal Entries
        async getJournalEntries(params = {}) {
            return API.get('/accounting/journal-entries', params);
        },
        
        async createJournalEntry(data) {
            return API.post('/accounting/journal-entries', data);
        },
        
        async updateJournalEntry(id, data) {
            return API.put(`/accounting/journal-entries/${id}`, data);
        },
        
        async deleteJournalEntry(id) {
            return API.delete(`/accounting/journal-entries/${id}`);
        },
        
        async validateJournalEntry(id) {
            return API.post(`/accounting/journal-entries/${id}/validate`);
        },
        
        // Chart of Accounts
        async getChartOfAccounts() {
            return API.get('/accounting/chart-of-accounts');
        },
        
        async createAccount(data) {
            return API.post('/accounting/accounts', data);
        },
        
        async updateAccount(id, data) {
            return API.put(`/accounting/accounts/${id}`, data);
        },
        
        async deleteAccount(id) {
            return API.delete(`/accounting/accounts/${id}`);
        }
    },
    
    // =====================================================
    // REPORTING ENDPOINTS
    // =====================================================
    
    Reporting: {
        async getTestData() {
            return API.get('/reporting/test');
        },
        
        async getBalanceSheet(params = {}) {
            return API.get('/reporting/balance-sheet', params);
        },
        
        async getIncomeStatement(params = {}) {
            return API.get('/reporting/income-statement', params);
        },
        
        async getTrialBalance(params = {}) {
            return API.get('/reporting/trial-balance', params);
        },
        
        async getGeneralLedger(params = {}) {
            return API.get('/reporting/general-ledger', params);
        },
        
        async getAllReports() {
            return API.get('/reporting/all-reports');
        },
        
        async getStatistics() {
            return API.get('/reporting/statistics');
        }
    },
    
    // =====================================================
    // THIRD PARTIES ENDPOINTS
    // =====================================================
    
    ThirdParties: {
        async getList(params = {}) {
            return API.get('/third-parties', params);
        },
        
        async create(data) {
            return API.post('/third-parties', data);
        },
        
        async update(id, data) {
            return API.put(`/third-parties/${id}`, data);
        },
        
        async delete(id) {
            return API.delete(`/third-parties/${id}`);
        },
        
        async getNumbering() {
            return API.get('/third-party-numbering');
        },
        
        async getReporting() {
            return API.get('/third-party-reporting');
        }
    },
    
    // =====================================================
    // HR ENDPOINTS
    // =====================================================
    
    HR: {
        // Employees
        async getEmployees(params = {}) {
            return API.get('/hr/employees', params);
        },
        
        async createEmployee(data) {
            return API.post('/hr/employees', data);
        },
        
        async updateEmployee(id, data) {
            return API.put(`/hr/employees/${id}`, data);
        },
        
        async deleteEmployee(id) {
            return API.delete(`/hr/employees/${id}`);
        },
        
        // Leaves
        async getLeaves(params = {}) {
            return API.get('/hr/leaves', params);
        },
        
        async createLeave(data) {
            return API.post('/hr/leaves', data);
        },
        
        async updateLeave(id, data) {
            return API.put(`/hr/leaves/${id}`, data);
        },
        
        async deleteLeave(id) {
            return API.delete(`/hr/leaves/${id}`);
        },
        
        // Management
        async getManagementData() {
            return API.get('/hr/management');
        }
    },
    
    // =====================================================
    // ASSETS ENDPOINTS
    // =====================================================
    
    Assets: {
        async getInventory(params = {}) {
            return API.get('/asset-inventory', params);
        },
        
        async createAsset(data) {
            return API.post('/asset-inventory', data);
        },
        
        async updateAsset(id, data) {
            return API.put(`/asset-inventory/${id}`, data);
        },
        
        async deleteAsset(id) {
            return API.delete(`/asset-inventory/${id}`);
        },
        
        async getAdvancedAssets() {
            return API.get('/asset-inventory-advanced');
        },
        
        async getReports() {
            return API.get('/asset-inventory-reports');
        }
    },
    
    // =====================================================
    // AI ENDPOINTS
    // =====================================================
    
    AI: {
        async getAssistant() {
            return API.get('/ai');
        },
        
        async analyzeDocument(data) {
            return API.post('/ai-document-analysis', data);
        },
        
        async getPredictions() {
            return API.get('/ai-financial-prediction');
        },
        
        async getAdvancedAI() {
            return API.get('/advanced-ai');
        }
    },
    
    // =====================================================
    // INTERNATIONAL ENDPOINTS
    // =====================================================
    
    International: {
        async getLocalization() {
            return API.get('/localization');
        },
        
        async getCurrencies() {
            return API.get('/currency');
        },
        
        async getExchangeRates() {
            return API.get('/exchange-rates');
        },
        
        async getAccountingStandards() {
            return API.get('/international-accounting');
        },
        
        async getInternationalData() {
            return API.get('/international');
        }
    },
    
    // =====================================================
    // TAX ENDPOINTS
    // =====================================================
    
    Tax: {
        async getTaxData() {
            return API.get('/tax');
        },
        
        async getTaxAndSocial() {
            return API.get('/tax-and-social');
        }
    },
    
    // =====================================================
    // SYSTEM ENDPOINTS
    // =====================================================
    
    System: {
        async getHealth() {
            return API.get('/actuator/health');
        },
        
        async getSystemOverview() {
            return API.get('/system');
        },
        
        async getMonitoring() {
            return API.get('/monitoring');
        },
        
        async getBackup() {
            return API.get('/backup');
        },
        
        async getAudit() {
            return API.get('/audit');
        },
        
        async getWorkflow() {
            return API.get('/workflow');
        },
        
        async getDocuments() {
            return API.get('/document-management');
        },
        
        async getExternalIntegrations() {
            return API.get('/external-integration');
        },
        
        async getSubscription() {
            return API.get('/subscription');
        }
    }
};

// Initialize API service
API.init();

// Export API for global use
window.API = API;