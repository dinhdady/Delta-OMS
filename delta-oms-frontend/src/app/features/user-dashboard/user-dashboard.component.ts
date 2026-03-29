// src/app/features/user-dashboard/user-dashboard.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService, User } from '../../core/services/auth.service';

@Component({
  selector: 'app-user-dashboard',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="dashboard-container">
      <div class="dashboard-header">
        <h1>👤 User Dashboard</h1>
        <div class="user-info">
          <span>Welcome, {{ user?.fullName || user?.username }}</span>
          <button (click)="logout()" class="logout-btn">Logout</button>
        </div>
      </div>

      <div class="content-card">
        <h2>Welcome to SportsHub</h2>
        <p>This is the user dashboard. You have limited access.</p>
        <p>Contact admin for more permissions.</p>
      </div>
    </div>
  `,
  styles: [`
    .dashboard-container {
      min-height: 100vh;
      background: #f5f7fa;
      padding: 24px;
    }

    .dashboard-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 32px;
      background: white;
      padding: 20px 24px;
      border-radius: 16px;
    }

    h1 {
      margin: 0;
      font-size: 24px;
      color: #1a1f2e;
    }

    .user-info {
      display: flex;
      align-items: center;
      gap: 16px;
    }

    .logout-btn {
      padding: 8px 16px;
      background: #e74c3c;
      color: white;
      border: none;
      border-radius: 8px;
      cursor: pointer;
    }

    .content-card {
      background: white;
      border-radius: 16px;
      padding: 32px;
    }

    .content-card h2 {
      margin: 0 0 16px 0;
      color: #1a1f2e;
    }

    .content-card p {
      margin: 8px 0;
      color: #6c757d;
    }
  `]
})
export class UserDashboardComponent implements OnInit {
  user: User | null = null;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.authService.getCurrentUser().subscribe((user: User | null) => {
      this.user = user;
      if (!user) {
        this.router.navigate(['/login']);
      }
    });
  }

  logout(): void {
    this.authService.logout().subscribe(() => {
      this.router.navigate(['/login']);
    });
  }
}
