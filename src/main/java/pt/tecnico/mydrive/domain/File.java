package pt.tecnico.mydrive.domain;

import org.apache.log4j.Logger;
import org.jdom2.Element;
import org.joda.time.DateTime;
import pt.tecnico.mydrive.domain.xml.IXMLVisitable;
import pt.tecnico.mydrive.domain.xml.IXMLVisitor;
import pt.tecnico.mydrive.exception.InvalidFileNameException;

import java.util.List;

public abstract class File extends File_Base implements IXMLVisitable, IPermissionable {
    private static final Logger logger = Logger.getLogger(File.class);
    public static final String XML_TAG = "file";

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

    @Override
    public void setParentDir(Directory parent) {
        logger.trace("setParentDir name: " + getName());
        if (parent == null) {
            super.setParentDir(parent);
            return;
        }
        parent.addFile(this);
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

    // A user-friendly interface for permissions

    /**
     * Checks if {@link File}'s permissions are positive for a certain mask
     */
    private boolean hasPermission(byte baseMask) {
        return MaskHelper.andMasks(getPermissions(), baseMask) == baseMask;
    }

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

    public abstract File getFileByName(String name);

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
    public Element accept(IXMLVisitor visitor) {
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
