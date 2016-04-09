package pt.tecnico.mydrive.domain;

import org.joda.time.DateTime;
import pt.tecnico.mydrive.domain.FileSystem;
import pt.tecnico.mydrive.exception.WrongPasswordException;
import pt.tecnico.mydrive.exception.InvalidTokenException;

public class Session extends Session_Base {

    protected Session() {
        super();
    }

    public Session(FileSystem fs, User u, String password) {
        if( ! u.checkPassword(password) ) {
            throw new WrongPasswordException(u.getUsername());
        }
        // FIXME TODO
    }

    protected void init(FileSystem fs, User u, long token, String workingPath, DateTime expirationDate) {
        setWorkingPath(workingPath);
        setExpirationDate(expirationDate);
        /* TODO gerar token*/
        /* TODO verificar se nao e' duplicado nem 0 */
        setToken(token);
        /* TODO delete all expired tokens */
        /* TODO add session to user */
    }
    
    protected boolean checkToken(FileSystem fs, long token) {
        return token != 0;
    }
    
    
    public void remove() {
        setUser(null);
        deleteDomainObject();
    }
}
