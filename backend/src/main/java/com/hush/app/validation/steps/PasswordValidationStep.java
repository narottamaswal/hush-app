package com.hush.app.validation.steps;

import com.hush.app.model.Item;
import com.hush.app.service.HashService;
import com.hush.app.validation.ItemContext;
import com.hush.app.validation.exceptions.PostValidationException;
import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PasswordValidationStep extends ItemValidationStep {

    private final HashService hashService;

    @Override
    public void validate(ItemContext context) {
        Item item = context.getItem();
        String password = context.getProvidedPassword();

        if (item.getPasswordHash() != null) {
            if (StringUtils.isBlank(password)) {
                throw PostValidationException.passwordRequired();
            }
            if (!item.getPasswordHash().equals(hashService.hashPassword(password))) {
                throw PostValidationException.wrongPassword();
            }
        }
        proceed(context);
    }
}
