import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { ItemService } from '../../services/item.service';
import { Item } from '../../models/item.model';
import {ItemFormComponent} from "../../components/item-form/item-form.component";

type ViewState = 'loading' | 'locked' | 'view' | 'edit' | 'not-found' | 'already-viewed';

@Component({
  selector: 'app-view-page',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, ItemFormComponent],
  templateUrl: './view-page.component.html',
  styleUrl: './view-page.component.scss'
})
export class ViewPageComponent implements OnInit {
  auth       = inject(AuthService);
  itemSvc    = inject(ItemService);
  route      = inject(ActivatedRoute);
  router     = inject(Router);

  hash       = '';
  item: Item | null = null;
  state: ViewState  = 'loading';

  password      = '';
  passwordError = '';

  saving   = false;
  editError = '';
  copied = false;

  showPasswordPrompt = false;

  ngOnInit() {
    this.hash = this.route.snapshot.paramMap.get('hash') ?? '';
    this.load();
  }

  load() {
    this.state = 'loading';
    this.passwordError = '';
    this.itemSvc.get(this.hash,this.password).subscribe({
      next: (item) => {
        this.item  = item;
        this.state = 'view';
      },
      error: (err) => {
        if (err.status===404 || err.status===410){
          this.state = 'not-found';
        }else if (err.status === 403 && err.error?.passwordProtected) {
          this.state = 'locked';
          this.item!.title = err.error?.title;
          this.passwordError = 'Wrong password.';
        } else if (err.status === 401 && err.error?.passwordProtected) {
          this.state = 'locked';
          this.item!.title = err.error?.title;
          this.showPasswordPrompt = true;
        } else if (err.status===410) {
          this.state = 'already-viewed';
        }
      }
    });
  }

  unlock() {
    this.passwordError = '';
    this.itemSvc.unlock(this.hash, this.password).subscribe({
      next: (item) => {
        this.item  = item;
        this.state = 'view';
      },
      error: (err) => {
        if (err.status===404 || err.status===406){
          this.state = 'not-found';
        }else if (err.status===403){
          this.state = 'locked';
          this.passwordError = 'Wrong password.';
        }else{
          this.state = 'not-found';
        }
      }
    });
  }

  startEdit() {
    if (!this.item) return;
    this.editError = '';
    this.state = 'edit';
  }

  cancelEdit() {
    this.state = 'view';
  }

  saveEdit(payload: any) {
    this.saving   = true;
    this.editError = '';

    this.itemSvc.update(this.hash, payload).subscribe({
      next: (updated) => {
        this.item   = updated;
        this.state  = 'view';
        this.saving = false;
        // If alias changed, we might need to update URL, but skipping for simplicity
      },
      error: () => {
        this.editError = 'Failed to save. Try again.';
        this.saving    = false;
      }
    });
  }

  deleteItem() {
    if (!confirm('Delete this hush permanently?')) return;
    this.itemSvc.delete(this.hash).subscribe({
      next: () => this.router.navigate(['/'])
    });
  }

  copyLink() {
    navigator.clipboard.writeText(window.location.href).then(() => {
      this.copied = true;
      setTimeout(() => this.copied = false, 2000);
    });
  }

  isOwner(): boolean {
    return !!this.auth.user() && !!this.item && this.auth.user()!.email === this.item.ownerEmail;
  }

  formatDate(d: string): string {
    return new Date(d).toLocaleDateString('en-IN', { day: 'numeric', month: 'short', year: 'numeric' });
  }
}