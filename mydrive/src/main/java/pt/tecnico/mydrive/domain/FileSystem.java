package pt.tecnico.mydrive.domain;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;

import org.joda.time.DateTime;
import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;
import pt.tecnico.mydrive.exception.InvalidUsernameException;
import pt.tecnico.mydrive.exception.UnknownPathException;
import pt.tecnico.mydrive.exception.FilenameAlreadyExistsException;
import pt.tecnico.mydrive.exception.FileNotFoundException;

import pt.tecnico.mydrive.xml.IXMLVisitable;
import pt.tecnico.mydrive.xml.IXMLVisitor;
import pt.tecnico.mydrive.xml.XMLVisitor;

import java.security.interfaces.ECKey;
import java.util.*;
import java.util.jar.Attributes;

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
        Directory rootDir = createRootDirectory();

        // Create home directory: "/home"
        Directory homeDir = createDirectory(rootDir, "home", permission);

        // Create Super User and respective directory: "/home/root"
         //FIXME: User should receive the FileSystem and add itself to it
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

    protected Directory createRootDirectory() {
        numFiles++;
        // FIXME: proper rootdir permission
        Directory rootDir = new Directory((byte)0, numFiles);
        setRootDir(rootDir);
        return rootDir;
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

    public void xmlImport(Document doc) {
        /*
            PSA: I AM SORRY FOR THIS CODE, WE'RE KIND OF IN A RUSH
        */
        List<Element> users = doc.getRootElement().getChildren(User.XML_TAG);
        List<Element> dirs = doc.getRootElement().getChildren(Directory.XML_TAG);
        List<Element> plains = doc.getRootElement().getChildren(PlainFile.XML_TAG);
        List<Element> apps = doc.getRootElement().getChildren(App.XML_TAG);
        List<Element> links = doc.getRootElement().getChildren(Link.XML_TAG);

        FileSystem fs = xmlCreateFileSystem();
        // TODO: modularize all this
        /* User instantiation */
        xmlImportUsers(users, fs);

    }

    @Atomic
    private FileSystem xmlCreateFileSystem() {
        Manager man = FenixFramework.getDomainRoot().getManager();
        // FIXME: temporary placeholder for FileSystem's name
        FileSystem fs = new FileSystem("ext4");
        man.addFileSystems(fs);
        return fs;
    }

    @Atomic
    private void xmlImportDirectories(List<Element> dirs, FileSystem fs) {
        Element e = null;
        String id, name, mask, lastMod, path;
        for (Element dir : dirs) {
            id = dir.getAttribute("id").getValue();
            name = dir.getChild("name").getText(); // must-have
            e = dir.getChild("path");
            if (e != null) {
                path = e.getText();
            } else {
                path = "/usr/nopath"; // FIXME: find a more suitable default path
            }
            fs.createFileParents(path);

            e = dir.getChild("mask");
            if (e != null) {
                mask = e.getText();
            } else {
                mask = "11111111"; // FIXME: find a more suitable default mask
            }

            e = dir.getChild("lastMod");
            if (e != null) {
                lastMod = e.getText();
            } else {
                lastMod = new DateTime().toString(); // FIXME: find a more suitable default lastMod
            }

            // FIXME: better default mask
            fs.getRootDir().addFile(new Directory(fs.getRootDir(), name, (byte)0b1111111, Long.valueOf(id)));
        }
    }

    @Atomic
    private void xmlImportUsers(List<Element> users, FileSystem fs) {
        // FIXME: this code is bad
        Manager man = FenixFramework.getDomainRoot().getManager();
        String username, password, name, home, mask;
        Element elem;
        for (Element u : users) {
            username = u.getAttribute("username").getValue(); // TODO: this assumes that it's actually there
            elem = u.getChild("password");
            if (elem != null) {
                password = elem.getText();
            } else {
                password = "toor";
            }

            elem = u.getChild("name");
            if (elem != null) {
                name = elem.getText();
            } else {
                name = "Noname";
            }

            elem = u.getChild("home");
            if (elem != null) {
                home = elem.getText();
            } else {
                home = "/usr/home/" + username;
            }
            // TODO: make sure this works
            fs.createFileParents(home);

            elem = u.getChild("mask");
            if (elem != null) {
                mask = elem.getText();
            } else {
                // TODO: fix mask default
                mask = "11111111";
            }

            try {
                // FIXME: hardcoded mask
                fs.addUser(new User(fs, username, password, name, (byte)0b00000000));
            } catch (InvalidUsernameException e) {
                e.printStackTrace();
            }
        }

    }

}
