package com.hush.app.validation.steps;

import com.hush.app.model.Item;
import com.hush.app.validation.ItemContext;
import com.hush.app.validation.exceptions.PostValidationException;
import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ExpiredValidationStep extends ItemValidationStep {

    @Override
    public void validate(ItemContext context) {
        Item item = context.getItem();
        if (Boolean.TRUE.equals(item.getIsExpired())) {
            String ownerEmail = context.getOwnerEmail();
            boolean isOwner = StringUtils.isNotBlank(ownerEmail)
                    && item.getOwnerEmail().equalsIgnoreCase(ownerEmail);

            if (!isOwner) {
                throw PostValidationException.expired();
            }
        }

        proceed(context);
    }
}

