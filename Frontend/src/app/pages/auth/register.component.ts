import { Component } from '@angular/core';
import { AuthService } from '../../core/services/auth.service';
import { Router } from '@angular/router';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html'
})
export class RegisterComponent {
  username = '';
  email = '';
  password = '';
  loading = false;

  constructor(
    private auth: AuthService,
    private router: Router,
    private msg: MessageService
  ) {}

  submit() {
    if (!this.username || !this.email || !this.password) {
      this.msg.add({ severity: 'warn', summary: 'Missing fields', detail: 'Please fill all fields' });
      return;
    }

    this.loading = true;
    this.auth.register({ username: this.username, email: this.email, password: this.password }).subscribe({
      next: () => {
        this.msg.add({ severity: 'success', summary: 'Registered successfully' });
        this.router.navigate(['/']);
      },
      error: err => {
        this.msg.add({ severity: 'error', summary: 'Registration failed', detail: err?.error?.message || 'Error' });
        this.loading = false;
      }
    });
  }
}
