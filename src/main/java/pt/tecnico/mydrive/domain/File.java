package pt.tecnico.mydrive.domain;

import org.apache.log4j.Logger;
import org.jdom2.Element;
import org.joda.time.DateTime;
import pt.tecnico.mydrive.domain.xml.Visitable;
import pt.tecnico.mydrive.domain.xml.Visitor;
import pt.tecnico.mydrive.exception.InvalidFileNameException;
import pt.tecnico.mydrive.exception.PermissionDeniedException;
import pt.tecnico.mydrive.exception.FileNameTooLongException;
import pt.tecnico.mydrive.exception.WriteDirectoryException;

import java.util.List;

public abstract class File extends File_Base implements Visitable, IPermissionable {
    public static final String XML_TAG = "file";
    public static final String DEFAULT_MASK_STR = "11110000";
    private static final Logger logger = Logger.getLogger(File.class);
    private static final int MAX_PATH = 1024;

    protected File() {
        super();
    }

    protected void init(FileSystem fs, Directory parent, User owner, String name, byte perm) {
        logger.trace("init name: " + name);
        setName(name);
        setParentDir(parent); // must be called after setName!
        String path = getFullPath();
        if(path.length() > MAX_PATH) {
            // undo what we did. Note that we needed the parent to be set in order for getFullPath to work
            remove();
            throw new FileNameTooLongException(path, MAX_PATH);
        }
        setPermissions(perm);
        setOwner(owner);
        setLastMod(new DateTime());
        setId(fs.commitNewFileId()); // commitNewFileId must be called only when we're sure the File was successfully created
    }

    public boolean isCdAble() {
        return false;
    }

    // Note: methods kept here for backwards compatibility
    public boolean checkReadPermission(User u) {
        return u.hasReadPermission(this);
    }

    public boolean checkWritePermission(User u) {
        return u.hasWritePermission(this);
    }

    public boolean checkExecutePermission(User u) {
        return u.hasExecutePermission(this);
    }

    public boolean checkDeletePermission(User u) {
        return u.hasDeletePermission(this);
    }

    /**
     * Checks if {@link File}'s permissions are positive for a certain mask
     */
    private boolean hasPermission(byte baseMask) {
        return MaskHelper.andMasks(getPermissions(), baseMask) == baseMask;
    }

    // A user-friendly interface for permissions

    public boolean ownerCanRead() {
        return hasPermission(MaskHelper.OWNER_READ_MASK);
    }

    public boolean ownerCanWrite() {
        return hasPermission(MaskHelper.OWNER_WRITE_MASK);
    }

    public boolean ownerCanExecute() {
        return hasPermission(MaskHelper.OWNER_EXEC_MASK);
    }

    public boolean ownerCanDelete() {
        return hasPermission(MaskHelper.OWNER_DELETE_MASK);
    }

    public boolean otherCanRead() {
        return hasPermission(MaskHelper.OTHER_READ_MASK);
    }

    public boolean otherCanWrite() {
        return hasPermission(MaskHelper.OTHER_WRITE_MASK);
    }

    public boolean otherCanExecute() {
        return hasPermission(MaskHelper.OTHER_EXEC_MASK);
    }

    public boolean otherCanDelete() {
        return hasPermission(MaskHelper.OTHER_DELETE_MASK);
    }

    public void remove(User initiator) throws PermissionDeniedException {
        Directory parent = getParentDir();
        if(!initiator.hasWritePermission(parent)) {
            throw new PermissionDeniedException("User '" + initiator.getUsername()
                                                + "' can not write to '"+parent.getFullPath()+"'");
        }
        if(!initiator.hasDeletePermission(this)) {
            throw new PermissionDeniedException("User '" + initiator.getUsername()
                                                + "' can not delete '"+getFullPath()+"'");
        }
        remove();
    }

    public void remove() {
        setParentDir(null);
        setOwner(null);
        deleteDomainObject();
    }

    /**
     * @returns the full path for this file (eg. "/home/root/file")
     */
    public String getFullPath() {
        return getParentDir().getFullPath() + "/" + getName();
    }

    public File getFileByName(String name) {
        throw new UnsupportedOperationException();
    }

    public File getFileByName(String name, User initiator) {
        throw new UnsupportedOperationException();
    }

    public void assertIsWritable() {
        if (isCdAble()) {
            throw new WriteDirectoryException("Cannot write in " + getFullPath() + " since it's a directory.");
        }
    }

    public abstract List<String> showContent();

    @Override
    public void setName(String name) throws InvalidFileNameException {
        if (name.contains("/") || name.contains("\0")) {
            throw new InvalidFileNameException(name);
        }
        super.setName(name);
    }

    @Override
    public Directory getParentDir() {
        logger.trace("getParentDir: " + getName());
        return super.getParentDir();
    }

    @Override
    public void setParentDir(Directory parent) {
        logger.trace("setParentDir name: " + getName());
        if (parent == null) { // root dir
            super.setParentDir(parent);
            return;
        }
        parent.addFile(this);
    }

    @Override
    public Element accept(Visitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public String getStringPermissions() {
        return MaskHelper.getStringPermissions(getPermissions());
    }

    @Override
    public String getStringMask() {
        return MaskHelper.getStringMask(getPermissions());
    }

    @Override
    public byte getByteMask() {
        return getPermissions();
    }

    @Override
    public byte getANDedByteMask(IPermissionable other) {
        return MaskHelper.andMasks(getByteMask(), other.getByteMask());
    }

    @Override
    public String getANDedStringMask(IPermissionable other) {
        return MaskHelper.getStringMask(getANDedByteMask(other));
    }

    @Override
    public String getANDedStringPermissions(IPermissionable other) {
        return MaskHelper.getStringPermissions(getANDedByteMask(other));
    }
}
