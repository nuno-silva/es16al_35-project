package pt.tecnico.mydrive.domain;

import org.apache.log4j.Logger;
import org.jdom2.Element;
import pt.tecnico.mydrive.domain.FileSystem;
import pt.tecnico.mydrive.domain.xml.Visitable;
import pt.tecnico.mydrive.domain.xml.Visitor;
import pt.tecnico.mydrive.exception.FilenameAlreadyExistsException;
import pt.tecnico.mydrive.exception.LinkCycleException;

import java.util.Optional;
import java.util.Set;

public class Link extends Link_Base implements Visitable {
    private static final Logger logger = Logger.getLogger(Link.class);
    public static final String LINE_SEPARATOR = "\n";
    public static final String XML_TAG = "link";

    //all params
    public Link(FileSystem fs, File parent, User owner, String name, byte perm, String content) {
        super();
        init(fs, parent, owner, name, perm, content);
    }

    //all but perm
    public Link(FileSystem fs, File parent, User owner, String name, String content) {
        this(fs, parent, owner, name, owner.getMask(), content);
    }

    //all but owner
    public Link(FileSystem fs, File parent, String name, byte perm, String content) {
        this(fs, parent, fs.getSuperUser(), name, perm, content);
    }

    //all but content
    public Link(FileSystem fs, File parent, User owner, String name, byte perm) {
        this(fs, parent, owner, name, perm, "");
    }

    //all but content and perm
    public Link(FileSystem fs, File parent, User owner, String name) {
        this(fs, parent, owner, name, owner.getMask(), "");
    }

    //all but content and owner
    public Link(FileSystem fs, File parent, String name, byte perm) {
        this(fs, parent, fs.getSuperUser(), name, perm, "");
    }

    //all but owner and perm
    public Link(FileSystem fs, File parent, String name, String content) {
        this(fs, parent, fs.getSuperUser(), name, fs.getSuperUser().getMask(), content);
    }

    @Override
    public boolean isCdAble() {
        return getPointedFile().isCdAble();
    }

    @Override
    public String getContent(User initiator) {
        File f = getPointedFile(initiator);
        return f.getContent(initiator);
    }

    @Override
    public void setContent(String content, User initiator) {
        File f = getPointedFile(initiator);
        f.setContent(content, initiator);
    }

    @Override
    public void addFile(File file, User initiator) {
        File f = getPointedFile(initiator);
        f.addFile(file, initiator);
    }


    @Override
    public File getFile(String path, User initiator, Set<File> visited) {
        logger.debug("getFile: '" + path+"'");
        File f = getPointedFile(initiator);

        if(visited.contains(f)) {
            throw new LinkCycleException(f.getFullPath());
        } else {
            visited.add(f);
        }

        return f.getFile(path, initiator, visited);
    }


    /**
     * Gets the actual {@link File} that the {@link Link} points to.
     *
     * @return {@link File} pointed by this {@link Link}
     */
    public File getPointedFile(User initiator) {
        String content = super.getContent(initiator).trim();
        if (FileSystem.PathHelper.isAbsolute(content)) {
            return FileSystem.getInstance().getFile(content, initiator);
        } else {
            return getParentDir().getFile(content, initiator);
        }
    }

    protected File getPointedFile() {
        FileSystem fs = FileSystem.getInstance();
        return getPointedFile(fs.getSuperUser());
    }

    @Override
    public Element accept(Visitor visitor) {
        return visitor.visit(this);
    }

    public static Optional<Link> createIfNotExists(FileSystem fs, File parent, User owner,
                                                                  String name, byte perm, String content) {
        Optional<Link> opt = Optional.empty();
        if (owner == null) {
            logger.debug("createIfNotExists(): provided user is null, setting SuperUser as owner");
            owner = fs.getSuperUser();
        }
        try {
            Link l = new Link(fs, parent, name, perm, content);
            l.setOwner(owner);
            opt = Optional.of(l);
        } catch (FilenameAlreadyExistsException _) {
            logger.debug("Link with name *[" + name + "]* already exists!");
        }
        return opt;
    }
}
