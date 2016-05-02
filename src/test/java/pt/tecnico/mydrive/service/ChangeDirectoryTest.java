package pt.tecnico.mydrive.service;

import static org.junit.Assert.*;

import org.joda.time.DateTime;
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
import pt.tecnico.mydrive.exception.EmptyPathException;
import pt.tecnico.mydrive.exception.InvalidTokenException;
import pt.tecnico.mydrive.exception.PermissionDeniedException;
import pt.tecnico.mydrive.exception.FileNotFoundException;
import pt.tecnico.mydrive.service.*;;

public class ChangeDirectoryTest extends AbstractServiceTest {

	String _username = "user";
	String _password = "pass";

	long _validToken;
    long _invalidToken = 0;
    final String _validAbsPath = "/home/user";
    final String _validRelPath = ".";

    protected void populate() {

    	FileSystem fs = FileSystem.getInstance();
    	User u = new User(fs, _username, _password, "Nome", (byte) 0xff);

    }

    //test 1: valid token and valid path(Absolute) ; must return current
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

        String workDir = s.getWorkingPath();

        ChangeDirectoryService service = new ChangeDirectoryService(_validToken, _validRelPath);
        service.execute();

        assertNotNull("Session was not found", s);
        assertEquals("Working Directory could not be changed", workDir, s.getWorkingPath());
    }


    //test 3: invalid token and valid path
    @Test (expected=InvalidTokenException.class)
    public void invalidToken() {

    	FileSystem fs = FileSystem.getInstance();
    	Session s = new Session(fs, fs.getUser(_username), _password);
        _invalidToken = s.getToken();
        DateTime expDate = new DateTime().minusHours(5);
        s.setExpirationDate(expDate);

        ChangeDirectoryService service = new ChangeDirectoryService(_invalidToken, _validAbsPath);
        service.execute();
    }

    //test 4: valid token and path inexistent path
    @Test (expected=FileNotFoundException.class)
    public void invalidPath() {

    	FileSystem fs = FileSystem.getInstance();
    	Session s = new Session(fs, fs.getUser(_username), _password);
        _validToken = s.getToken();
        System.out.println("token: "+ _validToken);

        String _invalidPath = "/home/this/path/does/not/exists";
        ChangeDirectoryService service = new ChangeDirectoryService(_validToken, _invalidPath);
        service.execute();
    }

    //test 5: valid token and path bigger than 1024
    @Test (expected=FileNotFoundException.class)
    public void bigPath() {

    	FileSystem fs = FileSystem.getInstance();
    	Session s = new Session(fs, fs.getUser(_username), _password);
        _validToken = s.getToken();
        System.out.println("token: "+ _validToken);

        String _bigPath = createBigPath();

        ChangeDirectoryService service = new ChangeDirectoryService(_validToken, _bigPath);
        service.execute();
    }

    //test 6: valid token and empty path
    @Test (expected=EmptyPathException.class)
    public void emptyPath() {

    	FileSystem fs = FileSystem.getInstance();
    	Session s = new Session(fs, fs.getUser(_username), _password);
        _validToken = s.getToken();
        System.out.println("token: "+ _validToken);

        ChangeDirectoryService service = new ChangeDirectoryService(_validToken, "");
        service.execute();
    }

   //test 7: valid token and valid path but no permissions
    @Test (expected=PermissionDeniedException.class)
    public void permissionDenied() {

    	FileSystem fs = FileSystem.getInstance();
        User u = new User(fs, "ola", "adeus", "Nomee", (byte) 0x00);

    	Session s = new Session(fs, fs.getUser("ola"), "adeus");
        _validToken = s.getToken();
        System.out.println("token: "+ _validToken);

        ChangeDirectoryService service = new ChangeDirectoryService(_validToken, "/home/user");
        service.execute();
    }


    private String createBigPath(){
    	String res = "/home/";
    	for (int i=0 ; i<1025 ; i++)
    		res = res + 'a';
    	return res;
    }

}
