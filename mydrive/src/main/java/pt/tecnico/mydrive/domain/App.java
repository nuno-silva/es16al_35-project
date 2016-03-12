package pt.tecnico.mydrive.domain;
import org.jdom2.Element;
import pt.tecnico.mydrive.xml.IXMLVisitable;
import pt.tecnico.mydrive.xml.IXMLVisitor;

public App(String name, byte perm, long id, String content) {
        init(name, perm, id, content);
    }
    public App() {
        super();
    }

    @Override
    public Element accept(IXMLVisitor visitor) {
        return visitor.visit(this);
    }
}
