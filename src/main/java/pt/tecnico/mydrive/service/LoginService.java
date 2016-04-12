package pt.tecnico.mydrive.service;

import pt.tecnico.mydrive.domain.FileSystem;
import pt.tecnico.mydrive.domain.Session;
import pt.tecnico.mydrive.domain.User;
import pt.tecnico.mydrive.exception.InvalidUsernameException;
import pt.tecnico.mydrive.exception.UserNotFoundException;
import pt.tecnico.mydrive.exception.WrongPasswordException;


public class LoginService extends MyDriveService {

    private String username, password;
    private long token;

    public LoginService(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public void dispatch() throws UserNotFoundException, WrongPasswordException, InvalidUsernameException {
        User.assertValidUsername(username);
        FileSystem fs = getFileSystem();
        if (!fs.getUser(username).checkPassword(password)) throw new WrongPasswordException(username);
        Session s = new Session(fs, fs.getUser(username), password);
        token = s.getToken();
    }

    public long result() {
        return token;
    }
}
