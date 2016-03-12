package pt.tecnico.mydrive.domain;
import org.jdom2.Element;
import pt.tecnico.mydrive.xml.IXMLVisitable;
import pt.tecnico.mydrive.xml.IXMLVisitor;


public class PlainFile extends PlainFile_Base implements IXMLVisitable {

    public PlainFile() {
        super();
    }

    public PlainFile(String name, byte perm, long id, String content) {
        init(name, perm, id, content);
    }
    
    protected void init(String name, byte perm, long id, String content){
		super.init(name, perm, id);
		setContent(content);
	}


    @Override
    public Element accept(IXMLVisitor visitor) {
        return visitor.visit(this);
    }
}
