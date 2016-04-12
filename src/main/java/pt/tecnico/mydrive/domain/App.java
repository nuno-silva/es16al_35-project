package pt.tecnico.mydrive.domain;

import org.apache.log4j.Logger;
import org.jdom2.Element;
import pt.tecnico.mydrive.domain.xml.IXMLVisitable;
import pt.tecnico.mydrive.domain.xml.IXMLVisitor;
import pt.tecnico.mydrive.exception.FilenameAlreadyExistsException;

import java.util.Optional;

public class App extends App_Base implements IXMLVisitable {
    public static final String XML_TAG = "app";
    private static final Logger logger = Logger.getLogger(App.class);

    //all params
    public App(FileSystem fs, Directory parent, User owner, String name, byte perm, String content) {
        super();
        init(fs, parent, owner, name, perm, content);
    }

    //all but perm
    public App(FileSystem fs, Directory parent, User owner, String name, String content) {
        super();
        init(fs, parent, owner, name, owner.getMask(), content);
    }

    //all but owner
    public App(FileSystem fs, Directory parent, String name, byte perm, String content) {
        super();
        init(fs, parent, fs.getSuperUser(), name, perm, content);
    }

    //all but content
    public App(FileSystem fs, Directory parent, User owner, String name, byte perm) {
        super();
        init(fs, parent, owner, name, perm, "");
    }

    //all but content and perm
    public App(FileSystem fs, Directory parent, User owner, String name) {
        super();
        init(fs, parent, owner, name, owner.getMask(), "");
    }

    //all but content and owner
    public App(FileSystem fs, Directory parent, String name, byte perm) {
        super();
        init(fs, parent, fs.getSuperUser(), name, perm, "");
    }

    //all but owner and perm
    public App(FileSystem fs, Directory parent, String name, String content) {
        super();
        init(fs, parent, fs.getSuperUser(), name, fs.getSuperUser().getMask(), content);
    }

    //all but owner, perm and content
    public App(FileSystem fs, Directory parent, String name) {
        super();
        init(fs, parent, fs.getSuperUser(), name, fs.getSuperUser().getMask(), "");
    }

    public static Optional<? extends PlainFile> createIfNotExists(FileSystem fs, Directory parent, String name, byte perm, String content) {
        Optional<App> opt = Optional.empty();
        try {
            App app = new App(fs, parent, name, perm, content);
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
