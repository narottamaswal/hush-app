package com.hush.app.validation.steps;

import com.hush.app.model.Item;
import com.hush.app.validation.ItemContext;
import com.hush.app.validation.exceptions.PostValidationException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ViewOnceValidationStep extends ItemValidationStep {

    @Override
    public void validate(ItemContext context) {
        Item item = context.getItem();
        if (item.getViewOnce()) {
            if(item.getViewed()){
                throw PostValidationException.alreadyViewed();
            }
            context.setMarkViewed(true);
        }
        proceed(context);
    }
}
