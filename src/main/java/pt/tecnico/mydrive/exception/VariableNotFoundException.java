package pt.tecnico.mydrive.exception;

public class VariableNotFoundException extends MyDriveException {

    private String _name;

    public VariableNotFoundException(String name) {
        _name = name;
    }

    public String getUsername() {
        return _name;
    }

    @Override
    public String getMessage() {
        return "Variable with name " + _name + " is not defined";
    }
}
