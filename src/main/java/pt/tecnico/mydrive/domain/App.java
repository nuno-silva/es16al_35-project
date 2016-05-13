package pt.tecnico.mydrive.domain;

import org.apache.log4j.Logger;
import org.jdom2.Element;
import pt.tecnico.mydrive.domain.xml.Visitable;
import pt.tecnico.mydrive.domain.xml.Visitor;
import pt.tecnico.mydrive.exception.FilenameAlreadyExistsException;
import pt.tecnico.mydrive.exception.NotJavaFullyQualifiedNameException;

import java.util.Optional;

public class App extends App_Base implements Visitable {
    public static final String XML_TAG = "app";
    private static final Logger logger = Logger.getLogger(App.class);

    //all params
    public App(FileSystem fs, File parent, User owner, String name, byte perm, String content) {
        super();
        checkContent(content);
        init(fs, parent, owner, name, perm, content);
    }

    //all but perm
    public App(FileSystem fs, File parent, User owner, String name, String content) {
        super();
        checkContent(content);
        init(fs, parent, owner, name, owner.getMask(), content);
    }

    //all but owner
    public App(FileSystem fs, File parent, String name, byte perm, String content) {
        super();
        checkContent(content);
        init(fs, parent, fs.getSuperUser(), name, perm, content);
    }

    //all but content
    public App(FileSystem fs, File parent, User owner, String name, byte perm) {
        super();
        init(fs, parent, owner, name, perm, "");
    }

    //all but content and perm
    public App(FileSystem fs, File parent, User owner, String name) {
        super();
        init(fs, parent, owner, name, owner.getMask(), "");
    }

    //all but content and owner
    public App(FileSystem fs, File parent, String name, byte perm) {
        super();
        init(fs, parent, fs.getSuperUser(), name, perm, "");
    }

    //all but owner and perm
    public App(FileSystem fs, File parent, String name, String content) {
        super();
        checkContent(content);
        init(fs, parent, fs.getSuperUser(), name, fs.getSuperUser().getMask(), content);
    }

    //all but owner, perm and content
    public App(FileSystem fs, File parent, String name) {
        super();
        init(fs, parent, fs.getSuperUser(), name, fs.getSuperUser().getMask(), "");
    }

    public static Optional<? extends PlainFile> createIfNotExists(FileSystem fs, File parent, User owner,
                                                                  String name, byte perm, String content) {
        Optional<App> opt = Optional.empty();
        if (owner == null) {
            logger.debug("createIfNotExists(): provided user is null, setting SuperUser as owner");
            owner = fs.getSuperUser();
        }
        try {
            App app = new App(fs, parent, name, perm, content);
            app.setOwner(owner);
            opt = Optional.of(app);
        } catch (FilenameAlreadyExistsException _) {
            logger.debug("App with name *[" + name + "]* already exists!");
        }
        return opt;
    }
    
    public void checkContent(String content) {
    	try {
			Class.forName(content);
		} catch (ClassNotFoundException e) {
			throw new NotJavaFullyQualifiedNameException();
		}
    }

    @Override
    public Element accept(Visitor visitor) {
        return visitor.visit(this);
    }
}
