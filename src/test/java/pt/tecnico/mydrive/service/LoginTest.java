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
import static org.junit.Assert.assertFalse;

public class LoginTest extends AbstractServiceTest {
    protected static final Logger log = LogManager.getRootLogger();

    private final String inexistent_username = "ajfbajfiabipfbwep";
    private final String valid_username = "nuno";
    private final String valid_password = "that's right dude";
    private final String invalid_username = "no";
    private final String invalid_password = "I'm wrong";
    private User valid_user;

    @Override
    protected void populate() {
        FileSystem fs = FileSystem.getInstance();

        valid_user = new User(fs, valid_username, valid_password, "Test Me");
    }

    @Test
    public void success() {
        FileSystem fs = FileSystem.getInstance();
        LoginService service = new LoginService(valid_username, valid_password);
        service.execute();

        long token = service.result();

        Session s = fs.getSession(token);

        assertEquals("Session not created", token, s.getToken());
        assertEquals("Session with wrong user", valid_user, s.getUser());
        assertFalse("Session expired", s.isExpired());
    }

    @Test
    public void successWithMultipleLogins() {
        FileSystem fs = FileSystem.getInstance();
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
    }

    @Test(expected = WrongPasswordException.class)
    public void successWithWrongPassword() {
        LoginService service = new LoginService(valid_username, invalid_password);
        service.execute();
    }

    @Test(expected = UserNotFoundException.class)
    public void successWithInexistentUser() {
        LoginService service = new LoginService(inexistent_username, valid_password);
        service.execute();
    }

    @Test(expected = InvalidUsernameException.class)
    public void successWithNullUsername() {
        LoginService service = new LoginService(null, valid_password);
        service.execute();
    }

    @Test(expected = InvalidPasswordException.class)
    public void successWithNullPassword() {
        LoginService service = new LoginService(valid_username, null);
        service.execute();
    }

    @Test(expected = InvalidUsernameException.class)
    public void successWithEmptyUsername1() {
        LoginService service = new LoginService("", valid_password);
        service.execute();
    }

    @Test(expected = InvalidUsernameException.class)
    public void successWithInvalidUsername() {
        LoginService service = new LoginService(invalid_username, valid_password);
        service.execute();
    }
}
