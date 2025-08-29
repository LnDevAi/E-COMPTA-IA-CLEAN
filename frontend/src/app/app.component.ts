import { Component, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { ApiService } from './core/services/api';

import { HeaderComponent } from './shared/components/header/header.component';
import { SidebarComponent } from './shared/components/sidebar/sidebar.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    RouterOutlet,
    HeaderComponent,
    SidebarComponent,
    HttpClientModule
  ],
  template: `
    <div class="app-container">
      <!-- Header -->
      <app-header></app-header>
      
      <!-- Sidebar -->
      <app-sidebar></app-sidebar>
      
      <!-- Main content -->
      <main class="main-content">
        <div class="content-wrapper">
          <div class="health-banner" *ngIf="healthStatus">
            Backend: {{ healthStatus.status }} — {{ healthStatus.message }}
          </div>
          <router-outlet></router-outlet>
        </div>
      </main>
    </div>
  `,
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  title = 'E-COMPTA-IA';
  healthStatus: any = null;

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.api.getHealth().subscribe({
      next: (data) => this.healthStatus = data,
      error: () => this.healthStatus = { status: 'DOWN', message: 'Backend unreachable' }
    });
  }
}


