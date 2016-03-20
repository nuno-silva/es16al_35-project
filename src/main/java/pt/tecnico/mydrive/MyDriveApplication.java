package pt.tecnico.mydrive;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import org.jdom2.JDOMException;
import pt.tecnico.mydrive.domain.*;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;
import pt.tecnico.mydrive.exception.InvalidUsernameException;

import java.io.IOException;
import java.util.Set;

/**
 * Hello world!
 *
 */

public class MyDriveApplication {
    public static void main(String[] args) {
        if (args.length > 0) {
            try {
                init(args[0]);
            } catch (JDOMException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Atomic
    public static void init(String fileName) throws JDOMException, IOException {
        Manager man = Manager.getInstance();
        man.getFirstFs().xmlImportFromFile(fileName);
    }
}
