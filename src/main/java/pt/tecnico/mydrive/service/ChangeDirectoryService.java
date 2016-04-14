package pt.tecnico.mydrive.service;

import pt.tecnico.mydrive.domain.FileSystem;
import pt.tecnico.mydrive.domain.Session;
import pt.tecnico.mydrive.exception.FileNotFoundException;
import pt.tecnico.mydrive.exception.InvalidTokenException;
import pt.tecnico.mydrive.exception.MydriveException;
import pt.tecnico.mydrive.exception.UnknownPathException;

public class ChangeDirectoryService extends MyDriveService {

    private long token;
    private String path;
    private String workingDir;

    public ChangeDirectoryService(long token, String path) {
        this.token = token;
        this.path = path;
    }

    @Override
    protected void dispatch() throws MydriveException {
        FileSystem fs = getFileSystem();
        Session session = getFileSystem().getSession(token);
        if (session.isExpired())
            throw new InvalidTokenException(token);

        try {
            if (path.substring(0, 1).matches("/")) {//path is absolute
                fs.getFile(path);
                session.setWorkingPath(path);
            } else {                                  //path is relative to current Directory
                String fullPath = session.getWorkingPath();
                fullPath.concat(path);
                fs.getFile(fullPath);
                session.setWorkingPath(fullPath);
            }
            workingDir = session.getWorkingPath();

        } catch (UnknownPathException e) {
            throw new UnknownPathException(path);
        } catch (FileNotFoundException e) {
            throw new UnknownPathException(path);
        }
    }
    
    protected String result() {
    	return workingDir;
    }
    
    

}
