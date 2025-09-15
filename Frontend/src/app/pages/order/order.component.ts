import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import { OrderService } from '../../core/services/order.service';
import { OrderResponse, OrderItemsResponse } from '../../core/models/order.models';
import { ProductService } from '../../core/services/product.service';
import { ProductResponse } from '../../core/models/product.models';

interface OrderItemView {
  productId: number;
  productName: string;
  quantity: number;
  price: number;
}

@Component({
  selector: 'app-orders',
  templateUrl: './order.component.html',
})
export class OrderComponent implements OnInit {
  orders: (OrderResponse & { items?: OrderItemView[] })[] = [];
  loadingItems: number[] = []; // track which orders are loading

  constructor(
    private orderService: OrderService,
    private productService: ProductService,
    private router: Router
  ) {}

  ngOnInit() {
    this.orderService.getMyOrders().subscribe(res => (this.orders = res));
  }

  checkout(orderId: number) {
    this.router.navigate(['/payment'], { queryParams: { orderId } });
  }

  loadOrderItems(order: OrderResponse & { items?: OrderItemView[] }) {
    if (order.items) return; // already loaded

    this.loadingItems.push(order.id);

    const requests = order.orderItems.map((oi: OrderItemsResponse) =>
      this.productService.getById(oi.productId)
    );

    forkJoin(requests).subscribe(products => {
      order.items = order.orderItems.map((oi, i) => ({
        productId: oi.productId,
        productName: products[i].name,
        quantity: oi.quantity,
        price: oi.price,
      }));

      // remove from loading list
      this.loadingItems = this.loadingItems.filter(id => id !== order.id);
    });
  }
}
