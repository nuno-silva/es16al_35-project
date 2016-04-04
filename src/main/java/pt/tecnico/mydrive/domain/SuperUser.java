package pt.tecnico.mydrive.domain;

import pt.tecnico.mydrive.exception.InvalidUsernameException;

/* Super User class for root user */

public class SuperUser extends User{

	/* Should this class have a static attribute to check if it gets created twice?
	 * Discusse please. */
	
	public SuperUser(FileSystem fs,String password) throws InvalidUsernameException{
			super(fs,"root",password,"Super User",(byte) 11111111); 
	}
}
