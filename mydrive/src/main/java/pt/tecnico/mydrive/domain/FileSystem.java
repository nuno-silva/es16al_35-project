package pt.tecnico.mydrive.domain;

import org.jdom2.Element;
import pt.tecnico.mydrive.xml.IXMLVisitable;
import pt.tecnico.mydrive.xml.IXMLVisitor;

public class FileSystem extends FileSystem_Base implements IXMLVisitable {
    
    public FileSystem() {
        super();
    }

    @Override
    public Element accept(IXMLVisitor visitor) {
        return visitor.visit(this);
    }
}
