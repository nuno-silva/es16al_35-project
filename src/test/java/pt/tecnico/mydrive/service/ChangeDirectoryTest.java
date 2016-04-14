package pt.tecnico.mydrive.service;

import static org.junit.Assert.*;

import org.junit.Test;
import pt.ist.fenixframework.FenixFramework;
import pt.tecnico.mydrive.domain.App;
import pt.tecnico.mydrive.domain.Directory;
import pt.tecnico.mydrive.domain.File;
import pt.tecnico.mydrive.domain.FileSystem;
import pt.tecnico.mydrive.domain.Link;
import pt.tecnico.mydrive.domain.PlainFile;
import pt.tecnico.mydrive.domain.Session;
import pt.tecnico.mydrive.domain.User;
import pt.tecnico.mydrive.exception.InvalidTokenException;
import pt.tecnico.mydrive.exception.UnknownPathException;
import pt.tecnico.mydrive.service.*;;

public class ChangeDirectoryTest extends AbstractServiceTest {
	
	String _username = "user";
	String _password = "pass";
	
	long _validToken;
    long _invalidToken = 0;
    final String _validAbsPath = "/test";
    final String _validRelPath = ".";
 
    protected void populate() {
    	
    	
    	FileSystem fs = FileSystem.getInstance();
        //User user = new User(fs, "mike", "MIKE", "Ronald McDonald", (byte) 11111111);
        //File home = fs.getFile(user.getHomePath());
        //new App(fs, (Directory) home, user, "TestApp", (byte) 1111111, "int main() {return 1;}");

        //create path /test/hello
        Directory d = fs.createFileParents("/test/hello"); //returns '/' directory
        //ou
        new Directory(fs, d, "hello");
        
        //create user
        User u = new User(fs, _username, _password, "Nome", (byte) 0xff);
        //Session s = new Session(fs, fs.getUser(_username), _password);
        //_validToken = s.getToken();
        //System.out.println("_token is:"+ _validToken);

    }
    
    //test 1: valid token and valid path(relative) ; must return current 
    @Test
    public void successWithAbsolutePath() {
    	FileSystem fs = FileSystem.getInstance();
    	Session s = new Session(fs, fs.getUser(_username), _password);
        _validToken = s.getToken();
        System.out.println("token: "+ _validToken);
        
        //case 1: valid token and valid path(absolute)
        ChangeDirectoryService service = new ChangeDirectoryService(_validToken, _validAbsPath);
        service.execute();
        
        // check currentDir changed with success
        //Session s = getSession(_validToken);
        assertNotNull("Session was not found", s);
        assertEquals("Working Directory could not be changed", _validAbsPath, s.getWorkingPath());
    }    
    
    //test 2: valid token and valid path(relative) ; must return current 
    @Test
    public void successWithRelativePath() {
    	FileSystem fs = FileSystem.getInstance();
    	Session s = new Session(fs, fs.getUser(_username), _password);
        _validToken = s.getToken();
        System.out.println("token: "+ _validToken);
        
        //String workDir = s.getWorkingPath();
        
        ChangeDirectoryService service = new ChangeDirectoryService(_validToken, _validRelPath);
        service.execute();

        assertNotNull("Session was not found", s);
        assertEquals("Working Directory could not be changed", service.result(), s.getWorkingPath());
    }    
     
        
    //test 3: invalid token and valid path
    @Test (expected=InvalidTokenException.class)
    public void invalidToken() {
  
        ChangeDirectoryService service = new ChangeDirectoryService(_invalidToken, _validAbsPath);
        service.execute();
    } 
    
    //test 4: valid token and path inexistent path
    @Test (expected=UnknownPathException.class)
    public void invalidPath() {
  
    	FileSystem fs = FileSystem.getInstance();
    	Session s = new Session(fs, fs.getUser(_username), _password);
        _validToken = s.getToken();
        System.out.println("token: "+ _validToken);
        
        String _invalidPath = "/home/this/path/does/not/exists";
        ChangeDirectoryService service = new ChangeDirectoryService(_validToken, _invalidPath);
        service.execute();
        
        assertNotNull("Session was not found", s);
        assertNotEquals("Working Directory shouldn't have changed", service.result(), _invalidPath);
    } 
    
    //test 5: valid token and path bigger than 1024
    @Test (expected=UnknownPathException.class)
    public void bigPath() {
  
    	FileSystem fs = FileSystem.getInstance();
    	Session s = new Session(fs, fs.getUser(_username), _password);
        _validToken = s.getToken();
        System.out.println("token: "+ _validToken);
        
        String _bigPath = createBigPath();
        
        ChangeDirectoryService service = new ChangeDirectoryService(_validToken, _bigPath);
        service.execute();
        
        assertNotNull("Session was not found", s);
        assertNotEquals("Working Directory shouldn't have changed", service.result(), _bigPath);
    } 
    
    private String createBigPath(){
    	String res = "/home/";
    	for (int i=0 ; i<1025 ; i++)
    		res = res + 'a';
    	return res;
    }
    
    private Session getSession(long validToken) {
        FileSystem fs = FenixFramework.getDomainRoot().getFileSystem();
        //TODO

        return fs.getSession(validToken);
        //return null;
    }
    
}