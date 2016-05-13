package pt.tecnico.mydrive.domain;

import org.apache.log4j.Logger;
import org.jdom2.Element;
import pt.tecnico.mydrive.domain.xml.Visitable;
import pt.tecnico.mydrive.domain.xml.Visitor;
import pt.tecnico.mydrive.exception.FilenameAlreadyExistsException;
import pt.tecnico.mydrive.exception.PermissionDeniedException;
import pt.tecnico.mydrive.exception.WriteDirectoryException;
import pt.tecnico.mydrive.exception.IsNotCdAbleException;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class PlainFile extends PlainFile_Base implements Visitable {
    public static final String LINE_SEPARATOR = "\n";
    public static final String XML_TAG = "plain";
    private static final Logger logger = Logger.getLogger(PlainFile.class);


    protected PlainFile() {
        super();
    }

    //all params
    public PlainFile(FileSystem fs, File parent, User owner, String name, byte perm, String content) {
        super();
        init(fs, parent, owner, name, perm, content);
    }

    //all but content
    public PlainFile(FileSystem fs, File parent, User owner, String name, byte perm) {
        super();
        init(fs, parent, owner, name, perm, "");
    }

    //all but owner
    public PlainFile(FileSystem fs, File parent, String name, byte perm, String content) {
        super();
        init(fs, parent, fs.getSuperUser(), name, perm, content);
    }

    //all but permissions
    public PlainFile(FileSystem fs, File parent, User owner, String name, String content) {
        super();
        init(fs, parent, owner, name, owner.getMask(), content);
    }

    //all but content and owner
    public PlainFile(FileSystem fs, File parent, String name, byte perm) {
        super();
        init(fs, parent, fs.getSuperUser(), name, perm, "");
    }

    //all but content and permissions
    public PlainFile(FileSystem fs, File parent, User owner, String name) {
        super();
        init(fs, parent, owner, name, owner.getMask(), "");
    }

    //all permissions and owner
    public PlainFile(FileSystem fs, File parent, String name, String content) {
        super();
        init(fs, parent, fs.getSuperUser(), name, fs.getSuperUser().getMask(), content);
    }

    //all permissions, owner and content
    public PlainFile(FileSystem fs, File parent, String name) {
        super();
        init(fs, parent, fs.getSuperUser(), name, fs.getSuperUser().getMask(), "");
    }

    public static Optional<? extends PlainFile> createIfNotExists(FileSystem fs, File parent, User owner,
                                                                  String name, byte perm, String content) {
        Optional<PlainFile> opt = Optional.empty();
        if (owner == null) {
            logger.debug("createIfNotExists(): provided user is null, setting SuperUser as owner");
            owner = fs.getSuperUser();
        }
        try {
            PlainFile pf = new PlainFile(fs, parent, name, perm, content);
            pf.setOwner(owner);
            opt = Optional.of(pf);
        } catch (FilenameAlreadyExistsException _) {
            logger.debug("PlainFile with name *[" + name + "]* already exists!");
        }
        return opt;
    }

    protected void init(FileSystem fs, File parent, User owner, String name, byte perm, String content) {
        logger.trace("init name: " + name);
        super.init(fs, parent, owner, name, perm);
        super.setContent(content);
    }

    @Override
    public boolean isCdAble() {
        return false;
    }

    @Override
    public String getContent(User initiator) throws PermissionDeniedException {
        if (!initiator.hasReadPermission(this)) {
            throw new PermissionDeniedException(initiator.getUsername() + " has no read permissions for "
                    + this.getFullPath());
        }

        return getContent();
    }

    /**
     * Execute the file: each line is interpreted as "<app path> <args>*"
     * and each app is executed
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws ClassNotFoundException
     */
    @Override
    public void execute(User initiator, String[] args) throws NoSuchMethodException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        if(!initiator.hasExecutePermission(this))
        	throw new PermissionDeniedException(initiator.getUsername() + " has no execute permissions for "+ this.getFullPath());

        String content = this.getContent();
        List<String> separateLines = Arrays.asList(content.split( "\n" ));

        for(String separate : separateLines) {
            System.out.println("One line: "+ separate);
            List<String> line = new ArrayList<String>(Arrays.asList( separate.split( " " )));

            String path = line.get(0);
            line.remove(0);

            int size = line.size();
            String[] arguments = new String[size];
            for(int i =0; i<size; i++) {
                arguments = line.toArray(arguments);
            }

            File file = FileSystem.getInstance().getFile(path, initiator);
            file.execute(initiator, arguments);
        }
    }


    @Override
    public void setContent(String content) {
        throw new PermissionDeniedException("Cannot set contents of a file directly.");
    }

    /**
     * Sets content of a file if the user has permission to do so.
     *
     * @param content
     * @param initiator
     */
    @Override
    public void setContent(String content, User initiator) {

        assertIsWritable();

        if (!initiator.hasWritePermission(this)) {
            throw new PermissionDeniedException(initiator.getUsername() + " has no write permissions for "
                    + getFullPath());
        }

        super.setContent(content);
    }


    @Override
    public Element accept(Visitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public File getFile(String path, User initiator, Set<File> visited) {
        throw new IsNotCdAbleException(getFullPath() + " isn't CdAble'");
    }

    @Override
    public Set<File> getFileSet(User initiator) {
        throw new IsNotCdAbleException(getFullPath() + " isn't CdAble'");
    }

    @Override
    public void addFile(File file, User initiator) throws FilenameAlreadyExistsException {
        throw new IsNotCdAbleException(getFullPath() + " isn't CdAble'");
    }
}
