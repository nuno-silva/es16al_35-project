package pt.tecnico.mydrive.domain;

import antlr.MakeGrammar;
import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.joda.time.DateTime;
import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;
import pt.tecnico.mydrive.exception.*;
import pt.tecnico.mydrive.domain.xml.XMLVisitor;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class FileSystem extends FileSystem_Base {

    private final static Logger logger = Logger.getLogger(FileSystem.class);

    protected FileSystem() {
        super();
        init();
    }

    public static FileSystem getInstance() {
        FileSystem fs = FenixFramework.getDomainRoot().getFileSystem();
        if (fs == null) {
            logger.trace("creating new FileSystem");
            fs = new FileSystem();
        }

        return fs;
    }

    protected void init() {
        setRoot(FenixFramework.getDomainRoot());
        setFileCounter(0);

        // Create root directory: "/"
        Directory rootDir = new Directory(this, SuperUser.SUPERUSER_MASK);
        setRootDir(rootDir);

        // Create home directory: "/home"
        Directory homeDir = new Directory(this, rootDir, null, "home", SuperUser.SUPERUSER_MASK);

        // Create Super User
        SuperUser su = new SuperUser(this, "***");

        rootDir.setOwner(su);
        homeDir.setOwner(su);

        // Create guest user
        new GuestUser(this);
    }

    /**
     * Creates all parent directories for the given file path, if they
     * don't exist. Does NOT create the given file.
     *
     * @returns the given file's parent Directory
     *
     * @deprecated avoid using this method as it does not take owners and permissions into account
     */
    @Deprecated
    public Directory createFileParents(String path) {
        logger.debug("createFileParents path: " + path);

        if (path.indexOf("/") != 0) {
            throw new IllegalArgumentException("path '" + path + "' is not relative to '/'");
        }

        Directory dir = getRootDir();
        String currentPath = "";
        String[] p = path.split("/");
        int i = 1; // p[0] is ""
        for (; i < p.length - 1; i++) {
            String dirName = p[i];
            logger.debug("createFileParents p[" + i + "]: " + dirName);
            try {
                File f = dir.getFileByName(dirName);
                if (!f.isCdAble()) {
                    // there's a file with that name
                    logger.debug("createFileParents filename already exists: " + dirName);
                    throw new FilenameAlreadyExistsException(dirName, currentPath);
                }
                logger.debug("createFileParents directory already exists: " + dirName
                        + " | full path: " + dir.getFullPath());
                dir = (Directory) f;
            } catch (FileNotFoundException e) {
                logger.debug("createFileParents creating directory: " + dirName + " | full path: " +
                        dir.getFullPath() + dirName);
                dir = new Directory(this, dir, dirName, dir.getPermissions());
            }
            currentPath += "/" + dirName;
        }
        return dir;
    }


    /**
     * get the next new file id (but don't store it) - use it when trying to create a new File
     */
    protected long peekNewFileId() {
        long newId = getFileCounter() + 1;
        return newId;
    }

    /**
     * "allocates" a new file id (when you're sure a new File was/will be created
     */
    protected long commitNewFileId() {
        long newId = peekNewFileId();
        setFileCounter(newId);
        return newId;
    }

    /**
     * Creates the {@link Directory} if it does not exist, in either case, returns a {@link Optional} with that directory.
     *
     * @param parent
     * @param name
     * @param permission
     * @return {@link java.util.Optional} with either the newly created directory or with the already existing directory
     * This Optional will contain the specified directory either way, it can never contain null.
     */
    public Optional<Directory> createDirectoryIfNotExists(Directory parent, User owner, String name, byte permission) {
        Optional<Directory> opt = Directory.createIfNotExists(this, parent, owner, name, permission);
        if (!opt.isPresent()) {
            // Assuming that getFile() will succeed, since createIfNotExists() reported that the directory
            // already exists. This will result in an exception if something goes wrong with getFile(). If we were
            // using C# 6, we could've taken advantage of nullables, which would give us more confidence and less
            // boilerplate code, but we're not (and the point of the project is not to test every edge scenario, as
            // mentioned in the classes).
            String path = parent.getFullPath() + name;
            logger.debug("Full Path: " + path);
            opt = Optional.of((Directory) (this.getFile(path)));
        } else {
            commitNewFileId(); // new directory has been created
        }
        return opt;
    }

    public PlainFile createPlainFile(Directory parent, String name, byte permission) {
        PlainFile newPlainFile = new PlainFile(this, parent, name, permission);
        return newPlainFile;
    }

    public PlainFile createPlainFileIfNotExists(Directory parent, String name, byte permission) {
        try {
            return createPlainFile(parent, name, permission);
        } catch (FilenameAlreadyExistsException _) {
            File f = getFile(parent.getFullPath() + "/" + name);
            logger.debug("Got file... " + f.getFullPath());
            return (PlainFile) f;
        }
    }

    public List<String> pathContent(String path) throws FileNotFoundException {
        /* FIXME duplicate of fileContent() ? */
        return getFile(path).showContent();
    }

    public List<String> fileContent(String path) throws FileNotFoundException {
        return getFile(path).showContent();
    }

    public void removeFile(String path) throws FileNotFoundException {
        getFile(path).remove();
    }

    /*
    public void createReadMe() {
        List<String> users = pathContent("/home");
        Directory home = (Directory) getFile("/home");
        numFiles+=1;

        PlainFile readMe = new PlainFile(home, "README", (byte) 0b00000000, numFiles);
        readMe.setLines(users);
    }
    */
    public File getFile(String path) throws FileNotFoundException {
        logger.debug("getFile: " + path);
        File currentDir = getRootDir();

        if (!path.substring(0, 1).matches("/")) // check if root directory is used, otherwise ERROR!
            throw new FileNotFoundException(path);

        path = path.substring(1); // remove '/'
        for (String dir : path.split("/")) {
            logger.trace("getFile: entering " + dir);
            currentDir = currentDir.getFileByName(dir);
        }
        logger.debug("getFileResult: " + currentDir.getFullPath());
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
                for (File f : ((Directory) file).getFileSet()) {
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

    /**
     * Returns {@link Optional} containing the User with the specified  username. If no User with such username is
     * found, an empty {@link Optional} is returned. The caller should always call {@link Optional#isPresent()} before
     * accessing the value.
     *
     * @param username the username of the {@link User} to find
     * @return {@link Optional} containing the {@link User} with the specified username
     */
    public Optional<User> getUserByUsername(String username) {
        Optional<User> opt = Optional.empty();
        if (username == null) {
            return opt;
        }

        for (User u : getUserSet()) {
            if (u.getUsername() == username) {
                opt = Optional.of(u);
                return opt;
            }
        }

        return opt;
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

        logger.debug("BEGIN xmlImport");
        logger.debug("BEGIN import Users");
        xmlImportUsers(users);
        logger.debug("END import Users");
        logger.debug("BEGIN import Directories");
        xmlImportDirectories(dirs);
        logger.debug("END import Directories");
        logger.debug("BEGIN import PlainFiles");
        xmlImportPlainFiles(plains);
        logger.debug("END import PlainFiles");
        logger.debug("BEGIN import Links");
        xmlImportLinks(links);
        logger.debug("END import Links");
        logger.debug("BEGIN import Apps");
        xmlImportApps(apps);
        logger.debug("END import Apps");
        logger.debug("END xmlImport");
        /* FIXME after importing everything, is the fileCounter begin set to
         * the greatest fileID that was imported? */
    }

    private FileParams parseFileParams(Element file, FileParams fp) {
        // This allows some code reuse
        Element e;
        String id, name, mask, lastMod, path, owner;
        id = file.getAttribute(XMLVisitor.ID_ATTR).getValue();
        logger.debug("File ID: " + id);

        e = file.getChild(XMLVisitor.NAME_TAG); // must-have
        if (e == null) {
            // root dir
            name = "";
        } else {
            name = e.getText();
        }
        logger.debug("File name: " + name);

        e = file.getChild(XMLVisitor.PATH_TAG);
        if (e != null) {
            path = e.getText();
        } else {
            path = "/usr/nopath"; // FIXME: find a more suitable default path
        }
        logger.debug("File path: " + path);

        e = file.getChild(XMLVisitor.MASK_TAG);
        if (e != null) {
            mask = e.getText();
        } else {
            mask = File.DEFAULT_MASK_STR;
        }
        logger.debug("File mask: " + mask);

        e = file.getChild(XMLVisitor.LASTMOD_TAG);
        if (e != null) {
            lastMod = e.getText();
        } else {
            lastMod = new DateTime().toString();
        }
        logger.debug("Last mod: " + lastMod);

        e = file.getChild(XMLVisitor.OWNER_TAG);
        if (e != null) {
            owner = e.getText();
        } else {
            owner = SuperUser.USERNAME;
        }
        return fp.parse(id, name, mask, lastMod, path, owner);
    }

    private void xmlImportApps(List<Element> apps) {
        xmlImportContentFiles(apps, true);
    }

    /**
     * Import content files from XML (i.e. PlainFiles Apps).
     *
     * @param contentFiles - list of Elements to import
     * @param isApp        - if true, create App files. If false, create PlainFiles
     */
    private void xmlImportContentFiles(List<Element> contentFiles, boolean isApp) {
        FileParams fp = new FileParams();
        String content;
        Element elem;
        for (Element contentFile : contentFiles) {
            fp = parseFileParams(contentFile, fp);
            elem = contentFile.getChild(XMLVisitor.CONTENT_TAG);
            if (elem != null) {
                content = elem.getText();
            } else {
                content = "";
            }

            PlainFile newPlainFile;
            Optional<? extends PlainFile> opt;

            Directory parent = createFileParents(fp.PATH);

            // if it's an App, all the App's constructor, otherwise use PlainFile's
            if (isApp) {
                opt = App.createIfNotExists(this, parent, getUser(fp.OWNER), fp.NAME,
                        MaskHelper.getByteMask(fp.MASK), content);
            } else {
                opt = PlainFile.createIfNotExists(this, parent, getUser(fp.OWNER), fp.NAME,
                        MaskHelper.getByteMask(fp.MASK), content);
            }
            if (opt.isPresent()) {
                newPlainFile = opt.get();
                newPlainFile.setLastMod(new DateTime()); // FIXME: placeholder lastMod
                getRootDir().addFileIfNotExists(newPlainFile);
                //createFileParents(fp.PATH);
            }

        }
    }

    private void xmlImportPlainFiles(List<Element> plains) {
        xmlImportContentFiles(plains, false);
    }

    private void xmlImportLinks(List<Element> links) {
        FileParams fp = new FileParams();
        String pointer;
        Element elem;
        for (Element link : links) {
            fp = parseFileParams(link, fp);
            elem = link.getChild(XMLVisitor.POINTER_TAG);
            if (elem != null) {
                pointer = elem.getText();
            } else {
                pointer = "";
            }
            File newLink = new Link(this, createFileParents(fp.PATH), fp.NAME,
                    MaskHelper.getByteMask(fp.MASK), pointer);
            newLink.setOwner(getUser(fp.OWNER));
            getRootDir().addFile(newLink); // needed now?
        }
    }

    private void xmlImportDirectories(List<Element> dirs) {
        FileParams fp = new FileParams();
        for (Element dir : dirs) {
            fp = parseFileParams(dir, fp);
            logger.trace("XML_DIR_IMPORT: begin import \"" + fp.NAME + "\" in path \"" + fp.PATH + "\"");
            createFileParents(fp.PATH + "/" + fp.NAME);
            Directory.createIfNotExists(this, (Directory) getFile(fp.PATH), getUser(fp.OWNER), fp.NAME,
                    MaskHelper.getByteMask(fp.MASK));
        }

    }

    private void xmlImportUsers(List<Element> users) {
        // FIXME: this code is bad
        String username, password, name, home, mask;
        Element elem;
        for (Element u : users) {
            username = u.getAttribute(XMLVisitor.USERNAME_TAG).getValue(); // TODO: this assumes that it's actually there
            elem = u.getChild(XMLVisitor.PASSWORD_TAG);
            if (elem != null) {
                password = elem.getText();
            } else {
                password = "toor";
            }

            elem = u.getChild(XMLVisitor.NAME_TAG);
            if (elem != null) {
                name = elem.getText();
            } else {
                name = "Noname";
            }

            elem = u.getChild(XMLVisitor.HOME_TAG);
            if (elem != null) {
                home = elem.getText();
            } else {
                home = "/usr/home/" + username;
            }
            // TODO: make sure this works
            createFileParents(home);

            elem = u.getChild(XMLVisitor.MASK_TAG);
            if (elem != null) {
                mask = elem.getText();
            } else {
                mask = User.DEFAULT_MASK_STR;
            }

            try {
                // FIXME: hardcoded mask
                addUser(new User(this, username, password, name, MaskHelper.getByteMask(mask)));
            } catch (UsernameAlreadyExistsException e) {
                logger.trace("Username " + username + " already exists" );
            }
        }   
    }

    @Override
    public void addUser(User u) throws UsernameAlreadyExistsException {
        logger.trace("addUser " + u.getUsername());
        String username = u.getUsername();
        if (hasUser(username)) {
            throw new UsernameAlreadyExistsException(username);
        } else {
            super.addUser(u);
        }
    }

    public boolean hasUser(String username) {
        try {
            getUser(username);
            return true;
        } catch (UserNotFoundException e) {
            return false;
        }

    }

    public User getUser(String username) {
        for (User u : getUserSet()) {
            if (u.getUsername().equals(username)) {
                return u;
            }
        }
        throw new UserNotFoundException(username);
    }

    public SuperUser getSuperUser() throws UserNotFoundException {
        return (SuperUser) getUser("root");
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

    public Session getSession(long token) throws InvalidTokenException {
        if (token == 0) {
            throw new InvalidTokenException(token, "Token can not be 0");
        }
        Set<User> users = getUserSet();
        for (User u : users) {
            try {
                return u.getSession(token);
            } catch (InvalidTokenException e) {
                // expected
            }
        }
        throw new InvalidTokenException(token);
    }

    public boolean hasSession(long token) {
        try {
            getSession(token);
            return true;
        } catch (InvalidTokenException e) {
            return false;
        }
    }

    public void removeExpiredTokens() {
        Set<User> users = getUserSet();
        for (User u : users) {
            u.removeExpiredTokens();
        }
    }

    /**
     * Contains parameters required by all File children.
     */
    private class FileParams {
        // Android-ish
        public String ID, NAME, MASK, LASTMOD, PATH, OWNER;

        public FileParams() {
        }

        public FileParams parse(String id, String name, String mask, String lastMod, String path, String owner) {
            ID = id;
            NAME = name;
            MASK = mask;
            LASTMOD = lastMod;
            PATH = path;
            OWNER = owner;

            return this;
        }
    }
}
