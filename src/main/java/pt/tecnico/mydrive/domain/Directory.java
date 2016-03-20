package pt.tecnico.mydrive.domain;
import org.jdom2.Element;

import org.apache.log4j.Logger;

import pt.tecnico.mydrive.xml.IXMLVisitable;
import pt.tecnico.mydrive.xml.IXMLVisitor;
import pt.tecnico.mydrive.exception.FilenameAlreadyExistsException;
import pt.tecnico.mydrive.exception.FileNotFoundException;
import pt.tecnico.mydrive.exception.DirectoryNotEmptyException;


import java.util.List;
import java.util.ArrayList;

public class Directory extends Directory_Base implements IXMLVisitable {
    private static final Logger logger = Logger.getLogger(Directory.class);
    public static final String XML_TAG = "dir";

    public Directory() {
        super();
    }

    public Directory(Directory parent, String name, byte perm, long id) {
        super();
        init(parent, name, perm, id);
    }
    
    public static Directory fromPath(String path, FileSystem fs) {
    	Directory newDir = fs.createFileParents(path);
    	// FIXME: owner placeholder
        return fs.createDirectory(newDir, "root" , (byte) 000000);
    }

    @Override
    public boolean isCdAble() {
        return true;
    }

    /** constructor for root directory */
    public Directory(byte perm, long id) {
        super();
        init(this, "", perm, id);
    }

    @Override
    public String getFullPath() {
        if(getDirectory() == this) { // we're the root dir
            logger.trace("Directory.getFullPath() reached root dir");
            return "";
        } else {
            logger.trace("Directory.getFullPath() " + super.getFullPath());
            return super.getFullPath();
        }
    }

    @Override
    public void addFile( File file ) throws FilenameAlreadyExistsException {
        String filename = file.getName();
        if( hasFile( filename ) ) {
            throw new FilenameAlreadyExistsException( filename, getFullPath() );
        } else {
            super.addFile( file );
        }
    }

    @Override
    public void removeFile( File file ) throws FileNotFoundException {
        String filename = file.getName();
        if( !hasFile( filename ) ) {
            throw new FileNotFoundException( filename );
        } else {
            super.removeFile( file );
        }
    }

    @Override
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

    @Override
    public List<String> showContent() {
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
    public void remove() throws DirectoryNotEmptyException {
        // FIXME: deprecated method
        if( getFileCount() != 0 ) {
            throw new DirectoryNotEmptyException( getName() );
        }
        super.remove(); // remove the directory from its parent
    }


    @Override
    public Element accept(IXMLVisitor visitor) {
        return visitor.visit(this);
    }
}
