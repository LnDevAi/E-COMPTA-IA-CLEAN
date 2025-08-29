import { Routes } from '@angular/router';

export const AI_ROUTES: Routes = [
  {
    path: '',
    redirectTo: 'document-analysis',
    pathMatch: 'full'
  },
  {
    path: 'document-analysis',
    loadComponent: () => import('./components/document-analysis.component').then(m => m.DocumentAnalysisComponent)
  }
];


