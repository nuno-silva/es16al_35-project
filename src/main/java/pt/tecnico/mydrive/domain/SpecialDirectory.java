package pt.tecnico.mydrive.domain;


import org.jdom2.Element;
import pt.tecnico.mydrive.domain.xml.Visitor;

public class SpecialDirectory extends SpecialDirectory_Base {


    public SpecialDirectory(FileSystem fs, Directory parent, User owner, String name, byte perm) {
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

}
