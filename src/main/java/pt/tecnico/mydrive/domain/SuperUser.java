package pt.tecnico.mydrive.domain;

import org.joda.time.DateTime;
import pt.tecnico.mydrive.exception.InvalidUsernameException;
import pt.tecnico.mydrive.exception.PermissionDeniedException;
import pt.tecnico.mydrive.exception.UsernameAlreadyExistsException;

/* Super User class for root user */

public class SuperUser extends SuperUser_Base {


    public SuperUser(FileSystem fs, String password) {
        super();
        init(fs, "root", password, "Super User", (byte) 0b11111010);
    }

    public SuperUser(FileSystem fs) {
        super();
        init(fs, "root", "***", "Super User", (byte) 0b11111010);
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
    public void init(FileSystem fs, String username, String password, String name, byte mask) throws InvalidUsernameException, UsernameAlreadyExistsException {
        setUsername(username);
        setPassword(password);
        setName(name);
        setMask(mask);
        fs.addUser(this);
        setHomePath("/home/root");
    }

    @Override
    public DateTime renewExpirationDate(){
      DateTime expirationDate = new DateTime().plusMinutes(10);
      return expirationDate;
    }
}
