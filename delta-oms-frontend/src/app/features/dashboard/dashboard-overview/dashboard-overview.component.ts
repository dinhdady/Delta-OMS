// src/app/features/dashboard/dashboard-overview/dashboard-overview.component.ts

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
        console.log('Statistics response:', res);
        // API trả về: { status, message, data }
        if (res && res.data) {
          this.stats = res.data;
        }
        // Nếu res trực tiếp là object chứa data
        else if (res && !res.status) {
          this.stats = res;
        }
      },
      error: (err: any) => {
        console.error('Error loading statistics:', err);
      }
    });

    // Load recent orders
    this.dashboardService.getRecentOrders(5).subscribe({
      next: (res: any) => {
        console.log('Recent orders response:', res);
        // API trả về: { status, message, data }
        if (res && res.data) {
          this.recentOrders = res.data;
        } else if (res && Array.isArray(res)) {
          this.recentOrders = res;
        } else {
          this.recentOrders = [];
        }
      },
      error: (err: any) => {
        console.error('Error loading recent orders:', err);
        this.recentOrders = [];
      }
    });

    // Load top products
    this.dashboardService.getTopProducts(5).subscribe({
      next: (res: any) => {
        console.log('Top products response:', res);
        // API trả về: { status, message, data }
        if (res && res.data) {
          this.topProducts = res.data;
        } else if (res && Array.isArray(res)) {
          this.topProducts = res;
        } else {
          this.topProducts = [];
        }
        this.loading = false;
      },
      error: (err: any) => {
        console.error('Error loading top products:', err);
        this.topProducts = [];
        this.loading = false;
      }
    });
  }

  loadSalesChart() {
    this.dashboardService.getSalesChartData(this.chartDays).subscribe({
      next: (res: any) => {
        console.log('Chart data response:', res);

        // API trả về: { status, message, data: { months, revenues } }
        if (res && res.data) {
          const data = res.data;
          // Dữ liệu từ API: months và revenues
          if (data.months && data.revenues) {
            this.initChart(data.months, data.revenues);
          }
          // Fallback nếu có cấu trúc khác
          else if (data.labels && data.orders) {
            this.initChart(data.labels, data.revenue || data.orders);
          }
          else {
            // Dữ liệu mẫu fallback
            this.initChartWithFallback();
          }
        } else {
          this.initChartWithFallback();
        }
      },
      error: (err: any) => {
        console.error('Error loading chart data:', err);
        this.initChartWithFallback();
      }
    });
  }

  initChartWithFallback() {
    // Dữ liệu mẫu theo tháng
    this.initChart(
      ['Tháng 1', 'Tháng 2', 'Tháng 3', 'Tháng 4', 'Tháng 5', 'Tháng 6',
       'Tháng 7', 'Tháng 8', 'Tháng 9', 'Tháng 10', 'Tháng 11', 'Tháng 12'],
      [65, 72, 88, 95, 102, 88, 76, 95, 110, 125, 140, 135]
    );
  }

  onChartFilterChange(days: string) {
    this.chartDays = parseInt(days, 10);
    this.loadSalesChart();
  }

  initChart(labels: string[], revenueData: number[]) {
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
            label: 'Doanh thu (VNĐ)',
            data: revenueData,
            borderColor: '#3b82f6',
            backgroundColor: 'rgba(59, 130, 246, 0.1)',
            tension: 0.4,
            fill: true,
            yAxisID: 'y'
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
                  label += new Intl.NumberFormat('vi-VN', {
                    style: 'currency',
                    currency: 'VND'
                  }).format(yValue);
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
              text: 'Doanh thu (VNĐ)'
            },
            beginAtZero: true,
            ticks: {
              callback: function(value) {
                return new Intl.NumberFormat('vi-VN', {
                  style: 'currency',
                  currency: 'VND',
                  minimumFractionDigits: 0,
                  maximumFractionDigits: 0
                }).format(value as number);
              }
            }
          }
        }
      }
    });
  }

  formatCurrency(amount: number): string {
    if (!amount && amount !== 0) return '₫0';
    return new Intl.NumberFormat('vi-VN', {
      style: 'currency',
      currency: 'VND',
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
    // Điều hướng đến trang orders và có thể truyền ID để xem chi tiết
    this.router.navigate(['/dashboard/orders'], { queryParams: { id: order.id } });
  }
}
