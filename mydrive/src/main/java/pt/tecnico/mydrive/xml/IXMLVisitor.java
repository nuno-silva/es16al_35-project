package pt.tecnico.mydrive.xml;

import org.jdom2.Document;
import org.jdom2.Element;
import pt.tecnico.mydrive.domain.*;

public interface IXMLVisitor {
    Element visit(Directory directory);
    Element visit(PlainFile plainFile);
    Element visit(Link link);
    Element visit(User user);
    Element visit(App app);
    Document visit(FileSystem fileSystem);
    Element visit(File file);
}
