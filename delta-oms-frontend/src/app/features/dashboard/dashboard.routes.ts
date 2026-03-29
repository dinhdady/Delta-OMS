import { Routes } from '@angular/router';
import { DashboardComponent } from './dashboard.component';
import { DashboardOverviewComponent } from './dashboard-overview/dashboard-overview.component';
import { CustomersComponent } from '../customers/customers.component';
import { OrdersComponent } from '../orders/orders.component';
import { ProductsComponent } from '../products/products.component';
import { ReportsComponent } from '../reports/reports.component';
import { SettingsComponent } from '../settings/settings.component';

export const DASHBOARD_ROUTES: Routes = [
  {
    path: '',
    component: DashboardComponent,
    children: [
      { path: '', component: DashboardOverviewComponent },
      { path: 'customers', component: CustomersComponent },
      { path: 'orders', component: OrdersComponent },
      { path: 'products', component: ProductsComponent },
      { path: 'reports', component: ReportsComponent },
      { path: 'settings', component: SettingsComponent }
    ]
  }
];
