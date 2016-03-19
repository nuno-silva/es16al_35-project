package pt.tecnico.mydrive.xml;

import ch.qos.logback.classic.db.names.ColumnName;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.joda.time.DateTime;
import pt.tecnico.mydrive.domain.*;

import java.util.Set;

/**
 * A concrete XMLVisitor implementation.
 */
public class XMLVisitor implements IXMLVisitor {
    private static XMLVisitor instance = null;

    // TODO: not reflection safe
    public static XMLVisitor getInstance() {
        if (instance == null) {
            instance = new XMLVisitor();
        }
        return instance;
    }

    private XMLVisitor() { /* empty on purpose */ }
    @Override
    public Element visit(Directory directory) {
        Element dirElement = visit((File)directory);
        dirElement.setName(Directory.XML_TAG);
        return dirElement;
    }

    @Override
    public Element visit(PlainFile plainFile) {
        String content;
        content = plainFile.getContent();
        Element fileElem = visit((File)plainFile);
        fileElem.setName(PlainFile.XML_TAG);

        if (content != null && content != "") {
            fileElem.addContent(new Element("content").setText(content));
        }

        return fileElem;
    }

    @Override
    public Element visit(Link link) {
        String pointer;
        pointer = link.getPointer();
        Element linkElem = visit((File)link);
        linkElem.setName(Link.XML_TAG);
        if(pointer != null && pointer != "") {
            linkElem.addContent(new Element("pointer").setText(pointer));
        }
        return linkElem;
    }

    @Override
    public Element visit(User user) {
        String password, name, home, mask;
        password = user.getPassword();
        name = user.getName();
        home = user.getHomePath();
        mask = PathHelper.getStringUmask(user.getMask());

        Element userElement = new Element(User.XML_TAG);

        userElement.setAttribute(new Attribute("username", user.getUsername()));

        if (password != null && password != "") {
            userElement.addContent(new Element("password").setText(user.getPassword()));
        }
        if (name != null && name != "") {
            userElement.addContent(new Element("name").setText(user.getName()));
        }
        if (home != null && home != "") {
            userElement.addContent(new Element("home").setText(user.getHomePath()));
        }
        if (mask != null && mask != "") {
            userElement.addContent(new Element("mask").setText(mask));
        }
        return userElement;
    }

    @Override
    public Element visit(App app) {
        return visit((PlainFile)app).setName(App.XML_TAG);
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
        mask = PathHelper.getStringUmask(file.getMask());
        lastMod = file.getLastMod();
        Element fileElement = new Element(File.XML_TAG); // temp placeholder
        fileElement.setAttribute(new Attribute("id", String.valueOf(file.getId())));

        if (name != null && name != "") {
            fileElement.addContent(new Element("name").setText(name));
        }

        if (mask != null && mask != "") {
            fileElement.addContent(new Element("mask").setText(mask));
        }

        if (lastMod != null) {
            fileElement.addContent(new Element("lastMod").setText(lastMod.toString()));
        }

        fileElement.addContent(new Element("path", file.getFullPath()));

        return fileElement;

    }
}
