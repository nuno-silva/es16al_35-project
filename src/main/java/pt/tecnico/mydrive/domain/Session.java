package pt.tecnico.mydrive.domain;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import pt.tecnico.mydrive.exception.WrongPasswordException;

import java.math.BigInteger;
import java.util.Random;

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
        init(fs, u, token, u.getHomePath(), expirationDate);
        logger.debug("new Session: token " + tokenToString(token));
    }

    public static String tokenToString(long token) {
        return Long.toHexString(token);
    }

    protected void init(FileSystem fs, User u, long token, String workingPath, DateTime expirationDate) {
        setWorkingPath(workingPath);
        setExpirationDate(expirationDate);
        setToken(token);
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
        setUser(null);
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
}
