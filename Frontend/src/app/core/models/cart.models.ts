import { ProductResponse } from "./product.models";

export interface CartResponse {
  userId: number;
  items: {
    cartItemId: string;
    userId: number;
    productId: number;
    quantity: number;
    expiryTime: string;
  }[];
  totalItems: number;
}


export interface CartItem {
  product: ProductResponse;
  quantity: number;
}