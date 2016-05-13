package pt.tecnico.mydrive.exception;

public class NotJavaFullyQualifiedNameException extends MyDriveException {
    private String _msg;

    public NotJavaFullyQualifiedNameException() {
        _msg = "Not Java Fully Qualified Name";
    }

    public NotJavaFullyQualifiedNameException(String msg) {
        _msg = msg;
    }

    @Override
    public String getMessage() {
        return _msg;
    }
}
