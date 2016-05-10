package pt.tecnico.mydrive;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;
import pt.tecnico.mydrive.domain.Directory;
import pt.tecnico.mydrive.domain.FileSystem;
import pt.tecnico.mydrive.domain.PlainFile;
import pt.tecnico.mydrive.domain.User;
import pt.tecnico.mydrive.domain.Session;
import pt.tecnico.mydrive.domain.File;

import java.io.IOException;
import java.util.List;
import java.util.Arrays;

/**
 * Hello world!
 */

public class MyDriveApplication {
    static final Logger log = LogManager.getRootLogger();

    public static void main(String[] args) {
        setup();
        /*try {
            for (String f : args) {
                xmlScan(f);
            }
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        FenixFramework.shutdown();
    }

    @Atomic
    public static void xmlScan(String fileName) throws JDOMException, IOException {
        FileSystem fs = FileSystem.getInstance();
        fs.xmlImportFromFile(fileName);
    }

    @Atomic
    public static void init() {
        /* TODO ? */
    }

    @Atomic
    public static void setup() {
        log.trace("Setup: " + FenixFramework.getDomainRoot());
        FileSystem fs = FileSystem.getInstance();
        /* FIXME: this was only needed for the first spring, but I'm leaving it
         * here because it's useful for testing */

        // Create "/home/README":
        PlainFile readme = fs.createPlainFileIfNotExists((Directory) fs.getFile("/home"), "README", (byte) 0b00000000);
        readme.setContent("lista de utilizadores");
        // Create "/usr/local/bin" directory:
        Directory local = fs.createFileParents("/usr/local/bin");
        new Directory(fs, local, "bin");
        // Print content of "/home/README":
        File f = fs.getFile("/home/README");
        printContent("Content of PlainFile README: ", f.getContent(fs.getSuperUser()));
        // Remove "/usr/local/bin":
        f = fs.getFile("/usr/local/bin");
        f.remove();
        //TODO: propagate exception
        Document doc = fs.xmlExport();
        XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
        User u = new User(fs,"mrtest","passwordatleast8chars");
        Session s = new Session(fs,u,"passwordatleast8chars");
        try {
            out.output(doc, System.out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Remove "/home/README":
        f = fs.getFile("/home/README");
        f.remove();
        // Print content of "/home":
        Directory home = (Directory)fs.getFile("/home");
        System.out.println("Content of directory /home: ");
        for(File ff : home.getFileSet() ) {
            System.out.println(ff.getName());
        }
        /*try {
            fs.xmlExportToFile("exported.xml");
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    public static void printContent(String description, String c) {
        System.out.println(description);
        List<String> content = Arrays.asList(c.split(PlainFile.LINE_SEPARATOR));
        for (String line : content) {
            System.out.println(line);
        }
    }
}
