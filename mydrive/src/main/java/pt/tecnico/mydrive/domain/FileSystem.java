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

    public FileSystem(String name) {
    	super();
        init(name);
    }

    private void init(String name) {
        setName(name);
        byte permission = (byte) 0b111101101;
        
        // Create root directory: "/"
        Directory rootDir = addDirectory(null, "", permission);
        setRootDir(rootDir);  // FIXME
        
        // Create home directory: "/home"
        Directory homeDir = addDirectory(rootDir, "home", permission);
        
        // Create Super User and respective directory: "/home/root"
        addUser(createSuperUser());
        addDirectory(homeDir, "root", permission);
    }
    
    public User createSuperUser() {
    	User root = new User();
		root.setUsername("root");
		root.setPassword("***");
		root.setName("Super User");
		root.setUmask((byte) 0b111101101);
		root.setFs(this);
		return root;
    }
    
    public Directory addDirectory(Directory parent, String name, byte permission) {
    	numFiles+=1;
    	Directory newDir = new Directory(parent, name, permission, numFiles);
    	return newDir;
    }
    
    

    public ArrayList<File> pathContent (ArrayList<String> path) throws UnknownPathException {
        return null;//return getFile(path).listFiles(); //FIXME
    }

    public String fileContent (ArrayList<String> path) throws UnknownPathException {
        return null; //return getFile(path).getLines(); //FIXME
    }

    public void removeFile (ArrayList<String> path) throws UnknownPathException {
        getFile(path).remove();
    }

    public File getFile(ArrayList<String> path) throws UnknownPathException {
    	File currentDir = getRootDir();
	/* FIXME
        for(String dir : path)
            currentDir = currentDir.getFileByName(dir); //FIXME
        */
        return currentDir;
    }

    @Override
    public Element accept(IXMLVisitor visitor) {
        return visitor.visit(this);
    }
}
