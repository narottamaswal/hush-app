import { Component, Input, Output, EventEmitter, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { Observable, of, timer } from 'rxjs';
import { switchMap, map, catchError } from 'rxjs/operators';
import { ItemService } from '../../services/item.service';

@Component({
    selector: 'app-item-form',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule],
    templateUrl: './item-form.component.html',
    styleUrl: './item-form.component.scss'
})
export class ItemFormComponent implements OnInit {
    private fb = inject(FormBuilder);
    private itemSvc = inject(ItemService); // Used for async alias validation

    @Input() isEdit = false;
    @Input() initialData: any = null;
    @Input() saving = false;
    @Input() externalError = '';
    @Input() showCancel = true;

    @Output() submitForm = new EventEmitter<any>();
    @Output() cancelForm = new EventEmitter<void>();

    form!: FormGroup;

    ngOnInit() {
        this.form = this.fb.group({
            title: [this.initialData?.title || '', [Validators.required, Validators.maxLength(200)]],
            content: [this.initialData?.content || '', [Validators.required]],
            password: [''],
            viewOnce: [this.initialData?.viewOnce || false],
            noForward: [this.initialData?.noForward || false],
            alias: [
                this.initialData?.alias || '',
                [Validators.pattern(/^[a-zA-Z0-9-]+$/)], // Only alphanumeric and hyphens
                [this.aliasAsyncValidator.bind(this)]    // Async validator
            ]
        });
    }

    // Asynchronous validator to check if alias exists
    aliasAsyncValidator(control: AbstractControl): Observable<ValidationErrors | null> {
        if (!control.value) return of(null);
        // If in edit mode and alias hasn't changed, it's valid
        if (this.isEdit && control.value === this.initialData?.alias) return of(null);

        // Debounce the API call by 500ms so we don't spam the server on every keystroke
        return timer(500).pipe(
            switchMap(() => this.itemSvc.checkAlias(control.value)), // Assuming checkAlias returns Observable<boolean> (true = available)
            map(isAvailable => isAvailable ? null : { aliasTaken: true }),
            catchError(() => of(null)) // On network error, fail open or handle accordingly
        );
    }

    onSubmit() {
        if (this.form.invalid) {
            this.form.markAllAsTouched();
            return;
        }

        const payload = this.form.getRawValue();
        // Clean up empty password so we don't overwrite with blank
        if (!payload.password) {
            delete payload.password;
        }

        this.submitForm.emit(payload);
    }

    onCancel() {
        this.cancelForm.emit();
    }
}