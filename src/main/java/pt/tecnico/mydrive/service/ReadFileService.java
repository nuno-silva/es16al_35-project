package pt.tecnico.mydrive.service;

import pt.tecnico.mydrive.domain.PlainFile;
import pt.tecnico.mydrive.domain.Directory;
import pt.tecnico.mydrive.domain.File;
import pt.tecnico.mydrive.domain.FileSystem;
import pt.tecnico.mydrive.domain.Session;
import pt.tecnico.mydrive.domain.User;
import pt.tecnico.mydrive.exception.EmptyFileNameException;
import pt.tecnico.mydrive.exception.InvalidTokenException;
import pt.tecnico.mydrive.exception.MydriveException;
import pt.tecnico.mydrive.exception.PermissionDeniedException;
import pt.tecnico.mydrive.exception.ReadDirectoryException;
import pt.tecnico.mydrive.exception.UnknownPathException;
import pt.tecnico.mydrive.exception.FileNotFoundException;


public class ReadFileService extends MyDriveService {

    private String fileName;
    private long token;

    public ReadFileService(long token, String fileName) {
        this.token = token;
        this.fileName = fileName;
    }

    @Override
    protected void dispatch() throws MydriveException {
    	
    	if (fileName.trim() == "") {
            throw new EmptyFileNameException();
        }
    	FileSystem fs = getFileSystem();
        Session session = getFileSystem().getSession(token);
        if (session.isExpired())
            throw new InvalidTokenException(token);
        
        Directory d = (Directory) fs.getFile(session.getWorkingPath());
        File f = d.getFileByName(fileName);

        if (f.isCdAble()) {
            throw new ReadDirectoryException("Cannot read " + f.getFullPath() + " since it's a directory.");
        }
        
        User activeUser = session.getUser();
        if (!activeUser.hasReadPermission(f)) {
            throw new PermissionDeniedException(activeUser.getUsername() + " has no read permissions for "
                    + f.getFullPath());
        }
        String fullpathtofile = session.getWorkingPath() + "/" + fileName;
        try {
        	PlainFile linkfile = (PlainFile) fs.getFile(fullpathtofile);
            linkfile.getContent();
        }catch (UnknownPathException e) {
        	throw new FileNotFoundException(fileName);
        }
    }
}
