import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from "../../services/auth.service";
import { ItemService } from "../../services/item.service";
import { Item } from "../../models/item.model";
import {ItemFormComponent} from "../../components/item-form/item-form.component";

@Component({
    selector: 'app-my-items-page',
    standalone: true,
    imports: [CommonModule, RouterLink, ItemFormComponent],
    templateUrl: './list.component.html',
    styleUrl: './list.component.scss'
})
export class ListComponent implements OnInit {
    auth    = inject(AuthService);
    itemSvc = inject(ItemService);

    items: Item[] = [];
    loading = true;

    creating = false;
    createError = '';
    showCreateForm = false;

    editingHash: string | null = null;
    saving = false;
    editError = '';

    ngOnInit() { this.load(); }

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
        this.createError    = '';
    }

    create(payload: any) {
        this.creating    = true;
        this.createError = '';

        this.itemSvc.create(payload).subscribe({
            next: (item) => {
                this.items          = [item, ...this.items];
                this.creating       = false;
                this.showCreateForm = false;
            },
            error: () => {
                this.createError = 'Failed to create. Try again.';
                this.creating    = false;
            }
        });
    }

    startEdit(item: Item) {
        this.editingHash = item.hash;
        this.editError   = '';
    }

    cancelEdit() {
        this.editingHash = null;
        this.editError   = '';
    }

    saveEdit(hash: string, payload: any) {
        this.saving    = true;
        this.editError = '';

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
        return new Date(d).toLocaleDateString('en-IN', { day: 'numeric', month: 'short', year: 'numeric' });
    }
}