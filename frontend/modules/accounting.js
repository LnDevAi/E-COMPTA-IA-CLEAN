// Accounting module for E-COMPTA-IA
const Accounting = {
    
    currentPage: 1,
    itemsPerPage: CONFIG.UI.ITEMS_PER_PAGE,
    
    /**
     * Initialize accounting module
     */
    async init(subModule = 'journal-entries') {
        switch (subModule) {
            case 'journal-entries':
                await this.initJournalEntries();
                break;
            case 'chart-of-accounts':
                await this.initChartOfAccounts();
                break;
            case 'general-ledger':
                await this.initGeneralLedger();
                break;
            case 'trial-balance':
                await this.initTrialBalance();
                break;
            case 'validation':
                await this.initValidation();
                break;
            default:
                await this.initJournalEntries();
        }
    },
    
    /**
     * Initialize journal entries
     */
    async initJournalEntries() {
        UI.createBreadcrumb([
            { label: I18n.t('nav.home'), href: '#dashboard' },
            { label: I18n.t('nav.accounting'), href: '#accounting' },
            { label: I18n.t('nav.journalEntries') }
        ]);
        
        try {
            UI.showLoading();
            await this.loadJournalEntries();
        } catch (error) {
            console.error('Journal entries loading error:', error);
            UI.updateContent(UI.createErrorState(
                I18n.t('message.loadingError'),
                'Accounting.initJournalEntries'
            ));
        } finally {
            UI.hideLoading();
        }
    },
    
    /**
     * Load journal entries
     */
    async loadJournalEntries() {
        const data = await API.Accounting.getJournalEntries({
            page: this.currentPage,
            size: this.itemsPerPage
        });
        
        const entries = data.data || data.entries || [];
        const total = data.total || entries.length;
        
        const content = `
            <div class="row">
                <div class="col-12">
                    <div class="d-flex justify-content-between align-items-center mb-4">
                        <div>
                            <h1 class="h3 mb-1">${I18n.t('accounting.journalEntries')}</h1>
                            <p class="text-muted mb-0">${total} écriture${total > 1 ? 's' : ''} au total</p>
                        </div>
                        <div>
                            ${UI.createActionButtons([
                                {
                                    label: I18n.t('accounting.createEntry'),
                                    icon: 'plus',
                                    className: 'btn btn-primary',
                                    onclick: 'Accounting.showCreateEntryModal()'
                                },
                                {
                                    label: I18n.t('common.refresh'),
                                    icon: 'arrow-clockwise',
                                    className: 'btn btn-outline-secondary',
                                    onclick: 'Accounting.initJournalEntries()'
                                }
                            ])}
                        </div>
                    </div>
                </div>
            </div>
            
            <div class="row">
                <div class="col-12">
                    ${this.createJournalEntriesTable(entries)}
                </div>
            </div>
        `;
        
        UI.updateContent(content);
    },
    
    /**
     * Create journal entries table
     */
    createJournalEntriesTable(entries) {
        if (!entries || entries.length === 0) {
            return UI.createCard(
                I18n.t('accounting.journalEntries'),
                UI.createEmptyState(
                    'Aucune écriture comptable',
                    'Commencez par créer votre première écriture comptable.',
                    'journal-text',
                    {
                        label: I18n.t('accounting.createEntry'),
                        icon: 'plus',
                        onclick: 'Accounting.showCreateEntryModal()'
                    }
                )
            );
        }
        
        const headers = [
            I18n.t('accounting.entryDate'),
            I18n.t('accounting.entryNumber'),
            I18n.t('common.description'),
            I18n.t('accounting.debit'),
            I18n.t('accounting.credit'),
            I18n.t('common.status'),
            I18n.t('common.actions')
        ];
        
        const rows = entries.map(entry => [
            Utils.Date.format(entry.entryDate),
            entry.entryNumber || entry.id,
            Utils.String.truncate(entry.description || '', 50),
            `<span class="text-end d-block">${Utils.Number.formatCurrency(entry.totalDebit || 0, entry.currency)}</span>`,
            `<span class="text-end d-block">${Utils.Number.formatCurrency(entry.totalCredit || 0, entry.currency)}</span>`,
            UI.createStatusBadge(entry.status),
            this.createEntryActions(entry)
        ]);
        
        return UI.createCard(
            I18n.t('accounting.journalEntries'),
            UI.createDataTable(headers, rows, { responsive: true, striped: true })
        );
    },
    
    /**
     * Create actions for journal entry
     */
    createEntryActions(entry) {
        const actions = [];
        
        // View action
        actions.push(`
            <button type="button" class="btn btn-sm btn-outline-primary" onclick="Accounting.viewEntry(${entry.id})" title="${I18n.t('common.view')}">
                <i class="bi bi-eye"></i>
            </button>
        `);
        
        // Edit action (if not validated)
        if (entry.status !== 'VALIDATED' && entry.status !== 'VALIDÉ') {
            actions.push(`
                <button type="button" class="btn btn-sm btn-outline-secondary" onclick="Accounting.editEntry(${entry.id})" title="${I18n.t('common.edit')}">
                    <i class="bi bi-pencil"></i>
                </button>
            `);
        }
        
        // Validate action (if pending)
        if (entry.status === 'PENDING' || entry.status === 'DRAFT' || entry.status === 'BROUILLON') {
            actions.push(`
                <button type="button" class="btn btn-sm btn-success" onclick="Accounting.validateEntry(${entry.id})" title="Valider">
                    <i class="bi bi-check-circle"></i>
                </button>
            `);
        }
        
        // Delete action (if not validated)
        if (entry.status !== 'VALIDATED' && entry.status !== 'VALIDÉ') {
            actions.push(`
                <button type="button" class="btn btn-sm btn-outline-danger" onclick="Accounting.deleteEntry(${entry.id})" title="${I18n.t('common.delete')}">
                    <i class="bi bi-trash"></i>
                </button>
            `);
        }
        
        return `<div class="btn-group" role="group">${actions.join('')}</div>`;
    },
    
    /**
     * Show create entry modal
     */
    async showCreateEntryModal() {
        const modalId = 'createEntryModal';
        const modalHtml = `
            <div class="modal fade" id="${modalId}" tabindex="-1">
                <div class="modal-dialog modal-lg">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title">${I18n.t('accounting.createEntry')}</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <form id="createEntryForm">
                                ${UI.createFormField('date', 'entryDate', I18n.t('accounting.entryDate'), '', { required: true })}
                                ${UI.createFormField('text', 'description', I18n.t('common.description'), '', { required: true })}
                                ${UI.createFormField('select', 'journalType', 'Type de journal', '', {
                                    required: true,
                                    options: [
                                        { value: '', label: 'Sélectionner...' },
                                        { value: 'VENTES', label: 'Ventes' },
                                        { value: 'ACHATS', label: 'Achats' },
                                        { value: 'BANQUE', label: 'Banque' },
                                        { value: 'CAISSE', label: 'Caisse' },
                                        { value: 'OD', label: 'Opérations diverses' }
                                    ]
                                })}
                                
                                <h6 class="mt-4 mb-3">Lignes d'écriture</h6>
                                <div id="entryLines">
                                    <div class="row entry-line mb-3">
                                        <div class="col-md-4">
                                            ${UI.createFormField('text', 'accountCode[]', I18n.t('accounting.accountCode'), '', { required: true })}
                                        </div>
                                        <div class="col-md-4">
                                            ${UI.createFormField('number', 'debit[]', I18n.t('accounting.debit'), '', { step: '0.01' })}
                                        </div>
                                        <div class="col-md-4">
                                            ${UI.createFormField('number', 'credit[]', I18n.t('accounting.credit'), '', { step: '0.01' })}
                                        </div>
                                    </div>
                                </div>
                                
                                <button type="button" class="btn btn-outline-secondary" onclick="Accounting.addEntryLine()">
                                    <i class="bi bi-plus"></i> Ajouter une ligne
                                </button>
                            </form>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">${I18n.t('common.cancel')}</button>
                            <button type="button" class="btn btn-primary" onclick="Accounting.createEntry()">${I18n.t('common.save')}</button>
                        </div>
                    </div>
                </div>
            </div>
        `;
        
        // Remove existing modal if any
        const existingModal = document.getElementById(modalId);
        if (existingModal) {
            existingModal.remove();
        }
        
        document.body.insertAdjacentHTML('beforeend', modalHtml);
        const modal = new bootstrap.Modal(document.getElementById(modalId));
        modal.show();
        
        // Set today's date as default
        const today = new Date().toISOString().split('T')[0];
        document.querySelector('input[name="entryDate"]').value = today;
    },
    
    /**
     * Add entry line
     */
    addEntryLine() {
        const container = document.getElementById('entryLines');
        const lineHtml = `
            <div class="row entry-line mb-3">
                <div class="col-md-4">
                    ${UI.createFormField('text', 'accountCode[]', I18n.t('accounting.accountCode'), '', { required: true })}
                </div>
                <div class="col-md-3">
                    ${UI.createFormField('number', 'debit[]', I18n.t('accounting.debit'), '', { step: '0.01' })}
                </div>
                <div class="col-md-3">
                    ${UI.createFormField('number', 'credit[]', I18n.t('accounting.credit'), '', { step: '0.01' })}
                </div>
                <div class="col-md-2 d-flex align-items-end">
                    <button type="button" class="btn btn-outline-danger mb-3" onclick="this.closest('.entry-line').remove()">
                        <i class="bi bi-trash"></i>
                    </button>
                </div>
            </div>
        `;
        container.insertAdjacentHTML('beforeend', lineHtml);
    },
    
    /**
     * Create entry
     */
    async createEntry() {
        const form = document.getElementById('createEntryForm');
        const formData = new FormData(form);
        
        try {
            const entryData = {
                entryDate: formData.get('entryDate'),
                description: formData.get('description'),
                journalType: formData.get('journalType'),
                lines: []
            };
            
            // Get all account codes, debits, and credits
            const accountCodes = formData.getAll('accountCode[]');
            const debits = formData.getAll('debit[]');
            const credits = formData.getAll('credit[]');
            
            // Create lines
            for (let i = 0; i < accountCodes.length; i++) {
                if (accountCodes[i]) {
                    entryData.lines.push({
                        accountCode: accountCodes[i],
                        debit: debits[i] ? parseFloat(debits[i]) : 0,
                        credit: credits[i] ? parseFloat(credits[i]) : 0
                    });
                }
            }
            
            // Validate entry balance
            const totalDebit = entryData.lines.reduce((sum, line) => sum + line.debit, 0);
            const totalCredit = entryData.lines.reduce((sum, line) => sum + line.credit, 0);
            
            if (Math.abs(totalDebit - totalCredit) > 0.01) {
                UI.showToast('L\'écriture doit être équilibrée (débit = crédit)', 'error');
                return;
            }
            
            await API.Accounting.createJournalEntry(entryData);
            
            // Close modal
            const modal = bootstrap.Modal.getInstance(document.getElementById('createEntryModal'));
            modal.hide();
            
            UI.showToast('Écriture créée avec succès', 'success');
            await this.loadJournalEntries();
            
        } catch (error) {
            console.error('Create entry error:', error);
            UI.showToast(error.message || 'Erreur lors de la création', 'error');
        }
    },
    
    /**
     * View entry
     */
    async viewEntry(id) {
        // Implementation for viewing entry details
        UI.showToast(`Affichage de l'écriture ${id}`, 'info');
    },
    
    /**
     * Edit entry
     */
    async editEntry(id) {
        // Implementation for editing entry
        UI.showToast(`Modification de l'écriture ${id}`, 'info');
    },
    
    /**
     * Validate entry
     */
    async validateEntry(id) {
        try {
            const confirmed = await UI.showConfirm(
                'Êtes-vous sûr de vouloir valider cette écriture ? Cette action est irréversible.',
                'Valider l\'écriture'
            );
            
            if (confirmed) {
                await API.Accounting.validateJournalEntry(id);
                UI.showToast('Écriture validée avec succès', 'success');
                await this.loadJournalEntries();
            }
        } catch (error) {
            console.error('Validate entry error:', error);
            UI.showToast(error.message || 'Erreur lors de la validation', 'error');
        }
    },
    
    /**
     * Delete entry
     */
    async deleteEntry(id) {
        try {
            const confirmed = await UI.showConfirm(
                I18n.t('message.confirmDelete'),
                'Supprimer l\'écriture'
            );
            
            if (confirmed) {
                await API.Accounting.deleteJournalEntry(id);
                UI.showToast('Écriture supprimée avec succès', 'success');
                await this.loadJournalEntries();
            }
        } catch (error) {
            console.error('Delete entry error:', error);
            UI.showToast(error.message || 'Erreur lors de la suppression', 'error');
        }
    },
    
    /**
     * Initialize chart of accounts
     */
    async initChartOfAccounts() {
        UI.createBreadcrumb([
            { label: I18n.t('nav.home'), href: '#dashboard' },
            { label: I18n.t('nav.accounting'), href: '#accounting' },
            { label: I18n.t('nav.chartOfAccounts') }
        ]);
        
        UI.updateContent(`
            <div class="row">
                <div class="col-12">
                    <h1 class="h3 mb-4">${I18n.t('nav.chartOfAccounts')}</h1>
                    <div class="alert alert-info">
                        <i class="bi bi-info-circle me-2"></i>
                        Cette fonctionnalité sera bientôt disponible.
                    </div>
                </div>
            </div>
        `);
    },
    
    /**
     * Initialize general ledger
     */
    async initGeneralLedger() {
        UI.createBreadcrumb([
            { label: I18n.t('nav.home'), href: '#dashboard' },
            { label: I18n.t('nav.accounting'), href: '#accounting' },
            { label: I18n.t('nav.generalLedger') }
        ]);
        
        UI.updateContent(`
            <div class="row">
                <div class="col-12">
                    <h1 class="h3 mb-4">${I18n.t('nav.generalLedger')}</h1>
                    <div class="alert alert-info">
                        <i class="bi bi-info-circle me-2"></i>
                        Cette fonctionnalité sera bientôt disponible.
                    </div>
                </div>
            </div>
        `);
    },
    
    /**
     * Initialize trial balance
     */
    async initTrialBalance() {
        UI.createBreadcrumb([
            { label: I18n.t('nav.home'), href: '#dashboard' },
            { label: I18n.t('nav.accounting'), href: '#accounting' },
            { label: I18n.t('nav.trialBalance') }
        ]);
        
        UI.updateContent(`
            <div class="row">
                <div class="col-12">
                    <h1 class="h3 mb-4">${I18n.t('nav.trialBalance')}</h1>
                    <div class="alert alert-info">
                        <i class="bi bi-info-circle me-2"></i>
                        Cette fonctionnalité sera bientôt disponible.
                    </div>
                </div>
            </div>
        `);
    },
    
    /**
     * Initialize validation
     */
    async initValidation() {
        UI.createBreadcrumb([
            { label: I18n.t('nav.home'), href: '#dashboard' },
            { label: I18n.t('nav.accounting'), href: '#accounting' },
            { label: I18n.t('nav.validation') }
        ]);
        
        UI.updateContent(`
            <div class="row">
                <div class="col-12">
                    <h1 class="h3 mb-4">${I18n.t('nav.validation')}</h1>
                    <div class="alert alert-info">
                        <i class="bi bi-info-circle me-2"></i>
                        Cette fonctionnalité sera bientôt disponible.
                    </div>
                </div>
            </div>
        `);
    }
};

// Export Accounting module
window.Accounting = Accounting;