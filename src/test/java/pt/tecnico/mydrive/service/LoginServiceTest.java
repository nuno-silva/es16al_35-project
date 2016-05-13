package pt.tecnico.mydrive.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import pt.tecnico.mydrive.domain.FileSystem;
import pt.tecnico.mydrive.domain.Session;
import pt.tecnico.mydrive.domain.User;
import pt.tecnico.mydrive.exception.InvalidPasswordException;
import pt.tecnico.mydrive.exception.InvalidUsernameException;
import pt.tecnico.mydrive.exception.UserNotFoundException;
import pt.tecnico.mydrive.exception.WrongPasswordException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertFalse;

public class LoginServiceTest extends AbstractServiceTest {
    protected static final Logger log = LogManager.getRootLogger();

    private final String inexistent_username = "ajfbajfiabipfbwep";
    private final String valid_username = "nuno";
    private final String valid_password = "that's right dude";
    private final String invalid_username = "no";
    private final String invalid_password = "I'm wrong";
    private final String root_username    = "root";
    private final String root_password    = "***";
    private final String guest_username = "nobody";
    private User valid_user;
    private FileSystem fs;

    @Override
    protected void populate() {
        fs = FileSystem.getInstance();

        valid_user = new User(fs, valid_username, valid_password, "Test Me");
    }

    @Test
    public void success() {
        LoginService service = new LoginService(valid_username, valid_password);
        service.execute();

        long token = service.result();

        Session s = fs.getSession(token);

        assertEquals("Session not created", token, s.getToken());
        assertEquals("Session with wrong user", valid_user, s.getUser());
        assertFalse("Session expired", s.isExpired());
        assertNotEquals("Token is 0!", token, 0);
    }

    @Test
    public void successWithMultipleLogins() {
        LoginService service1 = new LoginService(valid_username, valid_password);
        LoginService service2 = new LoginService(valid_username, valid_password);
        service1.execute();
        service2.execute();

        long token1 = service1.result();
        long token2 = service2.result();


        Session s1 = fs.getSession(token1);
        Session s2 = fs.getSession(token2);

        assertEquals("Session 1 not created", token1, s1.getToken());
        assertEquals("Session 2 not created", token2, s2.getToken());
        assertNotEquals("Sessions with duplicate tokens", token1, token2);
    }

    @Test
    public void successWithRootLogin() {
        LoginService service = new LoginService(root_username, root_password);
        service.execute();

        long token = service.result();

        Session s = fs.getSession(token);
        User root = fs.getUser(root_username);

        assertEquals("Session not created", token, s.getToken());
        assertEquals("Session with wrong user", root, s.getUser());
        assertFalse("Session expired", s.isExpired());
    }
    
    @Test
    public void successWithGuestLogin() {
        LoginService service = new LoginService(guest_username, "");
        service.execute();

        long token = service.result();

        Session s = fs.getSession(token);
        User guest = fs.getUser(guest_username);
        
        assertEquals("Session not created", token, s.getToken());
        assertEquals("Session with wrong user", guest, s.getUser());
    }

    @Test(expected = WrongPasswordException.class)
    public void successWithWrongPassword() {
        LoginService service = new LoginService(valid_username, invalid_password);
        service.execute();
    }

    @Test(expected = WrongPasswordException.class)
    public void successWithWrongRootPassword() {
        LoginService service = new LoginService(root_username, root_password + invalid_password);
        service.execute();
    }

    @Test(expected = UserNotFoundException.class)
    public void successWithInexistentUser() {
        LoginService service = new LoginService(inexistent_username, valid_password);
        service.execute();
    }

    @Test(expected = InvalidPasswordException.class)
    public void successWithNullPassword() {
        LoginService service = new LoginService(valid_username, null);
        service.execute();
    }

    @Test(expected = UserNotFoundException.class)
    public void successWithEmptyUsername1() {
        LoginService service = new LoginService("", valid_password);
        service.execute();
    }
}
