package pt.tecnico.mydrive.domain;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Element;
import pt.tecnico.mydrive.domain.xml.IXMLVisitable;
import pt.tecnico.mydrive.domain.xml.IXMLVisitor;
import pt.tecnico.mydrive.exception.InvalidUsernameException;


public class User extends User_Base implements IXMLVisitable, IPermissionable {
    public static final String XML_TAG = "user";

    private static final Logger logger = LogManager.getLogger();

	protected User(){
		super();
	}

    public User(FileSystem fs, String username, String password, String name, byte mask) throws InvalidUsernameException {
        super();
        init(fs, username, password, name, mask);
    }

    public void init(FileSystem fs, String username, String password, String name, byte mask) throws InvalidUsernameException {
		if (checkUserName(username)) {
			setUsername(username);
			setPassword(password);
			setName(name);
			setMask(mask);
			setFs(fs);
			Directory home = fs.createFileParents( "/home/"+username );
			//Directory d=(Directory)(fs.getFile("home"));  XXX this might be a little dirty
			//home.addFileIfNotExists(new Directory(username));
		}
		else {
            throw new InvalidUsernameException(username);
        }
    }

    @Override
    public void setFs( FileSystem fs ) {
        if( fs == null ) {
            super.setFs(fs);
            return;
        }
        fs.addUser(this);
    }
    
    /* Is this needed? Discuss please (Jorge) */
	public void remove(FileSystem fs){
		fs.removeUser(this);
	}
	
    public boolean checkUserName(String username) {
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

}
