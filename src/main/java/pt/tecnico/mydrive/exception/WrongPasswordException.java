package pt.tecnico.mydrive.exception;

public class WrongPasswordException extends MyDriveException {
    private final String _username;

    public WrongPasswordException(String username) {
        _username = username;
    }

    public String getUsername() {
        return _username;
    }

    @Override
    public String getMessage() {
        return "The password for '" + _username + "' is wrong";
    }
}
