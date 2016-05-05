package pt.tecnico.mydrive.service;

/*Other stuff*/
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/*Domain*/
import pt.ist.fenixframework.FenixFramework;
import pt.tecnico.mydrive.domain.PlainFile;
import pt.tecnico.mydrive.domain.App;
import pt.tecnico.mydrive.domain.Directory;
import pt.tecnico.mydrive.domain.FileSystem;
import pt.tecnico.mydrive.domain.Session;
import pt.tecnico.mydrive.domain.User;
import pt.tecnico.mydrive.domain.File;

/*Exceptions*/
import pt.tecnico.mydrive.exception.FilenameAlreadyExistsException;
import pt.tecnico.mydrive.exception.InvalidFileNameException;
import pt.tecnico.mydrive.exception.PermissionDeniedException;
import pt.tecnico.mydrive.exception.FileNotFoundException;


public class CreatePlainFileTest extends AbstractServiceTest {
	private static final byte DEFAULT_MASK = (byte) 0b11110000;

	@Override
	protected void populate() {
        FileSystem fs = FileSystem.getInstance();
        Directory f = (Directory) fs.getFile("/home");
        new App(fs, f, fs.getSuperUser(), "Test1", "I have a lot of work to do during this week!");
        new User(fs, "bbranco", "es2016ssssss", "Bernardo", DEFAULT_MASK);
        new User(fs, "jorge", "es2016ssssss", "jorgeheleno", DEFAULT_MASK);
	}

    @Test
    public void successSuperUser() {

    	FileSystem fs = FileSystem.getInstance();
    	LoginService lser = new LoginService( "root", "***" );
    	lser.execute();
    	CreatePlainFileService service = new CreatePlainFileService("PlainFileTest", lser.result(), "Contains stuff...");
    	service.execute();

        PlainFile textFile = (PlainFile) fs.getFile("/home/root/PlainFileTest");

        /*
         * Tests:
         * 1) PlainFile was created
         * 2) PlainFile has correct name
         * 3) PlainFile has correct owner
         * 4) PlainFile has correct permissions
         * 5) PlainFile has correct content
         * 6) PlainFile has correct path
         *
         */
        assertNotNull("PlainFile was not created", textFile);
        assertEquals("PlainFile created with wrong name", "PlainFileTest", textFile.getName());
        assertEquals("PlainFile created with wrong owner", fs.getSuperUser(), textFile.getOwner());
        assertEquals("PlainFile created with wrong permissions", textFile.getByteMask(), fs.getSuperUser().getByteMask());
        assertEquals("PlainFile created with wrong content", "Contains stuff...", textFile.getContent());
        assertEquals("PlainFile created with wrong content", "/home/root/PlainFileTest", textFile.getFullPath());

    }

    @Test
    public void successUser() {

    	FileSystem fs = FileSystem.getInstance();
    	LoginService lser = new LoginService( "bbranco", "es2016ssssss" );
    	lser.execute();
    	CreatePlainFileService service = new CreatePlainFileService("Test3", lser.result(), "Contains more stuff...");
    	service.execute();

        PlainFile textFile = (PlainFile) fs.getFile("/home/bbranco/Test3");

        /*
         * Tests:
         * 1) PlainFile was created
         * 2) PlainFile has correct name
         * 3) PlainFile has correct owner
         * 4) PlainFile has correct permissions
         * 5) PlainFile has correct content
         * 6) PlainFile has correct path
         *
         */

        assertNotNull("PlainFile was not created", textFile);
        assertEquals("PlainFile created with wrong name", "Test3", textFile.getName());
        //assertEquals("PlainFile created with wrong owner", fs.getUserByUsername("bbranco"), textFile.getOwner());
        assertEquals("PlainFile created with wrong permissions", textFile.getByteMask(), DEFAULT_MASK);
        assertEquals("PlainFile created with wrong content", "Contains more stuff...", textFile.getContent());
        assertEquals("PlainFile created with wrong path", "/home/bbranco/Test3", textFile.getFullPath());

    }

    /*
     * Filenames with the following caracters: '/' e \0 are not permitted
     *
     */
    @Test (expected = InvalidFileNameException.class)
    public void invalidName() {
        FileSystem fs = FenixFramework.getDomainRoot().getFileSystem();
        LoginService lser = new LoginService( "root", "***" );
        lser.execute();
				File f = fs.getFile("/home");
    		fs.getSession(lser.result()).setWorkDir((Directory)f);
        CreateFileService service = new CreateAppService("Test2/", lser.result(), "I have a lot of work to do during this week!");
        service.execute();

    }


    @Test (expected = PermissionDeniedException.class)
    public void createPlainFileinOtherUserDir() {
        FileSystem fs = FenixFramework.getDomainRoot().getFileSystem();
        LoginService lser = new LoginService( "bbranco", "es2016ssssss" );
        lser.execute();
				File f = fs.getFile("/home/jorge");
    	fs.getSession(lser.result()).setWorkDir((Directory)f);
        CreatePlainFileService service = new CreatePlainFileService("TestBernardo", lser.result(), "I have a lot of work to do during this week!");
        service.execute();

    }

	/*
	 * Tests:
	 * PlainFile with existing name cannot be created in same directory
	 */
    @Test (expected = FilenameAlreadyExistsException.class)
    public void unauthorizedFileCreation() {
        FileSystem fs = FenixFramework.getDomainRoot().getFileSystem();
        LoginService lser = new LoginService( "root", "***" );
        lser.execute();
				File f = fs.getFile("/home");
    	fs.getSession(lser.result()).setWorkDir((Directory)f);
        CreateFileService service = new CreateAppService("Test1", lser.result(), "I have a lot of work to do during this week!");
        service.execute();
    }

}
