package pt.tecnico.mydrive.domain;

import org.apache.log4j.Logger;
import org.jdom2.Element;
import pt.tecnico.mydrive.domain.xml.Visitable;
import pt.tecnico.mydrive.domain.xml.Visitor;

public class Link extends Link_Base implements Visitable {
    private static final Logger logger = Logger.getLogger(Link.class);
    public static final String LINE_SEPARATOR = "\n";
    public static final String XML_TAG = "link";
    private final FileSystem fs;

    //all params
    public Link(FileSystem fs, Directory parent, User owner, String name, byte perm, String content) {
        super();
        this.fs = fs;
        init(fs, parent, owner, name, perm, content);
    }

    //all but perm
    public Link(FileSystem fs, Directory parent, User owner, String name, String content) {
        this(fs, parent, owner, name, owner.getMask(), content);
    }

    //all but owner
    public Link(FileSystem fs, Directory parent, String name, byte perm, String content) {
        this(fs, parent, fs.getSuperUser(), name, perm, content);
    }

    //all but content
    public Link(FileSystem fs, Directory parent, User owner, String name, byte perm) {
        this(fs, parent, owner, name, perm, "");
    }

    //all but content and perm
    public Link(FileSystem fs, Directory parent, User owner, String name) {
        this(fs, parent, owner, name, owner.getMask(), "");
    }

    //all but content and owner
    public Link(FileSystem fs, Directory parent, String name, byte perm) {
        this(fs, parent, fs.getSuperUser(), name, perm, "");
    }

    //all but owner and perm
    public Link(FileSystem fs, Directory parent, String name, String content) {
        this(fs, parent, fs.getSuperUser(), name, fs.getSuperUser().getMask(), content);
    }

    //all but owner, perm and content
    public Link(FileSystem fs, Directory parent, String name) {
        this(fs, parent, fs.getSuperUser(), name, fs.getSuperUser().getMask(), "");
    }

    @Override
    public boolean isCdAble() {
        /* TODO ver se isto e' preciso */
        return false;
    }


    /**
     * Gets the actual {@link File} that the {@link Link} points to.
     *
     * @return {@link File} pointed by this {@link Link}
     */
    public File getPointedFile() {
        String content = getContent().trim();
        if (!content.startsWith("/")) {
            // relative path, so append the path of the link to it
             /* TODO FIXME get using a relative path */
            content = getParentDir().getFullPath() + "/" + content;
        }
        return fs.getFile(content);
    }

    @Override
    public Element accept(Visitor visitor) {
        return visitor.visit(this);
    }
}
