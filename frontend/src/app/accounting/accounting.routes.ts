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
  }
];


