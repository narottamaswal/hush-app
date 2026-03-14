import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { User } from '../models/item.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private base = '/api';

  user = signal<User | null>(null);
  checked = signal(false);

  loadUser() {
    this.http.get<User>(`${this.base}/me`).subscribe({
      next: (u) => {
        this.user.set(u);
        this.checked.set(true);
      },
      error: () => {
        this.user.set(null);
        this.checked.set(true);
      }
    });
  }

  signIn() {
    window.location.href = '/oauth2/authorization/google';
  }

  signOut() {
    window.location.href = '/api/signout';
  }
}
