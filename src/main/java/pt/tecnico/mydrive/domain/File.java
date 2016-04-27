package pt.tecnico.mydrive.domain;

import org.apache.log4j.Logger;
import org.jdom2.Element;
import org.joda.time.DateTime;
import pt.tecnico.mydrive.domain.xml.XMLVisitable;
import pt.tecnico.mydrive.domain.xml.XMLVisitor;
import pt.tecnico.mydrive.exception.InvalidFileNameException;
import pt.tecnico.mydrive.exception.PermissionDeniedException;

import java.util.List;

public abstract class File extends File_Base implements XMLVisitable, IPermissionable {
    public static final String XML_TAG = "file";
    private static final Logger logger = Logger.getLogger(File.class);

    protected File() {
        super();
    }

    //all params
    public File(FileSystem fs, Directory parent, User owner, String name, byte perm) {
        super();
        init(fs, parent, owner, name, perm);
    }

    //all but permissions
    public File(FileSystem fs, Directory parent, User owner, String name) {
        super();
        init(fs, parent, owner, name, owner.getMask());
    }

    //all but owner
    public File(FileSystem fs, Directory parent, String name, byte perm) {
        super();
        init(fs, parent, fs.getSuperUser(), name, perm);
    }

    //all but permissions and owner
    public File(FileSystem fs, Directory parent, String name) {
        super();
        init(fs, parent, fs.getSuperUser(), name, fs.getSuperUser().getMask());
    }

    protected void init(FileSystem fs, Directory parent, User owner, String name, byte perm) {
        logger.trace("init name: " + name);
        setName(name);
        setPermissions(perm);
        setOwner(owner);
        setParentDir(parent); // must be called after setName!
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
        if (parent == null) {
            super.setParentDir(parent);
            return;
        }
        parent.addFile(this);
    }

    @Override
    public Element accept(XMLVisitor visitor) {
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
