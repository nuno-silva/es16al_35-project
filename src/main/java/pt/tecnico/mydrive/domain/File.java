package pt.tecnico.mydrive.domain;

import org.jdom2.Element;
import org.joda.time.DateTime;
import pt.tecnico.mydrive.domain.xml.IXMLVisitable;
import pt.tecnico.mydrive.domain.xml.IXMLVisitor;
import pt.tecnico.mydrive.domain.User;
import pt.tecnico.mydrive.exception.InvalidFileNameException;

import java.util.List;

public abstract class File extends File_Base implements IXMLVisitable, IPermissionable {
    public static final String XML_TAG = "file";

    protected File() {
        super();
    }
    
    //all params
    public File(FileSystem fs, Directory parent, User owner, String name, byte perm ) {
        super();
        init( fs, parent, owner, name, perm );
    }

    //all but permissions
    public File(FileSystem fs, Directory parent, User owner, String name){
        super();
        init( fs, parent, owner, name, owner.getMask() );
    }

    //all but owner
    public File(FileSystem fs, Directory parent, String name, byte perm) {
        super();
        init( fs, parent, fs.getSuperUser(), name, perm);
    }

    //all but permissions and owner
    public File(FileSystem fs, Directory parent, String name) {
        super();
        init( fs, parent, fs.getSuperUser(), name, fs.getSuperUser().getMask() );
    }

    protected void init(FileSystem fs, Directory parent, User owner, String name, byte perm) {
        setName( name );
        setId( fs.commitNewFileId() );
        setMask( perm );
        setOwner( owner );
        setParentDir( parent ); // must be called after setName!
        setLastMod( new DateTime() );
    }

    @Override
    public void setParentDir( Directory parent ) {
        if( parent == null ) {
            super.setParentDir( parent );
            return;
        }
        parent.addFile(this);
    }

    public boolean isCdAble() {
        return false;
    }

    /* FIXME this is ugly \/ */
    private boolean isRootAccess( User u ){ return u.equals( u.getFs().getSuperUser() ) ? true : false; }
    
    public boolean checkReadPermission( User u){
        if( isRootAccess( u ) ) return true;
        return ( ( ( u.getByteMask() & this.getByteMask() ) & ( (byte)0b10001000 ) ) == 0 )? false : true;
    }

    public boolean checkWritePermission( User u){
        if( isRootAccess( u ) ) return true;
        return ( ( ( u.getByteMask() & this.getByteMask() ) & ( (byte)0b01000100 ) ) == 0 )? false : true;
    }

    public boolean checkExecutePermission( User u){
        if( isRootAccess( u ) ) return true;
        return ( ( ( u.getByteMask() & this.getByteMask() ) & ( (byte)0b00100010 ) ) == 0 )? false : true;
    }

    public boolean checkDeletePermission( User u){
        if( isRootAccess( u ) ) return true;
        return ( ( ( u.getByteMask() & this.getByteMask() ) & ( (byte)0b00010001 ) ) == 0 )? false : true;
    }

    public void remove() {
        setParentDir(null);
        setOwner(null);
        deleteDomainObject();
    }

    /** @returns the full path for this file (eg. "/home/root/file") */
    public String getFullPath() {
        return getParentDir().getFullPath() + "/" + getName();
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

    @Override
    public String getStringPermissions() {
        return MaskHelper.getStringPermissions(getMask());
    }

    @Override
    public String getStringMask() {
        return MaskHelper.getStringMask(getMask());
    }

    @Override
    public byte getByteMask() {
        return getMask();
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
