package pt.tecnico.mydrive.domain.xml;

import org.jdom2.Element;

public interface XMLVisitable {
    Element accept(XMLVisitor visitor);
}
