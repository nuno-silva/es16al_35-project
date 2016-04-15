package pt.tecnico.mydrive.service;


import org.junit.Test;
import static org.junit.Assert.assertNull;

import pt.tecnico.mydrive.domain.*;
import pt.tecnico.mydrive.exception.FileNotFoundException;
import pt.tecnico.mydrive.exception.PermissionDeniedException;

public final class DeleteFileServiceTest extends AbstractServiceTest {

    private static final String DIR_PERM_REMOVE_REC1 = "The_Documentary";
    private static final String DIR_PERM_REMOVE_REC1_F1 = "WestSide_Story";
    private static final String DIR_PERM_REMOVE_REC1_F2 = "How_We_Do";
    private static final String DIR_PERM_REMOVE_REC2 = "The_Doctors_Advocate";
    private static final String DIR_PERM_REMOVE_REC2_F1 = "Lets_Ride";
    private static final String DIR_PERM_REMOVE_REC2_F2 = "California_Vacation";
    private static final String DIR_PERM_REMOVE_REC2_DIR1 = "Leftovers";
    private static final String DIR_PERM_REMOVE_REC2_DIR1_F1 = "Bang_Along";
    private static final String DIR_PERM_REMOVE_REC2_DIR1_DIR1 = "The_Documentary_2";
    private static final String DIR_PERM_REMOVE_REC2_DIR1_DIR1_F1 = "My_Flag";
    private static final String DIR_PERM_REMOVE_FILE_NO_PERM_REMOVE = "LAX_Files";
    private static final String DIR_NO_PERM_REMOVE_FILE_PERM_REMOVE = "RED";
    private static final String INVALID_FILENAME = "undefinedIsNotAFunction";
    private static final String FILE_PERM_REMOVE = "lyrics";
    private static final String FILE_NO_PERM_REMOVE = "Dr.Dre";
    private static String DIR_PERM_REMOVE = "The_Chronic";

    private long token;
    private FileSystem fs;
    private User theGame;

    @Override
    protected void populate() {
        fs = FileSystem.getInstance();
        // anyone can do anything with his files
        theGame = new User(fs, "TheGame", "Dr.Dre", "The Protegé Of The D.R.E.", (byte)0b11111111);
        
        Directory gameHome = (Directory) fs.getFile(theGame.getHomePath());
        new PlainFile(fs, gameHome, theGame, FILE_PERM_REMOVE, theGame.getByteMask(), "Dr.Dre, Dr.Dre, Dr.Dre, Compton");
        new PlainFile(fs, gameHome, theGame, FILE_NO_PERM_REMOVE, (byte)11101110, "Touch Dre, catch an exception");

        // Create an empty directory, with permission to remove it
        new Directory(fs, gameHome, theGame, DIR_PERM_REMOVE, theGame.getByteMask());

        // Create a directory with some files in it, all can be deleted
        Directory theDocumentary = new Directory(fs, gameHome, theGame, DIR_PERM_REMOVE_REC1, theGame.getByteMask());
        new PlainFile(fs, theDocumentary, theGame, DIR_PERM_REMOVE_REC1_F1, theGame.getByteMask(), "Since the west coast " +
                "fell off, the streets been watching");
        new PlainFile(fs, theDocumentary, theGame, DIR_PERM_REMOVE_REC1_F2, theGame.getByteMask(), "This is how we do");

        // Create a directory with some files and folders with files in it
        Directory theDctorsAdvocate = new Directory(fs, gameHome, theGame, DIR_PERM_REMOVE_REC2, theGame.getByteMask());
        new PlainFile(fs, theDocumentary, theGame, DIR_PERM_REMOVE_REC2_F1, theGame.getByteMask(), "Pull the rag off the 6'4," +
                "hit the switch, show *em* how this *thing* go");
        File cv = new PlainFile(fs, theDocumentary, theGame, DIR_PERM_REMOVE_REC2_F2, theGame.getByteMask(), "Weeeest Cooooast");
        Directory leftovers = new Directory(fs, theDctorsAdvocate, theGame, DIR_PERM_REMOVE_REC2_DIR1, theGame.getByteMask());
        new Link(fs, leftovers, theGame, DIR_PERM_REMOVE_REC2_DIR1_F1, theGame.getByteMask(), cv.getFullPath());
        Directory theDocumentary2 = new Directory(fs, leftovers, theGame, DIR_PERM_REMOVE_REC2_DIR1_DIR1, theGame.getByteMask());
        new PlainFile(fs, theDocumentary2, theGame, DIR_PERM_REMOVE_REC2_DIR1_DIR1_F1, theGame.getByteMask(), "Dr.Dre wasn't mentioned here");

        // Create a directory with permission to delete, but with some files with no permission to delete
        Directory laxFiles = new Directory(fs, gameHome, theGame, DIR_PERM_REMOVE_FILE_NO_PERM_REMOVE, theGame.getByteMask());
        new PlainFile(fs, laxFiles, theGame, "California_Sunshine", (byte)11101110, "California sunshine," +
                "in the Summer time");
        new PlainFile(fs, laxFiles, theGame, "LAX_Files", theGame.getByteMask(), "L.A.X. Chronicles, L.A.X. Files," +
                                                                                                        "case closed");

        // Create a directory with no permission to delete, but with some files with permission to delete
        Directory red = new Directory(fs, gameHome, theGame, DIR_NO_PERM_REMOVE_FILE_PERM_REMOVE, (byte)11101110);
        new PlainFile(fs, red, theGame, "Dr.Dre_Intro", theGame.getByteMask(), "Dr.Dre Dr.Dre Dr.Dre Dre Dre Dre");
        new PlainFile(fs, red, theGame, "Dr.Dre_Outro", (byte)11101110, "Dr.Dre is leaving ;(");

        // Let's login a user
        Session s = new Session(fs, theGame, "Dr.Dre");
        token = s.getToken();
    }

