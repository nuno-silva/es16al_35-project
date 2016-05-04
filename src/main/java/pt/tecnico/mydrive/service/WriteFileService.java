package pt.tecnico.mydrive.service;

import pt.tecnico.mydrive.domain.*;
import pt.tecnico.mydrive.exception.PermissionDeniedException;
import pt.tecnico.mydrive.exception.WriteDirectoryException;

public class WriteFileService extends MyDriveService {

    private long token;
    private String fileName;
    private String content;

    public WriteFileService(long token, String fileName, String content) {
        this.token = token;
        this.fileName = fileName;
        this.content = content;
    }

    @Override
    protected void dispatch() {
        FileSystem fs = getFileSystem();
        Session s = fs.getSession(token);
        Directory d = s.getWorkDir();
        File f = d.getFileByName(fileName);
        if (f.isCdAble()) {
            throw new WriteDirectoryException("Cannot write in " + f.getFullPath() + " since it's a directory.");
        }

        User activeUser = s.getUser();
        if (!activeUser.hasWritePermission(f)) {
            throw new PermissionDeniedException(activeUser.getUsername() + " has no write permissions for "
                    + f.getFullPath());
        }

        PlainFile pf = (PlainFile)f;
        pf.setContent(content);
    }
}
