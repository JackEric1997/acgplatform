package com.acg.minio.errors;

import io.minio.errors.MinioException;

public class InvalidExpiresRangeException extends MinioException {
    private final int expires;

    /**
     * Constructs a new InvalidExpiresRangeException with expires value caused the error and error
     * message.
     */
    public InvalidExpiresRangeException(int expires, String message) {
        super(message);
        this.expires = expires;
    }

    @Override
    public String toString() {
        return this.expires + ": " + super.toString();
    }
}
