import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { OrderResponse, OrderItemRequest } from '../models/order.models';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class OrderService {
  private base = `${environment.apiGateway}/order`;

  constructor(private http: HttpClient) {}

  placeOrder(items: OrderItemRequest[]): Observable<OrderResponse> {
    return this.http.post<OrderResponse>(`${this.base}`, items);
  }
  
  getOrderById(orderId: number): Observable<OrderResponse> {
    return this.http.get<OrderResponse>(`${this.base}/${orderId}`);
  }

  getMyOrders(): Observable<OrderResponse[]> {
    return this.http.get<OrderResponse[]>(`${this.base}/me`);
  }
}
