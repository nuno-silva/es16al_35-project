package pt.tecnico.mydrive.domain;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pt.ist.fenixframework.FenixFramework;

import pt.tecnico.mydrive.exception.InvalidUsernameException;
import pt.tecnico.mydrive.exception.UnknownPathException;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;

/*
    Uses the Singleton pattern.
*/

public class Manager extends Manager_Base {
    static final Logger log = LogManager.getRootLogger();

    // TODO: not Reflection safe
    public static Manager getInstance() {
        Manager man = FenixFramework.getDomainRoot().getManager();
        if (man == null) {
            log.trace("created new Manager");
            man = new Manager();
        }

        setup();
        return man;
    }

    private Manager() {
        setRoot(FenixFramework.getDomainRoot());

        FileSystem newFs = new FileSystem("");
        addFileSystems(newFs); //dont know how to set fileSystem...

    }

    public static void setup() {
        FileSystem fs = getFirstFs();
    	// Create "/home/README":
    	PlainFile readme = fs.createPlainFile((Directory) fs.getFile("/home"), "README", (byte) 00000000);
    	// Create "/usr/local":
    	Directory local = fs.createFileParents("/usr/local/bin");
    	fs.createDirectory(local, "bin", (byte) 00000000);
    	// Print content of "/home/README":
    	insertUsersInFile(readme);
    	printContent("Content of plainfile README: ", fs.fileContent("/home/README"));
    	// Remove "/usr/local/bin":
    	fs.removeFile("/usr/local/bin");
    	//TODO: FAZER A EXPORTACAO DO XML
    	// Remove "/home/README":
    	fs.removeFile("/home/README");
    	// Print content of "/home":
    	printContent("Content of directory /home: ", fs.fileContent("/home"));
    }

    public static void printContent(String description, List<String> stringArray) {
    	System.out.println(description);
    	for (String line : stringArray)
    		System.out.println(line);
    }
    public static void insertUsersInFile(PlainFile file) {
    	List<String> users = new ArrayList<String>();
    	for (User user : getFirstFs().getUserSet()) {
    		users.add(user.getUsername());
    	}
    	file.setLines(users);
    }

    public static FileSystem getFirstFs() {
        Set<FileSystem> set = FenixFramework.getDomainRoot().getManager().getFileSystems();
        for( FileSystem fs : set ) {
            return fs;
        }
        return null;
    }

    public List<String> showPathContent(String path) throws UnknownPathException {
        return getFirstFs().pathContent(path);
    }

    public List<String> showFileContent(String path) throws UnknownPathException {
        return getFirstFs().fileContent(path);
    }

    public void removePathContent(String path) throws UnknownPathException {
        getFirstFs().removeFile(path);
    }

    public void createUser(String username, String password, String name) throws InvalidUsernameException {
        getFirstFs().createUser(username, password, name);
    }
}
