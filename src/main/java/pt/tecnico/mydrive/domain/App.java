package pt.tecnico.mydrive.domain;
import org.jdom2.Element;
import pt.tecnico.mydrive.xml.IXMLVisitable;
import pt.tecnico.mydrive.xml.IXMLVisitor;

public class App extends App_Base implements IXMLVisitable {
    public static final String XML_TAG = "app";

    public App() {
        super();
    }

    public App(Directory dir, String name, byte perm, long id, String content) {
            init(dir, name, perm, id, content);
    }
    @Override
    public Element accept(IXMLVisitor visitor) {
        return visitor.visit(this);
    }
}
