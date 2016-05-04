package pt.tecnico.mydrive.exception;

public class TooShortPasswordException extends MydriveException {
    private static final String DEFAULT_MSG = "Password too short! More than 8 characters please!";

    public TooShortPasswordException() {
        super(DEFAULT_MSG);
    }
}
