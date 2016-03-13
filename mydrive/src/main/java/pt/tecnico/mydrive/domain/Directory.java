package pt.tecnico.mydrive.domain;
import org.jdom2.Element;
import pt.tecnico.mydrive.xml.IXMLVisitable;
import pt.tecnico.mydrive.xml.IXMLVisitor;
import pt.tecnico.mydrive.exception.FilenameAlreadyExistsException;
import pt.tecnico.mydrive.exception.FileNotFoundException;

import java.util.List;
import java.util.ArrayList;

public class Directory extends Directory_Base implements IXMLVisitable {

    public Directory() {
        super();
    }

    public Directory(Directory parent, String name, byte perm, long id) {
        init(parent, name, perm, id);
    }

    @Override
    public void addFile( File file ) throws FilenameAlreadyExistsException {
        String filename = file.getName();
        if( hasFile( filename ) ) {
            throw new FilenameAlreadyExistsException( filename );
        } else {
            super.addFile( file );
        }
    }

    @Override
    public void removeFile( File file ) throws FilenameAlreadyExistsException {
        String filename = file.getName();
        if( hasFile( filename ) ) {
            throw new FilenameAlreadyExistsException( filename );
        } else {
            super.removeFile( file );
        }
    }

    public File getFileByName( String name ) throws FileNotFoundException {
        if( name.equals(".") ) {
            return this;
        } else if( name.equals( ".." ) ) {
            return getDirectory(); // parent
        }

        for( File f : getFileSet() ) {
            if( f.getName().equals(name) ) {
                return f;
            }
        }
        throw new FileNotFoundException(name);
    }

    public boolean hasFile( String name ) {
        try {
            getFileByName( name );
            return true;
        } catch (FileNotFoundException e) {
            return false;
        }
    }

    public List<String> listFiles() {
        //TODO: should we use a Visitor for this?
        List<String> files = new ArrayList<String>();

        files.add(".");
        files.add("..");
        for( File f : getFileSet() ) {
            files.add( f.getName() );
        }
        // TODO: the format should be "<type> <perm> <dim> <owner> <date> <name>", but not for the first sprint, I think
        return files;
    }

    /** removes the Directory (from its parent) and all its Files */
    @Override
    public void remove() {
        for( File f : getFileSet() ) {
            f.remove();
        }
        super.remove(); // remove the directory from its parent
    }

    @Override
    public Element accept(IXMLVisitor visitor) {
        return visitor.visit(this);
    }
}
