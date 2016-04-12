package pt.tecnico.mydrive.exception;

public class InvalidPasswordException extends MydriveException {
    private String _reason;

    public InvalidPasswordException(String reason) {
        _reason   = reason;
    }

    public String getReason() {
        return _reason;
    }

    @Override
    public String getMessage() {
        return "Password is invalid: " + _reason;
    }
}
