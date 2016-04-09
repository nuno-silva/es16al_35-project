package pt.tecnico.mydrive.exception;

public class UsernameAlreadyExistsException extends MydriveException {

	private String _username;
	
	public UsernameAlreadyExistsException(String username) {
		_username = username;
	}
	
	public String getUsername() {
		return _username;
	}
	
	@Override
	public String getMessage() {
		return "User with name " + _username +" already exists!";
	}	
}
