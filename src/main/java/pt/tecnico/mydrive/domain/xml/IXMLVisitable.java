package pt.tecnico.mydrive.domain.xml;

import org.jdom2.Element;

public interface IXMLVisitable {
    Element accept(IXMLVisitor visitor);
}
