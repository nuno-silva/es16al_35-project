package pt.tecnico.mydrive.xml;

import org.jdom2.Attribute;
import org.jdom2.Element;
import pt.tecnico.mydrive.domain.*;

/**
 * A concrete XMLVisitor implementation.
 */
public class XMLVisitor implements IXMLVisitor {
    @Override
    public Element visit(Directory directory) {
        return null;
    }

    @Override
    public Element visit(PlainFile plainFile) {
        return null;
    }

    @Override
    public Element visit(Link link) {
        return null;
    }

    @Override
    public Element visit(User user) {
        String password, name, home, mask;
        password = user.getPassword();
        name = user.getName();
        home = user.getHomePath();
        mask = user.getStringUmask();

        Element userElement = new Element("user");
        userElement.setAttribute(new Attribute("username", user.getUsername()));

        if (password != null && password != "") {
            userElement.addContent(new Element("password").setText(user.getPassword()));
        }
        if (name != null && name != "") {
            userElement.addContent(new Element("name").setText(user.getName()));
        }
        if (home != null && home != "") {
            userElement.addContent(new Element("home").setText(user.getHomePath()));
        }
        if (mask != null && mask != "") {
            userElement.addContent(new Element("mask").setText(mask));
        }
        return userElement;
    }

    @Override
    public Element visit(App app) {
        return null;
    }

    @Override
    public Element visit(FileSystem fileSystem) {
        return null;
    }
}
