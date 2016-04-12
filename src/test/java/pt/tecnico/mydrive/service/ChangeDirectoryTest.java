package pt.tecnico.mydrive.service;

import org.junit.Test;
import pt.ist.fenixframework.FenixFramework;
import pt.tecnico.mydrive.domain.FileSystem;
import pt.tecnico.mydrive.domain.Session;
import pt.tecnico.mydrive.domain.User;

public class ChangeDirectoryTest extends AbstractServiceTest {

    protected void populate() {

        //FileSystem fs = FenixFramework.getDomainRoot().getFileSystem()
        FileSystem fs = FileSystem.getInstance();

        //create path /test/hello
        //Directory d = fs.createFileParents("/test/hello"); //returns '/' directory
        //ou
        //new Directory(d, "hello", (byte) 255);

        //create user
        User u = new User(fs, "username", "password", "name", (byte) 0xff);

        //TODO: crate session for this user
        //chamar construtor de session e ir buscar token

    }

    @Test
    public void success() {

        long validToken = 1;
        long invalidToken = 0;
        //final String validAbsPath = "/test/hello";
        final String validAbsPath = "/test";


        FileSystem fs = FenixFramework.getDomainRoot().getFileSystem();

        //case 1: valid token and valid path(absolute)
        ChangeDirectoryService service = new ChangeDirectoryService(validToken, validAbsPath);
        service.execute();

        // check currentDir changed with success
        Session s = getSession(validToken);
        //assertNotNull("Session was not found", s);
        //assertEquals("Invalid phone number", validAbsPath, s.getWorkingPath());
        
     
        
        /*ListPersonPhoneBook service = new ListPersonPhoneBook("Ana");
        service.execute();
	List<ContactDto> cs = service.result();

        // check contact listing
        assertEquals("List with 6 Contacts", 6, cs.size());
	assertEquals("First name is Abel", "Abel", cs.get(0).getName());
	assertEquals("Last name is Zélia", "Zélia", cs.get(5).getName());
	assertEquals("Third name is Beatriz", "Beatriz", cs.get(2).getName());
	assertEquals("Third phoneNumber is 777777777", 777777777, cs.get(2).getPhoneNumber()); */
    }


    private Session getSession(long validToken) {
        FileSystem fs = FenixFramework.getDomainRoot().getFileSystem();
        //TODO

        //return fs.getSession(validToken);
        return null;
    }
}