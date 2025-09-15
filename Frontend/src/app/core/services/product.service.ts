import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { ProductResponse } from '../models/product.models';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ProductService {
  private base = `${environment.apiGateway}/product`;

  constructor(private http: HttpClient) {}

  getAll(page: number = 0, size: number = 10) {
    return this.http.get<{ content: ProductResponse[], totalElements: number, totalPages: number }>(
      `${this.base}?page=${page}&size=${size}`
    );
  }

  getById(id: number) {
    return this.http.get<ProductResponse>(`${this.base}/${id}`);
  }
}
