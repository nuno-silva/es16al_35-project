package pt.tecnico.mydrive.service;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/* domain things */
import pt.tecnico.mydrive.domain.FileSystem;
import pt.tecnico.mydrive.domain.File;
import pt.tecnico.mydrive.domain.Directory;

/* services */
import pt.tecnico.mydrive.service.ListDirectoryService;
import pt.tecnico.mydrive.service.LoginService;

/* exceptions */
import pt.tecnico.mydrive.exception.FileNotFoundException;

public class ListDirectoryTest extends AbstractServiceTest {



    protected void populate() {

		FileSystem fs = FileSystem.getInstance();
		File f = fs.getFile("/home");
		Directory d;
		if ( f.isCdAble() ){ d = ( Directory ) f; }
		else return; /* will think about this does in this case */
		Directory dir=new Directory( fs, d, "testyDir" );
	}

    @Test
    public void success() {
		FileSystem fs = FileSystem.getInstance();

		/* LOGIN */
		LoginService login = new LoginService( "root", "****" );
		login.dispatch();

		ListDirectoryService ser=new ListDirectoryService( "/home", fs.getUser( "root" ).getByteMask() );
		ser.dispatch();
		/* Asserts */
    }

    @Test /* (expected = FileNotFoundException.class) FIXME uncomment when ListDirectoryService is complete */
    public void incorrectDirName(){
		FileSystem fs = FileSystem.getInstance();
		/* LOGIN */
		LoginService login = new LoginService( "root", "****" );
		login.dispatch();

		ListDirectoryService ser= new ListDirectoryService( "thisNameIsSoRetardThatN01W0ouldRemember1t", fs.getUser( "root" ).getByteMask() );
		ser.dispatch();
		/* asserts if needed */
	}
}
