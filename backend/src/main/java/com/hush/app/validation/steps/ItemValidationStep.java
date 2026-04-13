package com.hush.app.validation.steps;


import com.hush.app.validation.ItemContext;

public abstract class ItemValidationStep {

    private ItemValidationStep next;

    public ItemValidationStep setNext(ItemValidationStep next) {
        this.next = next;
        return next;
    }

    public abstract void validate(ItemContext ctx);

    protected void proceed(ItemContext ctx) {
        if (next != null && !ctx.isEarlyExit()) {
            next.validate(ctx);
        }
    }
}