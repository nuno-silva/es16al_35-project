package pt.tecnico.mydrive.exception;

public class TooShortPasswordException extends MydriveException {
    private static final String DEFAULT_MSG = "Password must have more than 8 characters!";

    public TooShortPasswordException() {
        super(DEFAULT_MSG);
    }
}
