package pt.tecnico.mydrive.domain;

import org.jdom2.Element;
import pt.tecnico.mydrive.exception.InvalidUsernameException;
import pt.tecnico.mydrive.exception.UnknownPathException;
import pt.tecnico.mydrive.xml.IXMLVisitable;
import pt.tecnico.mydrive.xml.IXMLVisitor;
import java.util.List;

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
		root.setUmask((byte) 00000000);
		root.setFs(this);
		return root;
    }

    public Directory addDirectory(Directory parent, String name, byte permission) {
    	numFiles+=1;
    	Directory newDir = new Directory(parent, name, permission, numFiles);
    	return newDir;
    }

    public void createUser(String username, String password, String name) throws InvalidUsernameException {
    	addUser(new User(this, username, password, name, (byte) 00000000));
    }

    public List<String> pathContent (String path) throws UnknownPathException {
        return getFile(path).showContent();
    }

    public List<String> fileContent (String path) throws UnknownPathException {
        return getFile(path).showContent();
    }

    public void removeFile (String path) throws UnknownPathException {
        getFile(path).remove();
    }
    
    public void createReadMe() {
    	List<String> users = pathContent("/home");
    	Directory home = (Directory) getFile("/home");
    	numFiles+=1;
    		
    	PlainFile readMe = new PlainFile(home, "README", (byte) 00000000, numFiles);
    	readMe.setLines(users);
    }

    public File getFile(String path) throws UnknownPathException {
    	File currentDir = getRootDir();

    	if(!path.substring(0, 1).matches("/")) //check if root directory is used, otherwise ERROR!
    		throw new UnknownPathException(path);

        path = path.substring(1); // remove '/'
    	for(String dir : path.split("/"))
            currentDir = currentDir.getFileByName(dir);

        return currentDir;
    }

    @Override
    public Element accept(IXMLVisitor visitor) {
        return visitor.visit(this);
    }
}
