package com.hush.app.validation;

import com.hush.app.model.Item;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class ItemContext {
    private final Item item;
    private final String providedPassword;
    private final String deviceFingerprint;
    private final String ownerEmail;

    public ItemContext(Item item, String providedPassword, String deviceFingerprint, String ownerEmail) {
        this.item = item;
        this.providedPassword = providedPassword;
        this.deviceFingerprint = deviceFingerprint;
        this.ownerEmail = ownerEmail;
    }


    public ItemContext(Item item, String providedPassword, String deviceFingerprint, String ownerEmail, boolean updateFingerprint, boolean markViewed, boolean earlyExit, Object earlyExitResult) {
        this.item = item;
        this.providedPassword = providedPassword;
        this.deviceFingerprint = deviceFingerprint;
        this.ownerEmail = ownerEmail;
        this.updateFingerprint = updateFingerprint;
        this.markViewed = markViewed;
        this.earlyExit = earlyExit;
        this.earlyExitResult = earlyExitResult;
    }


    private boolean updateFingerprint = false;
    private boolean markViewed = false;
    private boolean earlyExit = false;

    private Object earlyExitResult = null;
}

