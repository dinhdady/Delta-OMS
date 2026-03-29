import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, switchMap } from 'rxjs/operators';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // Clone request và thêm withCredentials để gửi cookie
    const clonedReq = req.clone({
      withCredentials: true // ✅ Tự động gửi cookie kèm mỗi request
    });

    return next.handle(clonedReq).pipe(
      catchError((error: HttpErrorResponse) => {
        // Nếu gặp lỗi 401 Unauthorized, thử refresh token
        if (error.status === 401 && !req.url.includes('/auth/refresh')) {
          return this.handle401Error(clonedReq, next);
        }
        return throwError(() => error);
      })
    );
  }

  private handle401Error(request: HttpRequest<any>, next: HttpHandler) {
    return this.authService.refreshToken().pipe(
      switchMap(() => {
        // Refresh thành công, thử lại request cũ
        return next.handle(request.clone({ withCredentials: true }));
      }),
      catchError((error) => {
        // Refresh thất bại, logout và chuyển về login
        this.authService.logout().subscribe();
        this.router.navigate(['/login']);
        return throwError(() => error);
      })
    );
  }
}
