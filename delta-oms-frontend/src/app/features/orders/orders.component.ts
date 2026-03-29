// src/app/features/orders/orders.component.ts

import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { OrderService } from '../../core/services/order.service';
import { OrderResponse, OrderItemResponse } from '../../core/models/order.model';

@Component({
  selector: 'app-orders',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './orders.component.html',
  styleUrls: ['./orders.component.scss']
})
export class OrdersComponent implements OnInit {
  orders: OrderResponse[] = [];
  selectedOrder: OrderResponse | null = null;
  searchCode: string = '';
  loading: boolean = false;
  showDetailModal: boolean = false;

  constructor(private orderService: OrderService) {}

  ngOnInit() {
    // Không load tất cả orders vì API không có
  }

  searchOrder() {
    if (!this.searchCode.trim()) {
      alert('Please enter order code');
      return;
    }

    this.loading = true;
    this.orderService.getOrderByCode(this.searchCode).subscribe({
      next: (order: OrderResponse) => {
        this.orders = [order];
        this.loading = false;
      },
      error: (err: any) => {
        console.error('Error loading order:', err);
        alert('Order not found');
        this.orders = [];
        this.loading = false;
      }
    });
  }

  loadOrdersByCustomer(customerId: number) {
    this.loading = true;
    this.orderService.getOrdersByCustomer(customerId).subscribe({
      next: (orders: OrderResponse[]) => {
        this.orders = orders;
        this.loading = false;
      },
      error: (err: any) => {
        console.error('Error loading customer orders:', err);
        alert('Failed to load orders');
        this.loading = false;
      }
    });
  }

  viewOrderDetail(order: OrderResponse) {
    this.selectedOrder = order;
    this.showDetailModal = true;
  }

  closeModal() {
    this.showDetailModal = false;
    this.selectedOrder = null;
  }

  getStatusBadgeClass(status: string): string {
    switch (status?.toLowerCase()) {
      case 'pending': return 'status-pending';
      case 'processing': return 'status-processing';
      case 'completed': return 'status-completed';
      case 'cancelled': return 'status-cancelled';
      default: return 'status-default';
    }
  }

  getPaymentStatusClass(status: string): string {
    switch (status?.toLowerCase()) {
      case 'paid': return 'payment-paid';
      case 'unpaid': return 'payment-unpaid';
      case 'refunded': return 'payment-refunded';
      default: return 'payment-default';
    }
  }

  formatCurrency(amount: number): string {
    return new Intl.NumberFormat('vi-VN', {
      style: 'currency',
      currency: 'VND'
    }).format(amount);
  }

  formatDate(dateStr: string): string {
    if (!dateStr) return '';
    const date = new Date(dateStr);
    return date.toLocaleString('vi-VN');
  }
}
