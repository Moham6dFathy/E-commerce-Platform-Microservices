import { Component } from '@angular/core';
import { AuthService } from '../../core/services/auth.service';
import { Router } from '@angular/router';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html'
})
export class LoginComponent {
  username = '';
  password = '';
  loading = false;

  constructor(private auth: AuthService, private router: Router, private msg: MessageService) {}

  submit() {
    this.loading = true;
    this.auth.login({ username: this.username, password: this.password }).subscribe({
      next: () => { this.msg.add({severity:'success', summary:'Logged in'}); this.router.navigate(['/']); },
      error: err => { this.msg.add({severity:'error', summary:'Login failed', detail: err?.error?.message || 'Error'}); this.loading = false; }
    });
  }
}
