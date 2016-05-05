package pt.tecnico.mydrive.domain.xml;

import org.jdom2.Element;

public interface Visitable {
    Element accept(Visitor visitor);
}
