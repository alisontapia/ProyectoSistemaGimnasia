package edu.unl.ec.gimnasia.exception;

public class AlreadyEntityException extends Exception {

    public AlreadyEntityException() {
        this("La entidad ya existe");
    }

    public AlreadyEntityException(String message) {
        super(message);
    }

    public AlreadyEntityException(String message, Throwable cause) {
        super(message, cause);
    }
}