    @Test
    public void successRemoveFile() {
        DeleteFileService dfs = new DeleteFileService(token, FILE_PERM_REMOVE);
        dfs.execute();

        // make sure that the file was removed
        File f = getFileByPath(theGame.getHomePath() + "/" + FILE_PERM_REMOVE);
        assertNull("File has not been deleted", f);
    }

    @Test
    public void successRemoveDirectory() {
        DeleteFileService dfs = new DeleteFileService(token, DIR_PERM_REMOVE);
        dfs.execute();

        // make sure that the directory was removed
        File f = getFileByPath(theGame.getHomePath() + "/" + DIR_PERM_REMOVE);
        assertNull("Directory has not been deleted", f);
    }

    /**
     * Single level recursion directory deletion test
     */
    @Test
    public void successRemoveDirectoryRec1() {
        DeleteFileService dfs = new DeleteFileService(token, DIR_PERM_REMOVE_REC1);
        dfs.execute();

        // make sure that the directory was removed
        File f = getFileByPath(theGame.getHomePath() + "/" + DIR_PERM_REMOVE_REC1);
        assertNull("Directory has not been deleted", f);

        // make sure that the files within the directory have been removed
        f = getFileByPath(theGame.getHomePath() + "/" + DIR_PERM_REMOVE_REC1 + "/" + DIR_PERM_REMOVE_REC1_F1);
        assertNull("File 1 within the directory has not been deleted", f);

        f = getFileByPath(theGame.getHomePath() + "/" + DIR_PERM_REMOVE_REC1 + "/" + DIR_PERM_REMOVE_REC1_F2);
        assertNull("File 2 within the directory has not been deleted", f);
    }

    /**
     * Multiple level recursion directory deletion test (directory A with directory B in it, directory B has directory C
     * in it, all of the directories are non-empty; deleting directory A should remove all of them)
     */
    @Test
    public void successRemoveDirectoryRec2() {
        final String FIRST_LEVEL = theGame.getHomePath() + "/" + DIR_PERM_REMOVE_REC2;
        final String SECOND_LEVEL = FIRST_LEVEL + "/" + DIR_PERM_REMOVE_REC2_DIR1;
        final String THIRD_LEVEL = FIRST_LEVEL + "/" + DIR_PERM_REMOVE_REC2_DIR1;

        DeleteFileService dfs = new DeleteFileService(token, DIR_PERM_REMOVE_REC2);
        dfs.execute();

        // make sure that the first-level directory was removed
        File f = getFileByPath(FIRST_LEVEL);
        assertNull("First-level Directory has not been deleted", f);

        // make sure that the files within the first-level directory have been removed
        f = getFileByPath(FIRST_LEVEL + DIR_PERM_REMOVE_REC2_F1);
        assertNull("File 1 within the first-level directory has not been deleted", f);

        f = getFileByPath(FIRST_LEVEL + DIR_PERM_REMOVE_REC2_F2);
        assertNull("File 2 within the first-level directory has not been deleted", f);

        // make sure that the second-level directory was removed
        f = getFileByPath(SECOND_LEVEL);
        assertNull("First-level Directory has not been deleted", f);

        // make sure that the files within the second-level directory have been removed
        f = getFileByPath(SECOND_LEVEL + DIR_PERM_REMOVE_REC2_DIR1_F1);
        assertNull("File within the second-level directory has not been deleted", f);

        // make sure that the third-level directory was removed
        f = getFileByPath(THIRD_LEVEL);
        assertNull("First-level Directory has not been deleted", f);

        // make sure that the files within the third-level directory have been removed
        f = getFileByPath(THIRD_LEVEL + DIR_PERM_REMOVE_REC2_DIR1_DIR1_F1);
        assertNull("File within the third-level directory has not been deleted", f);
    }


    @Test(expected = PermissionDeniedException.class)
    public void failRemoveFile() {
        DeleteFileService dfs = new DeleteFileService(token, FILE_NO_PERM_REMOVE);
        dfs.execute();
    }

    /**
     * Directory with permission to remove, but it contains files that cannot be removed
     */
    @Test(expected = PermissionDeniedException.class)
    public void failRemoveDirectory1() {
        DeleteFileService dfs = new DeleteFileService(token, DIR_PERM_REMOVE_FILE_NO_PERM_REMOVE);
        dfs.execute();
    }

    /**
     * Directory with no permission to remove, but it contains files that can be removed
     */
    @Test(expected = PermissionDeniedException.class)
    public void failRemoveDirectory2() {
        DeleteFileService dfs = new DeleteFileService(token, DIR_NO_PERM_REMOVE_FILE_PERM_REMOVE);
        dfs.execute();
    }

    @Test(expected = FileNotFoundException.class)
    public void removeInvalidFile() {
        DeleteFileService dfs = new DeleteFileService(token, INVALID_FILENAME);
        dfs.execute();
    }

    private File getFileByPath(String path) {
        try {
            return fs.getFile(path);
        } catch (FileNotFoundException e ) {
            return null;
        }
    }

}
