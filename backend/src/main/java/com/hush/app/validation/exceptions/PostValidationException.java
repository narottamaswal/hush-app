package com.hush.app.validation.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Getter
public class PostValidationException extends RuntimeException {
    private final HttpStatus status;
    private final Object body;

    public PostValidationException(String message, HttpStatus status, Object body) {
        super(message);
        this.status = status;
        this.body = body;
    }
    public static PostValidationException alisAlreadyUsed() {
        return new PostValidationException(
                "Alias already used",
                HttpStatus.BAD_REQUEST,
                Map.of("error", "Alias already used")
        );
    }
    public static PostValidationException notFound() {
        return new PostValidationException(
                "Item not found",
                HttpStatus.NOT_FOUND,
                Map.of("error", "Item not found")
        );
    }
    public static PostValidationException fingerprintMismatch() {
        return new PostValidationException(
                "Device mismatch",
                HttpStatus.NOT_FOUND,
                Map.of("error", "You're not allowed to view this item.")
        );
    }

    // convenience static factories
    public static PostValidationException passwordRequired(String title) {
        return new PostValidationException(
                "Password required",
                HttpStatus.UNAUTHORIZED,
                Map.of("passwordProtected",true,"error", "Password is required","title",title)
        );
    }

    public static PostValidationException wrongPassword(String title) {
        return new PostValidationException(
                "Wrong password",
                HttpStatus.FORBIDDEN,
                Map.of("passwordProtected",true,"error", "Invalid password","title",title)
        );
    }

    public static PostValidationException alreadyViewed() {
        return new PostValidationException(
                "Post already viewed",
                HttpStatus.GONE,
                Map.of("error", "The item you're looking for is not found")
        );
    }


}
