package pt.tecnico.mydrive.domain;

import org.jdom2.Element;
import pt.tecnico.mydrive.exception.InvalidUsernameException;
import pt.tecnico.mydrive.exception.UnknownPathException;
import pt.tecnico.mydrive.xml.IXMLVisitable;
import pt.tecnico.mydrive.xml.IXMLVisitor;
import java.util.ArrayList;

public class FileSystem extends FileSystem_Base implements IXMLVisitable {

	private static long numFiles = 0;
	
    public FileSystem() {
        super();
    }

    public FileSystem(String name) throws InvalidUsernameException {
        init(name);
    }

    private void init(String name) throws InvalidUsernameException {
        setName(name);
        byte permission = (byte) 111101101;
        // Create root directory: "/"
        numFiles+=1;
        Directory rootDir = new Directory("/", permission, numFiles);
        setRootDir(rootDir);  // FIXME
        // Create home directory: "/home"
        numFiles+=1;
        Directory homeDir = new Directory("home", permission, numFiles);
        rootDir.addFile(homeDir);
        // Create Super User and respective directory: "/home/root"
        addUsers(new User("root", "***", "Super User", (byte) 111101101));   // FIXME: construtor de user ja cria o seu diretorio
        numFiles+=1;
        Directory homeroot = new Directory("root", permission, numFiles);
        homeDir.addFile(homeroot);
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
