package pt.tecnico.mydrive.domain;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Element;
import pt.tecnico.mydrive.exception.InvalidUsernameException;
import pt.tecnico.mydrive.xml.IXMLVisitable;
import pt.tecnico.mydrive.xml.IXMLVisitor;


public class User extends User_Base implements IXMLVisitable {
    public static final String XML_TAG = "user";

    public User() {
        super();
    }

    private static final Logger logger = LogManager.getLogger();

    public User(FileSystem fs, String username, String password, String name, byte mask) throws InvalidUsernameException {
        init(fs, username, password, name, mask);
    }

    public void init(FileSystem fs, String username, String password, String name, byte mask) throws InvalidUsernameException {
		if (checkUserName(username)) {
			setUsername(username);
			setPassword(password);
			setName(name);
			setUmask(mask);
			setFs(fs);
			// setDir(new Directory(username, (byte) 11111010, 123)); FIXME
			// depois metodo que cria o user tem de ligar a sua pasta ao directorio "home"
		}
		else {
            throw new InvalidUsernameException(username);
        }
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
}
