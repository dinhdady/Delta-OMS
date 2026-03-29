import { Routes } from '@angular/router';
import { AuthGuard } from './core/guards/auth.guard';
import { OrdersComponent } from './features/orders/orders.component';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () => import('./features/auth/pages/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'dashboard',
    loadChildren: () => import('./features/dashboard/dashboard.routes').then(m => m.DASHBOARD_ROUTES),
    canActivate: [AuthGuard]
  },
  {
    path: '',
    redirectTo: '/login',
    pathMatch: 'full'
  },
  {
    path: '**',
    redirectTo: '/login'
  },
  { path: 'orders', component: OrdersComponent },
  { path: 'orders/:code', component: OrdersComponent },
];
