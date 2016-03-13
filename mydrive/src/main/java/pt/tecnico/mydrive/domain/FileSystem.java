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
    	super();
        init(name);
    }

    private void init(String name) throws InvalidUsernameException {
        setName(name);
        byte permission = (byte) 0b111101101;
        
        // Create root directory: "/"
        Directory rootDir = addDirectory(null, "", permission);
        setRootDir(rootDir);  // FIXME
        
        // Create home directory: "/home"
        Directory homeDir = addDirectory(rootDir, "home", permission);
        
        // Create Super User and respective directory: "/home/root"
        addUsers(new User(this, "root", "***", "Super User", (byte) 0b111101101));
        addDirectory(homeDir, "root", permission);
    }
    
    public Directory addDirectory(Directory parent, String name, byte permission) {
    	numFiles+=1;
    	Directory newDir = new Directory(parent, name, permission, numFiles);
    	return newDir;
    }
    
    

    public ArrayList<File> pathContent (ArrayList<String> path) throws UnknownPathException {
        return null;//return splitPath(path).showContent(); //FIXME
    }

    public String fileContent (ArrayList<String> path) throws UnknownPathException {
        return null; //return splitPath(path).getContent(); //FIXME
    }

    public void removeFile (ArrayList<String> path) throws UnknownPathException {
        splitPath(path).remove();
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
