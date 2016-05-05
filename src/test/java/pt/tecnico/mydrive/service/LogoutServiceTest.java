package pt.tecnico.mydrive.service;

/*Other stuff*/
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import org.junit.Ignore;
import org.junit.Test;

/*Domain*/
import pt.tecnico.mydrive.domain.User;
import pt.tecnico.mydrive.domain.Session;
import pt.tecnico.mydrive.domain.FileSystem;

/*Exceptions*/
import pt.tecnico.mydrive.exception.*;

public class LogoutServiceTest extends AbstractServiceTest {

  private long tokenR;
  private long tokenG;
  private long token1;

	@Override
	protected void populate() {
    FileSystem fs = FileSystem.getInstance();
    LoginService lgs1 = new LoginService("root","***");
    tokenR = lgs1.result();
    LoginService lgs2 = new LoginService("nobody","");
    tokenG = lgs2.result();
  }

    @Ignore("Waiting for service") @Test
    public void successWithRoot() {
      FileSystem fs = FileSystem.getInstance();
      LogoutService logout1 = new LogoutService(tokenR);
      logout1.execute();
      assertFalse("Session still exists!!",fs.hasSession(tokenR));
    }

    @Ignore("Waiting for service")  @Test
    public void successWithGuest() {
      FileSystem fs = FileSystem.getInstance();
      LogoutService logout1 = new LogoutService(tokenG);
      logout1.execute();
      assertFalse("Session still exists!!",fs.hasSession(tokenG));
    }

    @Ignore("Waiting for service") @Test
    public void successWithNormalUser() {
      FileSystem fs = FileSystem.getInstance();
      LogoutService logout1 = new LogoutService(tokenG);
      logout1.execute();
      assertFalse("Session still exists!!",fs.hasSession(tokenG));
    }

    @Ignore("Waiting for service")  @Test( expected = InvalidTokenException.class)
    public void failWithInexistentToken() {
      FileSystem fs = FileSystem.getInstance();
      LogoutService logout1 = new LogoutService(tokenR);
      logout1.execute();
    }
}
