package pt.tecnico.mydrive.exception;

public class UnknownPathException extends Throwable {
	public UnknownPathException() {
		super("ERROR: Given path does not exist");
	}
}