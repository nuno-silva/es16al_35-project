package pt.tecnico.mydrive.domain;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Element;
import org.joda.time.DateTime;
import pt.tecnico.mydrive.domain.Session;
import pt.tecnico.mydrive.domain.xml.IXMLVisitable;
import pt.tecnico.mydrive.domain.xml.IXMLVisitor;
import pt.tecnico.mydrive.exception.InvalidUsernameException;
import pt.tecnico.mydrive.exception.InvalidPasswordException;
import pt.tecnico.mydrive.exception.UsernameAlreadyExistsException;

import pt.tecnico.mydrive.exception.InvalidTokenException;
import pt.tecnico.mydrive.exception.IsNotCdAbleException;
import java.util.Set;

public class User extends User_Base implements IXMLVisitable, IPermissionable {
    public static final String XML_TAG = "user";
    private static final byte DEFAULT_MASK = (byte)0b11110000;

    private static final Logger logger = LogManager.getLogger();

    protected User(){
        super();
    }

    // all attrs
    public User(FileSystem fs, String username, String password, String name, byte mask) throws InvalidUsernameException, UsernameAlreadyExistsException {
        super();
        init(fs, username, password, name, mask);
    }

    //all but password
    public User( FileSystem fs, String username, String name, byte mask) throws InvalidUsernameException, UsernameAlreadyExistsException {
        super();
        init( fs, username, username, name, mask );
    }

    //all but password and name
    public User( FileSystem fs, String username, byte mask) throws InvalidUsernameException, UsernameAlreadyExistsException {
        super();
        init( fs, username, username, username, mask );
    }

    //all but mask
    public User( FileSystem fs, String username, String password, String name ) throws InvalidUsernameException, UsernameAlreadyExistsException {
        super();
        init( fs, username, password, name, DEFAULT_MASK );
    }

    //all but mask and name
    public User( FileSystem fs, String username, String password) throws InvalidUsernameException, UsernameAlreadyExistsException {
        super();
        init( fs, username, password, username, DEFAULT_MASK );
    }

    //all but mask, name and password
    public User( FileSystem fs, String username) throws InvalidUsernameException, UsernameAlreadyExistsException {
        super();
        init( fs, username, username, username, DEFAULT_MASK );
    }

    public void init(FileSystem fs, String username, String password, String name, byte mask) throws InvalidUsernameException, UsernameAlreadyExistsException {
        logger.trace("User init " + username);
        if (checkUsername(username)) {
            setUsername(username);
            setPassword(password);
            setName(name);
            setMask(mask);

            if( username.length() >=  3 )
                fs.addUser( this );
            else
                throw new InvalidUsernameException( username , "usernames must be at least 3 characters long");

            File home = fs.getFile("/home");
            if( home.isCdAble() ) {
                home = new Directory(fs, (Directory)home, this, username);
                setHomePath(home.getFullPath());
            } else {
                setFs(null); // remove User from FileSystem
                throw new IsNotCdAbleException("'"+ home.getFullPath()+" is not cdAble. Can't create user home.");
            }
        }
        else {
            throw new InvalidUsernameException(username);
        }
    }

	public static void assertValidUsername(String username) throws InvalidUsernameException{
		if( username==null || username.length() <= 3  ) throw new InvalidUsernameException( username );
	}

    public boolean checkPassword(String password) throws InvalidPasswordException {
		if( password == null ) throw new InvalidPasswordException("Null password. I too can break the rules, bye!");
        return getPassword().equals(password);
    }

    public void remove() {
        getFs().removeUser(this);
    }

    /* FIXME I don't think this is needed (Nuno) */
    public boolean checkUsername(String username) {
        char[] chars = username.toCharArray();
        for (char c : chars) {
            if(!Character.isLetter(c) && !Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Element accept(IXMLVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public String getStringPermissions() {
        return MaskHelper.getStringPermissions(getMask());
    }

    @Override
    public String getStringMask() {
        return MaskHelper.getStringMask(getMask());
    }

    @Override
    public byte getByteMask() {
        return getMask();
    }

    @Override
    public byte getANDedByteMask(IPermissionable other) {
        return MaskHelper.andMasks(getByteMask(), other.getByteMask());
    }

    @Override
    public String getANDedStringMask(IPermissionable other) {
        return MaskHelper.getStringMask(getANDedByteMask(other));
    }

    @Override
    public String getANDedStringPermissions(IPermissionable other) {
        return MaskHelper.getStringPermissions(getANDedByteMask(other));
    }

    public Session getSession(long token) throws InvalidTokenException {
        if( token == 0) {
            throw new InvalidTokenException(token, "Token can not be 0");
        }
        Set<Session> sessions = super.getSessionSet();
        for(Session s : sessions) {
            if(s.getToken() == token) {
                if( s.isExpired() ) {
                    s.remove();
                    logger.warn("Tried to use an expired token " + Session.tokenToString(token));
                    throw new InvalidTokenException(token, "Token expired");
                } else {
                    return s;
                }
            }
        }
        throw new InvalidTokenException(token, "Token not found");
    }

    public void removeExpiredTokens() {
        Set<Session> sessions = super.getSessionSet();
        for(Session s : sessions) {
            if( s.isExpired() ) {
                s.remove();
            }
        }
    }

    @Override
    public Set<Session> getSessionSet() {
        throw new UnsupportedOperationException();
    }

}
