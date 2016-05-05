package pt.tecnico.mydrive.service;

import pt.tecnico.mydrive.domain.PlainFile;
import pt.tecnico.mydrive.domain.Directory;
import pt.tecnico.mydrive.domain.File;
import pt.tecnico.mydrive.domain.FileSystem;
import pt.tecnico.mydrive.domain.Session;
import pt.tecnico.mydrive.domain.User;
import pt.tecnico.mydrive.exception.EmptyFileNameException;
import pt.tecnico.mydrive.exception.PermissionDeniedException;
import pt.tecnico.mydrive.exception.ReadDirectoryException;


public class ReadFileService extends MyDriveService {

    private String fileName;
    private long token;

    public ReadFileService(long token, String fileName) {
        this.token = token;
        this.fileName = fileName;
    }

    @Override
    protected void dispatch() {

    	if (fileName.trim() == "") {
            throw new EmptyFileNameException();
        }
    	FileSystem fs = getFileSystem();
        Session session = fs.getSession(token);

        Directory d = session.getWorkDir();
        File f = d.getFileByName(fileName);

        if (f.isCdAble()) {
            throw new ReadDirectoryException("Cannot read " + f.getFullPath() + " since it's a directory.");
        }

        User activeUser = session.getUser();
        if (!activeUser.hasReadPermission(f)) {
            throw new PermissionDeniedException(activeUser.getUsername() + " has no read permissions for "
                    + f.getFullPath());
        }
        String fullpathtofile = d.getFullPath() + "/" + fileName;

        PlainFile linkfile = (PlainFile) fs.getFile(fullpathtofile);
        linkfile.getContent();
    }
}
