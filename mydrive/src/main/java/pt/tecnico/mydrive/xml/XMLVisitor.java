package pt.tecnico.mydrive.xml;

import ch.qos.logback.classic.db.names.ColumnName;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.joda.time.DateTime;
import pt.tecnico.mydrive.domain.*;

/**
 * A concrete XMLVisitor implementation.
 */
public class XMLVisitor implements IXMLVisitor {
    @Override
    public Element visit(Directory directory) {
        Element dirElement = visit((File)directory);
        dirElement.setName("dir");
        return dirElement;
    }

    @Override
    public Element visit(PlainFile plainFile) {
        String content;
        content = plainFile.getContent();
        Element fileElem = visit((File)plainFile);
        fileElem.setName("plain");

        if (content != null && content != "") {
            fileElem.addContent(new Element("content").setText(content));
        }

        return fileElem;
    }

    @Override
    public Element visit(Link link) {
        return null;
    }

    @Override
    public Element visit(User user) {
        String password, name, home, mask;
        password = user.getPassword();
        name = user.getName();
        home = user.getHomePath();
        mask = PathHelper.getStringUmask(user.getUmask());

        Element userElement = new Element("user");

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
        return visit((PlainFile)app);
    }

    @Override
    public Element visit(FileSystem fileSystem) {
        return null;
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
        mask = PathHelper.getStringUmask(file.getPerm());
        lastMod = file.getLastMod();
        Element fileElement = new Element("file"); // temp placeholder
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

        return fileElement;

    }
}
