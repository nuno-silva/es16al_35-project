package pt.tecnico.mydrive.exception;

public class FileNameTooLongException extends InvalidFileNameException {
    private final int _maxpath;

    public FileNameTooLongException(String path, int maxpath) {
        super(path);
        _maxpath = maxpath;
    }

    public int getMaxPath() {
        return _maxpath;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + " because it is longer than "
                + Integer.toString(_maxpath) + " characters";
    }
}
