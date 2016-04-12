package pt.tecnico.mydrive.exception;

import pt.tecnico.mydrive.domain.Session;

public class InvalidTokenException extends MydriveException {

    private final long _token;
    private final String _msg;

    public InvalidTokenException(long token) {
        _token = token;
        _msg = "Invalid token";
    }

    public InvalidTokenException(long token, String msg) {
        _token = token;
        _msg = msg;
    }

    @Override
    public String getMessage() {
        return _msg + ": " + Session.tokenToString(_token);
    }
}
