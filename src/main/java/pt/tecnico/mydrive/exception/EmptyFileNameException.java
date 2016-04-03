package pt.tecnico.mydrive.exception;

public class EmptyFileNameException extends MydriveException {
	
	@Override
	public String getMessage() {
		return "Empty file name not allowed!";
	}
}
