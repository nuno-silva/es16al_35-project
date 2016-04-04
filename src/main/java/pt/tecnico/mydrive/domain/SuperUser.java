package pt.tecnico.mydrive.domain;

import pt.tecnico.mydrive.exception.InvalidUsernameException;
import pt.tecnico.mydrive.exception.PermissionDeniedException;
/* Super User class for root user */

public class SuperUser extends SuperUser_Base {
	
	public SuperUser(FileSystem fs,String password) throws InvalidUsernameException{
			super();
			init(fs,"root",password,"Super User",(byte) 11111111); 
	}
	
	@Override
	public void remove(FileSystem fs) throws PermissionDeniedException{
		throw new PermissionDeniedException("deleting root user");
	}
}
