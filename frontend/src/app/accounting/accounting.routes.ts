import { Routes } from '@angular/router';

export const ACCOUNTING_ROUTES: Routes = [
  {
    path: '',
    redirectTo: 'journal-entries',
    pathMatch: 'full'
  },
  {
    path: 'journal-entries',
    loadComponent: () => import('./components/journal-entries.component').then(m => m.JournalEntriesComponent)
  },
  {
    path: 'journal-entries/new',
    loadComponent: () => import('./components/journal-entry-form.component').then(m => m.JournalEntryFormComponent)
  },
  {
    path: 'reporting',
    loadComponent: () => import('./components/reporting.component').then(m => m.ReportingComponent)
  }
];


