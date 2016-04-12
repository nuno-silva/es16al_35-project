package pt.tecnico.mydrive.service;

import static org.junit.Assert.*;

import org.junit.Test;

import pt.tecnico.mydrive.domain.Directory;
import pt.tecnico.mydrive.domain.File;
import pt.tecnico.mydrive.domain.FileSystem;
import pt.tecnico.mydrive.domain.PlainFile;
import pt.tecnico.mydrive.domain.User;

public class ReadFileTest extends AbstractServiceTest {

    protected void populate() {
    	/*
    	FileSystem fs = FileSystem.getInstance();
    	User mike = new User(fs, "mike", "lol", "Mike Doe", (byte) 1111000);
    	File file = fs.getFile("/home/mike");
    	new PlainFile(fs, (Directory) file, mike, "Test", (byte) 1111111, "Just a test string.");
    	*/
    }

    @Test
    public void success() {
    	/*
    	final String fileName = "Test";
    	long token;
    	
    	FileSystem fs = FileSystem.getInstance();
    	User mike = getUser("mike");
    	
    	//Login
    	Session session = new Session(fs, mike, "lol");
    	token = session.getToken();
    	
    	//Call ReadFileService
    	ReadFileService service = new ReadFileService(token, fileName);
    	service.dispatch();
    	
    	//assertEquals();
    	*/
    }
}