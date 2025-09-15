// app.component.ts
import { Component } from '@angular/core';
import { AuthService } from '../app/core/services/auth.service';  
import { CartService } from '../app/core/services/cart.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  cartCount = 0;

  constructor(public auth: AuthService, private cart: CartService) {}

  ngOnInit() {
    this.cart.itemsObservable.subscribe(items => {
      this.cartCount = items.reduce((sum, i) => sum + i.quantity, 0);
    });
  }

  logout() {
    this.auth.logout();
  }
}
