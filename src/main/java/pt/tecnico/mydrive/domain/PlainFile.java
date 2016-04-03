package pt.tecnico.mydrive.domain;

import org.apache.log4j.Logger;
import org.jdom2.Element;
import pt.tecnico.mydrive.domain.xml.IXMLVisitable;
import pt.tecnico.mydrive.domain.xml.IXMLVisitor;
import pt.tecnico.mydrive.exception.FilenameAlreadyExistsException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class PlainFile extends PlainFile_Base implements IXMLVisitable {
    public static final String LINE_SEPARATOR = "\n";
    public static final String XML_TAG = "plain";
    private static final Logger logger = Logger.getLogger(PlainFile.class);


    protected PlainFile() {
        super();
    }

    public PlainFile(Directory parent, String name, byte perm, long id, String content) {
        super();
        init(parent, name, perm, id, content);
    }

    public static Optional<? extends PlainFile> createIfNotExists(Directory parent, String name, byte perm, long id, String content) {
        Optional<PlainFile> opt = Optional.empty();
        try {
            PlainFile pf = new PlainFile(parent, name, perm, id, content);
            opt = Optional.of(pf);
        } catch (FilenameAlreadyExistsException _) {
            logger.debug("PlainFile with name *[" + name + "]* already exists!");
        }
        return opt;
    }

    /** construct an empty PlainFile */
    public PlainFile( Directory parent, String name, byte perm, long id ) {
        super();
        init( parent, name, perm, id, "" );
    }

    @Override
    public boolean isCdAble(){
		return false;
	}

    protected void init(Directory parent, String name, byte perm, long id, String content){
        super.init(parent, name, perm, id);
        setContent(content);
    }
	public String readFileContent(){
		return getContent();
	}
    /** Execute the file: each line is interpreted as "<app path> <args>*"
     *  and each app is executed */
    public void execute() {
        // FIXME: not sure what this should return
        // TODO: method not needed for the first sprint
    }

    /** sets the content of the PlainFile as a List of lines */
    public void setLines( List<String> lines ) {
        String content = "";
        for( String line : lines ) {
            content += line + LINE_SEPARATOR;
        }
        setContent( content );
    }

    /** @returns the content of the PlainFile as a List of lines */
    @Override
    public List<String> showContent() {
        String content = getContent();
        List<String> lines = Arrays.asList( content.split( LINE_SEPARATOR ) );
        return lines;
    }

    @Override
    public File getFileByName( String name ) {
    	return this;
    }

    @Override
    public Element accept(IXMLVisitor visitor) {
        return visitor.visit(this);
    }
}
