import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import {AuthService} from "../../services/auth.service";
import {ItemService} from "../../services/item.service";
import {Item} from "../../models/item.model";


@Component({
    selector: 'app-my-items-page',
    standalone: true,
    imports: [CommonModule, FormsModule, RouterLink],
    templateUrl: './list.component.html',
    styleUrl: './list.component.scss'
})
export class ListComponent implements OnInit {
    auth    = inject(AuthService);
    itemSvc = inject(ItemService);

    items: Item[] = [];
    loading = true;

    createForm = { title: '', content: '', password: '' };
    creating = false;
    createError = '';
    showCreateForm = false;

    editingHash: string | null = null;
    editForm = { title: '', content: '', password: '' };
    saving = false;
    editError = '';

    ngOnInit() {
        this.load();
    }

    load() {
        this.loading = true;
        this.itemSvc.getMine().subscribe({
            next: (items) => {
                this.items   = items;
                this.loading = false;
            },
            error: () => { this.loading = false; }
        });
    }

    toggleCreateForm() {
        this.showCreateForm = !this.showCreateForm;
        this.createForm     = { title: '', content: '', password: '' };
        this.createError    = '';
    }

    create() {
        if (!this.createForm.title.trim())   { this.createError = 'Title is required.'; return; }
        if (!this.createForm.content.trim()) { this.createError = 'Content is required.'; return; }

        this.creating    = true;
        this.createError = '';

        const payload: { title: string; content: string; password?: string } = {
            title:   this.createForm.title.trim(),
            content: this.createForm.content.trim()
        };
        if (this.createForm.password.trim()) payload.password = this.createForm.password.trim();

        this.itemSvc.create(payload).subscribe({
            next: (item) => {
                this.items          = [item, ...this.items];
                this.creating       = false;
                this.showCreateForm = false;
                this.createForm     = { title: '', content: '', password: '' };
            },
            error: () => {
                this.createError = 'Failed to create. Try again.';
                this.creating    = false;
            }
        });
    }

    startEdit(item: Item) {
        this.editingHash = item.hash;
        this.editForm    = { title: item.title, content: item.content, password: '' };
        this.editError   = '';
    }

    cancelEdit() {
        this.editingHash = null;
        this.editError   = '';
    }

    saveEdit(hash: string) {
        if (!this.editForm.title.trim())   { this.editError = 'Title is required.'; return; }
        if (!this.editForm.content.trim()) { this.editError = 'Content is required.'; return; }

        this.saving    = true;
        this.editError = '';

        const payload = {
            title:    this.editForm.title.trim(),
            content:  this.editForm.content.trim(),
            password: this.editForm.password
        };

        this.itemSvc.update(hash, payload).subscribe({
            next: (updated) => {
                this.items       = this.items.map(i => i.hash === hash ? updated : i);
                this.editingHash = null;
                this.saving      = false;
            },
            error: () => {
                this.editError = 'Failed to save. Try again.';
                this.saving    = false;
            }
        });
    }

    delete(hash: string) {
        if (!confirm('Delete this hush permanently?')) return;
        this.itemSvc.delete(hash).subscribe({
            next: () => { this.items = this.items.filter(i => i.hash !== hash); }
        });
    }

    formatDate(d: string): string {
        return new Date(d).toLocaleDateString('en-IN', {
            day: 'numeric', month: 'short', year: 'numeric'
        });
    }
}