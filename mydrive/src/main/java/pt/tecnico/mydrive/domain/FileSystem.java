package pt.tecnico.mydrive.domain;

import org.jdom2.Element;
import pt.tecnico.mydrive.xml.IXMLVisitable;
import pt.tecnico.mydrive.xml.IXMLVisitor;
import java.util.ArrayList;

public class FileSystem extends FileSystem_Base implements IXMLVisitable {

    public FileSystem() {
        super();
    }

    public FileSystem(String name) {
        init(name);
    }

    private void init(String name) {
        setName(name);
    }

    public ArrayList<File> pathContent (ArrayList<String> path) throws UnknownPathException {
        return null;//return splitPath(path).showContent(); //FIXME
    }

    public String fileContent (ArrayList<String> path) throws UnknownPathException {
        return null; //return splitPath(path).getContent(); //FIXME
    }

    public void removeFile (ArrayList<String> path) throws UnknownPathException {
        //FIXME splitPath(path).remove();
    }

    public File splitPath(ArrayList<String> path) throws UnknownPathException {
        File currentDir = getRootDir();
	/* FIXME
        for(String dir : path)
            currentDir = currentDir.getContent(dir); //FIXME
        */
        return currentDir;
    }

    @Override
    public Element accept(IXMLVisitor visitor) {
        return visitor.visit(this);
    }
}
