package pt.tecnico.mydrive.domain;

import org.apache.log4j.Logger;
import org.jdom2.Element;
import pt.tecnico.mydrive.domain.xml.IXMLVisitable;
import pt.tecnico.mydrive.domain.xml.IXMLVisitor;
import pt.tecnico.mydrive.exception.FileNotFoundException;
import pt.tecnico.mydrive.exception.FilenameAlreadyExistsException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Directory extends Directory_Base implements IXMLVisitable {
    private static final Logger logger = Logger.getLogger(Directory.class);
    public static final String XML_TAG = "dir";

    //all params
    public Directory(FileSystem fs, Directory parent, User owner, String name, byte perm) {
        super();
        init( fs, parent, owner, name, perm );
    }

    //all but owner
    public Directory(FileSystem fs, Directory parent, String name, byte perm) {
        super();
        init( fs, parent, fs.getSuperUser(), name, perm );
    }

    //all but perm
    public Directory(FileSystem fs, Directory parent, User owner, String name) {
        super();
        init( fs, parent, owner, name, owner.getMask() );
    }
     //all but owner and perm
    public Directory(FileSystem fs, Directory parent, String name) {
        super();
        init( fs, parent, fs.getSuperUser(), name, fs.getSuperUser().getMask() );
    }
    /**
     * Creates the directory if one with the same name and parent does not already exist.
     * @param parent parent Directory
     * @param name name
     * @param perm permissions
     * @param id ID
     * @return {@link java.util.Optional} containing either the newly created Directory or null.
     */
    public static Optional<Directory> createIfNotExists(FileSystem fs, Directory parent, String name, byte perm) {
        Optional<Directory> opt = Optional.empty();
        try {
            Directory dir = new Directory(fs, parent, name, perm);
            opt = Optional.of(dir);
        } catch (FilenameAlreadyExistsException _) {
            logger.debug("Directory with name *[" + name + "]* already exists!");
        }
        return opt;
    }

    public static Directory fromPath(String path, FileSystem fs) {
        /* FIXME why is this static method here? shouldn't this be done in FileSystem? (Nuno). Agreed(Jorge) */
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
    public Directory(FileSystem fs, byte perm) {
        super();
        init( fs, this, fs.getSuperUser(), "", perm);
    }

    @Override
    public String getFullPath() {
        if(getParentDir() == this) { // we're the root dir
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
            return getParentDir();
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
