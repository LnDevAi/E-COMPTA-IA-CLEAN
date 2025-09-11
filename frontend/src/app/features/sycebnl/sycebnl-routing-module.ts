import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [
  {
    path: '',
    redirectTo: 'organizations',
    pathMatch: 'full'
  },
  {
    path: 'organizations',
    loadComponent: () => import('./organization-list/organization-list').then(m => m.OrganizationList)
  },
  {
    path: 'organizations/new',
    loadComponent: () => import('./organization-detail/organization-detail').then(m => m.OrganizationDetail)
  },
  {
    path: 'organizations/edit/:id',
    loadComponent: () => import('./organization-detail/organization-detail').then(m => m.OrganizationDetail)
  },
  {
    path: 'organizations/view/:id',
    loadComponent: () => import('./organization-detail/organization-detail').then(m => m.OrganizationDetail)
  },
  {
    path: 'transactions',
    loadComponent: () => import('./transactions/transactions').then(m => m.Transactions)
  },
  {
    path: 'financial-statements',
    loadComponent: () => import('./financial-statements/financial-statements-list').then(m => m.FinancialStatementsList)
  },
  {
    path: 'financial-statements/generate',
    loadComponent: () => import('./financial-statements/financial-statement-generator').then(m => m.FinancialStatementGenerator)
  },
  {
    path: 'financial-statements/view/:id',
    loadComponent: () => import('./financial-statements/financial-statement-detail').then(m => m.FinancialStatementDetail)
  },
  {
    path: 'financial-statements/edit/:id',
    loadComponent: () => import('./financial-statements/financial-statement-detail').then(m => m.FinancialStatementDetail)
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class SycebnlRoutingModule { }
