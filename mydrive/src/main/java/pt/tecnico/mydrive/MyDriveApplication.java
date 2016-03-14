package pt.tecnico.mydrive;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import pt.tecnico.mydrive.domain.*;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;

/**
 * Hello world!
 *
 */
public class MyDriveApplication
{
    static final Logger log = LogManager.getRootLogger();

    public static void main( String[] args )
    {
        try {
            setup();
            System.out.println("I did stuff!");
            //print();
            //xmlprint();
        } finally {
            FenixFramework.shutdown();
        }
    }

    @Atomic
    public static void setup() {
        System.out.println("Setup did stuff!");
        log.trace("Setup: " + FenixFramework.getDomainRoot());
        Manager m = Manager.getInstance();

    // TODO more stuff
    }
}

