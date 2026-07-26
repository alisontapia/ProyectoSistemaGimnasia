package edu.unl.ec.gimnasia.exception;

public class CredentialInvalidException extends Exception {

    public CredentialInvalidException() {
        super(" Credenciales inválidas ");
    }

    public CredentialInvalidException(String message) {
        super(message);
    }

    public CredentialInvalidException(String message, Throwable cause) {
        super(message, cause);
    }
}
