package pt.tecnico.mydrive.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import pt.tecnico.mydrive.domain.App;
import pt.tecnico.mydrive.domain.Directory;
import pt.tecnico.mydrive.domain.FileSystem;
import pt.tecnico.mydrive.domain.Link;
import pt.tecnico.mydrive.domain.PlainFile;
import pt.tecnico.mydrive.domain.User;

public class CreateLinkServiceTest extends AbstractServiceTest {
	private static final byte DEFAULT_MASK = (byte) 0b11110000;

	@Override
	protected void populate() {
        FileSystem fs = FileSystem.getInstance();
        new User(fs, "bbranco", "es2016ssssss", "Bernardo", DEFAULT_MASK);

        //User(FileSystem fs, String username, String password, String name, byte mask)
	}

    @Test
    public void successUser() {

    	FileSystem fs = FileSystem.getInstance();
    	LoginService lser = new LoginService( "bbranco", "es2016ssssss" );
    	lser.execute();
    	CreateLinkService service = new CreateLinkService("TestLink", lser.result(), "/home/root/hello");
    	service.execute();
			service = new CreateLinkService("TestLink2", lser.result());
			service.execute();

      Link linkFile = (Link) fs.getFile("/home/bbranco/TestLink");

        /*
         * Tests:
         * 1) Link was created
         * 2) Link has correct name
         * 3) Link has correct owner
         * 4) Link has correct permissions
         * 5) Link has correct content - correct File
         * 6) Link has correct path
         *
         */

      assertNotNull("Link was not created", linkFile);
      assertEquals("Link created with wrong name", "TestLink", linkFile.getName());
      assertEquals("Link created with wrong owner", fs.getUser("bbranco"), linkFile.getOwner());
      assertEquals("Link created with wrong permissions", linkFile.getByteMask(), DEFAULT_MASK);
      assertEquals("Link created with wrong content", "/home/root/hello", linkFile.getContent());

    }

}
