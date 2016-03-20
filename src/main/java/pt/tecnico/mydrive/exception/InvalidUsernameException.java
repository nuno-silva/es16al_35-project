package pt.tecnico.mydrive.exception;

public class InvalidUsernameException extends MydriveException {

	private String _username;
	
	public InvalidUsernameException(String username) {
		_username = username;
	}
	
	public String getUsername() {
		return _username;
	}
	
	@Override
	public String getMessage() {
		return "The provided username is invalid: " + _username;
	}	
}