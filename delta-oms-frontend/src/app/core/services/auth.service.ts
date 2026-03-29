import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, tap, map } from 'rxjs/operators';

export interface AuthRequestDTO {
  username: string;
  password: string;
}

export interface ApiResponse<T> {
  status: number;
  message: string;
  data: T;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/auth';
  private rememberMeFlag: boolean = false;

  constructor(private http: HttpClient) {}

  set rememberMe(value: boolean) {
    this.rememberMeFlag = value;
  }

  login(credentials: AuthRequestDTO): Observable<ApiResponse<any>> {
    console.log('Calling login API with:', credentials);

    return this.http.post<ApiResponse<any>>(`${this.apiUrl}/login`, credentials, {
      withCredentials: true
    }).pipe(
      tap(response => {
        console.log('Login API raw response:', response);

        if (response && response.status === 200) {
          console.log('Login successful');

          // Lưu thông tin user
          localStorage.setItem('username', credentials.username);
          localStorage.setItem('isLoggedIn', 'true');

          // Lưu remember me nếu được chọn
          if (this.rememberMeFlag) {
            localStorage.setItem('rememberMe', 'true');
          }

          // Lưu token nếu có
          if (response.data) {
            localStorage.setItem('accessToken', response.data);
          }
        }
      }),
      catchError(this.handleError)
    );
  }

  logout(): Observable<ApiResponse<any>> {
    return this.http.post<ApiResponse<any>>(`${this.apiUrl}/logout`, {}, {
      withCredentials: true
    }).pipe(
      tap(() => {
        localStorage.removeItem('username');
        localStorage.removeItem('isLoggedIn');
        localStorage.removeItem('rememberMe');
        localStorage.removeItem('accessToken');
      }),
      catchError(this.handleError)
    );
  }

  isLoggedIn(): boolean {
    return localStorage.getItem('isLoggedIn') === 'true';
  }

  getUsername(): string | null {
    return localStorage.getItem('username');
  }

  private handleError(error: HttpErrorResponse) {
    console.error('HTTP Error:', error);
    let errorMessage = 'An error occurred';

    if (error.error instanceof ErrorEvent) {
      errorMessage = error.error.message;
    } else {
      errorMessage = error.error?.message || `Error Code: ${error.status}\nMessage: ${error.message}`;
    }

    return throwError(() => new Error(errorMessage));
  }
}
