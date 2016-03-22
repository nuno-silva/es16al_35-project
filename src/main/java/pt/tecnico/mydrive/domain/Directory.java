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
import java.util.Optional;

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

    /**
     * Creates the directory if one with the same name and parent does not already exist.
     * @param parent parent
     * @param name name
     * @param perm permissions
     * @param id ID
     * @return {@link java.util.Optional} containing either the newly created Directory or null.
     */
    public static Optional<Directory> createIfNotExists(Directory parent, String name, byte perm, long id) {
        Optional<Directory> opt = Optional.empty();
        try {
            Directory dir = new Directory(parent, name, perm, id);
            opt = Optional.of(dir);
        } catch (FilenameAlreadyExistsException _) {
            logger.debug("Directory with name *[" + name + "]* already exists!");
        }
        return opt;
    }
    
    public static Directory fromPath(String path, FileSystem fs) {
        logger.debug("Directory.fromPath: " + path);
    	Directory newDir = fs.createFileParents(path);
        logger.debug("Directory.fromPath: newDir path: " + newDir.getFullPath());
        String [] parts = path.split("/");
        Optional<Directory> opt = fs.createDirectoryIfNotExists(newDir, parts[parts.length - 1], (byte)0b00000000);
        return opt.get(); // NOTE: the Optional is guaranteed to have a Directory (read javadoc for more info)
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
        if(getDirectory() == this) { // we're the root dir (getDirectory() returns the parent dir)
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

    public void addFileIfNotExists(File file) {
        try {
            addFile(file);
        } catch (FilenameAlreadyExistsException _) {
            // Do nothing, filename already exists
            logger.debug("File with name *[" + file.getName() + "*] already exists in directory *[" + getName() + "]*");
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
    public File getFileByName(String name) throws FileNotFoundException {
        logger.debug("getFileByName: " + name);
        if(name.equals(".")) {
            return this;
        } else if(name.equals("..")) {
            return getDirectory(); // parent
        }

        for(File f : getFileSet()) {
            if(f.getName().equals(name)) {
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
