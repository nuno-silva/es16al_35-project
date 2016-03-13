package pt.tecnico.mydrive.domain;

public class InvalidUsernameException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String _username;
	
	public InvalidUsernameException(String username) {
		_username = username;
	}
	
	public String getUsername() {
		return _username;
	}
}