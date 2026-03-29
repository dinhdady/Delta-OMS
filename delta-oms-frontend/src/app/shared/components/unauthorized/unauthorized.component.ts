// src/app/shared/components/unauthorized/unauthorized.component.ts
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-unauthorized',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="unauthorized-container">
      <div class="unauthorized-card">
        <div class="icon">🔒</div>
        <h1>Access Denied</h1>
        <p>You don't have permission to access this page.</p>
        <p class="role-info">⚠️ This area is restricted to Administrators only.</p>
        <div class="actions">
          <button class="btn btn-primary" (click)="goBack()">Go Back</button>
          <button class="btn btn-secondary" (click)="goToLogin()">Login</button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .unauthorized-container {
      min-height: 100vh;
      display: flex;
      align-items: center;
      justify-content: center;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      padding: 20px;
    }

    .unauthorized-card {
      background: white;
      padding: 48px;
      border-radius: 24px;
      text-align: center;
      max-width: 500px;
    }

    .icon {
      font-size: 64px;
      margin-bottom: 24px;
    }

    h1 {
      font-size: 28px;
      color: #e74c3c;
      margin-bottom: 16px;
    }

    p {
      color: #6c757d;
      margin-bottom: 12px;
    }

    .role-info {
      background: #fff3e0;
      color: #f39c12;
      padding: 12px 16px;
      border-radius: 8px;
      margin: 20px 0;
    }

    .actions {
      display: flex;
      gap: 12px;
      justify-content: center;
      margin-top: 24px;
    }

    .btn {
      padding: 12px 24px;
      border: none;
      border-radius: 8px;
      cursor: pointer;
      font-size: 14px;
    }

    .btn-primary {
      background: #667eea;
      color: white;
    }

    .btn-secondary {
      background: #e2e8f0;
      color: #4a5568;
    }
  `]
})
export class UnauthorizedComponent {
  constructor(private router: Router) {}

  goBack(): void {
    window.history.back();
  }

  goToLogin(): void {
    this.router.navigate(['/login']);
  }
}
