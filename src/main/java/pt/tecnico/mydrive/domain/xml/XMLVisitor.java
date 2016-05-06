package pt.tecnico.mydrive.domain.xml;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.joda.time.DateTime;
import pt.tecnico.mydrive.domain.*;

/**
 * A concrete XMLVisitor implementation.
 */
public class XMLVisitor implements Visitor {
    private static XMLVisitor instance = null;

    public static final String CONTENT_TAG = "content";
    public static final String POINTER_TAG = "pointer";
    public static final String USERNAME_TAG = "username";
    public static final String PASSWORD_TAG = "password";
    public static final String NAME_TAG = "name";
    public static final String HOME_TAG = "home";
    public static final String MASK_TAG = "mask";
    public static final String PATH_TAG = "path";
    public static final String LASTMOD_TAG = "lastMod";
    public static final String OWNER_TAG = "owner";
    public static final String ID_ATTR = "id";


    private XMLVisitor() { /* empty on purpose */ }

    // TODO: not reflection safe
    public static XMLVisitor getInstance() {
        if (instance == null) {
            instance = new XMLVisitor();
        }
        return instance;
    }

    @Override
    public Element visit(Directory directory) {
        Element dirElement = visit((File) directory);
        dirElement.setName(Directory.XML_TAG);
        return dirElement;
    }

    @Override
    public Element visit(PlainFile plainFile) {
        String content;
        content = plainFile.getContent();
        Element fileElem = visit((File) plainFile);
        fileElem.setName(PlainFile.XML_TAG);

        if (content != null && content != "") {
            fileElem.addContent(new Element(XMLVisitor.CONTENT_TAG).setText(content));
        }

        return fileElem;
    }

    @Override
    public Element visit(Link link) {
        String pointer;
        pointer = link.getContent();
        Element linkElem = visit((File) link);
        linkElem.setName(Link.XML_TAG);
        if (pointer != null && pointer != "") {
            linkElem.addContent(new Element(XMLVisitor.POINTER_TAG).setText(pointer));
        }
        return linkElem;
    }

    @Override
    public Element visit(User user) {
        String password, name, home, mask;
        password = user.getPassword();
        name = user.getName();
        home = user.getHomePath();
        mask = MaskHelper.getStringMask(user.getMask());

        Element userElement = new Element(User.XML_TAG);

        userElement.setAttribute(new Attribute(XMLVisitor.USERNAME_TAG, user.getUsername()));

        if (password != null && password != "") {
            userElement.addContent(new Element(XMLVisitor.PASSWORD_TAG).setText(user.getPassword()));
        }
        if (name != null && name != "") {
            userElement.addContent(new Element("name").setText(user.getName()));
        }
        if (home != null && home != "") {
            userElement.addContent(new Element(XMLVisitor.HOME_TAG).setText(user.getHomePath()));
        }
        if (mask != null && mask != "") {
            userElement.addContent(new Element(XMLVisitor.MASK_TAG).setText(mask));
        }
        return userElement;
    }

    @Override
    public Element visit(App app) {
        return visit((PlainFile) app).setName(App.XML_TAG);
    }

    @Override
    public Element visit(File file) {
        /*
            Visitor for the abstract File class. Children must first call this visitor,
            then Element.setName() to rename the Element as needed.
        */
        String name, mask;
        DateTime lastMod;
        name = file.getName();
        mask = MaskHelper.getStringMask(file.getPermissions());
        lastMod = file.getLastMod();
        Element fileElement = new Element(File.XML_TAG); // temp placeholder
        fileElement.setAttribute(new Attribute(XMLVisitor.ID_ATTR, String.valueOf(file.getId())));
        String path = (file.getFullPath() == "") ? "/" : file.getFullPath();

        if (name != null && name != "") {
            fileElement.addContent(new Element("name").setText(name));
        }

        if (mask != null && mask != "") {
            fileElement.addContent(new Element(XMLVisitor.MASK_TAG).setText(mask));
        }

        if (lastMod != null) {
            fileElement.addContent(new Element(XMLVisitor.LASTMOD_TAG).setText(lastMod.toString()));
        }

        fileElement.addContent(new Element(XMLVisitor.PATH_TAG).setText(path));

        fileElement.addContent(new Element(XMLVisitor.OWNER_TAG).setText(file.getOwner().getUsername()));

        return fileElement;

    }
}
