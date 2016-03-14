package pt.tecnico.mydrive.domain;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pt.ist.fenixframework.FenixFramework;

import pt.tecnico.mydrive.exception.InvalidUsernameException;
import pt.tecnico.mydrive.exception.UnknownPathException;

import java.util.List;

/*
    Uses the Singleton pattern.
*/

public class Manager extends Manager_Base {
    static final Logger log = LogManager.getRootLogger();
    private static FileSystem currentFileSystem;

    // TODO: not Reflection safe
    public static Manager getInstance() {
        Manager man = FenixFramework.getDomainRoot().getManager();
        if (man == null) {
            log.trace("created new Manager");
            man = new Manager();
            setup();
        }
        return man;
    }

    private Manager() {
        setRoot(FenixFramework.getDomainRoot());

        FileSystem newFs = new FileSystem("");
        addFileSystems(newFs); //dont know how to set fileSystem...
        currentFileSystem = newFs;

    }
    
    public static void setup() {
    	currentFileSystem.createPlainFile((Directory) currentFileSystem.getFile("/home"), "README", (byte) 00000000);
    	currentFileSystem.createDirectory((Directory) currentFileSystem.getFile("/usr"), "bin", (byte) 00000000);
    	currentFileSystem.fileContent("/home/README");
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

    public void writeReadMe() {
    	currentFileSystem.createReadMe();
    }
}
