package pt.tecnico.mydrive.domain;

import pt.tecnico.mydrive.exception.InvalidUsernameException;

/* Super User class for root user */

public class SuperUser extends SuperUser_Base {
	
	public SuperUser(FileSystem fs,String password) throws InvalidUsernameException{
			super();
			init(fs,"root",password,"Super User",(byte) 11111111); 
	}
}
