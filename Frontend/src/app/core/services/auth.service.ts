import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import { LoginRequest, LoginResponse, RegisterRequest, RegisterResponse } from '../models/auth.models';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private base = `${environment.apiGateway}/auth`;

  constructor(private http: HttpClient, private router: Router) {}

  login(payload: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.base}/login`, payload).pipe(
      tap(res => this.setToken(res.accessToken))
    );
  }

  register(payload: RegisterRequest) {
    return this.http.post<RegisterResponse>(`${this.base}/register`, payload).pipe(
      tap(res => this.setToken(res.accessToken))
    );
  }

  logout() {
    this.clearToken();
    this.router.navigate(['/']);
  }

  setToken(token: string) { localStorage.setItem('accessToken', token); }
  getToken(): string | null { return localStorage.getItem('accessToken'); }
  clearToken() { localStorage.removeItem('accessToken'); }

  isLoggedIn(): boolean { return !!this.getToken(); }
}
