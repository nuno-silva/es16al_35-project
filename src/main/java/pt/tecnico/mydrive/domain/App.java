package pt.tecnico.mydrive.domain;

import org.apache.log4j.Logger;
import org.jdom2.Element;
import pt.tecnico.mydrive.domain.xml.Visitable;
import pt.tecnico.mydrive.domain.xml.Visitor;
import pt.tecnico.mydrive.exception.FileExecutionException;
import pt.tecnico.mydrive.exception.FilenameAlreadyExistsException;
import pt.tecnico.mydrive.exception.NotJavaFullyQualifiedNameException;
import pt.tecnico.mydrive.exception.PermissionDeniedException;
import pt.tecnico.mydrive.presentation.MydriveShell;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
    
    @Override
    public void execute(User initiator, String[] args) throws ClassNotFoundException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        if(!initiator.hasExecutePermission(this))
        	throw new PermissionDeniedException(initiator.getUsername() + " has no execute permissions for "+ this.getFullPath());
        
        Class<?> cls;
        Method meth;
        try { // name is a class: call main()
          cls = Class.forName(this.getName());
          meth = cls.getMethod("main", String[].class);
        } catch (ClassNotFoundException cnfe) { // name is a method
          int pos;
          if ((pos = this.getName().lastIndexOf('.')) < 0) throw cnfe;
          cls = Class.forName(this.getName().substring(0, pos));
          meth = cls.getMethod(this.getName().substring(pos+1), String[].class);
        }
        meth.invoke(null, (Object)args); // static method (ignore return)
    }
}
