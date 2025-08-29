import { Routes } from '@angular/router';

export const ASSET_INVENTORY_ROUTES: Routes = [
  {
    path: '',
    redirectTo: 'inventory',
    pathMatch: 'full'
  },
  {
    path: 'inventory',
    loadComponent: () => import('./components/inventory.component').then(m => m.InventoryComponent)
  }
];


