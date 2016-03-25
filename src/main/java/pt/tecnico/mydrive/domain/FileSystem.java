package pt.tecnico.mydrive.domain;

import org.apache.log4j.Logger;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;

import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
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

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.interfaces.ECKey;
import java.util.*;
import java.util.jar.Attributes;

public class FileSystem extends FileSystem_Base {

	/**
	 * Contains parameters required by all File children.
	 *
	 */
	private class FileParams {
		// Android-ish
		public String ID, NAME, MASK, LASTMOD, PATH;
		public FileParams() { }
		public FileParams parse (String id, String name, String mask, String lastMod, String path) {
			ID = id;
			NAME = name;
			MASK = mask;
			LASTMOD = lastMod;
			PATH = path;

			return this;
		}
	}
    private final static Logger logger = Logger.getLogger(FileSystem.class);

    public FileSystem(String name) {
        super();
        init(name);
    }

    private void init(String name) {
        setFileCounter(0);
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
    public Directory createFileParents(String path) {
        logger.debug("createFileParents path: " + path);
        Directory dir = getRootDir();
        String currentPath = "";
        String[] p = path.split("/");
        int i = 1; // p[0] is ""
        for( ; i < p.length - 1; i++ ) {
            String dirName = p[i];
            logger.debug("createFileParents p[" + i + "]: " + dirName);
            try {
                File f = dir.getFileByName(dirName);
                if(!f.isCdAble()) {
                    // there's a file with that name
                    logger.debug("createFileParents filename already exists: " + dirName);
                    throw new FilenameAlreadyExistsException(dirName, currentPath);
                }
                logger.debug("createFileParents directory already exists: " + dirName
                + " | full path: " + dir.getFullPath());
                dir = (Directory) f;
            } catch(FileNotFoundException e) {
                logger.debug("createFileParents creating directory: " + dirName + " | full path: " +
                        dir.getFullPath() + dirName);
                dir = createDirectory(dir, dirName, dir.getMask());
            }
            currentPath += "/" + dirName;
        }
        return dir;
    }

    protected Directory createRootDirectory() {
        // FIXME: proper rootdir permission
        Directory rootDir = new Directory( (byte) 0b11111010, peekNewFileId() );
        commitNewFileId(); // only allocate new fileId if Directory constructor throws no exception
        setRootDir( rootDir );
        return rootDir;
    }

    public Directory createDirectory(Directory parent, String name, byte permission) {
        /* FIXME should this be protected? I don't think we should leak Directories out of the FileSystem (Nuno) */
        Directory dir = new Directory( parent, name, permission, peekNewFileId() );
        commitNewFileId(); // only allocate new fileId if Directory constructor throws no exception
        return dir;
    }

    /** get the next new file id (but don't store it) - use it when trying to create a new File*/
    protected long peekNewFileId() {
        long newId = getFileCounter() + 1;
        return newId;
    }

    /** "allocates" a new file id (when you're sure a new File was/will be created */
    protected long commitNewFileId() {
        long newId = peekNewFileId();
        setFileCounter( newId );
        return newId;
    }

    /**
     * Creates the {@link Directory} if it does not exist, in either case, returns a {@link Optional} with that directory.
     * @param parent
     * @param name
     * @param permission
     * @return {@link java.util.Optional} with either the newly created directory or with the already existing directory
     * This Optional will contain the specified directory either way, it can never contain null.
     */
    public Optional<Directory> createDirectoryIfNotExists(Directory parent, String name, byte permission) {
        Optional<Directory> opt = Directory.createIfNotExists(parent, name, permission, peekNewFileId() );
        if (!opt.isPresent()) {
            // Assuming that getFile() will succeed, since createIfNotExists() reported that the directory
            // already exists. This will result in an exception if something goes wrong with getFile(). If we were
            // using C# 6, we could've taken advantage of nullables, which would give us more confidence and less
            // boilerplate code, but we're not (and the point of the project is not to test every edge scenario, as
            // mentioned in the classes).
            String path = parent.getFullPath() + name;
            logger.debug("Full Path: " + path);
            opt = Optional.of((Directory)(this.getFile(path)));
        } else {
            commitNewFileId(); // new directory has been created
        }
        return opt;
    }

    public PlainFile createPlainFile(Directory parent, String name, byte permission) {
    	PlainFile newPlainFile = new PlainFile(parent, name, permission, peekNewFileId() );
        commitNewFileId(); // only allocate new fileId if Directory constructor throws no exception
        return newPlainFile;
    }

    public PlainFile createPlainFileIfNotExists(Directory parent, String name, byte permission) {
        try {
            return createPlainFile(parent, name, permission);
        } catch(FilenameAlreadyExistsException _) {
            File f = getFile(parent.getFullPath() + "/" + name);
            logger.debug("Got file... " + f.getFullPath());
            return (PlainFile)f;
        }
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
        /* FIXME duplicate of fileContent() ? */
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
        logger.debug("getFile: " + path);
    	File currentDir = getRootDir();

    	if(!path.substring(0, 1).matches("/")) // check if root directory is used, otherwise ERROR!
    		throw new UnknownPathException(path);

        path = path.substring(1); // remove '/'
    	for(String dir : path.split("/")) {
            currentDir = currentDir.getFileByName(dir);
        }
        logger.debug("getFile: " + currentDir.getFullPath());
        return currentDir;
    }

    public Document xmlExport() {
        Document doc = new Document(new Element("mydrive"));
        Element e;
        // Convert all users to xml
        Set<User> users = getUserSet();
        for (User u : users) {
            e = u.accept(XMLVisitor.getInstance());
            doc.getRootElement().addContent(e);
        }

        Directory rootDir = getRootDir();
        File file;
        Queue<File> queue = new LinkedList<>();
        queue.add(getRootDir());
        while (!queue.isEmpty()) {
            file = queue.poll();
            logger.trace("Working with " + file.getName());
            e = file.accept(XMLVisitor.getInstance());
            doc.getRootElement().addContent(e);
            if (file.isCdAble()) {
                // if file is CDable, add all of it's children to the queue
                for(File f : ((Directory)file).getFileSet()) {
                    logger.info("Adding " + f.getName() + " to directory queue");
                    if (file != f) {
                        /* this prevents infinite loops, since the root directory contains itself as
                         * parent, which results in it being added over and over to the queue
                         */
                        queue.add(f);
                    }
                }

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

        logger.debug("BEGIN xmlImport");
        logger.debug("BEGIN import Users");
        xmlImportUsers(users, fs);
        logger.debug("END import Users");
        logger.debug("BEGIN import Directories");
        xmlImportDirectories(dirs, fs);
        logger.debug("END import Directories");
        logger.debug("BEGIN import PlainFiles");
        xmlImportPlainFiles(plains, fs);
        logger.debug("END import PlainFiles");
        logger.debug("BEGIN import Links");
        xmlImportLinks(links, fs);
        logger.debug("END import Links");
        logger.debug("BEGIN import Apps");
        xmlImportApps(apps, fs);
        logger.debug("END import Apps");
        logger.debug("END xmlImport");
        /* FIXME after importing everything, is the fileCounter begin set to
         * the greatest fileID that was imported? */
    }

    private FileSystem xmlCreateFileSystem() {
        Manager man = FenixFramework.getDomainRoot().getManager();
        // FIXME: temporary placeholder for FileSystem's name
        FileSystem fs = new FileSystem("ext4");
        man.addFileSystem(fs);
        return fs;
    }

    private FileParams parseFileParams(Element file, FileSystem fs, FileParams fp) {
        // This allows some code reuse
    	Element e;
        String id, name, mask, lastMod, path;
        id = file.getAttribute("id").getValue();
        logger.debug("File ID: " + id);

        e = file.getChild("name"); // must-have
        if (e == null) {
            // root dir
            name = "";
        } else {
            name = e.getText();
        }
        logger.debug("File name: " + name);

        e = file.getChild("path");
        if (e != null) {
            path = e.getText();
        } else {
            path = "/usr/nopath"; // FIXME: find a more suitable default path
        }
        logger.debug("File path: " + path);
        fs.createFileParents(path);

        e = file.getChild("mask");
        if (e != null) {
            mask = e.getText();
        } else {
            mask = "11111111"; // FIXME: find a more suitable default mask
        }
        logger.debug("File mask: " + mask);

        e = file.getChild("lastMod");
        if (e != null) {
            lastMod = e.getText();
        } else {
            lastMod = new DateTime().toString(); // FIXME: find a more suitable default lastMod
        }
        logger.debug("Last mod: " + lastMod);
        return fp.parse(id, name, mask, lastMod, path);
    }

    private void xmlImportApps(List<Element> apps, FileSystem fs) {
    	xmlImportContentFiles(apps, fs, true);
    }

    /**
     * Import content files from XML (i.e. PlainFiles Apps).
     *
     * @param  contentFiles - list of Elements to import
     * @param fs - FileSystem which every instance of the created Files will be associated with
     * @param isApp - if true, create App files. If false, create PlainFiles
     */
    private void xmlImportContentFiles(List<Element> contentFiles, FileSystem fs, boolean isApp) {
        FileParams fp = new FileParams();
        String content;
        Element elem;
        for (Element contentFile :  contentFiles) {
            fp = parseFileParams(contentFile, fs, fp);
            elem = contentFile.getChild("content");
            if (elem != null) {
                content = elem.getText();
            } else {
                content = "";
            }

            PlainFile newPlainFile;
            Optional<? extends PlainFile> opt;

            Directory parent = createFileParents(fp.PATH);

            // if it's an App, all the App's constructor, otherwise use PlainFile's
            if(isApp) {
                opt = App.createIfNotExists(parent, fp.NAME,
                        (byte)0b11111010, Long.valueOf(fp.ID), content);
            } else {
                opt = PlainFile.createIfNotExists(parent, fp.NAME,
                        (byte) 0b11111010, Long.valueOf(fp.ID), content);
            }
            if (opt.isPresent()) {
                newPlainFile = opt.get();
                newPlainFile.setLastMod(new DateTime()); // FIXME: placeholder lastMod
                fs.getRootDir().addFileIfNotExists(newPlainFile);
                //fs.createFileParents(fp.PATH);
            }

        }
    }

    private void xmlImportPlainFiles(List<Element> plains, FileSystem fs) {
        xmlImportContentFiles(plains, fs, false);
    }

    private void xmlImportLinks(List<Element> links, FileSystem fs) {
    	 FileParams fp = new FileParams();
    	 String pointer = null;
     	 Element elem = null;
         for(Element link : links) {
         	fp = parseFileParams(link, fs, fp);
            elem = link.getChild("pointer");
     		if (elem != null) {
     			pointer = elem.getText();
     		} else {
     			pointer = "";
     		}
     		File newLink = new Link(createFileParents(fp.PATH), fp.NAME,
                                    (byte)0b00000001, Long.valueOf(fp.ID), pointer);
             newLink.setLastMod(new DateTime()); // FIXME: placeholder lastMod
             fs.getRootDir().addFile(newLink);
             fs.createFileParents(fp.PATH);

         }
    }

    private void xmlImportDirectories(List<Element> dirs, FileSystem fs) {
        FileParams fp = new FileParams();
        for(Element dir : dirs) {
        	fp = parseFileParams(dir, fs, fp);
            Optional<Directory> opt =
                    Directory.createIfNotExists(fs.getRootDir(), fp.NAME, (byte)0b1111111, Long.valueOf(fp.ID));
            if (opt.isPresent()) {
                logger.debug("Creating directory");
                Directory newDir = opt.get();
                newDir.setLastMod(new DateTime()); // FIXME: placeholder lastMod
                fs.createFileParents(fp.PATH);
                fs.getRootDir().addFileIfNotExists(newDir);
            }
        }

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

    public void xmlImportFromFile(String fileName) throws JDOMException, IOException {
        xmlImport(getXMLDocumentFromFile(fileName));
    }

    public void xmlExportToFile(String fileName) throws IOException {
        writeXMLDocumentToFile(xmlExport(), fileName);
    }

    private Document getXMLDocumentFromFile(String fileName) throws JDOMException, IOException {
        return new SAXBuilder().build(new java.io.File(fileName));
    }

    private void writeXMLDocumentToFile(Document doc, String fileName) throws IOException {
        XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
        out.output(doc, new FileOutputStream(fileName));
    }

}
