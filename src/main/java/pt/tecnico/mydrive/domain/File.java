package pt.tecnico.mydrive.domain;

import org.apache.log4j.Logger;
import org.jdom2.Element;
import org.joda.time.DateTime;
import pt.tecnico.mydrive.domain.xml.IXMLVisitable;
import pt.tecnico.mydrive.domain.xml.IXMLVisitor;
import pt.tecnico.mydrive.domain.User;
import pt.tecnico.mydrive.exception.InvalidFileNameException;

import java.util.List;

public abstract class File extends File_Base implements IXMLVisitable, IPermissionable {
    private static final Logger logger = Logger.getLogger(File.class);
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
        logger.trace("init name: "+name);
        setName( name );
        setMask( perm );
        setOwner( owner );
        setParentDir( parent ); // must be called after setName!
        setLastMod( new DateTime() );
        setId( fs.commitNewFileId() ); // commitNewFileId must be called only when we're sure the File was successfully created
    }

    @Override
    public void setParentDir( Directory parent ) {
        logger.trace("setParentDir name: "+getName());
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
