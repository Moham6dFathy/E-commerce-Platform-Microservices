export interface PaymentRequest {
  orderId: number;
  userId: number;
}

export interface PaymentResponse {
  id: number;
  orderId: number;
  userId: number;
  status: string;
  paymentDate: string;
  amount: number;
}
