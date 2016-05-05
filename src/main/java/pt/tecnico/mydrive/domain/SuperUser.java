package pt.tecnico.mydrive.domain;

import org.joda.time.DateTime;
import pt.tecnico.mydrive.exception.InvalidUsernameException;
import pt.tecnico.mydrive.exception.PermissionDeniedException;
import pt.tecnico.mydrive.exception.UsernameAlreadyExistsException;

/* Super User class for root user */

public class SuperUser extends SuperUser_Base {
	public static final byte SUPERUSER_MASK = (byte)0b11111010;


    public SuperUser(FileSystem fs, String password) {
        super();
        init(fs, "root", password, "Super User", SUPERUSER_MASK);
    }

    public SuperUser(FileSystem fs) {
        super();
        init(fs, "root", "***", "Super User", SUPERUSER_MASK);
    }

    @Override
    public boolean hasReadPermission(File f) {
        return true;
    }

    @Override
    public boolean hasWritePermission(File f) {
        return true;
    }

    @Override
    public boolean hasExecutePermission(File f) {
        return true;
    }

    @Override
    public boolean hasDeletePermission(File f) {
        return true;
    }

    @Override
    public void remove() throws PermissionDeniedException {
        throw new PermissionDeniedException("can not delete SuperUser " + getUsername());
    }


    @Override
    public DateTime renewExpirationDate(){
      DateTime expirationDate = new DateTime().plusMinutes(10);
      return expirationDate;
    }

		@Override
		public boolean checkPaswordRestrictions( String password ){
			return true;
		}
}
