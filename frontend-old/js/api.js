const API = (() => {
  const base = '';

  async function request(path, options = {}) {
    const res = await fetch(base + path, {
      headers: { 'Content-Type': 'application/json', ...(options.headers || {}) },
      credentials: 'omit',
      ...options,
    });
    if (!res.ok) {
      const text = await res.text().catch(() => '');
      throw new Error(`HTTP ${res.status} ${res.statusText}: ${text}`);
    }
    const ct = res.headers.get('content-type') || '';
    if (ct.includes('application/json')) return res.json();
    return res.text();
  }

  // Health & reporting
  const health = () => request('/api/actuator/health');
  const reportingTest = () => request('/api/reporting/test');

  // Journal entries (support both variants)
  const getJournalEntries = async () => {
    try { return await request('/api/journal-entries'); } catch (_) {}
    return request('/api/accounting/journal-entries');
  };

  const validateJournalEntry = (id) => request(`/api/accounting/journal-entries/${id}/validate`, { method: 'POST', body: JSON.stringify({}) });
  const deleteJournalEntry = (id) => request(`/api/accounting/journal-entries/${id}`, { method: 'DELETE' });

  return {
    health,
    reportingTest,
    getJournalEntries,
    validateJournalEntry,
    deleteJournalEntry,
  };
})();

