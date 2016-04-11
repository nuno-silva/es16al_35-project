package pt.tecnico.mydrive.service;

import static org.junit.Assert.*;

import org.junit.Test;

import pt.ist.fenixframework.FenixFramework;
import pt.tecnico.mydrive.domain.File;
import pt.tecnico.mydrive.domain.FileSystem;
import pt.tecnico.mydrive.domain.User;
import pt.tecnico.mydrive.domain.App;
import pt.tecnico.mydrive.domain.Directory;

import pt.tecnico.mydrive.exception.FilenameAlreadyExistsException;

public class CreateAppTest extends AbstractServiceTest {

	@Override
	protected void populate() {
		FileSystem fs = FenixFramework.getDomainRoot().getFileSystem();
		Directory f = (Directory) fs.getFile("/home");  // FIXME : nao gosto do cast!
		new App(fs, f, fs.getSuperUser(), "Work", "I have to work a lot during this week!");
	}
    @Test
    public void success() {
        
    	FileSystem fs = FenixFramework.getDomainRoot().getFileSystem();
    	
    	CreateFileService service = new CreateAppService("ApplicationTest","Super User","/home", "Do stuff...");
        service.execute();
        
        File work = fs.getFile("/home/ApplicationTest");
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
    
    @Test(expected = FilenameAlreadyExistsException.class)
    public void unauthorizedAppCreation() {
    	
    	/*
    	 * Tests:
    	 * App with existing name cannot be created in same directory - maybe this could on the CreateFileTest
    	 */
    	CreateFileService service = new CreateAppService("Work","Super User","/home", "Do stuff...");
        service.execute();
    }
}
