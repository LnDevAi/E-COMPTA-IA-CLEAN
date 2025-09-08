// UI Components service for E-COMPTA-IA
const UI = {
    
    /**
     * Show loading indicator
     */
    showLoading() {
        const indicator = Utils.DOM.select('#loadingIndicator');
        if (indicator) {
            Utils.DOM.show(indicator);
        }
    },
    
    /**
     * Hide loading indicator
     */
    hideLoading() {
        const indicator = Utils.DOM.select('#loadingIndicator');
        if (indicator) {
            Utils.DOM.hide(indicator);
        }
    },
    
    /**
     * Show toast notification
     */
    showToast(message, type = 'info', duration = CONFIG.UI.TOAST_DURATION) {
        const container = Utils.DOM.select('#alertContainer');
        if (!container) return;
        
        const alertId = `alert-${Utils.String.random()}`;
        const alertClass = `alert-${type === 'error' ? 'danger' : type}`;
        
        const alertHtml = `
            <div id="${alertId}" class="alert ${alertClass} alert-dismissible fade show" role="alert">
                <i class="bi bi-${this.getAlertIcon(type)}"></i>
                ${Utils.String.escapeHtml(message)}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        `;
        
        container.insertAdjacentHTML('beforeend', alertHtml);
        
        // Auto dismiss after duration
        setTimeout(() => {
            const alert = Utils.DOM.select(`#${alertId}`);
            if (alert) {
                const bsAlert = new bootstrap.Alert(alert);
                bsAlert.close();
            }
        }, duration);
    },
    
    /**
     * Get icon for alert type
     */
    getAlertIcon(type) {
        const icons = {
            success: 'check-circle',
            error: 'exclamation-triangle',
            warning: 'exclamation-triangle',
            info: 'info-circle'
        };
        return icons[type] || 'info-circle';
    },
    
    /**
     * Show confirmation dialog
     */
    async showConfirm(message, title = 'Confirmation') {
        return new Promise((resolve) => {
            const modalId = `confirm-${Utils.String.random()}`;
            const modalHtml = `
                <div class="modal fade" id="${modalId}" tabindex="-1">
                    <div class="modal-dialog">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h5 class="modal-title">${Utils.String.escapeHtml(title)}</h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                            </div>
                            <div class="modal-body">
                                <p>${Utils.String.escapeHtml(message)}</p>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal" data-action="cancel">
                                    ${I18n.t('common.cancel')}
                                </button>
                                <button type="button" class="btn btn-primary" data-action="confirm">
                                    ${I18n.t('common.confirm')}
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            `;
            
            document.body.insertAdjacentHTML('beforeend', modalHtml);
            const modal = new bootstrap.Modal(document.getElementById(modalId));
            
            // Handle button clicks
            document.getElementById(modalId).addEventListener('click', (e) => {
                const action = e.target.getAttribute('data-action');
                if (action) {
                    modal.hide();
                    resolve(action === 'confirm');
                }
            });
            
            // Clean up when modal is hidden
            document.getElementById(modalId).addEventListener('hidden.bs.modal', () => {
                document.getElementById(modalId).remove();
            });
            
            modal.show();
        });
    },
    
    /**
     * Create a card component
     */
    createCard(title, content, headerClass = '') {
        return `
            <div class="card">
                <div class="card-header ${headerClass}">
                    <h5 class="card-title mb-0">${Utils.String.escapeHtml(title)}</h5>
                </div>
                <div class="card-body">
                    ${content}
                </div>
            </div>
        `;
    },
    
    /**
     * Create a stats card
     */
    createStatsCard(title, value, icon, color = 'primary') {
        return `
            <div class="card stats-card bg-${color}">
                <div class="card-body text-center">
                    <div class="stats-icon">
                        <i class="bi bi-${icon}"></i>
                    </div>
                    <div class="stats-value">${Utils.String.escapeHtml(value)}</div>
                    <div class="stats-label">${Utils.String.escapeHtml(title)}</div>
                </div>
            </div>
        `;
    },
    
    /**
     * Create a data table
     */
    createDataTable(headers, rows, options = {}) {
        const tableClass = options.striped ? 'table-striped' : '';
        const responsive = options.responsive !== false;
        
        let headerHtml = '<tr>';
        headers.forEach(header => {
            headerHtml += `<th>${Utils.String.escapeHtml(header)}</th>`;
        });
        headerHtml += '</tr>';
        
        let rowsHtml = '';
        rows.forEach(row => {
            rowsHtml += '<tr>';
            row.forEach(cell => {
                rowsHtml += `<td>${cell}</td>`;
            });
            rowsHtml += '</tr>';
        });
        
        const tableHtml = `
            <table class="table ${tableClass}">
                <thead>${headerHtml}</thead>
                <tbody>${rowsHtml}</tbody>
            </table>
        `;
        
        return responsive ? `<div class="table-responsive">${tableHtml}</div>` : tableHtml;
    },
    
    /**
     * Create a form field
     */
    createFormField(type, name, label, value = '', options = {}) {
        const required = options.required ? 'required' : '';
        const placeholder = options.placeholder || '';
        const className = options.className || 'form-control';
        const helpText = options.helpText || '';
        
        let input = '';
        switch (type) {
            case 'select':
                input = `<select class="${className}" name="${name}" ${required}>`;
                if (options.options) {
                    options.options.forEach(option => {
                        const selected = option.value === value ? 'selected' : '';
                        input += `<option value="${option.value}" ${selected}>${Utils.String.escapeHtml(option.label)}</option>`;
                    });
                }
                input += '</select>';
                break;
            case 'textarea':
                input = `<textarea class="${className}" name="${name}" placeholder="${placeholder}" ${required}>${Utils.String.escapeHtml(value)}</textarea>`;
                break;
            default:
                input = `<input type="${type}" class="${className}" name="${name}" value="${Utils.String.escapeHtml(value)}" placeholder="${placeholder}" ${required}>`;
        }
        
        return `
            <div class="mb-3">
                <label for="${name}" class="form-label">${Utils.String.escapeHtml(label)}</label>
                ${input}
                ${helpText ? `<div class="form-text">${Utils.String.escapeHtml(helpText)}</div>` : ''}
            </div>
        `;
    },
    
    /**
     * Create action buttons
     */
    createActionButtons(buttons) {
        let html = '<div class="action-buttons">';
        buttons.forEach(button => {
            const className = button.className || 'btn btn-primary';
            const icon = button.icon ? `<i class="bi bi-${button.icon}"></i> ` : '';
            const onclick = button.onclick ? `onclick="${button.onclick}"` : '';
            const disabled = button.disabled ? 'disabled' : '';
            
            html += `
                <button type="button" class="${className}" ${onclick} ${disabled}>
                    ${icon}${Utils.String.escapeHtml(button.label)}
                </button>
            `;
        });
        html += '</div>';
        return html;
    },
    
    /**
     * Create breadcrumb
     */
    createBreadcrumb(items) {
        const container = Utils.DOM.select('#breadcrumb');
        if (!container) return;
        
        let html = '';
        items.forEach((item, index) => {
            if (index === items.length - 1) {
                html += `<li class="breadcrumb-item active">${Utils.String.escapeHtml(item.label)}</li>`;
            } else {
                html += `<li class="breadcrumb-item"><a href="${item.href}">${Utils.String.escapeHtml(item.label)}</a></li>`;
            }
        });
        
        container.innerHTML = html;
    },
    
    /**
     * Create empty state
     */
    createEmptyState(title, message, icon = 'inbox', actionButton = null) {
        let buttonHtml = '';
        if (actionButton) {
            buttonHtml = `
                <button type="button" class="btn btn-primary mt-3" onclick="${actionButton.onclick}">
                    <i class="bi bi-${actionButton.icon || 'plus'}"></i>
                    ${Utils.String.escapeHtml(actionButton.label)}
                </button>
            `;
        }
        
        return `
            <div class="empty-state">
                <i class="bi bi-${icon}"></i>
                <h4>${Utils.String.escapeHtml(title)}</h4>
                <p class="text-muted">${Utils.String.escapeHtml(message)}</p>
                ${buttonHtml}
            </div>
        `;
    },
    
    /**
     * Create error state
     */
    createErrorState(message, retryCallback = null) {
        let retryButton = '';
        if (retryCallback) {
            retryButton = `
                <button type="button" class="btn btn-outline-primary mt-3" onclick="${retryCallback}">
                    <i class="bi bi-arrow-clockwise"></i>
                    ${I18n.t('common.retry', 'Réessayer')}
                </button>
            `;
        }
        
        return `
            <div class="error-state">
                <i class="bi bi-exclamation-triangle"></i>
                <h4>${I18n.t('common.error')}</h4>
                <p class="text-muted">${Utils.String.escapeHtml(message)}</p>
                ${retryButton}
            </div>
        `;
    },
    
    /**
     * Create loading state
     */
    createLoadingState(message = null) {
        const text = message || I18n.t('common.loading');
        return `
            <div class="text-center py-5">
                <div class="spinner-border text-primary" role="status">
                    <span class="visually-hidden">${text}</span>
                </div>
                <p class="mt-3 text-muted">${Utils.String.escapeHtml(text)}</p>
            </div>
        `;
    },
    
    /**
     * Create pagination
     */
    createPagination(currentPage, totalPages, onPageChange) {
        if (totalPages <= 1) return '';
        
        let html = '<nav><ul class="pagination justify-content-center">';
        
        // Previous button
        const prevDisabled = currentPage === 1 ? 'disabled' : '';
        html += `
            <li class="page-item ${prevDisabled}">
                <a class="page-link" href="#" onclick="${onPageChange}(${currentPage - 1})">
                    <i class="bi bi-chevron-left"></i>
                </a>
            </li>
        `;
        
        // Page numbers
        for (let i = 1; i <= totalPages; i++) {
            const active = i === currentPage ? 'active' : '';
            html += `
                <li class="page-item ${active}">
                    <a class="page-link" href="#" onclick="${onPageChange}(${i})">${i}</a>
                </li>
            `;
        }
        
        // Next button
        const nextDisabled = currentPage === totalPages ? 'disabled' : '';
        html += `
            <li class="page-item ${nextDisabled}">
                <a class="page-link" href="#" onclick="${onPageChange}(${currentPage + 1})">
                    <i class="bi bi-chevron-right"></i>
                </a>
            </li>
        `;
        
        html += '</ul></nav>';
        return html;
    },
    
    /**
     * Create badge
     */
    createBadge(text, color = 'primary') {
        return `<span class="badge bg-${color}">${Utils.String.escapeHtml(text)}</span>`;
    },
    
    /**
     * Create status badge
     */
    createStatusBadge(status) {
        const statusConfig = CONFIG.ENTRY_STATUSES.find(s => s.value === status);
        if (statusConfig) {
            return this.createBadge(statusConfig.label, statusConfig.color);
        }
        return this.createBadge(status);
    },
    
    /**
     * Update page content
     */
    updateContent(content) {
        const container = Utils.DOM.select('#mainContent');
        if (container) {
            container.innerHTML = content;
            
            // Apply animations
            container.classList.add('page-enter');
            setTimeout(() => {
                container.classList.add('page-enter-active');
                container.classList.remove('page-enter');
            }, 10);
            
            // Apply translations to new content
            I18n.applyTranslations();
        }
    },
    
    /**
     * Clear page content
     */
    clearContent() {
        const container = Utils.DOM.select('#mainContent');
        if (container) {
            container.innerHTML = '';
        }
    }
};

// Export UI service
window.UI = UI;