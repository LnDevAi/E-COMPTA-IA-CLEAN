import { Routes } from '@angular/router';

export const INTERNATIONAL_ROUTES: Routes = [
  {
    path: '',
    redirectTo: 'currencies',
    pathMatch: 'full'
  },
  {
    path: 'currencies',
    loadComponent: () => import('./components/currencies.component').then(m => m.CurrenciesComponent)
  }
];


