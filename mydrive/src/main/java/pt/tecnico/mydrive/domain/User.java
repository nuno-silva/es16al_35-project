package pt.tecnico.mydrive.domain;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Element;
import pt.tecnico.mydrive.xml.IXMLVisitable;
import pt.tecnico.mydrive.xml.IXMLVisitor;


public class User extends User_Base implements IXMLVisitable {
    public User() {
        super();
    }

    private static final Logger logger = LogManager.getLogger();

    @Override
    public Element accept(IXMLVisitor visitor) {
        return visitor.visit(this);
    }
}
