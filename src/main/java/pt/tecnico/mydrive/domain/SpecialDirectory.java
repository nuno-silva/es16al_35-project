package pt.tecnico.mydrive.domain;


import org.apache.log4j.Logger;
import org.jdom2.Element;
import pt.tecnico.mydrive.domain.xml.Visitor;

public class SpecialDirectory extends SpecialDirectory_Base {
    private static final Logger logger = Logger.getLogger(SpecialDirectory.class);


    public SpecialDirectory(FileSystem fs, File parent, User owner, String name, byte perm) {
        super();
        init(fs, parent, owner, name, perm);
    }

    /**
     * constructor for root directory
     */
    public SpecialDirectory(FileSystem fs, byte perm) {
        super();
        init(fs, this, null, "", perm);
    }

    @Override
    public Element accept(Visitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public String getFullPath() {
        if (getName() == "") {
            logger.trace("getFullPath() reached root dir");
            return "";
        } else {
            logger.trace("getFullPath() " + super.getName());
            return super.getFullPath();
        }
    }

    @Override
    public void addFile(File file, User initiator) {
        // this is a hack to allow Users to create their homes
        // home is null when creating a new User
        if(initiator.getHome() == null) {
            addFile(file);
        } else {
            super.addFile(file, initiator);
        }
    }

    @Override
    public void setParentDir(File parent) {
        if(parent == null) { // used by remove()
            super.setParentDir(null);
        } else {
            // this is a hack to allow Users to create their homes
            // owner is null when creating root dir
            User owner = getOwner();
            if( owner == null ) {
                parent.addFile(this);
            } else {
                parent.addFile(this, owner);
            }
        }
    }
}
