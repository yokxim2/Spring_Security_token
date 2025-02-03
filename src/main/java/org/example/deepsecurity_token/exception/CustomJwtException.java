package org.example.deepsecurity_token.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomJwtException extends RuntimeException {

    private final HttpStatus httpStatus;

    public CustomJwtException(final String message, final HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
