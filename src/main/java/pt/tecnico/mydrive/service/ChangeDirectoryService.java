package pt.tecnico.mydrive.service;

/*Domain*/
import pt.tecnico.mydrive.domain.Directory;
import pt.tecnico.mydrive.domain.FileSystem;
import pt.tecnico.mydrive.domain.Session;
import pt.tecnico.mydrive.domain.User;
import pt.tecnico.mydrive.domain.User_Base;
import pt.tecnico.mydrive.domain.File;

/*Exceptions*/
import pt.tecnico.mydrive.exception.EmptyPathException;
import pt.tecnico.mydrive.exception.InvalidTokenException;
import pt.tecnico.mydrive.exception.PermissionDeniedException;
import pt.tecnico.mydrive.exception.IsNotCdAbleException;
import pt.tecnico.mydrive.exception.MyDriveException;

public class ChangeDirectoryService extends MyDriveService {

    private long token;
    private String path;
    private String newPath;

    public ChangeDirectoryService(long token, String path) {
        this.token = token;
        this.path = path;
    }

    public ChangeDirectoryService(long token) {
    	FileSystem fs = getFileSystem();
        Session s = fs.getSession(token);
        User u = s.getUser();
        
        this.token = token;
        this.path = u.getHomePath();
    	

    }

    @Override
    protected void dispatch() throws MyDriveException, PermissionDeniedException{
        if (path.trim() == "") {
            throw new EmptyPathException(path);
        }

        FileSystem fs = getFileSystem();
        Session session = fs.getSession(token);
        User u = session.getUser();

	    if (path.charAt(0) == '/' ) {//path is absolute
		File f = fs.getFile(path,u );
		session.setWorkDir(f);
		newPath = path;
	    }
	    else { //relative
	    	File workDir = session.getWorkDir();
	    	newPath = workDir.getFullPath() + "/" + path;
        	File f = fs.getFile(newPath,u);
        	session.setWorkDir(f);
        	newPath = f.getFullPath();
        }

    }

    public String result() {
    	assertExecuted();
    	return newPath;
    }
}
