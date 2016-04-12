package pt.tecnico.mydrive.service;

import org.junit.Test;
import pt.tecnico.mydrive.domain.*;

public class ReadFileTest extends AbstractServiceTest {

    protected void populate() {

        FileSystem fs = FileSystem.getInstance();
        User user = new User(fs, "mike", "MIKE", "Ronald McDonald", (byte) 11111111);
        File home = fs.getFile(user.getHomePath());
        new PlainFile(fs, (Directory) home, user, "TestPlainFile", (byte) 1111111, "Just a test string.");
        new Link(fs, (Directory) home, user, "TestLink", (byte) 1111111, "/home");
        new App(fs, (Directory) home, user, "TestApp", (byte) 1111111, "int main() {return 1;}");
    }

    @Test
    public void successPlainFile() {

        final String fileName = "TestPlainFile";
        long token;

        FileSystem fs = FileSystem.getInstance();
        User user = fs.getUser("mike");

        //Login
        Session session = new Session(fs, user, "MIKE");
        token = session.getToken();

        //Call ReadFileService
        ReadFileService service = new ReadFileService(token, fileName);
        service.dispatch();

        PlainFile file = (PlainFile) fs.getFile("/home/mike/" + fileName);
        String content = file.getContent();

        //assertEquals("Just a test string.", content);
    }

    @Test
    public void successLink() {
        final String fileName = "TestLink";
        long token;

        FileSystem fs = FileSystem.getInstance();
        User user = fs.getUser("mike");

        //Login
        Session session = new Session(fs, user, "MIKE");
        token = session.getToken();

        //Call ReadFileService
        ReadFileService service = new ReadFileService(token, fileName);
        service.dispatch();

        Link file = (Link) fs.getFile("/home/mike/" + fileName);
        String content = file.getContent();

        //assertEquals("/home", content);
    }

    @Test
    public void successApp() {
        final String fileName = "TestApp";
        long token;

        FileSystem fs = FileSystem.getInstance();
        User user = fs.getUser("mike");

        //Login
        Session session = new Session(fs, user, "MIKE");
        token = session.getToken();

        //Call ReadFileService
        ReadFileService service = new ReadFileService(token, fileName);
        service.dispatch();

        PlainFile file = (PlainFile) fs.getFile("/home/mike/" + fileName);
        String content = file.getContent();

        //assertEquals("int main() {return 1;}", content);
    }

    @Test
    public void failPlainFile() {

        final String fileName = "TestPlainFile";
        long token;

        FileSystem fs = FileSystem.getInstance();
        User user = fs.getUser("mike");

        //Login
        Session session = new Session(fs, user, "MIKE");
        token = session.getToken();

        //Call ReadFileService
        ReadFileService service = new ReadFileService(token, fileName);
        service.dispatch();

        PlainFile file = (PlainFile) fs.getFile("/home/mike/" + fileName);
        String content = file.getContent();

        //assertNotEquals("bla bla", content);
    }

    @Test
    public void failLink() {
        final String fileName = "TestLink";
        long token;

        FileSystem fs = FileSystem.getInstance();
        User user = fs.getUser("mike");

        //Login
        Session session = new Session(fs, user, "MIKE");
        token = session.getToken();

        //Call ReadFileService
        ReadFileService service = new ReadFileService(token, fileName);
        service.dispatch();

        Link file = (Link) fs.getFile("/home/mike/" + fileName);
        String content = file.getContent();

        //assertNotEquals("bla bla", content);
    }

    @Test
    public void failApp() {
        final String fileName = "TestApp";
        long token;

        FileSystem fs = FileSystem.getInstance();
        User user = fs.getUser("mike");

        //Login
        Session session = new Session(fs, user, "MIKE");
        token = session.getToken();

        //Call ReadFileService
        ReadFileService service = new ReadFileService(token, fileName);
        service.dispatch();

        PlainFile file = (PlainFile) fs.getFile("/home/mike/" + fileName);
        String content = file.getContent();

        //assertNotEquals("bla bla", content);
    }
    /*
    @Test (expected=FileNotFoundException.class)
    public void fileNotFoundException() {
    	final String fileName = "Test";
    	long token;
    	
    	FileSystem fs = FileSystem.getInstance();
    	User user = fs.getUser("mike");
    	
    	//Login
    	Session session = new Session(fs, user, "MIKE");
    	token = session.getToken();
    	
    	//Call ReadFileService
    	ReadFileService service = new ReadFileService(token, fileName);
    	service.dispatch();
    }*/
}