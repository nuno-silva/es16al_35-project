package pt.tecnico.mydrive.service;

import org.junit.Test;
import org.junit.Ignore;
import pt.ist.fenixframework.FenixFramework;
import pt.tecnico.mydrive.domain.*;
import pt.tecnico.mydrive.exception.FilenameAlreadyExistsException;
import pt.tecnico.mydrive.exception.MyDriveException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CreateAppServiceTest extends AbstractServiceTest {

    @Override
    protected void populate() {
        FileSystem fs = FileSystem.getInstance();
        File f = fs.getFile("/home");
        new App(fs, f, fs.getSuperUser(), "Work", "pt.tecnico.mydrive.exception.MyDriveException.class");
    }

    @Test
    public void success() {

        FileSystem fs = FileSystem.getInstance();
        LoginService lser = new LoginService( "root", "***" );
        lser.execute();
        CreateFileService service = new CreateAppService("ApplicationTest", lser.result(), "pt.tecnico.mydrive.exception.MyDriveException.class");
        service.execute();
        service = new CreateAppService("ApplicationTest2", lser.result());
        service.execute();

        File work = fs.getFile("/home/root/ApplicationTest");
        /*
         * Tests:
         * 1) App was created
         * 2) App has correct name
         * 3) App has correct owner
         * 4) App has correct content
         *
         * What other tests should i make?
         *
         */
        assertNotNull("App was not created", work);
        assertEquals("App created with wrong name", "ApplicationTest", work.getName());
        assertEquals("App created with wrong owner", fs.getSuperUser(), work.getOwner());
        assertEquals("App created with wrong content", "pt.tecnico.mydrive.exception.MyDriveException.class", ((App) work).getContent()); // ha outra maneira de fazer isto sem cast?

    }

    @Test (expected = FilenameAlreadyExistsException.class)
    public void unauthorizedAppCreation() {
    	FileSystem fs = FenixFramework.getDomainRoot().getFileSystem();
        LoginService lser = new LoginService( "root", "***" );
        lser.execute();
    	/*
    	 * Tests:
    	 * App with existing name cannot be created in same directory - maybe this could on the CreateFileTest
    	 */
        File f = fs.getFile("/home");
        fs.getSession(lser.result()).setWorkDir(f);
        CreateFileService service = new CreateAppService("Work", lser.result(), "pt.tecnico.mydrive.domain.App.class");
        service.execute();
    }

   // change expected Exception
   @Ignore @Test (expected = MyDriveException.class)
   public void appCreationFail() {
	   	FileSystem fs = FenixFramework.getDomainRoot().getFileSystem();
	   	LoginService lser = new LoginService( "root", "***" );
	   	lser.execute();
	   	/*
	   	 * Tests:
	   	 * Creating App with incorrect content - Not a java fully qualified name
	   	 */
	   	File f = fs.getFile("/home");
	   	fs.getSession(lser.result()).setWorkDir(f);
	   	CreateFileService service = new CreateAppService("TestApp", lser.result(), "Do stuff...");
	   	service.execute();

    }
}
