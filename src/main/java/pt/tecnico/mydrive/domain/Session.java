package pt.tecnico.mydrive.domain;

/*Other stuff*/
import java.math.BigInteger;
import java.util.Random;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import java.util.Optional;

/*Domain*/
import pt.tecnico.mydrive.domain.Directory;

/* Exceptions */
import pt.tecnico.mydrive.exception.WrongPasswordException;
import pt.tecnico.mydrive.exception.PermissionDeniedException;
import pt.tecnico.mydrive.exception.IsNotCdAbleException;


public class Session extends Session_Base {
    private static final Logger logger = Logger.getLogger(Session.class);

    protected Session() {
        super();
    }

    public Session(FileSystem fs, User u, String password) {
        super();
        if (!u.checkPassword(password)) {
            throw new WrongPasswordException(u.getUsername());
        }

        long token = generateToken(fs);
        DateTime expirationDate = u.renewExpirationDate();
        fs.removeExpiredTokens();
        Directory d = u.getHome();
        if(d==null) throw new RuntimeException("BUG: User's home is NULL");
        init(fs, u, token, d, expirationDate);
        logger.debug("new Session: token " + tokenToString(token));
    }

    public static String tokenToString(long token) {
        return Long.toHexString(token);
    }

    protected void init(FileSystem fs, User u, long token, Directory wd, DateTime expirationDate) {
        super.setWorkDir(wd);
        super.setExpirationDate(expirationDate);
        super.setToken(token);
        u.addSession(this);
    }

    protected long generateToken(FileSystem fs) {
        long token;

        do {
            token = (new BigInteger(64, new Random())).longValue();
        } while (token == 0 || fs.hasSession(token));

        return token;
    }

    public boolean isExpired() {
        logger.trace("isExpired: token " + tokenToString(getToken()));
        return getUser().isExpired(this);
    }

    public void remove() {
        logger.trace("remove: token " + tokenToString(getToken()));
        super.setUser(null);
        super.setWorkDir(null);
        deleteDomainObject();
    }

    @Override
    public void addVariable(Variable nv) {
        for(Variable v : getVariableSet()) {
            if(v.getName().equals(nv.getName())) {
                v.setValue(nv.getValue());
                /* FIXME: the caller will be left with a wrong reference to
                 * the variable, but this is how they did it in es16t1tipo-sol.pdf */
                return;
            }
        }
        super.addVariable(nv);
    }

    public boolean hasVariable(String name) {
        for(Variable v : getVariableSet()) {
            if(v.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    // If you want to avoid this create a Variable Dto
    public String getVariableValue(String name){
      if(!hasVariable(name)) return null;
      for(Variable v : getVariableSet()){
        if(v.getName().equals(name)){
          return v.getValue();
        }
      }
      return null;
    }

    @Override
    public void setUser(User u) {
        throw new PermissionDeniedException("change Session's User");
    }

    @Override
    public void setToken(long token) {
        throw new PermissionDeniedException("change Session's token");
    }

    /* // we need this to expire the session during testing...
    @Override
    public void setExpirationDate(DateTime expirationDate) {
        throw new PermissionDeniedException("change Session's expiration date");
    }
    */

    public void renewExpirationDate() {
        DateTime expirationDate = getUser().renewExpirationDate(); // I'm not sure whether we should do this
        super.setExpirationDate(expirationDate);
    }
}
