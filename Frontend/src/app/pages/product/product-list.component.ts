import { Component, OnInit } from '@angular/core';
import { ProductService } from '../../core/services/product.service';
import { ProductResponse } from '../../core/models/product.models';
import { CartService } from '../../core/services/cart.service';
import { MessageService } from 'primeng/api';
import { getUserIdFromToken } from 'src/app/core/utils/jwt.util';
import { Router } from '@angular/router';

@Component({
  selector: 'app-product-list',
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.scss']
})
export class ProductListComponent implements OnInit {
  products: ProductResponse[] = [];
  selectedProduct?: ProductResponse;
  displayDetails = false;
  userId!: number;

  // Pagination
  totalRecords = 0;
  pageSize = 6;
  currentPage = 0;

  constructor(
    private router: Router,
    private productService: ProductService,
    private cart: CartService,
    private msg: MessageService
  ) {}

  ngOnInit() {
    this.loadProducts();
  }

  loadProducts() {
    this.productService.getAll(this.currentPage, this.pageSize).subscribe(res => {
      this.products = res.content;
      this.totalRecords = res.totalElements;
    });
  }

  onPageChange(event: any) {
    this.currentPage = event.page;
    this.pageSize = event.rows;
    this.loadProducts();
  }

  addToCart(p: ProductResponse) {
    this.userId = getUserIdFromToken() ?? 0;
    
    if (!this.userId) {
      this.msg.add({
        severity: 'error',
        summary: 'Not Logged In',
        detail: 'User not authenticated',
      });
      this.router.navigate(['/login']);
      return;
    }

    this.cart.add(p, 1).subscribe({
      next: () => {
        this.msg.add({
          severity: 'success',
          summary: 'Added to Cart',
          detail: `${p.name} was added`
        });
      },
      error: () => {
        this.msg.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Could not add item'
        });
      }
    });
  }

  showDetails(p: ProductResponse) {
    this.selectedProduct = p;
    this.displayDetails = true;
  }
}
