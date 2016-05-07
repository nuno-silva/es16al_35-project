package pt.tecnico.mydrive.exception;

public class EmptyFileNameException extends MyDriveException {
    private static final String DEFAULT_MSG = "Empty file name not allowed!";

    public EmptyFileNameException() {
        super(DEFAULT_MSG);
    }

    public EmptyFileNameException(String msg) {
        super(msg);
    }
}
