// src/app/services/order.service.ts

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { OrderRequest, OrderResponse } from '../models/order.model';

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private apiUrl = 'http://localhost:8080/api/orders';

  constructor(private http: HttpClient) {}

  createOrder(orderData: OrderRequest): Observable<OrderResponse> {
    return this.http.post<OrderResponse>(`${this.apiUrl}`, orderData, {
      withCredentials: true
    });
  }
  getAllOrders(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl, {
      withCredentials: true
    });
  }
  getOrderByCode(orderCode: string): Observable<OrderResponse> {
    return this.http.get<OrderResponse>(`${this.apiUrl}/${orderCode}`, {
      withCredentials: true
    });
  }

  getOrdersByCustomer(customerId: number): Observable<OrderResponse[]> {
    return this.http.get<OrderResponse[]>(`${this.apiUrl}/customer/${customerId}`, {
      withCredentials: true
    });
  }
}
