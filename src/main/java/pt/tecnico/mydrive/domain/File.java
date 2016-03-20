package pt.tecnico.mydrive.domain;

import java.util.List;

import org.jdom2.Element;
import pt.tecnico.mydrive.exception.InvalidFileNameException;
import pt.tecnico.mydrive.xml.IXMLVisitable;
import pt.tecnico.mydrive.xml.IXMLVisitor;

public abstract class File extends File_Base implements IXMLVisitable, IPermissionable {
    public static final String XML_TAG = "file";

    public File() {
        super();
    }

    public File(Directory dir, String name, byte perm, long id) {
        super();
        init(dir, name, perm, id);
    }

    protected void init(Directory parent, String name, byte perm, long id){
        setName(name);
        setId(id);
        setMask(perm);
        setDirectory(parent); // must be called after setName!
        //still need to add DateTime lastMod
    }

    @Override
    public void setDirectory( Directory parent ) {
        if( parent == null ) {
            super.setDirectory( parent );
            return;
        }
        parent.addFile(this);
    }

    public boolean isCdAble() { return false; };

    public void remove() {
        setDirectory(null);
        deleteDomainObject();
    }

    /** @returns the full path for this file (eg. "/home/root/file") */
    public String getFullPath() {
        return getDirectory().getFullPath() + "/" + getName();
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
