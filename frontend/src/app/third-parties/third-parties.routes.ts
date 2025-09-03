import { Routes } from '@angular/router';

export const THIRD_PARTIES_ROUTES: Routes = [
  {
    path: '',
    redirectTo: 'customers',
    pathMatch: 'full'
  },
  {
    path: 'customers',
    loadComponent: () => import('./components/customers.component').then(m => m.CustomersComponent)
  }
];



