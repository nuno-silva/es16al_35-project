package pt.tecnico.mydrive.service;

import static org.junit.Assert.*;

import org.joda.time.DateTime;
import org.junit.Test;
import pt.tecnico.mydrive.domain.*;
import pt.tecnico.mydrive.exception.EmptyFileNameException;
import pt.tecnico.mydrive.exception.FileNotFoundException;
import pt.tecnico.mydrive.exception.InvalidTokenException;
import pt.tecnico.mydrive.exception.PermissionDeniedException;
import pt.tecnico.mydrive.exception.ReadDirectoryException;

public class ReadFileServiceTest extends AbstractServiceTest {

    protected void populate() {

        FileSystem fs = FileSystem.getInstance();
        User user = new User(fs, "mike", "MIKEssssss", "Ronald McDonald", (byte) 0xff);
        File home = user.getHome();
        new PlainFile(fs, home, user, "TestPlainFile", (byte) 0xff, "Just a test string.");
        new Link(fs, home, user, "TestLink", (byte) 0xff, "/home/mike/TestPlainFile");
        new App(fs, home, user, "TestApp", (byte) 0xff, "pt.tecnico.mydrive.service.populate");
    	new Link(fs, user.getHome(), user, "TestLinkToLink", (byte) 0xff, "/home/mike/TestLink");
    }

    @Test
    public void successPlainFile() {

        final String fileName = "TestPlainFile";
        long token;

        FileSystem fs = FileSystem.getInstance();
        User user = fs.getUser("mike");

        //Login
        Session session = new Session(fs, user, "MIKEssssss");
        token = session.getToken();

        //Call ReadFileService
        ReadFileService service = new ReadFileService(token, fileName);
        service.execute();

        assertEquals("Just a test string.", service.result());
    }

    @Test
    public void successLink() {
        final String fileName = "TestLink";
        long token;

        FileSystem fs = FileSystem.getInstance();
        User user = fs.getUser("mike");

        //Login
        Session session = new Session(fs, user, "MIKEssssss");
        token = session.getToken();

        //Call ReadFileService
        ReadFileService service = new ReadFileService(token, fileName);
        service.execute();

        assertEquals("Just a test string.", service.result());
    }

    @Test
    public void linkPointsToLink() {
        final String fileName = "TestLinkToLink";
        long token;

        FileSystem fs = FileSystem.getInstance();
        User user = fs.getUser("mike");

        //Login
        Session session = new Session(fs, user, "MIKEssssss");
        token = session.getToken();

        //Call ReadFileService
        ReadFileService service = new ReadFileService(token, fileName);
        service.execute();

        assertEquals("Just a test string.", service.result());
    }

    @Test
    public void successApp() {
        final String fileName = "TestApp";
        long token;

        FileSystem fs = FileSystem.getInstance();
        User user = fs.getUser("mike");

        //Login
        Session session = new Session(fs, user, "MIKEssssss");
        token = session.getToken();

        //Call ReadFileService
        ReadFileService service = new ReadFileService(token, fileName);
        service.execute();

        assertEquals("pt.tecnico.mydrive.service.populate", service.result());
    }

    @Test
    public void failPlainFile() {

        final String fileName = "TestPlainFile";
        long token;

        FileSystem fs = FileSystem.getInstance();
        User user = fs.getUser("mike");

        //Login
        Session session = new Session(fs, user, "MIKEssssss");
        token = session.getToken();

        //Call ReadFileService
        ReadFileService service = new ReadFileService(token, fileName);
        service.execute();

        assertNotEquals("bla bla", service.result());
    }

    @Test
    public void failLink() {
        final String fileName = "TestLink";
        long token;

        FileSystem fs = FileSystem.getInstance();
        User user = fs.getUser("mike");

        //Login
        Session session = new Session(fs, user, "MIKEssssss");
        token = session.getToken();

        //Call ReadFileService
        ReadFileService service = new ReadFileService(token, fileName);
        service.execute();

        assertNotEquals("bla bla", service.result());
    }

    @Test
    public void failApp() {
        final String fileName = "TestApp";
        long token;

        FileSystem fs = FileSystem.getInstance();
        User user = fs.getUser("mike");

        //Login
        Session session = new Session(fs, user, "MIKEssssss");
        token = session.getToken();

        //Call ReadFileService
        ReadFileService service = new ReadFileService(token, fileName);
        service.execute();

        assertNotEquals("bla bla", service.result());
    }

    @Test (expected = EmptyFileNameException.class)
    public void failInvalidFileName() {
        final String fileName = "";
        long token;

        FileSystem fs = FileSystem.getInstance();
        User user = fs.getUser("mike");

        //Login
        Session session = new Session(fs, user, "MIKEssssss");
        token = session.getToken();

        //Call ReadFileService
        ReadFileService service = new ReadFileService(token, fileName);
        service.execute();
    }

    @Test (expected = InvalidTokenException.class)
    public void failInvalidToken() {
        final String fileName = "Test";
        long token;

        FileSystem fs = FileSystem.getInstance();
        User user = fs.getUser("mike");

        //Login
        Session session = new Session(fs, user, "MIKEssssss");
        token = session.getToken();

        DateTime expirationDate = new DateTime().minusHours(5);
        session.setExpirationDate(expirationDate);
        System.out.println(session.getExpirationDate());
        //Call ReadFileService
        ReadFileService service = new ReadFileService(token, fileName);
        service.execute();
    }

    @Test (expected = ReadDirectoryException.class)
    public void failNotFile() {
        final String fileName = "Test";
        long token;

        FileSystem fs = FileSystem.getInstance();
        User user = fs.getUser("mike");

        //Login
        Session session = new Session(fs, user, "MIKEssssss");
        token = session.getToken();

        File d = user.getHome();
        new Directory(fs, d, fileName);

        //Call ReadFileService
        ReadFileService service = new ReadFileService(token, fileName);
        service.execute();
    }

    @Test (expected = PermissionDeniedException.class)
    public void failPermissionDenied() {
        final String fileName = "Test";
        long token;

        FileSystem fs = FileSystem.getInstance();
        User user = fs.getUser("mike");

        //Login
        Session session = new Session(fs, user, "MIKEssssss");
        token = session.getToken();

        File home = fs.getFile(user.getHomePath());
        new PlainFile(fs, home, user, fileName, (byte) 0x00, "Just a test string.");

        //Call ReadFileService
        ReadFileService service = new ReadFileService(token, fileName);
        service.execute();

        assertNotEquals("bla bla", service.result());
    }

    @Test (expected=FileNotFoundException.class)
    public void fileNotFoundException() {
    	final String fileName = "Test";
    	long token;

    	FileSystem fs = FileSystem.getInstance();
    	User user = fs.getUser("mike");

    	//Login
    	Session session = new Session(fs, user, "MIKEssssss");
    	token = session.getToken();

    	//Call ReadFileService
    	ReadFileService service = new ReadFileService(token, fileName);
    	service.execute();
    }
}
