package pt.tecnico.mydrive.domain;

import pt.ist.fenixframework.FenixFramework;
import pt.tecnico.mydrive.exception.InvalidUsernameException;
import pt.tecnico.mydrive.exception.UnknownPathException;

import java.util.List;

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

    public List<String> showPathContent(String path) throws UnknownPathException {
    	return currentFileSystem.pathContent(path); 
    }

    public List<String> showFileContent(String path) throws UnknownPathException {
    	return currentFileSystem.fileContent(path); 
    }

    public void removePathContent(String path) throws UnknownPathException {
    	currentFileSystem.removeFile(path); 
    }

    public void createUser(String username, String password, String name) throws InvalidUsernameException {
    	currentFileSystem.createUser(username, password, name);
    }

}
