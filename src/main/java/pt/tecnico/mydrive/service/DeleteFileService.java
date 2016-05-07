package pt.tecnico.mydrive.service;

import pt.tecnico.mydrive.domain.File;
import pt.tecnico.mydrive.domain.FileSystem;
import pt.tecnico.mydrive.domain.Session;
import pt.tecnico.mydrive.domain.User;
import pt.tecnico.mydrive.exception.MyDriveException;
import pt.tecnico.mydrive.exception.PermissionDeniedException;

public class DeleteFileService extends MyDriveService {

    private String fileName;
    private String path, workingDir;
    private long token;

    public DeleteFileService(long token, String fileName) {
        this.token = token;
        this.fileName = fileName;
    }

    @Override
    protected void dispatch() throws MyDriveException, PermissionDeniedException {
        FileSystem fs = getFileSystem();
        Session session = fs.getSession(token);
        workingDir = session.getWorkDir().getFullPath();

        User activeUser = session.getUser();

        String fullpathtofile = workingDir + "/" + this.fileName;
        File file = fs.getFile(fullpathtofile);

        file.remove(activeUser);
    }
}
