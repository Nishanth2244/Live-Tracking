package com.Project.Mechanic.ExceptionHandler;

public class ExternalServiceException extends RuntimeException {
    public ExternalServiceException(String message) {
        super(message);
    }
}