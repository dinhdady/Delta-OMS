// models/order.model.ts
export interface OrderItemRequest {
  productId: number;
  quantity: number;
  unitPrice?: number;
}

export interface OrderRequest {
  customerId: number;
  paymentMethodId: number;
  shippingAddress: string;
  note?: string;
  items: OrderItemRequest[];
}

export interface OrderItemResponse {
  id: number;
  productId: number;
  productName: string;
  productCode: string;
  quantity: number;
  unitPrice: number;
  totalPrice: number;
}

export interface OrderResponse {
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
  items: OrderItemResponse[];
}

export interface ApiResponse<T> {
  status: number;
  message: string;
  data: T;
}
