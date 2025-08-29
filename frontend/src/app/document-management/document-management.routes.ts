import { Routes } from '@angular/router';

export const DOCUMENT_MANAGEMENT_ROUTES: Routes = [
  {
    path: '',
    redirectTo: 'explorer',
    pathMatch: 'full'
  },
  {
    path: 'explorer',
    loadComponent: () => import('./components/explorer.component').then(m => m.ExplorerComponent)
  }
];


