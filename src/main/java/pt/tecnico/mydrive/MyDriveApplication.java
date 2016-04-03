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

import java.io.IOException;
import java.util.List;

/**
 * Hello world!
 *
 */

public class MyDriveApplication {
    static final Logger log = LogManager.getRootLogger();
    public static void main(String[] args) {
        setup();
        try {
            for( String f: args ) {
                xmlScan( f );
            }
        } catch (JDOMException e) {
            e.printStackTrace() ;
        } catch (IOException e) {
            e.printStackTrace();
        }

        FenixFramework.shutdown();
    }

    @Atomic
    public static void xmlScan( String fileName ) throws JDOMException, IOException {
        FileSystem fs = FileSystem.getInstance();
        fs.xmlImportFromFile( fileName );
    }

    @Atomic
    public static void init() {
        /* TODO? */
    }

    @Atomic
    public static void setup() {
        log.trace("Setup: " + FenixFramework.getDomainRoot());
        FileSystem fs = FileSystem.getInstance();
        /* FIXME: this was only needed for the first spring, but I'm leaving it
         * here because it's useful for testing */
        
        // Create "/home/README":
        PlainFile readme = fs.createPlainFileIfNotExists((Directory) fs.getFile("/home"), "README", (byte) 00000000);
        readme.setContent("lista de utilizadores");
        // Create "/usr/local/bin" directory:
        Directory local = fs.createFileParents("/usr/local/bin");
        fs.createDirectory(local, "bin", (byte) 00000000);
        // Print content of "/home/README":
        printContent("Content of PlainFile README: ", fs.fileContent("/home/README"));
        // Remove "/usr/local/bin":
        fs.removeFile("/usr/local/bin");
        //TODO: propagate exception
        Document doc = fs.xmlExport();
        XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
        try {
            out.output(doc, System.out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Remove "/home/README":
        fs.removeFile("/home/README");
        // Print content of "/home":
        printContent("Content of directory /home: ", fs.fileContent("/home"));
    }

    public static void printContent(String description, List<String> content) {
        System.out.println( description );
        for( String line : content ) {
            System.out.println( line );
        }
    }
}
