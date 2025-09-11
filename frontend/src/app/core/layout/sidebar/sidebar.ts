import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatTooltipModule } from '@angular/material/tooltip';
import { Subject, takeUntil } from 'rxjs';

import { AuthService } from '../../../shared/services/auth';
import { User } from '../../../shared/models/user';

interface MenuItem {
  label: string;
  icon: string;
  route?: string;
  children?: MenuItem[];
  permission?: string;
}

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatSidenavModule,
    MatListModule,
    MatIconModule,
    MatButtonModule,
    MatExpansionModule,
    MatTooltipModule
  ],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.scss'
})
export class SidebarComponent implements OnInit, OnDestroy {
  currentUser: User | null = null;
  isExpanded = true;
  private destroy$ = new Subject<void>();

  menuItems: MenuItem[] = [
    {
      label: 'Dashboard',
      icon: 'dashboard',
      route: '/dashboard'
    },
    {
      label: 'CRM',
      icon: 'people',
      children: [
        { label: 'Clients', icon: 'person', route: '/crm/clients' },
        { label: 'Prospects', icon: 'person_add', route: '/crm/prospects' },
        { label: 'Opportunités', icon: 'trending_up', route: '/crm/opportunities' },
        { label: 'Activités', icon: 'event', route: '/crm/activities' }
      ]
    },
    {
      label: 'SYCEBNL',
      icon: 'account_balance',
      children: [
        { label: 'Comptes', icon: 'account_balance_wallet', route: '/sycebnl/accounts' },
        { label: 'Transactions', icon: 'swap_horiz', route: '/sycebnl/transactions' },
        { label: 'Rapprochements', icon: 'compare_arrows', route: '/sycebnl/reconciliations' },
        { label: 'Rapports', icon: 'assessment', route: '/sycebnl/reports' }
      ]
    },
    {
      label: 'Comptabilité',
      icon: 'calculate',
      children: [
        { label: 'Écritures', icon: 'edit', route: '/accounting/entries' },
        { label: 'Grand Livre', icon: 'book', route: '/accounting/ledger' },
        { label: 'Balance', icon: 'balance', route: '/accounting/balance' },
        { label: 'Bilan', icon: 'account_balance', route: '/accounting/balance-sheet' }
      ]
    },
    {
      label: 'Facturation',
      icon: 'receipt',
      children: [
        { label: 'Factures', icon: 'description', route: '/billing/invoices' },
        { label: 'Devis', icon: 'request_quote', route: '/billing/quotes' },
        { label: 'Clients', icon: 'people', route: '/billing/customers' },
        { label: 'Produits', icon: 'inventory', route: '/billing/products' }
      ]
    },
    {
      label: 'GED',
      icon: 'folder_open',
      children: [
        { label: 'Explorateur', icon: 'folder_open', route: '/ged/explorer' },
        { label: 'Upload', icon: 'cloud_upload', route: '/ged/upload' },
        { label: 'Statistiques', icon: 'analytics', route: '/ged/stats' },
        { label: 'Configuration', icon: 'settings', route: '/ged/config' }
      ]
    },
    {
      label: 'Rapports',
      icon: 'analytics',
      children: [
        { label: 'Financiers', icon: 'trending_up', route: '/reports/financial' },
        { label: 'Ventes', icon: 'sell', route: '/reports/sales' },
        { label: 'Clients', icon: 'people', route: '/reports/customers' },
        { label: 'Personnalisés', icon: 'tune', route: '/reports/custom' }
      ]
    },
    {
      label: 'Administration',
      icon: 'admin_panel_settings',
      permission: 'ADMIN',
      children: [
        { label: 'Utilisateurs', icon: 'people', route: '/admin/users' },
        { label: 'Rôles', icon: 'security', route: '/admin/roles' },
        { label: 'Paramètres', icon: 'settings', route: '/admin/settings' },
        { label: 'Logs', icon: 'history', route: '/admin/logs' }
      ]
    }
  ];

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.authService.currentUser$
      .pipe(takeUntil(this.destroy$))
      .subscribe(user => {
        this.currentUser = user;
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  toggleSidebar(): void {
    this.isExpanded = !this.isExpanded;
  }

  hasPermission(permission?: string): boolean {
    if (!permission) return true;
    if (!this.currentUser?.roles) return false;
    return this.currentUser.roles.some(role => role.name === permission);
  }

  isActiveRoute(route: string): boolean {
    return this.router.url === route;
  }

  navigateTo(route: string): void {
    this.router.navigate([route]);
  }
}
