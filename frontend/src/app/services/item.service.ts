import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Item } from '../models/item.model';

@Injectable({ providedIn: 'root' })
export class ItemService {
  private http = inject(HttpClient);
  private base = '/api/items';

  getMine(): Observable<Item[]> {
    return this.http.get<Item[]>(`${this.base}/mine`);
  }
  create(payload: { title: string; content: string; password?: string }): Observable<Item> {
    return this.http.post<Item>(this.base, payload);
  }

  getMeta(hash: string): Observable<Item> {
    return this.http.get<Item>(`${this.base}/${hash}/meta`);
  }

  get(hash: string): Observable<Item> {
    return this.http.get<Item>(`${this.base}/${hash}`);
  }

  unlock(hash: string, password: string): Observable<Item> {
    return this.http.post<Item>(`${this.base}/${hash}/unlock`, { password });
  }

  update(hash: string, payload: { title: string; content: string; password?: string }): Observable<Item> {
    return this.http.put<Item>(`${this.base}/${hash}`, payload);
  }

  delete(hash: string): Observable<void> {
    return this.http.delete<void>(`${this.base}/${hash}`);
  }
}
