package pt.tecnico.mydrive.exception;

public class DuplicateVariableException extends MyDriveException {
    private String _name;

    public DuplicateVariableException(String name) {
        _name = name;
    }

    public String getName() {
        return _name;
    }

    @Override
    public String getMessage() {
        return "Variable already exists: " + _name;
    }
}
