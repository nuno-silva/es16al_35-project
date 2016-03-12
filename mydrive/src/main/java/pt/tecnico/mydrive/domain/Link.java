package pt.tecnico.mydrive.domain;
import org.jdom2.Element;
import pt.tecnico.mydrive.xml.IXMLVisitable;
import pt.tecnico.mydrive.xml.IXMLVisitor;
    
public class Link extends Link_Base {
    
    public Link(String name,byte perm,long id,String path) {
        init(name,perm,id);
        setPath(path);
    }
    
	public Link() {
        super();
    }

    @Override
    public Element accept(IXMLVisitor visitor) {
        return visitor.visit(this);
    }
}
