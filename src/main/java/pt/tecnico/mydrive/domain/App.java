package pt.tecnico.mydrive.domain;
import org.apache.log4j.Logger;
import org.jdom2.Element;
import pt.tecnico.mydrive.exception.FilenameAlreadyExistsException;
import pt.tecnico.mydrive.xml.IXMLVisitable;
import pt.tecnico.mydrive.xml.IXMLVisitor;

import java.util.Optional;

public class App extends App_Base implements IXMLVisitable {
    public static final String XML_TAG = "app";
    private static final Logger logger = Logger.getLogger(App.class);

    public App(Directory parent, String name, byte perm, long id, String content) {
        super();
        init(parent, name, perm, id, content);
    }

    public static Optional<? extends PlainFile> createIfNotExists(Directory parent, String name, byte perm, long id, String content) {
        Optional<App> opt = Optional.empty();
        try {
            App app = new App(parent, name, perm, id, content);
            opt = Optional.of(app);
        } catch (FilenameAlreadyExistsException _) {
            logger.debug("App with name *[" + name + "]* already exists!");
        }
        return opt;
    }

    @Override
    public Element accept(IXMLVisitor visitor) {
        return visitor.visit(this);
    }
}
