// Utility functions for E-COMPTA-IA
const Utils = {
    
    // DOM manipulation utilities
    DOM: {
        /**
         * Select element by ID or selector
         */
        select(selector) {
            return document.querySelector(selector);
        },
        
        /**
         * Select all elements by selector
         */
        selectAll(selector) {
            return document.querySelectorAll(selector);
        },
        
        /**
         * Create element with attributes and content
         */
        create(tag, attributes = {}, content = '') {
            const element = document.createElement(tag);
            
            Object.entries(attributes).forEach(([key, value]) => {
                if (key === 'className') {
                    element.className = value;
                } else if (key === 'innerHTML') {
                    element.innerHTML = value;
                } else {
                    element.setAttribute(key, value);
                }
            });
            
            if (content) {
                element.textContent = content;
            }
            
            return element;
        },
        
        /**
         * Show element
         */
        show(selector) {
            const element = typeof selector === 'string' ? this.select(selector) : selector;
            if (element) element.classList.remove('d-none');
        },
        
        /**
         * Hide element
         */
        hide(selector) {
            const element = typeof selector === 'string' ? this.select(selector) : selector;
            if (element) element.classList.add('d-none');
        },
        
        /**
         * Toggle element visibility
         */
        toggle(selector) {
            const element = typeof selector === 'string' ? this.select(selector) : selector;
            if (element) element.classList.toggle('d-none');
        }
    },
    
    // String utilities
    String: {
        /**
         * Escape HTML characters
         */
        escapeHtml(text) {
            const div = document.createElement('div');
            div.textContent = text;
            return div.innerHTML;
        },
        
        /**
         * Capitalize first letter
         */
        capitalize(str) {
            return str.charAt(0).toUpperCase() + str.slice(1);
        },
        
        /**
         * Truncate string with ellipsis
         */
        truncate(str, maxLength) {
            return str.length > maxLength ? str.slice(0, maxLength) + '...' : str;
        },
        
        /**
         * Generate random string
         */
        random(length = 8) {
            return Math.random().toString(36).substring(2, length + 2);
        },
        
        /**
         * Convert to slug (URL-friendly)
         */
        toSlug(str) {
            return str
                .toLowerCase()
                .normalize('NFD')
                .replace(/[\u0300-\u036f]/g, '')
                .replace(/[^a-z0-9]+/g, '-')
                .replace(/^-+|-+$/g, '');
        }
    },
    
    // Number utilities
    Number: {
        /**
         * Format number with thousand separators
         */
        format(number, decimals = 2) {
            return new Intl.NumberFormat('fr-FR', {
                minimumFractionDigits: decimals,
                maximumFractionDigits: decimals
            }).format(number || 0);
        },
        
        /**
         * Format currency
         */
        formatCurrency(number, currency = 'XOF') {
            const currencyConfig = CONFIG.CURRENCIES.find(c => c.code === currency);
            const symbol = currencyConfig ? currencyConfig.symbol : currency;
            
            return `${this.format(number)} ${symbol}`;
        },
        
        /**
         * Parse number from string
         */
        parse(str) {
            return parseFloat(str.replace(/[^\d.-]/g, '')) || 0;
        },
        
        /**
         * Check if valid number
         */
        isValid(value) {
            return !isNaN(value) && isFinite(value);
        }
    },
    
    // Date utilities
    Date: {
        /**
         * Format date
         */
        format(date, format = 'DD/MM/YYYY') {
            if (!date) return '';
            
            const d = new Date(date);
            const day = String(d.getDate()).padStart(2, '0');
            const month = String(d.getMonth() + 1).padStart(2, '0');
            const year = d.getFullYear();
            const hours = String(d.getHours()).padStart(2, '0');
            const minutes = String(d.getMinutes()).padStart(2, '0');
            
            return format
                .replace('DD', day)
                .replace('MM', month)
                .replace('YYYY', year)
                .replace('HH', hours)
                .replace('mm', minutes);
        },
        
        /**
         * Get relative time (e.g., "2 hours ago")
         */
        relative(date) {
            const now = new Date();
            const diff = now - new Date(date);
            const seconds = Math.floor(diff / 1000);
            const minutes = Math.floor(seconds / 60);
            const hours = Math.floor(minutes / 60);
            const days = Math.floor(hours / 24);
            
            if (days > 0) return `Il y a ${days} jour${days > 1 ? 's' : ''}`;
            if (hours > 0) return `Il y a ${hours} heure${hours > 1 ? 's' : ''}`;
            if (minutes > 0) return `Il y a ${minutes} minute${minutes > 1 ? 's' : ''}`;
            return 'À l\'instant';
        },
        
        /**
         * Check if date is today
         */
        isToday(date) {
            const today = new Date();
            const d = new Date(date);
            return d.toDateString() === today.toDateString();
        }
    },
    
    // Array utilities
    Array: {
        /**
         * Group array by property
         */
        groupBy(array, property) {
            return array.reduce((groups, item) => {
                const key = item[property];
                groups[key] = groups[key] || [];
                groups[key].push(item);
                return groups;
            }, {});
        },
        
        /**
         * Sort array by property
         */
        sortBy(array, property, direction = 'asc') {
            return array.sort((a, b) => {
                const aVal = a[property];
                const bVal = b[property];
                
                if (direction === 'asc') {
                    return aVal > bVal ? 1 : -1;
                } else {
                    return aVal < bVal ? 1 : -1;
                }
            });
        },
        
        /**
         * Remove duplicates
         */
        unique(array, property = null) {
            if (property) {
                return array.filter((item, index, self) => 
                    index === self.findIndex(t => t[property] === item[property])
                );
            }
            return [...new Set(array)];
        },
        
        /**
         * Chunk array into smaller arrays
         */
        chunk(array, size) {
            const chunks = [];
            for (let i = 0; i < array.length; i += size) {
                chunks.push(array.slice(i, i + size));
            }
            return chunks;
        }
    },
    
    // Local storage utilities
    Storage: {
        /**
         * Set item in localStorage
         */
        set(key, value) {
            try {
                localStorage.setItem(key, JSON.stringify(value));
            } catch (e) {
                console.warn('Failed to save to localStorage:', e);
            }
        },
        
        /**
         * Get item from localStorage
         */
        get(key, defaultValue = null) {
            try {
                const item = localStorage.getItem(key);
                return item ? JSON.parse(item) : defaultValue;
            } catch (e) {
                console.warn('Failed to read from localStorage:', e);
                return defaultValue;
            }
        },
        
        /**
         * Remove item from localStorage
         */
        remove(key) {
            try {
                localStorage.removeItem(key);
            } catch (e) {
                console.warn('Failed to remove from localStorage:', e);
            }
        },
        
        /**
         * Clear all localStorage
         */
        clear() {
            try {
                localStorage.clear();
            } catch (e) {
                console.warn('Failed to clear localStorage:', e);
            }
        }
    },
    
    // Validation utilities
    Validation: {
        /**
         * Validate email
         */
        email(email) {
            const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            return regex.test(email);
        },
        
        /**
         * Validate phone number
         */
        phone(phone) {
            const regex = /^[\+]?[0-9\s\-\(\)]{8,}$/;
            return regex.test(phone);
        },
        
        /**
         * Validate required field
         */
        required(value) {
            return value !== null && value !== undefined && value.toString().trim() !== '';
        },
        
        /**
         * Validate number
         */
        number(value) {
            return !isNaN(value) && isFinite(value);
        },
        
        /**
         * Validate date
         */
        date(value) {
            return !isNaN(Date.parse(value));
        }
    },
    
    // Debounce function
    debounce(func, wait) {
        let timeout;
        return function executedFunction(...args) {
            const later = () => {
                clearTimeout(timeout);
                func(...args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    },
    
    // Throttle function
    throttle(func, limit) {
        let inThrottle;
        return function() {
            const args = arguments;
            const context = this;
            if (!inThrottle) {
                func.apply(context, args);
                inThrottle = true;
                setTimeout(() => inThrottle = false, limit);
            }
        };
    },
    
    // Copy to clipboard
    async copyToClipboard(text) {
        try {
            await navigator.clipboard.writeText(text);
            UI.showToast('Copié dans le presse-papiers', 'success');
        } catch (err) {
            console.error('Failed to copy:', err);
            UI.showToast('Échec de la copie', 'error');
        }
    },
    
    // Download file
    downloadFile(data, filename, type = 'application/octet-stream') {
        const blob = new Blob([data], { type });
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = filename;
        a.click();
        window.URL.revokeObjectURL(url);
    },
    
    // Generate UUID
    generateUUID() {
        return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
            const r = Math.random() * 16 | 0;
            const v = c == 'x' ? r : (r & 0x3 | 0x8);
            return v.toString(16);
        });
    },
    
    // Deep clone object
    clone(obj) {
        return JSON.parse(JSON.stringify(obj));
    },
    
    // Check if object is empty
    isEmpty(obj) {
        return Object.keys(obj).length === 0;
    },
    
    // Merge objects
    merge(...objects) {
        return Object.assign({}, ...objects);
    }
};

// Export utilities for global use
window.Utils = Utils;