package com.FTMS.FTMS_app.common.exception;

import java.time.LocalDateTime;

/**
 * Un record care definește formatul JSON standard pentru erori.
 */
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path
) {
}