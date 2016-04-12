package pt.tecnico.mydrive.exception;

public class FileNotFoundException extends MydriveException {
    private String _filename;

    public FileNotFoundException(String filename) {
        _filename = filename;
    }

    public String getFilename() {
        return _filename;
    }

    @Override
    public String getMessage() {
        return "The file name '" + _filename + "' was not found";
    }
}
