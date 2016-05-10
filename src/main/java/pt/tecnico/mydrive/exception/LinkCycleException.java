package pt.tecnico.mydrive.exception;

public class LinkCycleException extends MyDriveException {
    private String _filepath;

    public LinkCycleException(String filepath) {
        _filepath = filepath;
    }

    public String getFilepath() {
        return _filepath;
    }

    @Override
    public String getMessage() {
        return "Cycle detected in "+_filepath;
    }
}
