package pt.tecnico.mydrive.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import pt.tecnico.mydrive.domain.FileSystem;
import pt.tecnico.mydrive.domain.PlainFile;
import pt.tecnico.mydrive.domain.Session;
import pt.tecnico.mydrive.domain.User;
import pt.tecnico.mydrive.domain.Directory;
import pt.tecnico.mydrive.exception.FileNotFoundException;
import pt.tecnico.mydrive.exception.InvalidTokenException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class WriteFileTest extends AbstractServiceTest {
    protected static final Logger log = LogManager.getRootLogger();
    private static final long invalidToken = 1232455424;
    private static final String valid_username = "ilovees";
    private static final String valid_password = "1234!";
    private static final String good_plain_file_name = "IAmAPlainFile";
    private static final String invalid_plain_file_name = "IAmsdfgsdf3wb5345bAPlainFile";
    private static final String good_plain_file_content = "IamSoBiggContent";

    //private String good_plain_file_path;
    private long valid_token;
    private User valid_user;
    private FileSystem fs;
    private PlainFile good_plain_file;

    @Override
    protected void populate() {
        fs = FileSystem.getInstance();

        valid_user = new User(fs, valid_username, valid_password, "Test Me");
        Directory home = (Directory)fs.getFile(valid_user.getHomePath());
        good_plain_file = new PlainFile(fs, home, valid_user, good_plain_file_name, (byte) 0b11111111);
        //good_plain_file_path = good_plain_file.getFullPath();

        LoginService login = new LoginService( valid_username, valid_password );
        valid_token = login.result();

    }

    //@Test
    public void success() {
        WriteFileService service = new WriteFileService(valid_token, good_plain_file_name, good_plain_file_content);
        assertEquals("file content is wrong", good_plain_file_content, good_plain_file.getContent());
    }

    @Test(expected = FileNotFoundException.class)
    public void successWithInexistentFile() {
        WriteFileService service = new WriteFileService(valid_token, invalid_plain_file_name, good_plain_file_content);
    }

    @Test(expected = InvalidTokenException.class)
    public void successWithInvalidSession() {
        WriteFileService service = new WriteFileService(invalidToken, good_plain_file_name, good_plain_file_content);
    }


}

