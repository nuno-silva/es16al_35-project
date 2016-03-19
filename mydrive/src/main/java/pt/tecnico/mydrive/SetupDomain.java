package pt.tecnico.mydrive;

import pt.ist.fenixframework.Atomic;
import pt.tecnico.mydrive.domain.FileSystem;
import pt.tecnico.mydrive.domain.User;
import pt.tecnico.mydrive.exception.InvalidUsernameException;
import pt.tecnico.mydrive.domain.Manager;


/*
    Setups a sample domain for testing.
 */
public class SetupDomain {

    @Atomic
    public static void populateDomain() throws InvalidUsernameException {
        Manager man = Manager.getInstance();
        /* not needed...
        FileSystem fs = new FileSystem("ext4");
        man.addFileSystem(fs);
        User jayceon = new User(fs, "jayceon", "drerules", "Jayceon", (byte) 0b11111111);
        fs.addUser(jayceon);
        */
    }
}
