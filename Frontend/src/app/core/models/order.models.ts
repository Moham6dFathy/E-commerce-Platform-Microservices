export interface OrderItemRequest {
  productId: number;
  quantity: number;
}

export interface OrderItemsResponse {
  productId: number;
  quantity: number;
  price: number;
}

export interface OrderResponse {
  message: string;
  id: number;
  userId: number;
  orderItems: OrderItemsResponse[];
  orderDate: string;
  orderStatus: string;
  totalAmount: number;
}
