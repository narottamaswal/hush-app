package com.hush.app.validation.steps;

import com.hush.app.model.Item;
import com.hush.app.validation.ItemContext;
import com.hush.app.validation.exceptions.PostValidationException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class FingerprintValidationStep extends ItemValidationStep {

    @Override
    public void validate(ItemContext context) {
        Item item = context.getItem();
        String fingerprint = context.getDeviceFingerprint();
        if(item.getNoForward()){
            if (item.getFingerprint() == null) {
                context.setUpdateFingerprint(true);
            } else if (!item.getFingerprint().equals(fingerprint)) {
                throw PostValidationException.fingerprintMismatch();
            }
        }
        proceed(context);
    }
}
