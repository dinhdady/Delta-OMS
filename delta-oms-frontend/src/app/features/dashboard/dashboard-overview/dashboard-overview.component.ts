import { Component, OnInit, AfterViewInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { Router } from '@angular/router';
import Chart from 'chart.js/auto';
import { DashboardService } from '../../../core/services/dashboard.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-dashboard-overview',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard-overview.component.html',
  styleUrls: ['./dashboard-overview.component.scss']
})
export class DashboardOverviewComponent implements OnInit, AfterViewInit {
  stats: any = {
    totalCustomers: 0,
    totalOrders: 0,
    totalProducts: 0,
    totalRevenue: 0
  };

  recentOrders: any[] = [];
  topProducts: any[] = [];
  currentDate: Date = new Date();
  loading: boolean = false;
  username: string = '';
  chartDays: number = 7;

  salesChart: any;

  constructor(
    private dashboardService: DashboardService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit() {
    this.username = this.authService.getUsername() || 'Admin';
    this.loadData();
  }

  ngAfterViewInit() {
    setTimeout(() => {
      this.loadSalesChart();
    }, 500);
  }

  loadData() {
    this.loading = true;

    // Load statistics
    this.dashboardService.getStatistics().subscribe({
      next: (res: any) => {
        if (res && res.data) {
          this.stats = res.data;
        }
      },
      error: (err: any) => {
        console.error('Error loading statistics:', err);
      }
    });

    // Load recent orders
    this.dashboardService.getRecentOrders(5).subscribe({
      next: (res: any) => {
        this.recentOrders = res.data || [];
      },
      error: (err: any) => {
        console.error('Error loading recent orders:', err);
      }
    });

    // Load top products
    this.dashboardService.getTopProducts(5).subscribe({
      next: (res: any) => {
        this.topProducts = res.data || [];
        this.loading = false;
      },
      error: (err: any) => {
        console.error('Error loading top products:', err);
        this.loading = false;
      }
    });
  }

  loadSalesChart() {
    this.dashboardService.getSalesChartData(this.chartDays).subscribe({
      next: (res: any) => {
        const data = res.data || {
          labels: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
          orders: [65, 72, 88, 95, 102, 88, 76],
          revenue: [450, 520, 680, 750, 890, 720, 580]
        };
        this.initChart(data.labels, data.orders, data.revenue);
      },
      error: (err: any) => {
        console.error('Error loading chart data:', err);
        this.initChart(
          ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
          [65, 72, 88, 95, 102, 88, 76],
          [450, 520, 680, 750, 890, 720, 580]
        );
      }
    });
  }

  onChartFilterChange(days: string) {
    this.chartDays = parseInt(days);
    this.loadSalesChart();
  }

  initChart(labels: string[], ordersData: number[], revenueData: number[]) {
    const canvas = document.getElementById('salesChart') as HTMLCanvasElement;
    if (!canvas) return;

    if (this.salesChart) {
      this.salesChart.destroy();
    }

    this.salesChart = new Chart(canvas, {
      type: 'line',
      data: {
        labels: labels,
        datasets: [
          {
            label: 'Orders',
            data: ordersData,
            borderColor: '#667eea',
            backgroundColor: 'rgba(102, 126, 234, 0.1)',
            tension: 0.4,
            fill: true,
            yAxisID: 'y'
          },
          {
            label: 'Revenue ($)',
            data: revenueData,
            borderColor: '#f093fb',
            backgroundColor: 'rgba(240, 147, 251, 0.1)',
            tension: 0.4,
            fill: true,
            yAxisID: 'y1'
          }
        ]
      },
      options: {
        responsive: true,
        maintainAspectRatio: true,
        interaction: {
          mode: 'index',
          intersect: false,
        },
        plugins: {
          legend: {
            position: 'top',
          },
          tooltip: {
            callbacks: {
              label: function(context) {
                let label = context.dataset.label || '';
                if (label) {
                  label += ': ';
                }
                const yValue = context.parsed.y;
                if (yValue !== null && yValue !== undefined) {
                  if (context.dataset.label?.includes('Revenue')) {
                    label += '$' + yValue.toLocaleString();
                  } else {
                    label += yValue;
                  }
                }
                return label;
              }
            }
          }
        },
        scales: {
          y: {
            type: 'linear',
            display: true,
            position: 'left',
            title: {
              display: true,
              text: 'Orders'
            },
            beginAtZero: true
          },
          y1: {
            type: 'linear',
            display: true,
            position: 'right',
            title: {
              display: true,
              text: 'Revenue ($)'
            },
            grid: {
              drawOnChartArea: false,
            },
            beginAtZero: true
          },
        }
      }
    });
  }

  formatCurrency(amount: number): string {
    if (!amount) return '$0';
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
      minimumFractionDigits: 0,
      maximumFractionDigits: 0
    }).format(amount);
  }

  getStatusClass(status: string): string {
    switch (status?.toLowerCase()) {
      case 'completed': return 'completed';
      case 'processing': return 'processing';
      case 'pending': return 'pending';
      case 'shipped': return 'shipped';
      case 'cancelled': return 'cancelled';
      default: return '';
    }
  }

  navigateTo(tab: string) {
    this.router.navigate(['/dashboard', tab]);
  }

  viewOrder(order: any) {
    alert(`Order Details:
Order Code: ${order.orderCode}
Customer: ${order.customerName}
Date: ${new Date(order.orderDate).toLocaleDateString()}
Total: ${this.formatCurrency(order.finalAmount)}
Status: ${order.orderStatus}
Payment Status: ${order.paymentStatus}`);
  }
}
