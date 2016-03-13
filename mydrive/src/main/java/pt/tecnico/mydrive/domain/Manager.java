package pt.tecnico.mydrive.domain;

import pt.ist.fenixframework.FenixFramework;
import pt.tecnico.mydrive.exception.InvalidUsernameException;
import pt.tecnico.mydrive.exception.UnknownPathException;

import java.util.ArrayList;

/*
    Uses the Singleton pattern.
*/

public class Manager extends Manager_Base {
	private FileSystem currentFileSystem;
	
    // TODO: not Reflection safe
    public static Manager getInstance() {
        Manager man = FenixFramework.getDomainRoot().getManager();
        if (man == null) {
            man = new Manager();
        }
        return man;
    }

    private Manager() {
        super();
        FenixFramework.getDomainRoot().setManager(this);
        FileSystem newFs = new FileSystem("");
        addFileSystems(newFs); //dont know how to set fileSystem...
        currentFileSystem = newFs;
    }
    
    public ArrayList<File> showPathContent(String path) throws UnknownPathException {
    	return currentFileSystem.pathContent(splitPath(path)); //FIXME 
    }
    
    public String showFileContent(String path) throws UnknownPathException {
    	return currentFileSystem.fileContent(splitPath(path)); //FIXME
    }
    
    public void removePathContent(String path) throws UnknownPathException {
    	currentFileSystem.removeFile(splitPath(path)); //FIXME
    }
    
    public void createUser(String username, String password, String name) throws InvalidUsernameException {
    	currentFileSystem.createUser(username, password, name);
    }
    
    public ArrayList<String> splitPath(String path) throws UnknownPathException {
    	ArrayList<String> directories = new ArrayList<String>();
    	
    	if(!path.substring(0, 1).matches("/")) //check if root directory is used, otherwise ERROR!
    		throw new UnknownPathException();
    	
    	directories.add("/");
    	
    	path = path.substring(1);
    	
    	for(String dir : path.split("/")) 
    		directories.add(dir);
    	
    	return directories;
    } 
}
