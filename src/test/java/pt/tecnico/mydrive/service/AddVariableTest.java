package pt.tecnico.mydrive.service;

/*Other stuff*/
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import org.junit.Test;
import java.util.Map;
import java.util.HashMap;

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
		String name = "PS1";
		String value = "/afs/.ist.utl.pt/users/4/3/ist178134";
		FileSystem fs = FileSystem.getInstance();
		AddVariableService avs = new AddVariableService(token1, name, value);
		avs.execute();
		assertEquals("variable not added", value, fs.getSession(token1).getVariable(name));
		assertFalse("Variable found in another session!", fs.getSession(token2).hasVariable(name));
    }

		@Test
		public void successRevaluingVariable() {
			FileSystem fs = FileSystem.getInstance();
      AddVariableService avs = new AddVariableService(token1, "PS1", "/afs/.ist.utl.pt/users/4/3/ist178134");
			avs.execute();
			avs = new AddVariableService(token1, "PS1", ":D :D :D :D :D :D :D :D :D");
			avs.execute();
			assertTrue("No variable was found",fs.getSession(token1).hasVariable("PS1"));
			assertEquals("Variable was not revalued",":D :D :D :D :D :D :D :D :D",fs.getSession(token1).getVariable("PS1"));
		}

		@Test
		public void successSameNameDifferentSession(){
			FileSystem fs = FileSystem.getInstance();
			AddVariableService avs = new AddVariableService(token1, "PS1", "/afs/.ist.utl.pt/users/4/3/ist178134");
			avs.execute();
			avs = new AddVariableService(token2, "PS1", "/afs/.ist.utl.pt/users/4/3/ist178134");
			avs.execute();
			assertEquals("Variable with wrong value session1","/afs/.ist.utl.pt/users/4/3/ist178134",fs.getSession(token1).getVariable("PS1"));
			assertEquals("Variable with wrong value session2","/afs/.ist.utl.pt/users/4/3/ist178134",fs.getSession(token2).getVariable("PS1"));
		}

		@Test (expected=VariableNotFoundException.class)
		public void failInexistentVariable(){
			FileSystem fs = FileSystem.getInstance();
			assertEquals("Variable with wrong value session1","/afs/.ist.utl.pt/users/4/3/ist178134",fs.getSession(token1).getVariable("PS1"));
		}

		@Test (expected=VariableNotFoundException.class)
		public void failInexistentVariableInSpecificSession(){
			FileSystem fs = FileSystem.getInstance();
			AddVariableService avs = new AddVariableService(token1, "PS1", "/afs/.ist.utl.pt/users/4/3/ist178134");
			avs.execute();
			assertEquals("Variable with wrong value session1","/afs/.ist.utl.pt/users/4/3/ist178134",fs.getSession(token2).getVariable("PS1"));
		}


}
