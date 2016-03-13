package pt.tecnico.mydrive.domain;

public class InvalidFileNameException extends Throwable {
	public InvalidFileNameException(String name) {
		System.out.println("ERROR: File name is invalid "+name+"\n");
	}
}
