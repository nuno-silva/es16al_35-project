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

        // Create Super User
        try {
            createUser( "root", "***", "Super User" );
        } catch (InvalidUsernameException e ) {
            e.printStackTrace(); // should never happen. "root" is a valid username
        }
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
            try {
                File f = dir.getFileByName( dirName );
                if( ! f.isCdAble() ) {
                    // there's a file with that name
                    throw new FilenameAlreadyExistsException( dirName, currentPath );
                }
                dir = (Directory) f;
            } catch( FileNotFoundException e ) {
                dir = createDirectory( dir, dirName, dir.getPerm() );
            }
            currentPath += "/" + dirName;
        }
        return dir;
    }

    protected Directory createRootDirectory() {
        numFiles++;
        // FIXME: proper rootdir permission
        Directory rootDir = new Directory((byte) 0b11111010, numFiles);
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

    /** creates a new User and its home directory in the FileSystem */
    public void createUser(String username, String password, String name) throws InvalidUsernameException {
        User user = new User(this, username, password, name, (byte) 00000000);
        String userHome = "/home/" + username;
        Directory home = createFileParents( userHome );
        createDirectory(home, username, (byte) 000000);
        user.setHomePath( userHome );
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

    @Atomic
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

        xmlImportUsers(users, fs);
        xmlImportDirectories(dirs, fs);
        xmlImportPlainFiles(plains, fs);

    }

    private FileSystem xmlCreateFileSystem() {
        Manager man = FenixFramework.getDomainRoot().getManager();
        // FIXME: temporary placeholder for FileSystem's name
        FileSystem fs = new FileSystem("ext4");
        man.addFileSystems(fs);
        return fs;
    }

    private void xmlImportFiles(List<Element> files, FileSystem fs) {
    	Element e = null;
        String id, name, mask, lastMod, path;
        for (Element file : files) {
            id = file.getAttribute("id").getValue();
            name = file.getChild("name").getText(); // must-have
            e = file.getChild("path");
            if (e != null) {
                path = e.getText();
            } else {
                path = "/usr/nopath"; // FIXME: find a more suitable default path
            }
            fs.createFileParents(path);

            e = file.getChild("mask");
            if (e != null) {
                mask = e.getText();
            } else {
                mask = "11111111"; // FIXME: find a more suitable default mask
            }

            e = file.getChild("lastMod");
            if (e != null) {
                lastMod = e.getText();
            } else {
                lastMod = new DateTime().toString(); // FIXME: find a more suitable default lastMod
            }
            Directory newFile = new Directory(fs.getRootDir(), name, (byte)0b1111111, Long.valueOf(id));
            newFile.setLastMod(new DateTime()); // FIXME: placeholder lastMod

            // FIXME: better default mask
            fs.getRootDir().addFile(newFile);
        }
    }

    private void xmlImportPlainFiles(List<Element> plains, FileSystem fs) {
    	xmlImportFiles(plains, fs);
    	String content = null;
    	Element elem = null;
    	for (Element plain : plains) {
    		elem = plain.getChild("content");
    		if (elem != null) {
    			content = elem.getText();
    		} else {
    			content = "";
    		}
    	}
    }

    private void xmlImportDirectories(List<Element> dirs, FileSystem fs) {
        xmlImportFiles(dirs, fs);
    }


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
