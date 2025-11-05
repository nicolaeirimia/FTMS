package com.FTMS.FTMS_app.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Această adnotare este o alternativă la a folosi @ExceptionHandler,
// dar pentru consistență vom folosi @ExceptionHandler.
// Este totuși bine să o avem.
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}