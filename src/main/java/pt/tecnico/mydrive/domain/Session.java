package pt.tecnico.mydrive.domain;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import pt.tecnico.mydrive.domain.FileSystem;
import pt.tecnico.mydrive.exception.WrongPasswordException;
import pt.tecnico.mydrive.exception.InvalidTokenException;

import java.math.BigInteger;
import java.util.Random;

public class Session extends Session_Base {
    private static final Logger logger = Logger.getLogger(Session.class);

    protected Session() {
        super();
    }

    public Session(FileSystem fs, User u, String password) {
        if( ! u.checkPassword(password) ) {
            throw new WrongPasswordException(u.getUsername());
        }

        long token = generateToken(fs);
        DateTime expirationDate = renewExpirationDate();
        init(fs, u, token, u.getHomePath(), expirationDate);
        fs.removeExpiredTokens();
        logger.debug("new Session: token " + tokenToString(token));
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
        } while( token == 0 || fs.hasSession(token) );

        return token;
    }

    public boolean isExpired() {
        return getExpirationDate().isAfterNow();
    }

    public DateTime renewExpirationDate() {
        DateTime expirationDate = new DateTime().plusHours(2);
        setExpirationDate(expirationDate);
        return expirationDate;
    }


    public void remove() {
        setUser(null);
        deleteDomainObject();
    }

    public static String tokenToString(long token) {
        return Long.toHexString(token);
    }
}
