package pt.tecnico.mydrive.domain;

import org.jdom2.Element;
import pt.tecnico.mydrive.domain.xml.IXMLVisitable;
import pt.tecnico.mydrive.domain.xml.IXMLVisitor;

import java.util.Arrays;
import java.util.List;

public class Link extends Link_Base implements IXMLVisitable {
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
        return false;
    }

    @Override
    public File getFileByName(String name) {
        /* FIXME wtf is this? */
        return this;
    }

    /**
     * @returns the content of the pointed {@link PlainFile} as a List of lines
     */
    @Override
    public List<String> showContent() {
        PlainFile f = getPointedFile();
        List<String> lines = Arrays.asList(f.getContent().split(LINE_SEPARATOR));
        return lines;
    }

    @Override
    public void setContent(String newContent) {
        getPointedFile().setContent(newContent);
    }

    /**
     * Gets the actual {@link PlainFile} that the {@link Link} points to.
     *
     * @return {@link PlainFile} pointed by this {@link Link}
     */
    private PlainFile getPointedFile() {
        String content = getContent().trim();
        if (!content.startsWith("/")) {
            // relative path, so append the path of the link to it
            content = getParentDir().getFullPath() + "/" + content;
        }
        // TODO: this ignores executables, basically treats them as plain files
        return (PlainFile) fs.getFile(content);
    }

    @Override
    public Element accept(IXMLVisitor visitor) {
        return visitor.visit(this);
    }
}
