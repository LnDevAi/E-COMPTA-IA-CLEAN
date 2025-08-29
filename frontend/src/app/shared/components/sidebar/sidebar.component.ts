import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { Subject, takeUntil, filter } from 'rxjs';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatDividerModule } from '@angular/material/divider';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';

import { AuthService } from '../../../core/services/auth.service';
import { UserProfile } from '../../interfaces/user.interface';
import { UserPermission } from '../../enums/user-roles.enum';

interface MenuItem {
  id: string;
  label: string;
  icon: string;
  route?: string;
  permission?: string;
  children?: MenuItem[];
  badge?: number;
  isActive?: boolean;
}

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [
    CommonModule,
    MatListModule,
    MatIconModule,
    MatExpansionModule,
    MatDividerModule,
    RouterModule
  ],
  template: `
    <div class="sidebar-container" [class.collapsed]="isCollapsed">
      <!-- En-tête de la sidebar -->
      <div class="sidebar-header">
        <div class="company-info" *ngIf="!isCollapsed">
          <mat-icon class="company-icon">business</mat-icon>
          <div class="company-details">
            <div class="company-name">{{ companyName }}</div>
            <div class="company-type">{{ companyType }}</div>
          </div>
        </div>
        <button mat-icon-button (click)="toggleCollapse()" class="collapse-button">
          <mat-icon>{{ isCollapsed ? 'chevron_right' : 'chevron_left' }}</mat-icon>
        </button>
      </div>

      <mat-divider></mat-divider>

      <!-- Navigation principale -->
      <nav class="sidebar-nav">
        <mat-list>
          <!-- Tableau de bord -->
          <mat-list-item 
            *ngIf="hasPermission(UserPermission.VIEW_DASHBOARD)"
            [routerLink]="['/dashboard']" 
            routerLinkActive="active"
            class="nav-item">
            <mat-icon matListItemIcon>dashboard</mat-icon>
            <span matListItemTitle *ngIf="!isCollapsed">Tableau de bord</span>
          </mat-list-item>

          <!-- Comptabilité -->
          <mat-expansion-panel 
            *ngIf="hasPermission(UserPermission.VIEW_ACCOUNTING)"
            class="nav-expansion"
            [expanded]="isModuleActive('accounting')">
            <mat-expansion-panel-header>
              <mat-icon matListItemIcon>account_balance</mat-icon>
              <span matListItemTitle *ngIf="!isCollapsed">Comptabilité</span>
            </mat-expansion-panel-header>
            
            <mat-list>
              <mat-list-item 
                [routerLink]="['/accounting/journal-entries']" 
                routerLinkActive="active"
                class="sub-nav-item">
                <mat-icon matListItemIcon>receipt</mat-icon>
                <span matListItemTitle>Écritures comptables</span>
              </mat-list-item>
              
              <mat-list-item 
                [routerLink]="['/accounting/chart-of-accounts']" 
                routerLinkActive="active"
                class="sub-nav-item">
                <mat-icon matListItemIcon>account_tree</mat-icon>
                <span matListItemTitle>Plan de comptes</span>
              </mat-list-item>
              
              <mat-list-item 
                [routerLink]="['/accounting/reconciliations']" 
                routerLinkActive="active"
                class="sub-nav-item">
                <mat-icon matListItemIcon>compare_arrows</mat-icon>
                <span matListItemTitle>Rapprochements</span>
              </mat-list-item>
              
              <mat-list-item 
                [routerLink]="['/accounting/reports']" 
                routerLinkActive="active"
                class="sub-nav-item">
                <mat-icon matListItemIcon>assessment</mat-icon>
                <span matListItemTitle>Rapports</span>
              </mat-list-item>
            </mat-list>
          </mat-expansion-panel>

          <!-- RH & Paie -->
          <mat-expansion-panel 
            *ngIf="hasPermission(UserPermission.VIEW_HR)"
            class="nav-expansion"
            [expanded]="isModuleActive('hr')">
            <mat-expansion-panel-header>
              <mat-icon matListItemIcon>people</mat-icon>
              <span matListItemTitle *ngIf="!isCollapsed">RH & Paie</span>
            </mat-expansion-panel-header>
            
            <mat-list>
              <mat-list-item 
                [routerLink]="['/hr/employees']" 
                routerLinkActive="active"
                class="sub-nav-item">
                <mat-icon matListItemIcon>person</mat-icon>
                <span matListItemTitle>Employés</span>
              </mat-list-item>
              
              <mat-list-item 
                [routerLink]="['/hr/payroll']" 
                routerLinkActive="active"
                class="sub-nav-item">
                <mat-icon matListItemIcon>payments</mat-icon>
                <span matListItemTitle>Paie</span>
              </mat-list-item>
              
              <mat-list-item 
                [routerLink]="['/hr/leaves']" 
                routerLinkActive="active"
                class="sub-nav-item">
                <mat-icon matListItemIcon>event</mat-icon>
                <span matListItemTitle>Congés</span>
              </mat-list-item>
            </mat-list>
          </mat-expansion-panel>

          <!-- Gestion des Tiers -->
          <mat-expansion-panel 
            *ngIf="hasPermission(UserPermission.VIEW_THIRD_PARTIES)"
            class="nav-expansion"
            [expanded]="isModuleActive('third-parties')">
            <mat-expansion-panel-header>
              <mat-icon matListItemIcon>business</mat-icon>
              <span matListItemTitle *ngIf="!isCollapsed">Tiers</span>
            </mat-expansion-panel-header>
            
            <mat-list>
              <mat-list-item 
                [routerLink]="['/third-parties/customers']" 
                routerLinkActive="active"
                class="sub-nav-item">
                <mat-icon matListItemIcon>people_outline</mat-icon>
                <span matListItemTitle>Clients</span>
              </mat-list-item>
              
              <mat-list-item 
                [routerLink]="['/third-parties/suppliers']" 
                routerLinkActive="active"
                class="sub-nav-item">
                <mat-icon matListItemIcon>local_shipping</mat-icon>
                <span matListItemTitle>Fournisseurs</span>
              </mat-list-item>
              
              <mat-list-item 
                [routerLink]="['/third-parties/partners']" 
                routerLinkActive="active"
                class="sub-nav-item">
                <mat-icon matListItemIcon>handshake</mat-icon>
                <span matListItemTitle>Partenaires</span>
              </mat-list-item>
            </mat-list>
          </mat-expansion-panel>

          <!-- Gestion des Actifs -->
          <mat-expansion-panel 
            *ngIf="hasPermission(UserPermission.VIEW_ASSETS)"
            class="nav-expansion"
            [expanded]="isModuleActive('assets')">
            <mat-expansion-panel-header>
              <mat-icon matListItemIcon>inventory</mat-icon>
              <span matListItemTitle *ngIf="!isCollapsed">Actifs & Inventaires</span>
            </mat-expansion-panel-header>
            
            <mat-list>
              <mat-list-item 
                [routerLink]="['/assets/inventory']" 
                routerLinkActive="active"
                class="sub-nav-item">
                <mat-icon matListItemIcon>category</mat-icon>
                <span matListItemTitle>Inventaire</span>
              </mat-list-item>
              
              <mat-list-item 
                [routerLink]="['/assets/fixed-assets']" 
                routerLinkActive="active"
                class="sub-nav-item">
                <mat-icon matListItemIcon>build</mat-icon>
                <span matListItemTitle>Immobilisations</span>
              </mat-list-item>
              
              <mat-list-item 
                [routerLink]="['/assets/movements']" 
                routerLinkActive="active"
                class="sub-nav-item">
                <mat-icon matListItemIcon>swap_horiz</mat-icon>
                <span matListItemTitle>Mouvements</span>
              </mat-list-item>
            </mat-list>
          </mat-expansion-panel>

          <!-- Gestion Documentaire -->
          <mat-expansion-panel 
            *ngIf="hasPermission(UserPermission.VIEW_DOCUMENTS)"
            class="nav-expansion"
            [expanded]="isModuleActive('documents')">
            <mat-expansion-panel-header>
              <mat-icon matListItemIcon>folder</mat-icon>
              <span matListItemTitle *ngIf="!isCollapsed">Documents</span>
            </mat-expansion-panel-header>
            
            <mat-list>
              <mat-list-item 
                [routerLink]="['/documents/explorer']" 
                routerLinkActive="active"
                class="sub-nav-item">
                <mat-icon matListItemIcon>folder_open</mat-icon>
                <span matListItemTitle>Explorateur</span>
              </mat-list-item>
              
              <mat-list-item 
                [routerLink]="['/documents/upload']" 
                routerLinkActive="active"
                class="sub-nav-item">
                <mat-icon matListItemIcon>cloud_upload</mat-icon>
                <span matListItemTitle>Upload</span>
              </mat-list-item>
              
              <mat-list-item 
                [routerLink]="['/documents/search']" 
                routerLinkActive="active"
                class="sub-nav-item">
                <mat-icon matListItemIcon>search</mat-icon>
                <span matListItemTitle>Recherche</span>
              </mat-list-item>
            </mat-list>
          </mat-expansion-panel>

          <!-- Intelligence Artificielle -->
          <mat-expansion-panel 
            *ngIf="hasPermission(UserPermission.USE_AI_FEATURES)"
            class="nav-expansion"
            [expanded]="isModuleActive('ai')">
            <mat-expansion-panel-header>
              <mat-icon matListItemIcon>psychology</mat-icon>
              <span matListItemTitle *ngIf="!isCollapsed">IA</span>
            </mat-expansion-panel-header>
            
            <mat-list>
              <mat-list-item 
                [routerLink]="['/ai/document-analysis']" 
                routerLinkActive="active"
                class="sub-nav-item">
                <mat-icon matListItemIcon>document_scanner</mat-icon>
                <span matListItemTitle>Analyse de documents</span>
              </mat-list-item>
              
              <mat-list-item 
                [routerLink]="['/ai/predictions']" 
                routerLinkActive="active"
                class="sub-nav-item">
                <mat-icon matListItemIcon>trending_up</mat-icon>
                <span matListItemTitle>Prédictions</span>
              </mat-list-item>
              
              <mat-list-item 
                [routerLink]="['/ai/assistant']" 
                routerLinkActive="active"
                class="sub-nav-item">
                <mat-icon matListItemIcon>smart_toy</mat-icon>
                <span matListItemTitle>Assistant IA</span>
              </mat-list-item>
            </mat-list>
          </mat-expansion-panel>

          <!-- International -->
          <mat-expansion-panel 
            *ngIf="hasPermission(UserPermission.MANAGE_INTERNATIONAL)"
            class="nav-expansion"
            [expanded]="isModuleActive('international')">
            <mat-expansion-panel-header>
              <mat-icon matListItemIcon>public</mat-icon>
              <span matListItemTitle *ngIf="!isCollapsed">International</span>
            </mat-expansion-panel-header>
            
            <mat-list>
              <mat-list-item 
                [routerLink]="['/international/currencies']" 
                routerLinkActive="active"
                class="sub-nav-item">
                <mat-icon matListItemIcon>currency_exchange</mat-icon>
                <span matListItemTitle>Devises</span>
              </mat-list-item>
              
              <mat-list-item 
                [routerLink]="['/international/taxes']" 
                routerLinkActive="active"
                class="sub-nav-item">
                <mat-icon matListItemIcon>receipt_long</mat-icon>
                <span matListItemTitle>Taxes</span>
              </mat-list-item>
              
              <mat-list-item 
                [routerLink]="['/international/standards']" 
                routerLinkActive="active"
                class="sub-nav-item">
                <mat-icon matListItemIcon>rule</mat-icon>
                <span matListItemTitle>Standards</span>
              </mat-list-item>
            </mat-list>
          </mat-expansion-panel>

          <!-- Système -->
          <mat-expansion-panel 
            *ngIf="hasPermission(UserPermission.MANAGE_SYSTEM_CONFIG)"
            class="nav-expansion"
            [expanded]="isModuleActive('system')">
            <mat-expansion-panel-header>
              <mat-icon matListItemIcon>settings</mat-icon>
              <span matListItemTitle *ngIf="!isCollapsed">Système</span>
            </mat-expansion-panel-header>
            
            <mat-list>
              <mat-list-item 
                [routerLink]="['/system/users']" 
                routerLinkActive="active"
                class="sub-nav-item">
                <mat-icon matListItemIcon>group</mat-icon>
                <span matListItemTitle>Utilisateurs</span>
              </mat-list-item>
              
              <mat-list-item 
                [routerLink]="['/system/configuration']" 
                routerLinkActive="active"
                class="sub-nav-item">
                <mat-icon matListItemIcon>tune</mat-icon>
                <span matListItemTitle>Configuration</span>
              </mat-list-item>
              
              <mat-list-item 
                [routerLink]="['/system/audit']" 
                routerLinkActive="active"
                class="sub-nav-item">
                <mat-icon matListItemIcon>security</mat-icon>
                <span matListItemTitle>Audit</span>
              </mat-list-item>
            </mat-list>
          </mat-expansion-panel>
        </mat-list>
      </nav>
    </div>
  `,
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent implements OnInit, OnDestroy {
  isCollapsed = false;
  companyName = 'E-COMPTA-IA';
  companyType = 'Entreprise';
  currentRoute = '';
  UserPermission = UserPermission; // Pour l'utiliser dans le template

  private destroy$ = new Subject<void>();

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.router.events
      .pipe(
        filter(event => event instanceof NavigationEnd),
        takeUntil(this.destroy$)
      )
      .subscribe((event: NavigationEnd) => {
        this.currentRoute = event.url;
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  toggleCollapse(): void {
    this.isCollapsed = !this.isCollapsed;
  }

  hasPermission(permission: string): boolean {
    return this.authService.hasPermission(permission);
  }

  isModuleActive(module: string): boolean {
    return this.currentRoute.startsWith(`/${module}`);
  }
}
