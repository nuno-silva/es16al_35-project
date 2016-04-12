package pt.tecnico.mydrive.exception;

public class UserNotFoundException extends MydriveException {

	private String _username;
	
	public UserNotFoundException(String username) {
		_username = username;
	}
	
	public String getUsername() {
		return _username;
	}
	
	@Override
	public String getMessage() {
		return "User with name " + _username +" doesn't exist!";
	}	
}
