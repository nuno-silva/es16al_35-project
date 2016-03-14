package pt.tecnico.mydrive.exception;

public class IsNotCdAbleException extends MydriveException {
	public IsNotCdAbleException() {}

	@Override
	public String getMessage() {
		return "File aren't cdAble";
	}
}
