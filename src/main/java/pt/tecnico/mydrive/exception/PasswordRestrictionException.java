package pt.tecnico.mydrive.exception;

public class PasswordRestrictionException extends MydriveException {
    private static final String DEFAULT_MSG = "Password not within the restrictions.";

    public PasswordRestrictionException() {
        super(DEFAULT_MSG);
    }

    public PasswordRestrictionException(String msg) {
        super(msg);
    }
}
