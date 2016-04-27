package pt.tecnico.mydrive.domain.xml;

import org.jdom2.Element;
import pt.tecnico.mydrive.domain.*;

public interface XMLVisitor {
    Element visit(Directory directory);

    Element visit(PlainFile plainFile);

    Element visit(Link link);

    Element visit(User user);

    Element visit(App app);

    Element visit(File file);
}
