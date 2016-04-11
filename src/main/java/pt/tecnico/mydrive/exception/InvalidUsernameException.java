package pt.tecnico.mydrive.exception;

public class InvalidUsernameException extends MydriveException {

	private String _username;
	private String _reason;
	
	public InvalidUsernameException(String username, String reason) {
		_username = username;
		_reason   = reason;
	}
	
	public InvalidUsernameException(String username) {
		_username = username;
		_reason   = null;
	}

	public String getUsername() {
		return _username;
	}

	public String getReason() {
		return _reason;
	}
	
	@Override
	public String getMessage() {
		if( _reason != null ) {
			return "'" + _username + "' is an invalid username because " + _reason;
		} else {
			return "'" + _username + "' is an invalid username";
		}
	}
}
