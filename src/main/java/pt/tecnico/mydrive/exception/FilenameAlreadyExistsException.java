package pt.tecnico.mydrive.exception;

public class FilenameAlreadyExistsException extends MydriveException {
    private String _filename;
    private String _path;

    public FilenameAlreadyExistsException(String filename, String path) {
        _filename = filename;
        _path = path;
    }

    public FilenameAlreadyExistsException(String filename) {
        _filename = filename;
        _path = null;
    }

    public String getFilename() {
        return _filename;
    }

    public String getPath() {
        return _path;
    }

    @Override
    public String getMessage() {
        if (_path == null) {
            return "The name '" + _filename + "' is already in use";
        } else {
            return "The name '" + _filename + "' is already in use in " + _path;
        }
    }
}
