package pt.tecnico.mydrive.domain;
import java.util.Arrays;
import java.util.List;
import pt.tecnico.mydrive.exception.IsNotCdAbleException;

import org.jdom2.Element;
import pt.tecnico.mydrive.xml.IXMLVisitable;
import pt.tecnico.mydrive.xml.IXMLVisitor;

public class Link extends Link_Base implements IXMLVisitable {
	public static final String LINE_SEPARATOR = "\n";
    public static final String XML_TAG = "link";


    public Link() {
        super();
    }

    public Link(Directory dir, String name, byte perm, long id, String pointer) {
        init(dir, name, perm, id);
        setPointer(pointer);
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
        String content = getPointer();
        List<String> lines = Arrays.asList( content.split( LINE_SEPARATOR ) );
        return lines;
    }

    @Override
    public Element accept(IXMLVisitor visitor) {
        return visitor.visit(this);
    }
}
