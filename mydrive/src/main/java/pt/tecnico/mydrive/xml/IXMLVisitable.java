package pt.tecnico.mydrive.xml;

import org.jdom2.Element;

public interface IXMLVisitable {
    Element accept(IXMLVisitor visitor);
}
