package pt.tecnico.mydrive.service;

import org.junit.Test;
import pt.ist.fenixframework.FenixFramework;
import pt.tecnico.mydrive.domain.*;
import pt.tecnico.mydrive.exception.FilenameAlreadyExistsException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CreateAppTest extends AbstractServiceTest {

    @Override
    protected void populate() {
        FileSystem fs = FileSystem.getInstance();
        Directory f = (Directory) fs.getFile("/home");  // FIXME : nao gosto do cast!
        new App(fs, f, fs.getSuperUser(), "Work", "I have to work a lot during this week!");
    }

    @Test
    public void success() {

        FileSystem fs = FileSystem.getInstance();
        LoginService lser = new LoginService( "root", "***" );
        lser.execute();
        CreateFileService service = new CreateAppService("ApplicationTest", lser.result(), "Do stuff...");
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
        assertEquals("App created with wrong content", "Do stuff...", ((App) work).getContent()); // ha outra maneira de fazer isto sem cast? 

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
    	 fs.getSession(lser.result()).setWorkingPath("/home");
        CreateFileService service = new CreateAppService("Work", lser.result(), "Do stuff...");
        service.execute();
    }
}
