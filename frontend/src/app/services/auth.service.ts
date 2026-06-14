import {inject, Injectable, signal} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {User} from '../models/item.model';

@Injectable({providedIn: 'root'})
export class AuthService {
    private http = inject(HttpClient);
    private base = '/api';

    // Starts as `undefined` (= "not yet checked").
    // The authGuard filters on `undefined` and waits until this is resolved,
    // so it always sees the server-verified value, never a stale localStorage cache.
    private _user = signal<User | null | undefined>(undefined);
    readonly user = this._user.asReadonly();

    checked = signal(false);

    constructor() {
        // Kick off the real server check immediately so the guard never has to
        // wait longer than one /api/me round-trip.
        this.loadUser();
    }

    loadUser() {
        this.http.get<User>(`${this.base}/me`).subscribe({
            next: (user) => {
                localStorage.setItem('user_session', JSON.stringify(user));
                this._user.set(user);
                this.checked.set(true);
            },
            error: () => {
                localStorage.removeItem('user_session');
                this._user.set(null);
                this.checked.set(true);
            }
        });
    }

    signIn() {
        window.location.href = '/oauth2/authorization/google';
    }

    signOut() {
        window.location.href = '/api/signout';
        localStorage.removeItem('user_session');
        this._user.set(null);
    }
}
