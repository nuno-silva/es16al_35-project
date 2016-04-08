package pt.tecnico.mydrive.domain;

import org.joda.time.DateTime;
import pt.tecnico.mydrive.exception.WrongPasswordException;

public class Session extends Session_Base {

    protected Session() {
        super();
    }

    public Session(User u, String password) {
        if( ! u.checkPassword(password) ) {
            throw new WrongPasswordException(u.getUsername());
        }
        // FIXME TODO
    }

    protected void init(long token, String workingPath, DateTime expirationDate) {
        setToken(token);
        setWorkingPath(workingPath);
        setExpirationDate(expirationDate);
    }
}
