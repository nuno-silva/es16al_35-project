package pt.tecnico.mydrive.exception;

public class InvalidUsernameException extends Exception {

	private static final long serialVersionUID = 1L;
	private String _username;
	
	public InvalidUsernameException(String username) {
        super("The provided username is invalid"); // TODO: something more specific would be nice
		_username = username;
	}
	
	public String getUsername() {
		return _username;
	}
}