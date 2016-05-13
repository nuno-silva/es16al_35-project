package pt.tecnico.mydrive.domain;

import org.apache.log4j.Logger;
import org.jdom2.Element;
import java.util.Set;
import pt.tecnico.mydrive.domain.xml.Visitable;
import pt.tecnico.mydrive.domain.xml.Visitor;
import pt.tecnico.mydrive.exception.FileExecutionException;
import pt.tecnico.mydrive.exception.FileNotFoundException;
import pt.tecnico.mydrive.exception.FilenameAlreadyExistsException;
import pt.tecnico.mydrive.exception.PermissionDeniedException;
import pt.tecnico.mydrive.exception.ReadDirectoryException;
import pt.tecnico.mydrive.exception.WriteDirectoryException;
import pt.tecnico.mydrive.exception.LinkCycleException;

import java.lang.UnsupportedOperationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Directory extends Directory_Base implements Visitable {
    public static final String XML_TAG = "dir";
    private static final Logger logger = Logger.getLogger(Directory.class);

	protected Directory(){
		super();
	}

    //all params
    public Directory(FileSystem fs, File parent, User owner, String name, byte perm) {
        super();
        init(fs, parent, owner, name, perm);
    }

    //all but owner
    public Directory(FileSystem fs, File parent, String name, byte perm) {
        super();
        init(fs, parent, fs.getSuperUser(), name, perm);
    }

    //all but perm
    public Directory(FileSystem fs, File parent, User owner, String name) {
        super();
        init(fs, parent, owner, name, owner.getMask());
    }

    //all but owner and perm
    public Directory(FileSystem fs, File parent, String name) {
        super();
        init(fs, parent, fs.getSuperUser(), name, fs.getSuperUser().getMask());
    }


    /**
     * Creates the directory if one with the same name and parent does not already exist.
     * If no ower is provided, {@link SuperUser} is set to be the owner.
     *
     * @return {@link Optional} containing the {@link Directory} if it was created or one with such a name was found
     * or an empty Option, in case a {@link File} with such path and name exists, but it's not a Directory.
     */
    protected static Optional<Directory> createIfNotExists(FileSystem fs, File parent,
                                                           User owner, String name, byte perm) {
        try {
            Directory dir = new Directory(fs, parent, name, perm);
            if (owner == null) {
                logger.debug("createIfNotExists(): provided user is null, setting SuperUser as owner");
                owner = fs.getSuperUser();
            }
            dir.setOwner(owner);
            return Optional.of(dir);
        } catch (FilenameAlreadyExistsException _) {
            logger.debug("File(possibly a directory) with name *[" + name + "]* already exists!");
            File f = parent.getFile(name, owner);
            if (f.isCdAble()) {
                // File exists and it's a Directory
                return Optional.of((Directory)f);
            } else {
                return Optional.empty(); // file with such a name and path exists, but it's not a Directory
            }

        }
    }

    public static Directory fromPath(String path, User owner, FileSystem fs) {
        logger.debug("Directory.fromPath: " + path);
        Directory newDir = fs.createFileParents(path);
        logger.debug("Directory.fromPath: newDir path: " + newDir.getFullPath());
        String[] parts = path.split("/");
        Optional<Directory> opt = fs.createDirectoryIfNotExists(newDir, owner, parts[parts.length - 1], (byte) 0b00000000);
        return opt.get(); // NOTE: the Optional is guaranteed to have a Directory (read javadoc for more info)
    }

    @Override
    public boolean isCdAble() {
        return true;
    }

    @Override
    public String getContent(User initiator) throws ReadDirectoryException {
        throw new ReadDirectoryException("Cannot read " + getFullPath() + " since it's a directory.");
    }

    @Override
    public void setContent(String content, User initiator) throws WriteDirectoryException {
        throw new WriteDirectoryException("Cannot write to " + getFullPath() + " since it's a directory.");
    }

    @Override
    public String getFullPath() {
        logger.trace("getFullPath() " + super.getName());
        return super.getFullPath();
    }

    public void addFile(File file, User initiator) throws FilenameAlreadyExistsException, PermissionDeniedException {
        if(!initiator.hasWritePermission(this)) {
            throw new PermissionDeniedException("User '" + initiator.getUsername()
                    + "' can not write to '"+getFullPath()+"'");
        }
        addFile(file);
    }

    @Deprecated // should be protected
    public void addFile(File file) {
        String filename = file.getName();
        logger.debug("addFile: '" + filename + "' in '" + getName() + "'");
        if (hasFile(filename)) {
            logger.trace("addFile result: FilenameAlreadyExistsException");
            throw new FilenameAlreadyExistsException(filename, getFullPath());
        } else {
            logger.trace("addFile result:");
            super.addFile(file);
            logger.trace("super.addFile( file )");
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
    public File getFile(String path, User initiator, Set<File> visited) {
        logger.debug("getFile: '" + path+"'");
        String head = FileSystem.PathHelper.getHead(path);
        String tail = FileSystem.PathHelper.getTail(path);

        File f = getFileByName(head, initiator);

        if (tail == "") {
            /*while( f instanceof Link ) {
                f = ((Link)f).getPointedFile(initiator);
                if(visited.contains(f)) {
                    throw new LinkCycleException(f.getFullPath());
                } else {
                    visited.add(f);
                }
            }*/ // we're overriding File operations in Link
            return f;
        } else {
            return f.getFile(tail, initiator, visited);
        }
    }

    protected File getFileByName(String name) throws FileNotFoundException {
        logger.debug("getFileByName: '" + name + "' in '" + getName() + "'");
        if (name.equals(".")) {
            return this;
        } else if (name.equals("..")) {
            return getParentDir();
        }

        for (File f : getFileSet()) {
            if (f.getName().equals(name)) {
                return f;
            }
        }
        throw new FileNotFoundException(name, getFullPath());
    }

    protected File getFileByName(String name, User initiator) throws FileNotFoundException, PermissionDeniedException {
        if(!initiator.hasExecutePermission(this)) {
            throw new PermissionDeniedException("User '" + initiator.getUsername()
                    + "' can not read '"+getFullPath()+"'");
        }
        return getFileByName(name);
    }

    public boolean hasFile(String name) {
        try {
            getFileByName(name);
            return true;
        } catch (FileNotFoundException e) {
            return false;
        }
    }

    public boolean hasFile(String name, User initiator) {
        try {
            getFileByName(name, initiator);
            return true;
        } catch (FileNotFoundException e) {
            return false;
        }
    }

    @Override
    public void execute(User initiator, String[] args) {
    	throw new FileExecutionException("Directories can't be executed");
    }


    @Override
    public void remove(User initiator) throws PermissionDeniedException {
        File parent = getParentDir();
        if(!initiator.hasWritePermission(parent)) {
            throw new PermissionDeniedException("User '" + initiator.getUsername()
                    + "' can not write to '"+parent.getFullPath()+"'");
        }
        if(!initiator.hasDeletePermission(this)) {
            throw new PermissionDeniedException("User '" + initiator.getUsername()
                    + "' can not delete '"+getFullPath()+"'");
        }
        for (File f : getFileSet()) {
            f.remove(initiator);
        }
        super.remove(initiator); // remove the directory from its parent
    }

    /**
     * removes the Directory (from its parent) and all its Files
     */
    @Override
    public void remove() {
        for (File f : getFileSet()) {
            f.remove();
        }
        super.remove(); // remove the directory from its parent
    }


    @Override
    public Element accept(Visitor visitor) {
        return visitor.visit(this);
    }

    /** getFileCheck with permission checks */
    public Set<File> getFileSet(User initiator) throws PermissionDeniedException {
        if(!initiator.hasReadPermission(this)) {
            throw new PermissionDeniedException("User '" + initiator.getUsername()
                    + "' can not list '"+getFullPath()+"'");
        }
        return getFileSet();
    }

    @Override
    public void setUser(User u)throws UnsupportedOperationException{
        throw new UnsupportedOperationException("Setting user for Directory");
	}

  @Override
  public void setSession(Session s)throws UnsupportedOperationException{
    throw new UnsupportedOperationException("Setting session for Directory!");
  }
}
