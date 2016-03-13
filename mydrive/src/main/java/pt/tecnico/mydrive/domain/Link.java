package pt.tecnico.mydrive.domain;
import org.jdom2.Element;
import pt.tecnico.mydrive.xml.IXMLVisitable;
import pt.tecnico.mydrive.xml.IXMLVisitor;

public class Link extends Link_Base implements IXMLVisitable {

    public Link() {
        super();
    }

    public Link(Directory dir, String name, byte perm, long id, String path) {
        init(dir, name, perm, id);
        setPath(path);
    }

    @Override
    public Element accept(IXMLVisitor visitor) {
        return visitor.visit(this);
    }
}
