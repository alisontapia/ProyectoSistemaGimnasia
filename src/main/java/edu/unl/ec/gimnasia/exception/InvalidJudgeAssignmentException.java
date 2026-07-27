package edu.unl.ec.gimnasia.exception;

import jakarta.ejb.ApplicationException;
import java.io.Serial;

@ApplicationException(rollback = true)
public class InvalidJudgeAssignmentException extends Exception {

    @Serial
    private static final long serialVersionUID = 1L;

    public InvalidJudgeAssignmentException(String message) {
        super(message);
    }
}

