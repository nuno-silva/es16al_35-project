package pt.tecnico.mydrive.exception;

public class DirectoryNotEmptyException extends MydriveException {
	private String _dirname;

	public DirectoryNotEmptyException( String dirname ) {
		_dirname = dirname;
	}

	public String getDirectoryName() {
		return _dirname;
	}

	@Override
	public String getMessage() {
		return "The Directory '" + _dirname + "' is not empty";
	}
}
