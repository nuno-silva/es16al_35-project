package pt.tecnico.mydrive.service;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import pt.tecnico.mydrive.domain.Directory;
import pt.tecnico.mydrive.domain.FileSystem;
import pt.tecnico.mydrive.domain.User;
import pt.tecnico.mydrive.exception.FileNotFoundException;
import pt.tecnico.mydrive.exception.XMLFormatException;

import java.util.Optional;

public class XMLImportServiceTest extends AbstractServiceTest {
    private final String RESOURCES_BASE_DIR = "./src/test/resources/";

    private final String fileName = RESOURCES_BASE_DIR + "test.xml";
    private final String nonExistentFileName = RESOURCES_BASE_DIR + "thechronic.xml";
    private final String malformedXMLFileName = RESOURCES_BASE_DIR + "malformed.xml";

    private FileSystem fs;

    @Override
    protected void populate() {
        fs = FileSystem.getInstance();
    }

    @Test(expected = FileNotFoundException.class)
    public void fileNotFoundFail() {
        new XMLImportService(nonExistentFileName).execute();
    }

    @Test(expected = XMLFormatException.class)
    public void malformedXMLFileFail() {
        new XMLImportService(malformedXMLFileName).execute();
    }

    @Test
    public void directoryImportSuccess() {
        new XMLImportService(fileName).execute();

        final String expectedPath = "/home/root/testDir";
        final String expectedName = "testDir";
        final String expectedOwnerUsername = "nobody";
        final String expectedMask = "10010101";
        final String expectedLastMod = "2016-05-07T17:57:08.131+01:00";

        Directory testDir = (Directory) fs.getFile("/home/root/testDir");
        assertNotNull(testDir);

        final String realName = testDir.getName();
        assertEquals("Name mismatch", expectedName, realName);

        final String realPath = testDir.getFullPath();
        assertEquals("Path mismatch", expectedPath, realPath);

        final String realMask = testDir.getStringMask();
        assertEquals("Mask mismatch", expectedMask, realMask);

        final String realLastMod = testDir.getLastMod().toString();
        assertEquals("LastMod mismatch", expectedLastMod, realLastMod);

        // TODO: this creates a test dependency (that the user import part is functional)
        Optional<User> user = fs.getUserByUsername(expectedOwnerUsername);
        assertTrue("User owner not found", user.isPresent());

        final User expectedOwner = user.get();
        assertEquals("Onwer mismatch", expectedOwner, testDir.getOwner());
    }

}
