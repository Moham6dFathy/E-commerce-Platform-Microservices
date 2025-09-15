import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, forkJoin, map, switchMap, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ProductResponse } from '../models/product.models';
import { ProductService } from './product.service';
import { CartResponse , CartItem } from '../models/cart.models';


@Injectable({ providedIn: 'root' })
export class CartService {
  private base = `${environment.apiGateway}/cart`;

  private items$ = new BehaviorSubject<CartItem[]>([]);
  itemsObservable = this.items$.asObservable();

  constructor(private http: HttpClient, private products: ProductService) {}

  /** Load cart from API */
  loadCart() {
    this.http.get<CartResponse>(`${this.base}`).pipe(
      switchMap(cart =>
        forkJoin(
          cart.items.map(it =>
            this.products.getById(it.productId).pipe(
              map(prod => ({
                product: prod,
                quantity: it.quantity
              }))
            )
          )
        )
      )
    ).subscribe(items => this.items$.next(items));
  }

  /** Add item */
  add(product: ProductResponse, quantity = 1) {
    return this.http.post<CartResponse>(`${this.base}/items`, {
      productId: product.id,
      quantity
    }).pipe(
      switchMap(cart =>
        forkJoin(
          cart.items.map(it =>
            this.products.getById(it.productId).pipe(
              map(prod => ({
                product: prod,
                quantity: it.quantity
              }))
            )
          )
        )
      ),
      tap(items => this.items$.next(items))
    );
  }

  /** Update quantity */
  updateQty(productId: number, quantity: number) {
    return this.http.put<CartResponse>(`${this.base}/items/${productId}`, { quantity }).pipe(
      switchMap(cart =>
        forkJoin(
          cart.items.map(it =>
            this.products.getById(it.productId).pipe(
              map(prod => ({
                product: prod,
                quantity: it.quantity
              }))
            )
          )
        )
      ),
      tap(items => this.items$.next(items))
    );
  }

  /** Remove item */
  remove(productId: number) {
    return this.http.delete<CartResponse>(`${this.base}/items/${productId}`).pipe(
      switchMap(cart =>
        forkJoin(
          cart.items.map(it =>
            this.products.getById(it.productId).pipe(
              map(prod => ({
                product: prod,
                quantity: it.quantity
              }))
            )
          )
        )
      ),
      tap(items => this.items$.next(items))
    );
  }

  /** Clear cart */
  clear() {
    return this.http.delete<CartResponse>(`${this.base}`).pipe(
      map(() => []),
      tap(items => this.items$.next(items))
    );
  }

  /** Helpers */
  get items(): CartItem[] {
    return this.items$.value;
  }

  totalAmount(): number {
    return this.items.reduce((s, it) => s + it.product.price * it.quantity, 0);
  }

  toOrderItems() {
    return this.items.map(i => ({
      productId: i.product.id,
      quantity: i.quantity
    }));
  }
}
