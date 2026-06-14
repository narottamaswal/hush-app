package com.hush.app.validation;

import com.hush.app.service.HashService;
import com.hush.app.validation.steps.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ItemValidationChainFactory {

    private final HashService hashService;

    public ItemValidationStep buildDefaultChain() {
        ItemValidationStep expiry     = new ExpiredValidationStep();
        ItemValidationStep password   = new PasswordValidationStep(hashService);
        ItemValidationStep owner      = new OwnerValidationStep();
        ItemValidationStep viewOnce   = new ViewOnceValidationStep();
        ItemValidationStep fingerprint = new FingerprintValidationStep();
        expiry.setNext(password).setNext(owner).setNext(viewOnce).setNext(fingerprint);
        return expiry;
    }
}