package pt.tecnico.mydrive.service;

/*Other stuff*/
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import org.junit.Test;
import java.util.Set;

/*Domain*/
import pt.tecnico.mydrive.domain.User;
import pt.tecnico.mydrive.domain.Session;
import pt.tecnico.mydrive.domain.FileSystem;

/*Exceptions*/
import pt.tecnico.mydrive.exception.*;

public class AddVariableTest extends AbstractServiceTest {

	private long token1;
	private long token2;

	@Override
	protected void populate() {
		FileSystem fs = FileSystem.getInstance();
		User u1 = new User(fs, "Illya", "Swag overload$$", "Dr. Illya(not Dre)");
		Session session1 = new Session(fs, u1, "Swag overload$$");
		User u2 = new User(fs, "esp16g35", "mayb3th15ismyP4$$word", "Group 35");
		Session session2 = new Session(fs, u2, "mayb3th15ismyP4$$word");
		token1 = session1.getToken();
		token2 = session2.getToken();
  }

    @Test
    public void successAddVariable() {
			FileSystem fs = FileSystem.getInstance();
      AddVariableService avs = new AddVariableService(token1, "PS1", "/afs/.ist.utl.pt/users/4/3/ist178134");
			avs.execute();
			assertTrue("No variable was found",fs.getSession(token1).hasVariable("PS1"));
			assertFalse("Variable found in other session!",fs.getSession(token2).hasVariable("PS1"));
    }

		@Test
		public void successRevaluingVariable() {
			FileSystem fs = FileSystem.getInstance();
      AddVariableService avs = new AddVariableService(token1, "PS1", "/afs/.ist.utl.pt/users/4/3/ist178134");
			avs.execute();
			avs = new AddVariableService(token1, "PS1", ":D :D :D :D :D :D :D :D :D");
			avs.execute();
			assertTrue("No variable was found",fs.getSession(token1).hasVariable("PS1"));
			assertEquals("Variable was not revalued",":D :D :D :D :D :D :D :D :D",fs.getSession(token1).getVariableValue("PS1"));
		}
		


}
