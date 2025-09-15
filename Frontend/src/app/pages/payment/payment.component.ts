import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import { PaymentService } from '../../core/services/payment.service';
import { OrderService } from '../../core/services/order.service';
import { ProductService } from '../../core/services/product.service';
import { MessageService } from 'primeng/api';
import { PaymentResponse } from '../../core/models/payment.models';
import { OrderItemsResponse } from '../../core/models/order.models';
import { getUserIdFromToken } from '../../core/utils/jwt.util';

interface OrderItemView {
  productId: number;
  productName: string;
  quantity: number;
  price: number;
}

@Component({
  selector: 'app-payment',
  templateUrl: './payment.component.html',
  providers: [MessageService],
})
export class PaymentComponent implements OnInit {
  orderId!: number;
  userId!: number;
  paymentResult?: PaymentResponse;

  // Form fields
  selectedMethod: string = '';
  cardNumber: string = '';
  expiry: string = '';
  cvv: string = '';

  paymentMethods = [
    { label: 'Credit Card', value: 'card' },
    { label: 'PayPal', value: 'paypal' },
    { label: 'Cash on Delivery', value: 'cod' },
  ];

  // Order items
  orderItems: OrderItemView[] = [];
  loadingItems: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private paymentService: PaymentService,
    private orderService: OrderService,
    private productService: ProductService,
    private message: MessageService
  ) {}

  ngOnInit() {
    this.orderId = Number(this.route.snapshot.queryParamMap.get('orderId'));
    this.userId = getUserIdFromToken() ?? 0;

    if (!this.userId) {
      this.message.add({
        severity: 'error',
        summary: 'Not Logged In',
        detail: 'User not authenticated',
      });
      this.router.navigate(['/login']);
      return;
    }

    if (this.orderId) {
      this.loadOrderItems();
    }
  }

  loadOrderItems() {
    this.loadingItems = true;

    this.orderService.getOrderById(this.orderId).subscribe(order => {
      const requests = order.orderItems.map((oi: OrderItemsResponse) =>
        this.productService.getById(oi.productId)
      );

      forkJoin(requests).subscribe(products => {
        this.orderItems = order.orderItems.map((oi, i) => ({
          productId: oi.productId,
          productName: products[i].name,
          quantity: oi.quantity,
          price: oi.price,
        }));
        this.loadingItems = false;
      });
    });
  }

  pay() {
    if (!this.selectedMethod) {
      this.message.add({
        severity: 'warn',
        summary: 'Missing Payment Method',
        detail: 'Please select a payment method',
      });
      return;
    }

    // Example validation for card method
    if (this.selectedMethod === 'card' && (!this.cardNumber || !this.expiry || !this.cvv)) {
      this.message.add({
        severity: 'warn',
        summary: 'Incomplete Details',
        detail: 'Please enter credit card details',
      });
      return;
    }

    this.paymentService.processPayment(this.orderId, this.userId).subscribe({
      next: res => {
        this.paymentResult = res;
        this.message.add({
          severity: 'success',
          summary: 'Payment Success',
          detail: `Payment #${res.id}, Amount: ${res.amount}`,
        });
      },
      error: err => {
        this.message.add({
          severity: 'error',
          summary: 'Payment Failed',
          detail: err.message,
        });
      },
    });
  }

  goToOrders() {
    this.router.navigate(['/order']);
  }
}
