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
        Element userElement = new Element("user");
        userElement.setAttribute(new Attribute("username", user.getUsername()));


        return null;
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
