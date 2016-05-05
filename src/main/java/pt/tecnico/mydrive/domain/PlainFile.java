package pt.tecnico.mydrive.domain;

import org.apache.log4j.Logger;
import org.jdom2.Element;
import pt.tecnico.mydrive.domain.xml.Visitable;
import pt.tecnico.mydrive.domain.xml.Visitor;
import pt.tecnico.mydrive.exception.FilenameAlreadyExistsException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class PlainFile extends PlainFile_Base implements Visitable {
    public static final String LINE_SEPARATOR = "\n";
    public static final String XML_TAG = "plain";
    private static final Logger logger = Logger.getLogger(PlainFile.class);


    protected PlainFile() {
        super();
    }

    //all params
    public PlainFile(FileSystem fs, Directory parent, User owner, String name, byte perm, String content) {
        super();
        init(fs, parent, owner, name, perm, content);
    }

    //all but content
    public PlainFile(FileSystem fs, Directory parent, User owner, String name, byte perm) {
        super();
        init(fs, parent, owner, name, perm, "");
    }

    //all but owner
    public PlainFile(FileSystem fs, Directory parent, String name, byte perm, String content) {
        super();
        init(fs, parent, fs.getSuperUser(), name, perm, content);
    }

    //all but permissions
    public PlainFile(FileSystem fs, Directory parent, User owner, String name, String content) {
        super();
        init(fs, parent, owner, name, owner.getMask(), content);
    }

    //all but content and owner
    public PlainFile(FileSystem fs, Directory parent, String name, byte perm) {
        super();
        init(fs, parent, fs.getSuperUser(), name, perm, "");
    }

    //all but content and permissions
    public PlainFile(FileSystem fs, Directory parent, User owner, String name) {
        super();
        init(fs, parent, owner, name, owner.getMask(), "");
    }

    //all permissions and owner
    public PlainFile(FileSystem fs, Directory parent, String name, String content) {
        super();
        init(fs, parent, fs.getSuperUser(), name, fs.getSuperUser().getMask(), content);
    }

    //all permissions, owner and content
    public PlainFile(FileSystem fs, Directory parent, String name) {
        super();
        init(fs, parent, fs.getSuperUser(), name, fs.getSuperUser().getMask(), "");
    }

    public static Optional<? extends PlainFile> createIfNotExists(FileSystem fs, Directory parent, String name, byte perm, String content) {
        Optional<PlainFile> opt = Optional.empty();
        try {
            PlainFile pf = new PlainFile(fs, parent, name, perm, content);
            opt = Optional.of(pf);
        } catch (FilenameAlreadyExistsException _) {
            logger.debug("PlainFile with name *[" + name + "]* already exists!");
        }
        return opt;
    }

    protected void init(FileSystem fs, Directory parent, User owner, String name, byte perm, String content) {
        logger.trace("init name: " + name);
        super.init(fs, parent, owner, name, perm);
        setContent(content);
    }

    @Override
    public boolean isCdAble() {
        return false;
    }


    public String readFileContent() {
        return getContent();
    }

    /**
     * Execute the file: each line is interpreted as "<app path> <args>*"
     * and each app is executed
     */
    public void execute() {
        // FIXME: not sure what this should return
        // TODO: method not needed for the first sprint
    }

    /**
     * sets the content of the PlainFile as a List of lines
     */
    public void setLines(List<String> lines) {
        String content = "";
        for (String line : lines) {
            content += line + LINE_SEPARATOR;
        }
        setContent(content);
    }

    /**
     * @returns the content of the PlainFile as a List of lines
     */
    @Override
    public List<String> showContent() {
        String content = getContent();
        List<String> lines = Arrays.asList(content.split(LINE_SEPARATOR));
        return lines;
    }

    @Override
    public File getFileByName(String name) {
        /* FIXME wtf is this? */
        return this;
    }

    @Override
    public Element accept(Visitor visitor) {
        return visitor.visit(this);
    }
}
