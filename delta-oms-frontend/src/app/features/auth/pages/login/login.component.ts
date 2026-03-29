import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService, AuthRequestDTO } from '../../../../core/services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    FormsModule,
    CommonModule
  ],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {

  username: string = '';
  password: string = '';
  loading: boolean = false;
  errorMessage: string = '';
  showPassword: boolean = false;
  rememberMe: boolean = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }

  login() {
    // Reset error message
    this.errorMessage = '';

    // Validate inputs
    if (!this.username || !this.username.trim()) {
      this.errorMessage = 'Please enter username';
      return;
    }

    if (!this.password || !this.password.trim()) {
      this.errorMessage = 'Please enter password';
      return;
    }

    this.loading = true;

    const credentials: AuthRequestDTO = {
      username: this.username,
      password: this.password
    };

    // Gọi API login
    this.authService.login(credentials).subscribe({
      next: (response) => {
        console.log('Login API response:', response);

        // Kiểm tra response thành công
        if (response && response.status === 200) {
          console.log('Login successful, navigating to dashboard...');

          // Điều hướng đến dashboard
          this.router.navigate(['/dashboard']).then(success => {
            if (success) {
              console.log('Navigation to dashboard successful');
              this.loading = false;
            } else {
              console.error('Navigation to dashboard failed');
              this.errorMessage = 'Navigation failed';
              this.loading = false;
            }
          }).catch(err => {
            console.error('Navigation error:', err);
            this.errorMessage = 'Navigation error: ' + err.message;
            this.loading = false;
          });
        } else {
          console.error('Login response not successful:', response);
          this.errorMessage = response?.message || 'Login failed';
          this.loading = false;
        }
      },
      error: (error) => {
        console.error('Login error:', error);
        this.errorMessage = error.message || 'Login failed. Invalid credentials';
        this.loading = false;
      }
    });
  }
}
