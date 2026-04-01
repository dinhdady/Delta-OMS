
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { OrderService } from '../../core/services/order.service';
import { OrderResponse } from '../../core/models/order.model';

@Component({
  selector: 'app-orders',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './orders.component.html',
  styleUrls: ['./orders.component.scss']
})
export class OrdersComponent implements OnInit {
  // Data
  allOrders: OrderResponse[] = [];
  filteredOrders: OrderResponse[] = [];
  paginated: OrderResponse[] = [];
  selectedOrder: OrderResponse | null = null;
  orders: any[] | null = null;
  // Search
  searchCode: string = '';
  selectedCategory: any = null;

  // State
  loading: boolean = false;
  showDetailModal: boolean = false;

  // Pagination
  page: number = 1;
  pageSize: number = 10;
  totalPages: number = 1;

  constructor(private orderService: OrderService) {}

  ngOnInit(): void {
    this.loadOrders();
  }

  // src/app/features/orders/orders.component.ts

loadOrders() {
  this.loading = true;
  this.orderService.getAllOrders().subscribe({
    next: (res: any) => {
      console.log('API Response:', res); // Debug

      // ✅ Fix: Gán đúng dữ liệu
      // API trả về trực tiếp array
      if (Array.isArray(res)) {
        this.allOrders = res;
        this.filteredOrders = res;
        this.orders = res;
      }
      // Trường hợp API trả về object chứa data array
      else if (res && Array.isArray(res.data)) {
        this.allOrders = res.data;
        this.filteredOrders = res.data;
        this.orders = res.data;
      }

      // Cập nhật phân trang
      this.updatePagination();
      this.loading = false;

      console.log('Orders after assignment:', this.orders);
      console.log('Filtered orders:', this.filteredOrders);
      console.log('Paginated:', this.paginated);
    },
    error: (err) => {
      console.error('Error loading orders:', err);
      this.loading = false;
    }
  });
}
  // Search (filter client-side theo orderCode + customerName)
  searchOrder(): void {
    const term = this.searchCode.trim().toLowerCase();
    if (!term) {
      this.filteredOrders = [...this.allOrders];
    } else {
      this.filteredOrders = this.allOrders.filter(o =>
        o.orderCode?.toLowerCase().includes(term) ||
        o.customerName?.toLowerCase().includes(term)
      );
    }
    this.page = 1;
    this.updatePagination();
  }

  // Load theo customer
  loadOrdersByCustomer(customerId: number): void {
    this.loading = true;
    this.orderService.getOrdersByCustomer(customerId).subscribe({
      next: (orders: OrderResponse[]) => {
        this.allOrders = orders;
        this.filteredOrders = [...orders];
        this.page = 1;
        this.updatePagination();
        this.loading = false;
      },
      error: (err: any) => {
        console.error('Error loading customer orders:', err);
        this.loading = false;
      }
    });
  }

  // Modal
  viewOrderDetail(order: OrderResponse): void {
    this.selectedOrder = order;
    this.showDetailModal = true;
  }

  closeModal(): void {
    this.showDetailModal = false;
    this.selectedOrder = null;
  }

  // Pagination
  updatePagination(): void {
    this.totalPages = Math.max(1, Math.ceil(this.filteredOrders.length / this.pageSize));
    if (this.page > this.totalPages) this.page = this.totalPages;
    const start = (this.page - 1) * this.pageSize;
    this.paginated = this.filteredOrders.slice(start, start + this.pageSize);
  }

  prevPage(): void {
    if (this.page > 1) {
      this.page--;
      this.updatePagination();
    }
  }

  nextPage(): void {
    if (this.page < this.totalPages) {
      this.page++;
      this.updatePagination();
    }
  }

  goToPage(p: number): void {
    this.page = p;
    this.updatePagination();
  }

  getPageNumbers(): number[] {
    const pages: number[] = [];
    const total = this.totalPages;
    const cur = this.page;
    if (total <= 7) {
      for (let i = 1; i <= total; i++) pages.push(i);
    } else {
      pages.push(1);
      if (cur > 3) pages.push(-1);
      for (let i = Math.max(2, cur - 1); i <= Math.min(total - 1, cur + 1); i++) pages.push(i);
      if (cur < total - 2) pages.push(-1);
      pages.push(total);
    }
    return pages;
  }

  min(a: number, b: number): number {
    return Math.min(a, b);
  }

  // Badge helpers
  getStatusBadgeClass(status: string): string {
    switch (status?.toLowerCase()) {
      case 'pending':    return 'status-badge status-pending';
      case 'processing': return 'status-badge status-processing';
      case 'completed':  return 'status-badge status-completed';
      case 'cancelled':  return 'status-badge status-cancelled';
      default:           return 'status-badge status-default';
    }
  }

  getPaymentStatusClass(status: string): string {
    switch (status?.toLowerCase()) {
      case 'paid':     return 'payment-badge payment-paid';
      case 'unpaid':   return 'payment-badge payment-unpaid';
      case 'refunded': return 'payment-badge payment-refunded';
      default:         return 'payment-badge payment-default';
    }
  }

  formatCurrency(amount: number): string {
    if (amount == null) return '—';
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount);
  }

  formatDate(dateStr: string): string {
    if (!dateStr) return '—';
    return new Date(dateStr).toLocaleString('vi-VN');
  }
}
