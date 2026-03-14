import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { ItemService } from '../../services/item.service';

@Component({
  selector: 'app-create-page',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './create-page.component.html',
  styleUrl: './create-page.component.scss'
})
export class CreatePageComponent {
  auth   = inject(AuthService);
  item   = inject(ItemService);
  router = inject(Router);

  form = { title: '', content: '', password: '' };
  saving = false;
  error  = '';

  submit() {
    if (!this.form.title.trim()) { this.error = 'Title is required.'; return; }
    if (!this.form.content.trim()) { this.error = 'Content is required.'; return; }

    this.saving = true;
    this.error  = '';

    const payload: { title: string; content: string; password?: string } = {
      title:   this.form.title.trim(),
      content: this.form.content.trim()
    };

    if (this.form.password.trim()) {
      payload.password = this.form.password.trim();
    }

    this.item.create(payload).subscribe({
      next: (created) => {
        this.router.navigate(['/items', created.hash]);
      },
      error: () => {
        this.error  = 'Failed to create. Try again.';
        this.saving = false;
      }
    });
  }
}
