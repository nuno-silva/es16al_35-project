package pt.tecnico.mydrive.domain;

import org.jdom2.Document;
import org.jdom2.Element;

import pt.tecnico.mydrive.exception.InvalidUsernameException;
import pt.tecnico.mydrive.exception.UnknownPathException;
import pt.tecnico.mydrive.exception.FilenameAlreadyExistsException;
import pt.tecnico.mydrive.exception.FileNotFoundException;

import pt.tecnico.mydrive.xml.IXMLVisitable;
import pt.tecnico.mydrive.xml.IXMLVisitor;
import pt.tecnico.mydrive.xml.XMLVisitor;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class FileSystem extends FileSystem_Base {

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
        Directory rootDir = createDirectory(null, "", permission);
        setRootDir(rootDir);  // FIXME

        // Create home directory: "/usr"
        createDirectory(rootDir, "usr", permission);
        // Create home directory: "/home"
        Directory homeDir = createDirectory(rootDir, "home", permission);

        // Create Super User and respective directory: "/home/root"
        addUser(createSuperUser());
        createDirectory(homeDir, "root", permission);
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

    /** Creates all parent directories for the given file path, if they
     * don't exist. Does NOT create the given file.
     * @returns the given file's parent Directory
     */
    public Directory createFileParents( String path ) {
        Directory dir = getRootDir();
        String currentPath = "";
        String[] p = path.split("/");
        int i = 1; // p[0] is ""
        for( ; i < p.length - 1; i++ ) {
            String dirName = p[i];
            currentPath += "/" + dirName;
            try {
                File f = dir.getFileByName( dirName );
                if( ! f.isCdAble() ) {
                    // there's a file with that name
                    throw new FilenameAlreadyExistsException( currentPath );
                }
                dir = (Directory) f;
            } catch( FileNotFoundException e ) {
                dir = createDirectory( dir, dirName, dir.getPerm() );
            }
        }
        return dir;
    }

    public Directory createDirectory(Directory parent, String name, byte permission) {
    	numFiles+=1;
    	Directory newDir = new Directory(parent, name, permission, numFiles);
    	return newDir;
    }

    public PlainFile createPlainFile(Directory parent, String name, byte permission) {
    	numFiles+=1;
    	PlainFile newPlainFile = new PlainFile(parent, name, permission, numFiles);
    	return newPlainFile;
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
    /*
    public void createReadMe() {
    	List<String> users = pathContent("/home");
    	Directory home = (Directory) getFile("/home");
    	numFiles+=1;

    	PlainFile readMe = new PlainFile(home, "README", (byte) 00000000, numFiles);
    	readMe.setLines(users);
    }
    */
    public File getFile(String path) throws UnknownPathException {
    	File currentDir = getRootDir();

    	if(!path.substring(0, 1).matches("/")) //check if root directory is used, otherwise ERROR!
    		throw new UnknownPathException(path);

        path = path.substring(1); // remove '/'
    	for(String dir : path.split("/"))
            currentDir = currentDir.getFileByName(dir);

        return currentDir;
    }

    public Document xmlExport() {
        Document doc = new Document(new Element("mydrive"));
        Element e = null;
        // Convert all users to xml
        Set<User> users = getUserSet();
        for (User u : users) {
            e = u.accept(XMLVisitor.getInstance());
            doc.getRootElement().addContent(e);
        }
        // TODO: go through directories and add them and their contents
        Directory rootDir = getRootDir();
        File f = null;
        Queue<File> queue = new LinkedList<>();
        e = rootDir.accept(XMLVisitor.getInstance());
        doc.getRootElement().addContent(e);
        queue.addAll(rootDir.getFileSet());
        while (!queue.isEmpty()) {
            f = queue.poll(); // TODO: should never return null, but maybe do a sanity check
            e = f.accept(XMLVisitor.getInstance());
            doc.getRootElement().addContent(e);
            if (f.isCdAble()) {
                queue.add(f);
            }
        }
        return doc;
    }
}
