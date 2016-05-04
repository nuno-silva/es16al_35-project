package pt.tecnico.mydrive.service;

/*Domain*/
import pt.tecnico.mydrive.domain.File;
import pt.tecnico.mydrive.domain.FileSystem;
import pt.tecnico.mydrive.domain.Session;
import pt.tecnico.mydrive.domain.User;
import pt.tecnico.mydrive.domain.Directory;

/*Exceptions*/
import pt.tecnico.mydrive.exception.EmptyFileNameException;
import pt.tecnico.mydrive.exception.IsNotCdAbleException;
import pt.tecnico.mydrive.exception.PermissionDeniedException;


public abstract class CreateFileService extends MyDriveService {

    private String fileName;
    private long token;

    public CreateFileService(String fileName, long token) {
        this.fileName = fileName;
        this.token = token;
    }

    protected long getToken() {
        return token;
    }

    protected String getFileName() {
        return fileName;
    }

    @Override
    protected void dispatch() throws EmptyFileNameException, IsNotCdAbleException, PermissionDeniedException {
        if (fileName.trim() == "")
            throw new EmptyFileNameException();
        /* Retrieve the user through the token received */
        FileSystem fs = getFileSystem();
        Session s = fs.getSession(token);
        Directory d = s.getWorkDir();
        User u = s.getUser();
        if (!d.checkWritePermission(u))
            throw new PermissionDeniedException("creating file.");
    }
}
