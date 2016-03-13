package pt.tecnico.mydrive.exception;

public class FilenameAlreadyExistsException extends MydriveException {
	private String _filename;

	public FilenameAlreadyExistsException( String filename ) {
		_filename = filename;
	}

	public String getFilename() {
		return _filename;
	}

	@Override
	public String getMessage() {
		return "The name '" + _filename + "' is already in use";
	}
}
