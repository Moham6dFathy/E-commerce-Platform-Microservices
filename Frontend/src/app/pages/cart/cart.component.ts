import { Component, OnInit } from '@angular/core';
import { CartService} from '../../core/services/cart.service';
import { CartItem } from '../../core/models/cart.models';
import { OrderService } from '../../core/services/order.service';
import { MessageService } from 'primeng/api';
import { ProductResponse } from 'src/app/core/models/product.models';
import { Router } from '@angular/router';
import { getUserIdFromToken } from 'src/app/core/utils/jwt.util';

@Component({
  selector: 'app-cart',
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.scss'],
  providers: [MessageService],
})
export class CartComponent implements OnInit {
  items: CartItem[] = [];
  paymentMethod: string = 'card';

  constructor(
    private cart: CartService,
    private orderService: OrderService,
    private router: Router,
    private message: MessageService
  ) {}

  ngOnInit() {
    // Subscribe to the BehaviorSubject so UI updates automatically
    this.cart.itemsObservable.subscribe(items => {
      this.items = items;
    });

    this.cart.loadCart();
  }

  checkout() {
    const userId = getUserIdFromToken();
    if (!userId) {
      this.message.add({
        severity: 'error',
        summary: 'Not Logged In',
        detail: 'You must log in to place an order',
      });
      this.router.navigate(['/login']);
      return;
    }

    const orderItems = this.cart.toOrderItems();
    this.orderService.placeOrder(orderItems).subscribe({
      next: res => {
        this.cart.clear().subscribe();

        this.router.navigate(['/payment'], {
          queryParams: { orderId: res.id },
        });
      },
      error: err => {
        this.message.add({
          severity: 'error',
          summary: 'Order Failed',
          detail: err.message,
        });
      },
    });
  }

  get total() {
    return this.cart.totalAmount();
  }

  addToCart(product: ProductResponse) {
    this.cart.add(product, 1).subscribe();
  }

  remove(productId: number) {
    this.cart.remove(productId).subscribe();
  }

  updateQty(productId: number, qty: number) {
    this.cart.updateQty(productId, qty).subscribe();
  }
}
