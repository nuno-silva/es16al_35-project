package pt.tecnico.mydrive.exception;

public class EmptyPathException extends MydriveException {
    private static final String DEFAULT_MSG = "Empty file name not allowed!";

    public EmptyPathException() {
        super(DEFAULT_MSG);
    }

    public EmptyPathException(String msg) {
        super(msg);
    }
}
