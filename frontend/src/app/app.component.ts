import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    RouterOutlet
  ],
  template: `
    <div class="app-container">
      <header class="app-header">
        <h1>E-COMPTA-IA</h1>
        <p>Test de Connexion Backend</p>
      </header>
      
      <main class="app-main">
        <router-outlet></router-outlet>
      </main>
    </div>
  `,
  styles: [`
    .app-container {
      min-height: 100vh;
      background-color: #f5f5f5;
    }
    
    .app-header {
      background-color: #1976d2;
      color: white;
      padding: 20px;
      text-align: center;
      box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    }
    
    .app-header h1 {
      margin: 0;
      font-size: 2rem;
    }
    
    .app-header p {
      margin: 5px 0 0 0;
      opacity: 0.9;
    }
    
    .app-main {
      padding: 20px;
    }
  `]
})
export class AppComponent {
  title = 'E-COMPTA-IA';
}