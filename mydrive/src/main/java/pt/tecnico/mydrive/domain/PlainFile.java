package pt.tecnico.mydrive.domain;
import org.jdom2.Element;
import pt.tecnico.mydrive.xml.IXMLVisitable;
import pt.tecnico.mydrive.xml.IXMLVisitor;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class PlainFile extends PlainFile_Base implements IXMLVisitable {
    public static final String LINE_SEPARATOR = "\n";

    public PlainFile() {
        super();
    }

    public PlainFile(Directory dir, String name, byte perm, long id, String content) {
        init(dir, name, perm, id, content);
    }

    /** construct an empty PlainFile */
    public PlainFile( Directory dir, String name, byte perm, long id ) {
        init( dir, name, perm, id, "" );
    }

    protected void init(Directory dir, String name, byte perm, long id, String content){
        super.init(dir, name, perm, id);
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

    /** @returns the content of the PlainFile as a List of lines */
    public List<String> getLines() {
        String content = getContent();
        List<String> lines = Arrays.asList( content.split( LINE_SEPARATOR ) );
        return lines;
    }

    /** sets the content of the PlainFile as a List of lines */
    public void setLines( List<String> lines ) {
        String content = "";
        for( String line : lines ) {
            content += line + LINE_SEPARATOR;
        }
        setContent( content );
    }

    @Override
    public Element accept(IXMLVisitor visitor) {
        return visitor.visit(this);
    }
}
