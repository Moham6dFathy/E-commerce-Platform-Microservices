import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { getUserIdFromToken } from '../utils/jwt.util';
import { MessageService } from 'primeng/api';

@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {
  constructor(private router: Router, private message: MessageService) {}

  canActivate(): boolean {
    const userId = getUserIdFromToken();
    if (!userId) {
      this.message.add({
        severity: 'warn',
        summary: 'Access Denied',
        detail: 'You must be logged in to access this page',
      });
      this.router.navigate(['/login']);
      return false;
    }
    return true;
  }
}
