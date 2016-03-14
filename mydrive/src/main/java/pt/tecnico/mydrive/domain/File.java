package pt.tecnico.mydrive.domain;

import java.util.List;

import org.jdom2.Element;
import pt.tecnico.mydrive.exception.InvalidFileNameException;
import pt.tecnico.mydrive.xml.IXMLVisitable;
import pt.tecnico.mydrive.xml.IXMLVisitor;

public abstract class File extends File_Base implements IXMLVisitable {

    public File() {
        super();
    }

    public File(Directory dir, String name, byte perm, long id) {
        super();
        init(dir, name, perm, id);
    }

    protected void init(Directory dir, String name, byte perm, long id){
        setDirectory(dir);
        setName(name);
        setId(id);
        setPerm(perm);
        //still need to add DateTime lastMod
    }

    public void remove() {
        setDirectory(null);
        deleteDomainObject();
    }
    
    public abstract File getFileByName( String name );
    
    public abstract List<String> showContent();

    @Override
    public void setName(String name) throws InvalidFileNameException {
        if( name.contains("/") || name.contains("\0") ) {
            throw new InvalidFileNameException(name);
        }
        super.setName(name);
    }

    @Override
    public Element accept(IXMLVisitor visitor) {
        return visitor.visit(this);
    }

}
