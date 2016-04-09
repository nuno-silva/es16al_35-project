package pt.tecnico.mydrive.domain;

import org.jdom2.Element;
import pt.tecnico.mydrive.domain.xml.IXMLVisitable;
import pt.tecnico.mydrive.domain.xml.IXMLVisitor;

import java.util.Arrays;
import java.util.List;

public class Link extends Link_Base implements IXMLVisitable {
	public static final String LINE_SEPARATOR = "\n";
    public static final String XML_TAG = "link";

    //all params
    public Link(FileSystem fs, Directory parent, User owner, String name, byte perm, String content) {
        super();
        init( fs, parent, owner, name, perm, content );
    }
    
    //all but perm
    public Link(FileSystem fs, Directory parent, User owner, String name, String content) {
        super();
        init( fs, parent, owner, name, owner.getMask(), content );
    }
     
    //all but owner
    public Link(FileSystem fs, Directory parent, String name, byte perm, String content) {
        super();
        init( fs, parent, fs.getSuperUser(), name, perm, content );
    }

    //all but content
    public Link(FileSystem fs, Directory parent, User owner, String name, byte perm) {
        super();
        init( fs, parent, owner, name, perm, "" );
    }
        
    //all but content and perm
    public Link(FileSystem fs, Directory parent, User owner, String name) {
        super();
        init( fs, parent, owner, name, owner.getMask(), "" );
    }

    //all but content and owner
    public Link(FileSystem fs, Directory parent, String name, byte perm) {
        super();
        init( fs, parent, fs.getSuperUser(), name, perm, "" );
    }
        
    //all but owner and perm
    public Link(FileSystem fs, Directory parent, String name, String content) {
        super();
        init( fs, parent, fs.getSuperUser(), name, fs.getSuperUser().getMask(), content );
    }
        
    //all but owner, perm and content
    public Link(FileSystem fs, Directory parent, String name) {
        super();
        init( fs, parent, fs.getSuperUser(), name, fs.getSuperUser().getMask(), "" );
    }


    @Override
    public boolean isCdAble() {
            return false;
	}

    @Override
    public File getFileByName( String name ) {
    	return this;
    }

    /** @returns the content of the PlainFile as a List of lines */
    @Override
    public List<String> showContent() {
        String content = getContent();
        List<String> lines = Arrays.asList( content.split( LINE_SEPARATOR ) );
        return lines;
    }

    @Override
    public Element accept(IXMLVisitor visitor) {
        return visitor.visit(this);
    }
}
