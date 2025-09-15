import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';
import { PaymentRequest, PaymentResponse } from '../models/payment.models';

@Injectable({ providedIn: 'root' })
export class PaymentService {
  private base = `${environment.apiGateway}/payment`;

  constructor(private http: HttpClient) {}

  processPayment(orderId: number, userId: number): Observable<PaymentResponse> {
    const req: PaymentRequest = { orderId, userId };
    return this.http.post<PaymentResponse>(this.base, req);
  }
}
