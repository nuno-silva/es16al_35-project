package pt.tecnico.mydrive.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.core.WriteOnReadError;
import pt.tecnico.mydrive.MyDriveApplication;

import pt.tecnico.mydrive.domain.User;
import pt.tecnico.mydrive.domain.FileSystem;
import pt.tecnico.mydrive.domain.Session;

import pt.tecnico.mydrive.service.LoginService;

import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;

public class LoginTest extends AbstractServiceTest {
    protected static final Logger log = LogManager.getRootLogger();

    @Override
    protected void populate() {
        FileSystem fs = FileSystem.getInstance();
        
        // FIXME use another constructor after #116
        User u = new User(fs, "testme", "after", "Test Me", (byte) 0xff);
    }
    

    
    @Test
    public void success() {
        final String username = "nuno";
        final String password = "n0n0";
        LoginService service = new LoginService(username, password);
        service.execute();
        
        /* TODO check if session was created */
    }
    
    /* TODO more tests */
}
