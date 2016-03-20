package pt.tecnico.mydrive.exception;

public class InvalidFileNameException extends MydriveException {
	private String _filename;

	public InvalidFileNameException( String filename ) {
		_filename = filename;
	}

	public String getFilename() {
		return _filename;
	}

	@Override
	public String getMessage() {
		return "The file name '" + _filename + "' is invalid";
	}
}
