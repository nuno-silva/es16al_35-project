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
        User activeUser = s.getUser();
        File f;
        if (fileName.contains("/"))
        	f = fs.getFile(fileName);
        else {
            File d = s.getWorkDir();
            f = d.getFile(fileName, activeUser);
        }
        f.assertIsWritable(); // make sure that it's a not a directory
        PlainFile pf = (PlainFile) f;
        pf.setContent(content, activeUser);
    }
}
