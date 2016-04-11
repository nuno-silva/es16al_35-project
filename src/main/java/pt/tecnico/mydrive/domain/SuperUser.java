package pt.tecnico.mydrive.domain;

import pt.tecnico.mydrive.exception.InvalidUsernameException;
import pt.tecnico.mydrive.exception.PermissionDeniedException;
import pt.tecnico.mydrive.exception.UsernameAlreadyExistsException;

/* Super User class for root user */

public class SuperUser extends SuperUser_Base {


    public SuperUser(FileSystem fs,String password) {
        super();
        init(fs,"root",password,"Super User",(byte) 0b11111010);
    }

    public SuperUser(FileSystem fs) {
        super();
        init(fs,"root","***","Super User",(byte) 0b11111010);
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
}
