package pt.tecnico.mydrive.exception;

public class PermissionDeniedException extends MyDriveException {

    private String operation;

    public PermissionDeniedException(String op) {
        operation = op;
    }

    @Override
    public String getMessage() {
        return "Permission denied for operation " + operation;
    }
}
