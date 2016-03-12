package pt.tecnico.mydrive.domain;

public class UnknownPathException extends Throwable {
	public UnknownPathException() {
		System.out.println("ERROR: Given path does not exist \n");
	}
}