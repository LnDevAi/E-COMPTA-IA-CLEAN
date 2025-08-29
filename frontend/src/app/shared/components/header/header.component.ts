import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';
import { MatMenuModule } from '@angular/material/menu';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatBadgeModule } from '@angular/material/badge';
import { MatDividerModule } from '@angular/material/divider';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';

import { AuthService } from '../../../core/services/auth.service';
import { UserProfile } from '../../interfaces/user.interface';
import { AuthState } from '../../models/auth.model';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [
    CommonModule,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatMenuModule,
    MatBadgeModule,
    MatDividerModule,
    RouterModule
  ],
  template: `
    <mat-toolbar color="primary" class="header-toolbar">
      <!-- Logo et titre -->
      <div class="header-left">
        <button mat-icon-button (click)="toggleSidebar()" class="menu-button">
          <mat-icon>menu</mat-icon>
        </button>
        
        <div class="logo-container">
          <mat-icon class="logo-icon">account_balance</mat-icon>
          <span class="app-title">E-COMPTA-IA</span>
        </div>
      </div>

      <!-- Navigation centrale -->
      <div class="header-center">
        <nav class="nav-links">
          <a mat-button routerLink="/dashboard" routerLinkActive="active">
            <mat-icon>dashboard</mat-icon>
            Tableau de bord
          </a>
          <a mat-button routerLink="/accounting" routerLinkActive="active">
            <mat-icon>account_balance</mat-icon>
            Comptabilité
          </a>
          <a mat-button routerLink="/hr" routerLinkActive="active">
            <mat-icon>people</mat-icon>
            RH & Paie
          </a>
          <a mat-button routerLink="/third-parties" routerLinkActive="active">
            <mat-icon>business</mat-icon>
            Tiers
          </a>
        </nav>
      </div>

      <!-- Actions utilisateur -->
      <div class="header-right">
        <!-- Notifications -->
        <button mat-icon-button [matMenuTriggerFor]="notificationMenu" class="notification-button">
          <mat-icon [matBadge]="notificationCount" matBadgeColor="warn">notifications</mat-icon>
        </button>

        <!-- Menu utilisateur -->
        <button mat-button [matMenuTriggerFor]="userMenu" class="user-menu-button" *ngIf="user">
          <mat-icon>account_circle</mat-icon>
          <span class="user-name">{{ user.firstName }} {{ user.lastName }}</span>
          <mat-icon>arrow_drop_down</mat-icon>
        </button>

        <!-- Menu notifications -->
        <mat-menu #notificationMenu="matMenu" class="notification-menu">
          <div class="notification-header">
            <h3>Notifications</h3>
            <button mat-button color="primary">Marquer comme lu</button>
          </div>
          <mat-divider></mat-divider>
          <div class="notification-list">
            <div class="notification-item" *ngFor="let notification of notifications">
              <mat-icon class="notification-icon">{{ notification.icon }}</mat-icon>
              <div class="notification-content">
                <div class="notification-title">{{ notification.title }}</div>
                <div class="notification-message">{{ notification.message }}</div>
                <div class="notification-time">{{ notification.time }}</div>
              </div>
            </div>
          </div>
          <mat-divider></mat-divider>
          <div class="notification-footer">
            <button mat-button routerLink="/notifications">Voir toutes</button>
          </div>
        </mat-menu>

        <!-- Menu utilisateur -->
        <mat-menu #userMenu="matMenu" class="user-menu">
          <div class="user-menu-header">
            <div class="user-info">
              <mat-icon class="user-avatar">account_circle</mat-icon>
              <div class="user-details">
                <div class="user-full-name">{{ user?.fullName }}</div>
                <div class="user-email">{{ user?.email }}</div>
                <div class="user-role">{{ getRoleDisplayName(user?.roles?.[0]) }}</div>
              </div>
            </div>
          </div>
          <mat-divider></mat-divider>
          
          <button mat-menu-item routerLink="/profile">
            <mat-icon>person</mat-icon>
            <span>Mon profil</span>
          </button>
          
          <button mat-menu-item routerLink="/settings">
            <mat-icon>settings</mat-icon>
            <span>Paramètres</span>
          </button>
          
          <button mat-menu-item (click)="changePassword()">
            <mat-icon>lock</mat-icon>
            <span>Changer mot de passe</span>
          </button>
          
          <mat-divider></mat-divider>
          
          <button mat-menu-item (click)="logout()">
            <mat-icon>exit_to_app</mat-icon>
            <span>Déconnexion</span>
          </button>
        </mat-menu>
      </div>
    </mat-toolbar>
  `,
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit, OnDestroy {
  user: UserProfile | null = null;
  notificationCount = 0;
  notifications: any[] = [];
  private destroy$ = new Subject<void>();

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.authService.authState$
      .pipe(takeUntil(this.destroy$))
      .subscribe((authState: AuthState) => {
        this.user = authState.user;
      });

    this.loadNotifications();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  toggleSidebar(): void {
    // Émettre un événement pour basculer la sidebar
    // TODO: Implémenter avec un service de layout
  }

  logout(): void {
    this.authService.logout();
  }

  changePassword(): void {
    // TODO: Ouvrir le dialogue de changement de mot de passe
  }

  loadNotifications(): void {
    // TODO: Charger les notifications depuis le service
    this.notifications = [
      {
        icon: 'info',
        title: 'Nouvelle écriture comptable',
        message: 'Une nouvelle écriture a été créée',
        time: 'Il y a 5 minutes'
      },
      {
        icon: 'warning',
        title: 'Rapprochement en attente',
        message: '3 rapprochements nécessitent votre attention',
        time: 'Il y a 1 heure'
      }
    ];
    this.notificationCount = this.notifications.length;
  }

  getRoleDisplayName(role?: string): string {
    if (!role) return '';
    
    const roleNames: { [key: string]: string } = {
      'ROLE_ADMIN': 'Administrateur',
      'ROLE_USER': 'Utilisateur',
      'ROLE_ACCOUNTANT': 'Comptable',
      'ROLE_MANAGER': 'Manager',
      'ROLE_AUDITOR': 'Auditeur',
      'ROLE_HR_MANAGER': 'Manager RH',
      'ROLE_FINANCE_MANAGER': 'Manager Financier',
      'ROLE_IT_ADMIN': 'Admin IT',
      'ROLE_VIEWER': 'Lecteur'
    };

    return roleNames[role] || role;
  }
}
