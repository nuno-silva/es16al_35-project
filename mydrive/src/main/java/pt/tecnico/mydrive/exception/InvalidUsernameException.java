package pt.tecnico.mydrive.exception;

public class InvalidUsernameException extends MydriveException {

	private String _username;
	
	public InvalidUsernameException(String username) {
        super("The provided username is invalid: " + username); // TODO: something more specific would be nice
		_username = username;
	}
	
	public String getUsername() {
		return _username;
	}
	
	@Override
	public String getMessage() {
		return "The provided username is invalid: " + username;
	}
}