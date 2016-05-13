package pt.tecnico.mydrive.exception;

public class FileExecutionException extends MyDriveException {
    private String _message;

    public FileExecutionException(String message) {
        _message = message;
    }

    public String getFilepath() {
        return _message;
    }

    @Override
    public String getMessage() {
        return _message;
    }
}
