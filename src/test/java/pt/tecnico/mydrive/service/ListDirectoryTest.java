package pt.tecnico.mydrive.service;

import org.junit.Test;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/* domain things */
import pt.tecnico.mydrive.domain.Directory;
import pt.tecnico.mydrive.domain.File;
import pt.tecnico.mydrive.domain.App;
import pt.tecnico.mydrive.domain.PlainFile;
import pt.tecnico.mydrive.domain.User;
import pt.tecnico.mydrive.domain.FileSystem;
import pt.tecnico.mydrive.service.dto.FileDto;


/* services */
import pt.tecnico.mydrive.service.ListDirectoryService;

/* exceptions */
import pt.tecnico.mydrive.exception.IsNotCdAbleException;
import pt.tecnico.mydrive.exception.FileNotFoundException;
import pt.tecnico.mydrive.exception.PermissionDeniedException;

public class ListDirectoryTest extends AbstractServiceTest {


    protected void populate() {

        FileSystem fs = FileSystem.getInstance();
        File f = fs.getFile("/home");
        Directory d;
        if (f.isCdAble()) {
            d = (Directory) f;
        } else throw new IsNotCdAbleException();
        User u = new User( fs, "mrtesty", "123", "Monsiour Testy", (byte) 0b10100010);
        Directory dir = new Directory(fs, d, "testyDir", (byte) 0b11100000);
        dir = new Directory(fs, d, "Sluty Dir", (byte) 0b00001111);
        f = fs.getFile("/home/mrtesty");
        if (f.isCdAble()) {
            d = (Directory) f;
        } else throw new IsNotCdAbleException();
        new App(fs, d, u, "MyFile");
        new PlainFile(fs, d, "RootFile", (byte) 0b00000000 );
    }


    @Test
    public void successEmptyDir() {
        FileSystem fs = FileSystem.getInstance();

        //* LOGIN */
        LoginService login = new LoginService("root", "***");
        login.execute();
        /* home folder perms 0b111101101  */
        ListDirectoryService lsSer = new ListDirectoryService( login.result() );
        lsSer.execute();
        List<FileDto> results = lsSer.result();
        /* Asserts */
        assertEquals("Too much directories here",results.size(),2);

		assertEquals( "dot(.) Name incorrect", results.get(0).getName(), "." );
		assertEquals("dot(.) permissions incorrect", results.get(0).getPermissions(), (byte) 0b11111010);
		assertNotNull("dot(.) lastMod null", results.get(0).getLastMod());
		assertEquals("dot(.) fileType incorrect", results.get(0).getType(),FileDto.FileType.DIRECTORY);

		assertEquals( "parent(..) Name incorrect", results.get(1).getName(), ".." );
		assertEquals("parent(..) permissions incorrect", results.get(1).getPermissions(), (byte) 0b11111010);
		assertNotNull("parent(..) lastMod null", results.get(1).getLastMod());
		assertEquals("parent(..) fileType incorrect", results.get(1).getType(),FileDto.FileType.DIRECTORY);


    }

    @Test (expected = PermissionDeniedException.class)
    public void wrongPermissions() {
        FileSystem fs = FileSystem.getInstance();
        //* LOGIN */
        LoginService login = new LoginService("mrtesty", "123");
        login.execute();

        fs.getSession( login.result() ).setWorkingPath("/home/testyDir");
        ListDirectoryService lsSer = new ListDirectoryService( login.result() );
        lsSer.execute();
        List<FileDto> results = lsSer.result();
    }

    @Test
    public void PopulatedDir() {
        FileSystem fs = FileSystem.getInstance();
        //* LOGIN */
        LoginService login = new LoginService("mrtesty", "123");
        login.execute();

        ListDirectoryService lsSer = new ListDirectoryService( login.result() );
        lsSer.execute();
        List<FileDto> results = lsSer.result();
        assertEquals("Too much directories here",results.size(),4);

		assertEquals( "dot(.) Name incorrect", results.get(0).getName(), "." );
		assertEquals("dot(.) permissions incorrect", results.get(0).getPermissions(), (byte) 0b10100010);
		assertNotNull("dot(.) lastMod null", results.get(0).getLastMod());
		assertEquals("dot(.) fileType incorrect", results.get(0).getType(),FileDto.FileType.DIRECTORY);

		assertEquals( "parent(..) Name incorrect", results.get(1).getName(), ".." );
		assertEquals("parent(..) permissions incorrect", results.get(1).getPermissions(), (byte) 0b11111010);
		assertNotNull("parent(..) lastMod null", results.get(1).getLastMod());
		assertEquals("parent(..) fileType incorrect", results.get(1).getType(),FileDto.FileType.DIRECTORY);

		assertEquals( "Name incorrect", results.get(2).getName(), "MyFile" );
		assertEquals("permissions incorrect", results.get(2).getPermissions(), (byte) 0b10100010);
		assertNotNull("lastMod null", results.get(2).getLastMod());
		assertEquals("fileType incorrect", results.get(2).getType(),FileDto.FileType.APP);

		assertEquals( "Name incorrect", results.get(3).getName(), "RootFile" );
		assertEquals("permissions incorrect @ RootFile", results.get(3).getPermissions(), (byte) 0b00000000 );
		assertNotNull("lastMod null", results.get(3).getLastMod());
		assertEquals("fileType incorrect", results.get(3).getType(),FileDto.FileType.PLAINFILE);
    }
}
