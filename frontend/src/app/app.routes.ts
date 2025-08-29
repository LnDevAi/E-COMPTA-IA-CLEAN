import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    redirectTo: '/dashboard',
    pathMatch: 'full'
  },
  {
    path: 'dashboard',
    loadComponent: () => import('./dashboard/dashboard.component').then(m => m.DashboardComponent)
  },
  {
    path: 'accounting',
    loadChildren: () => import('./accounting/accounting.routes').then(m => m.ACCOUNTING_ROUTES)
  },
  {
    path: 'hr',
    loadChildren: () => import('./hr/hr.routes').then(m => m.HR_ROUTES)
  },
  {
    path: 'third-parties',
    loadChildren: () => import('./third-parties/third-parties.routes').then(m => m.THIRD_PARTIES_ROUTES)
  },
  {
    path: 'assets',
    loadChildren: () => import('./asset-inventory/asset-inventory.routes').then(m => m.ASSET_INVENTORY_ROUTES)
  },
  {
    path: 'documents',
    loadChildren: () => import('./document-management/document-management.routes').then(m => m.DOCUMENT_MANAGEMENT_ROUTES)
  },
  {
    path: 'ai',
    loadChildren: () => import('./ai/ai.routes').then(m => m.AI_ROUTES)
  },
  {
    path: 'international',
    loadChildren: () => import('./international/international.routes').then(m => m.INTERNATIONAL_ROUTES)
  },
  {
    path: 'system',
    loadChildren: () => import('./system/system.routes').then(m => m.SYSTEM_ROUTES)
  },
  {
    path: 'auth',
    loadChildren: () => import('./auth/auth.routes').then(m => m.AUTH_ROUTES)
  },
  {
    path: '**',
    redirectTo: '/dashboard'
  }
];
