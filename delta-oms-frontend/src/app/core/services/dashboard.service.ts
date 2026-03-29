import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// Request DTOs
export interface CustomerRequestDTO {
  code: string;
  name: string;
  phone: string;
  email: string;
  taxCode?: string;
  customerTypeId: number;
}

export interface ProductRequestDTO {
  sku: string;
  name: string;
  description: string;
  importPrice: number;
  salePrice: number;
  quantity: number;
  status: string;
  categoryId: number;
  unitId: number;
}

export interface OrderRequestDTO {
  customerId: number;
  paymentMethodId: number;
  shippingAddress: string;
  note?: string;
  items: OrderItemRequestDTO[];
}

export interface OrderItemRequestDTO {
  productId: number;
  quantity: number;
  unitPrice: number;
}

// Response DTOs
export interface CustomerResponseDTO {
  id: number;
  code: string;
  name: string;
  phone: string;
  email: string;
  taxCode: string;
  customerTypeName: string;
}

export interface ProductResponseDTO {
  id: number;
  sku: string;
  name: string;
  description: string;
  salePrice: number;
  quantity: number;
  status: string;
  categoryName: string;
  unitName: string;
}

export interface OrderResponseDTO {
  orderCode: string;
  orderDate: string;
  totalAmount: number;
  discountAmount: number;
  finalAmount: number;
  orderStatus: string;
  paymentStatus: string;
  customerName: string;
  createdBy: string;
  paymentMethod: string;
  items: OrderItemResponseDTO[];
}

export interface OrderItemResponseDTO {
  productId: number;
  productName: string;
  quantity: number;
  unitPrice: number;
  totalPrice: number;
}

export interface CategoryResponseDTO {
  id: number;
  name: string;
}

export interface StatisticsResponse {
  totalCustomers: number;
  totalOrders: number;
  totalProducts: number;
  totalRevenue: number;
}

export interface ApiResponse<T> {
  status: number;
  message: string;
  data: T;
}

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  // Statistics
  getStatistics(): Observable<ApiResponse<StatisticsResponse>> {
    return this.http.get<ApiResponse<StatisticsResponse>>(`${this.apiUrl}/dashboard/statistics`, {
      withCredentials: true
    });
  }

  // Customers
  getCustomers(): Observable<ApiResponse<CustomerResponseDTO[]>> {
    return this.http.get<ApiResponse<CustomerResponseDTO[]>>(`${this.apiUrl}/customers`, {
      withCredentials: true
    });
  }

  getCustomerById(id: number): Observable<ApiResponse<CustomerResponseDTO>> {
    return this.http.get<ApiResponse<CustomerResponseDTO>>(`${this.apiUrl}/customers/${id}`, {
      withCredentials: true
    });
  }

  createCustomer(customer: CustomerRequestDTO): Observable<ApiResponse<CustomerResponseDTO>> {
    return this.http.post<ApiResponse<CustomerResponseDTO>>(`${this.apiUrl}/customers`, customer, {
      withCredentials: true
    });
  }

  updateCustomer(id: number, customer: CustomerRequestDTO): Observable<ApiResponse<CustomerResponseDTO>> {
    return this.http.put<ApiResponse<CustomerResponseDTO>>(`${this.apiUrl}/customers/${id}`, customer, {
      withCredentials: true
    });
  }

  deleteCustomer(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/customers/${id}`, {
      withCredentials: true
    });
  }

  // Products
  getProducts(): Observable<ApiResponse<ProductResponseDTO[]>> {
    return this.http.get<ApiResponse<ProductResponseDTO[]>>(`${this.apiUrl}/products`, {
      withCredentials: true
    });
  }

  getProductById(id: number): Observable<ApiResponse<ProductResponseDTO>> {
    return this.http.get<ApiResponse<ProductResponseDTO>>(`${this.apiUrl}/products/${id}`, {
      withCredentials: true
    });
  }

  createProduct(product: ProductRequestDTO): Observable<ApiResponse<ProductResponseDTO>> {
    return this.http.post<ApiResponse<ProductResponseDTO>>(`${this.apiUrl}/products`, product, {
      withCredentials: true
    });
  }

  updateProduct(id: number, product: ProductRequestDTO): Observable<ApiResponse<ProductResponseDTO>> {
    return this.http.put<ApiResponse<ProductResponseDTO>>(`${this.apiUrl}/products/${id}`, product, {
      withCredentials: true
    });
  }

  deleteProduct(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/products/${id}`, {
      withCredentials: true
    });
  }

  // Orders
  getOrders(): Observable<ApiResponse<OrderResponseDTO[]>> {
    return this.http.get<ApiResponse<OrderResponseDTO[]>>(`${this.apiUrl}/orders`, {
      withCredentials: true
    });
  }

  getOrderById(id: number): Observable<ApiResponse<OrderResponseDTO>> {
    return this.http.get<ApiResponse<OrderResponseDTO>>(`${this.apiUrl}/orders/${id}`, {
      withCredentials: true
    });
  }

  createOrder(order: OrderRequestDTO): Observable<ApiResponse<OrderResponseDTO>> {
    return this.http.post<ApiResponse<OrderResponseDTO>>(`${this.apiUrl}/orders`, order, {
      withCredentials: true
    });
  }

  updateOrderStatus(id: number, status: string): Observable<ApiResponse<OrderResponseDTO>> {
    return this.http.patch<ApiResponse<OrderResponseDTO>>(`${this.apiUrl}/orders/${id}/status`, { status }, {
      withCredentials: true
    });
  }

  deleteOrder(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/orders/${id}`, {
      withCredentials: true
    });
  }

  // Categories
  getCategories(): Observable<ApiResponse<CategoryResponseDTO[]>> {
    return this.http.get<ApiResponse<CategoryResponseDTO[]>>(`${this.apiUrl}/categories`, {
      withCredentials: true
    });
  }

  // Recent Orders for Dashboard
  getRecentOrders(limit: number = 5): Observable<ApiResponse<OrderResponseDTO[]>> {
    return this.http.get<ApiResponse<OrderResponseDTO[]>>(`${this.apiUrl}/orders/recent?limit=${limit}`, {
      withCredentials: true
    });
  }

  // Top Products for Dashboard
  getTopProducts(limit: number = 5): Observable<ApiResponse<ProductResponseDTO[]>> {
    return this.http.get<ApiResponse<ProductResponseDTO[]>>(`${this.apiUrl}/products/top?limit=${limit}`, {
      withCredentials: true
    });
  }

  // Sales Chart Data
  getSalesChartData(days: number = 7): Observable<ApiResponse<any>> {
    return this.http.get<ApiResponse<any>>(`${this.apiUrl}/dashboard/sales-chart?days=${days}`, {
      withCredentials: true
    });
  }
}
