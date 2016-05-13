package pt.tecnico.mydrive.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import pt.tecnico.mydrive.domain.FileSystem;
import pt.tecnico.mydrive.domain.Link;
import pt.tecnico.mydrive.domain.PlainFile;
import pt.tecnico.mydrive.domain.Session;
import pt.tecnico.mydrive.domain.User;
import pt.tecnico.mydrive.domain.Directory;
import pt.tecnico.mydrive.domain.File;
import pt.tecnico.mydrive.exception.FileNotFoundException;
import pt.tecnico.mydrive.exception.InvalidTokenException;
import pt.tecnico.mydrive.exception.WriteDirectoryException;
import pt.tecnico.mydrive.exception.PermissionDeniedException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class WriteFileServiceTest extends AbstractServiceTest {
    protected static final Logger log = LogManager.getRootLogger();
    private static final long invalidToken = 1232455424;
    private static final String valid_username = "ilovees";
    private static final String valid_password = "1234!sssss";
    private static final String valid_dirname  = "lalalalalala";
    private static final String good_plain_file_name = "IAmAPlainFile";
    private static final String invalid_plain_file_name = "IAmsdfgsdf3wb5345bAPlainFile";
    private static final String good_plain_file_content = "IamSoBiggContent";
    private static final String good_plain_file_name2 = "TesteWithLink1";
    private static final String good_plain_file_name3 = "TesteWithLink2";
    private static final String good_link_file = "LinkTest1";
    private static final String good_link_file2_name = "LinkTest2";
    
    private static final String valid_plain_file_path = "/home"+"/"+valid_username+"/"+good_plain_file_name;

    //private String good_plain_file_path;
    private long valid_token;
    private User valid_user, another_user;
    private FileSystem fs;
    private PlainFile good_plain_file;
    private PlainFile good_plain_file2;
    private Directory home, another_user_home, valid_dir;

    @Override
    protected void populate() {
        fs = FileSystem.getInstance();

        valid_user = new User(fs, valid_username, valid_password, "Test Me");
        home = valid_user.getHome();
        good_plain_file = new PlainFile(fs, home, valid_user, good_plain_file_name, (byte) 0b11111111);
        valid_dir = new Directory(fs, home, valid_user, valid_dirname, (byte) 0b11111111);
        good_plain_file2 = new PlainFile(fs, home, valid_user, good_plain_file_name2, (byte) 0b11111111, "");
        //good_plain_file3 = new PlainFile(fs, valid_dir, valid_user, good_plain_file_name3, (byte) 0b11111111, "");
        new Link(fs, home, valid_user, good_link_file, good_plain_file_name2);

        another_user = new User(fs, "anotheruser", "another password", "Test Me 2");
        another_user_home = another_user.getHome();
        
        

        LoginService login = new LoginService( valid_username, valid_password );
        login.execute();
        valid_token = login.result();

    }

    @Test
    public void success() {
        WriteFileService service = new WriteFileService(valid_token, good_plain_file_name, good_plain_file_content);
        service.execute();
        assertEquals("file content is wrong", good_plain_file_content, good_plain_file.getContent());
    }

    @Test(expected = FileNotFoundException.class)
    public void successWithInexistentFile() {
        WriteFileService service = new WriteFileService(valid_token, invalid_plain_file_name, good_plain_file_content);
        service.execute();
    }

    @Test(expected = InvalidTokenException.class)
    public void successWithInvalidSession() {
        WriteFileService service = new WriteFileService(invalidToken, good_plain_file_name, good_plain_file_content);
        service.execute();
    }

    @Test(expected = WriteDirectoryException.class)
    public void failWriteToDirectory() {
        WriteFileService service = new WriteFileService(valid_token, valid_dirname, good_plain_file_content);
        service.execute();
    }

    @Test
    public void successWithDifferentOwner() {
        String filename = "a file owner by another user";
        byte perm = home.getPermissions();
        home.setPermissions((byte)0xff);
        PlainFile another_users_file = new PlainFile(fs, home, another_user, filename, (byte) 0b11111111);
        home.setPermissions(perm);

        WriteFileService service = new WriteFileService(valid_token, filename, good_plain_file_content);
        service.execute();
    }

    @Test(expected = PermissionDeniedException.class)
    public void failWithDifferentOwner() {
        String filename = "a file owner by another user";
        home.setPermissions((byte)0xff);
        PlainFile another_users_file = new PlainFile(fs, home, another_user, filename, (byte) 0b11111011);

        WriteFileService service = new WriteFileService(valid_token, filename, good_plain_file_content);
        service.execute();
    }

    @Test(expected = PermissionDeniedException.class)
    public void failWithNoWritePermission() {
        good_plain_file.setPermissions((byte)0x10111111);

        WriteFileService service = new WriteFileService(valid_token, good_plain_file_name, good_plain_file_content);
        service.execute();
    }

    @Test(expected = PermissionDeniedException.class)
    public void directSetContentFail() {
        good_plain_file.setContent("Dr.Dre");
    }
    
    @Test
    public void successWriteFileWithPath() {
        WriteFileService service = new WriteFileService(valid_token, valid_plain_file_path, good_plain_file_content);
        service.execute();
        assertEquals("file content is wrong", good_plain_file_content, good_plain_file.getContent());
    }
    
    @Test
    public void successWriteToLink() {
    	/*Writing to plainFile through link
    	 * 1) Create link and point it to respective plainFile
    	 * 2) Execute service with content to insert in plainFile
    	 */
        WriteFileService service = new WriteFileService(valid_token, good_link_file, "Does it work?");
        service.execute();
        assertEquals("file content is wrong", "Does it work?", good_plain_file2.getContent());
    }
    
    @Test
    public void successLinkToPlainFile() {
    	/*Writing to a plainfile which is inside a directory which is fetched through a link:
    	 * 1) Create plainFile (good_plain_file3) inside directory valid_dir
    	 * 2) Create link which points to valid_dir directory
    	 * 3) Fetch plainFile from the directory and execute service to insert content in it
    	 */
    	PlainFile good_plain_file3 = new PlainFile(fs, valid_dir, valid_user, good_plain_file_name3, (byte) 0b11111111, "");
    	Link l = new Link(fs, valid_user.getHome(), valid_user, good_link_file2_name, valid_dirname);
    	File dir = l.getPointedFile(valid_user);
    	assertEquals("link not working", valid_dir, dir);
    	File f = dir.getFile(good_plain_file_name3,valid_user);
    	assertEquals("not fetching right file", good_plain_file3, f);
        WriteFileService service = new WriteFileService(valid_token, f.getFullPath(), "Does it work?");
        service.execute();
        assertEquals("file content is wrong", "Does it work?", good_plain_file3.getContent());
    }
}
