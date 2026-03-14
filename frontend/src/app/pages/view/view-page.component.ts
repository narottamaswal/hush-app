import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { ItemService } from '../../services/item.service';
import { Item } from '../../models/item.model';

type ViewState = 'loading' | 'locked' | 'view' | 'edit' | 'not-found';

@Component({
  selector: 'app-view-page',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
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

  editForm = { title: '', content: '', password: '' };
  saving   = false;
  editError = '';

  copied = false;

  ngOnInit() {
    this.hash = this.route.snapshot.paramMap.get('hash') ?? '';
    this.load();
  }

  load() {
    this.state = 'loading';

    this.itemSvc.get(this.hash).subscribe({
      next: (item) => {
        this.item  = item;
        this.state = 'view';
      },
      error: (err) => {
        if (err.status === 403 || err.status === 404) {
          this.itemSvc.getMeta(this.hash).subscribe({
            next: (meta) => {
              this.item  = meta;
              this.state = meta.passwordProtected ? 'locked' : 'not-found';
            },
            error: () => { this.state = 'not-found'; }
          });
        } else {
          this.state = 'not-found';
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
      error: () => {
        this.passwordError = 'Wrong password.';
      }
    });
  }

  startEdit() {
    if (!this.item) return;
    this.editForm = { title: this.item.title, content: this.item.content, password: '' };
    this.editError = '';
    this.state = 'edit';
  }

  cancelEdit() {
    this.state = 'view';
  }

  saveEdit() {
    if (!this.editForm.title.trim()) { this.editError = 'Title is required.'; return; }
    if (!this.editForm.content.trim()) { this.editError = 'Content is required.'; return; }

    this.saving   = true;
    this.editError = '';

    const payload: { title: string; content: string; password?: string } = {
      title:   this.editForm.title.trim(),
      content: this.editForm.content.trim(),
      password: this.editForm.password
    };

    this.itemSvc.update(this.hash, payload).subscribe({
      next: (updated) => {
        this.item   = updated;
        this.state  = 'view';
        this.saving = false;
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
