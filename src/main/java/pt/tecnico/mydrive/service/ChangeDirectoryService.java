package pt.tecnico.mydrive.service;

/*Domain*/
import pt.tecnico.mydrive.domain.Directory;
import pt.tecnico.mydrive.domain.FileSystem;
import pt.tecnico.mydrive.domain.Session;
import pt.tecnico.mydrive.domain.User;
import pt.tecnico.mydrive.domain.File;

/*Exceptions*/
import pt.tecnico.mydrive.exception.EmptyPathException;
import pt.tecnico.mydrive.exception.InvalidTokenException;
import pt.tecnico.mydrive.exception.PermissionDeniedException;
import pt.tecnico.mydrive.exception.IsNotCdAbleException;

public class ChangeDirectoryService extends MyDriveService {

    private long token;
    private String path;
    private String newPath;

    public ChangeDirectoryService(long token, String path) {
        this.token = token;
        this.path = path;   
    }

    public ChangeDirectoryService(long token) {
        this.token = token;
        //this.path = path;
    }
    
    
    @Override
    protected void dispatch() {
        if (path.trim() == "") {
            throw new EmptyPathException(path);
        }

        FileSystem fs = getFileSystem();
        Session session = fs.getSession(token);

	    if (path.charAt(0) == '/' ) {//path is absolute
		File f = fs.getFile(path);
		session.setWorkDir((Directory)f);
		newPath = path;
	    }
	    else { //relative
	    	Directory workDir = session.getWorkDir();
	    	newPath = workDir.getFullPath() + "/" + path;
        	File f = fs.getFile(newPath);
        	session.setWorkDir((Directory)f);
	
        }
	
        User activeUser = session.getUser();
        if (!activeUser.getStringPermissions().equals(session.getWorkDir().getStringPermissions()))
            throw new PermissionDeniedException(activeUser.getUsername() + " has no read permissions for "
                    + session.getWorkDir().getFullPath());
    }
    
    public String result() {
    	assertExecuted();
    	return newPath;
    }
}
