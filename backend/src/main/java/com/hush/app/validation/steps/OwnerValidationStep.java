package com.hush.app.validation.steps;

import com.hush.app.model.Item;
import com.hush.app.model.ResponseDto;
import com.hush.app.validation.ItemContext;
import com.hush.app.validation.exceptions.PostValidationException;
import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class OwnerValidationStep extends ItemValidationStep {

    @Override
    public void validate(ItemContext context) {
        Item item = context.getItem();
        String ownerEmail = context.getOwnerEmail();
        if (StringUtils.isNotBlank(ownerEmail)
                && item.getOwnerEmail().equalsIgnoreCase(ownerEmail)) {
            context.setEarlyExit(true);
            context.setEarlyExitResult(ResponseDto.Response.from(item));
            return;
        }
        proceed(context);
    }
}
