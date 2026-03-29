// core/guards/role.guard.ts
import { Injectable } from '@angular/core';
import { Router, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { map, take } from 'rxjs/operators';
import { AuthService, User } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class RoleGuard {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> | Promise<boolean> | boolean {

    const requiredRoles = route.data['roles'] as Array<string>;

    return this.authService.getCurrentUser().pipe(
      take(1),
      map((user: User | null) => {
        if (!user) {
          this.router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
          return false;
        }

        if (requiredRoles && requiredRoles.includes(user.role)) {
          return true;
        }

        this.router.navigate(['/unauthorized']);
        return false;
      })
    );
  }
}
