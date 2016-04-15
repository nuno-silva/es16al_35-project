package pt.tecnico.mydrive.service;

import pt.tecnico.mydrive.domain.Directory;
import pt.tecnico.mydrive.domain.FileSystem;
import pt.tecnico.mydrive.domain.Session;
import pt.tecnico.mydrive.domain.User;
import pt.tecnico.mydrive.exception.EmptyPathException;
import pt.tecnico.mydrive.exception.FileNotFoundException;
import pt.tecnico.mydrive.exception.InvalidTokenException;
import pt.tecnico.mydrive.exception.MydriveException;
import pt.tecnico.mydrive.exception.PermissionDeniedException;
import pt.tecnico.mydrive.exception.UnknownPathException;

public class ChangeDirectoryService extends MyDriveService {

    private long token;
    private String path;

    public ChangeDirectoryService(long token, String path) {
        this.token = token;
        this.path = path;
    }

    @Override
    protected void dispatch() throws MydriveException {
    	String absPath;
    	if (path.trim() == "") {
            throw new EmptyPathException(path);
        }
    	
        FileSystem fs = getFileSystem();
        Session session = fs.getSession(token);
        if (session.isExpired()){
            throw new InvalidTokenException(token);
        }

        try {
        	if (path.charAt(0) == '/' ) {//path is absolute
        		fs.getFile(path);
                session.setWorkingPath(path);
                absPath = path;
        	}
        	else { //relative
        		absPath = fs.getFile(session.getWorkingPath() + "/" + path).getFullPath();
        		session.setWorkingPath(absPath);
        	}

            
            User activeUser = session.getUser();
            if (!activeUser.getStringPermissions().equals(fs.getFile(absPath).getStringPermissions()))
            	throw new PermissionDeniedException(activeUser.getUsername() + " has no read permissions for "
                        + session.getWorkingPath());
          
        } catch (UnknownPathException e) {
            throw new UnknownPathException(path);
        } catch (FileNotFoundException e) {
            throw new UnknownPathException(path);
        }
    }
}
