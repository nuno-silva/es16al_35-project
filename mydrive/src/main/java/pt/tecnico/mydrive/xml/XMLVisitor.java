package pt.tecnico.mydrive.xml;

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
