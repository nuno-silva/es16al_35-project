package pt.tecnico.mydrive.exception;

public class FileNotFoundException extends MyDriveException {
    private String _filename;
    private String _inpath;

    public FileNotFoundException(String filename) {
        this(filename, null);
    }
    public FileNotFoundException(String filename, String path) {
        _filename = filename;
        _inpath = path;
    }

    public String getFilename() {
        return _filename;
    }
    public String getPath() {
        return _inpath;
    }

    @Override
    public String getMessage() {
        String message = "The file name '" + _filename + "' was not found";
        if(_inpath != null) {
            message += " in '" +_inpath+ "'";
        }
        return message;
    }
}
