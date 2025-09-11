import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatBadgeModule } from '@angular/material/badge';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatDividerModule } from '@angular/material/divider';
import { Subject, takeUntil } from 'rxjs';

import { AuthService } from '../../../shared/services/auth';
import { NotificationService } from '../../../shared/services/notification';
import { User } from '../../../shared/models/user';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatMenuModule,
    MatBadgeModule,
    MatTooltipModule,
    MatSidenavModule,
    MatDividerModule
  ],
  templateUrl: './header.html',
  styleUrl: './header.scss'
})
export class HeaderComponent implements OnInit, OnDestroy {
  currentUser: User | null = null;
  unreadNotifications = 0;
  private destroy$ = new Subject<void>();

  constructor(
    private authService: AuthService,
    private notificationService: NotificationService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // S'abonner aux changements d'utilisateur
    this.authService.currentUser$
      .pipe(takeUntil(this.destroy$))
      .subscribe(user => {
        this.currentUser = user;
      });

    // S'abonner aux notifications non lues
    this.notificationService.getUnreadCount()
      .pipe(takeUntil(this.destroy$))
      .subscribe(count => {
        this.unreadNotifications = count;
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  toggleSidebar(): void {
    // TODO: Implémenter le toggle de la sidebar
    console.log('Toggle sidebar');
  }

  logout(): void {
    this.authService.logout().subscribe({
      next: () => {
        this.notificationService.showSuccess('Déconnexion réussie');
        this.router.navigate(['/login']);
      },
      error: (error) => {
        this.notificationService.notifyApiError(error);
      }
    });
  }

  openNotifications(): void {
    // TODO: Ouvrir le panneau de notifications
    console.log('Open notifications');
  }

  openUserMenu(): void {
    // TODO: Ouvrir le menu utilisateur
    console.log('Open user menu');
  }

  goToProfile(): void {
    this.router.navigate(['/profile']);
  }

  goToSettings(): void {
    this.router.navigate(['/settings']);
  }

  goToHelp(): void {
    this.router.navigate(['/help']);
  }
}
