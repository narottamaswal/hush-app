import {Component, inject} from '@angular/core';
import {CommonModule} from '@angular/common';
import {Router} from '@angular/router';
import {AuthService} from '../../services/auth.service';
import {ItemService} from '../../services/item.service';
import {ItemFormComponent} from "../../components/item-form/item-form.component";

@Component({
    selector: 'app-create-page',
    standalone: true,
    imports: [CommonModule, ItemFormComponent],
    templateUrl: './create-page.component.html',
    styleUrl: './create-page.component.scss'
})
export class CreatePageComponent {
    auth = inject(AuthService);
    item = inject(ItemService);
    router = inject(Router);

    saving = false;
    error = '';


    submit(payload: any) {
        this.saving = true;
        this.error = '';
        this.item.create(payload).subscribe({
            next: (created) => {
                this.router.navigate(['/items', created.alias || created.hash]);
            },
            error: () => {
                this.error = 'Failed to create. Try again.';
                this.saving = false;
            }
        });
    }
}