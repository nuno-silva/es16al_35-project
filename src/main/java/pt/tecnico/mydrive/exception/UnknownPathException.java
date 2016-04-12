package pt.tecnico.mydrive.exception;

public class UnknownPathException extends MydriveException {
    private String _path;

    public UnknownPathException(String path) {
        _path = path;
    }

    public String getPath() {
        return _path;
    }

    @Override
    public String getMessage() {
        return "The given path '" + _path + "' is invalid";
    }
}