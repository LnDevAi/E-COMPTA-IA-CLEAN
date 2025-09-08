const $v = (sel) => document.querySelector(sel);
const $html = (el, html) => (el.innerHTML = html);
const alertBox = $v('#app-alert');
const view = $v('#app-view');

function showAlert(kind, msg) {
  alertBox.className = `alert alert-${kind}`;
  alertBox.textContent = msg;
  alertBox.classList.remove('d-none');
  setTimeout(() => alertBox.classList.add('d-none'), 4000);
}

function route() {
  const hash = location.hash || '#/dashboard';
  if (hash.startsWith('#/accounting/journal-entries')) return pageJournalEntries();
  if (hash.startsWith('#/reporting')) return pageReporting();
  if (hash.startsWith('#/actuator')) return pageActuator();
  return pageDashboard();
}

async function pageDashboard() {
  $html(view, `<div class="card"><div class="card-body"><h5 class="card-title">Tableau de bord</h5><p class="text-muted">Bienvenue sur E‑COMPTA‑IA.</p></div></div>`);
}

async function pageActuator() {
  try {
    const data = await API.health();
    $html(view, `<div class="card"><div class="card-body"><h5 class="card-title">Santé</h5><pre class="mb-0">${escapeHtml(JSON.stringify(data, null, 2))}</pre></div></div>`);
  } catch (e) {
    showAlert('danger', e.message);
  }
}

async function pageReporting() {
  try {
    const data = await API.reportingTest();
    $html(view, `<div class="card"><div class="card-body"><h5 class="card-title">Reporting</h5><pre class="mb-0">${escapeHtml(JSON.stringify(data, null, 2))}</pre></div></div>`);
  } catch (e) {
    showAlert('danger', e.message);
  }
}

async function pageJournalEntries() {
  $html(view, `<div class="card"><div class="card-body"><div class="d-flex justify-content-between align-items-center mb-3"><h5 class="card-title mb-0">Écritures comptables</h5><button id="btn-refresh" class="btn btn-outline-light btn-sm">Actualiser</button></div><div id="table-wrap"><div class="text-muted">Chargement...</div></div></div></div>`);
  const wrap = $v('#table-wrap');
  const load = async () => {
    try {
      const res = await API.getJournalEntries();
      const rows = (res.data || res.entries || []).map((e) => `
        <tr>
          <td>${escapeHtml(e.entryDate || '')}</td>
          <td>${escapeHtml(e.entryNumber || '')}</td>
          <td>${escapeHtml(e.description || '')}</td>
          <td class="text-end">${fmt(e.totalDebit)}</td>
          <td class="text-end">${fmt(e.totalCredit)}</td>
          <td><span class="badge bg-${badge(e.status)}">${escapeHtml(e.status || '')}</span></td>
          <td class="text-end">
            <button class="btn btn-sm btn-primary" data-act="validate" data-id="${e.id}">Valider</button>
            <button class="btn btn-sm btn-outline-light ms-2" data-act="delete" data-id="${e.id}">Supprimer</button>
          </td>
        </tr>`).join('');
      $html(wrap, `
        <div class="table-responsive">
          <table class="table table-sm align-middle">
            <thead><tr><th>Date</th><th>N°</th><th>Description</th><th class="text-end">Débit</th><th class="text-end">Crédit</th><th>Statut</th><th class="text-end">Actions</th></tr></thead>
            <tbody>${rows || ''}</tbody>
          </table>
        </div>`);
      wrap.querySelectorAll('button[data-act]').forEach(btn => btn.addEventListener('click', onRowAction));
    } catch (e) {
      showAlert('danger', e.message);
    }
  };
  $v('#btn-refresh').addEventListener('click', load);
  await load();
}

async function onRowAction(ev) {
  const btn = ev.currentTarget;
  const id = btn.getAttribute('data-id');
  const act = btn.getAttribute('data-act');
  try {
    if (act === 'validate') {
      await API.validateJournalEntry(id);
      showAlert('success', 'Écriture validée');
    } else if (act === 'delete') {
      await API.deleteJournalEntry(id);
      showAlert('success', 'Écriture supprimée');
    }
    route();
  } catch (e) {
    showAlert('danger', e.message);
  }
}

function fmt(n){ try{ return Number(n ?? 0).toLocaleString('fr-FR', { minimumFractionDigits: 0 }); } catch { return n ?? ''; } }
function badge(s){ return (s||'').toLowerCase().includes('valid') ? 'success' : (s||'').toLowerCase().includes('brou') ? 'secondary' : 'warning'; }
function escapeHtml(s){ return String(s).replace(/[&<>"]g, (c)=>({"&":"&amp;","<":"&lt;","<":"&lt;",">":"&gt;","\"":"&quot;"}[c])); }

window.addEventListener('hashchange', route);
window.addEventListener('load', route);

