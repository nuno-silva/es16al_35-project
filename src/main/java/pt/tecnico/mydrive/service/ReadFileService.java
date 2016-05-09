package pt.tecnico.mydrive.service;

import pt.tecnico.mydrive.domain.PlainFile;
import pt.tecnico.mydrive.domain.Directory;
import pt.tecnico.mydrive.domain.File;
import pt.tecnico.mydrive.domain.FileSystem;
import pt.tecnico.mydrive.domain.Session;
import pt.tecnico.mydrive.domain.User;
import pt.tecnico.mydrive.exception.EmptyFileNameException;
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

        User activeUser = session.getUser();

        String fullpathtofile = d.getFullPath() + "/" + fileName;

        File file = fs.getFile(fullpathtofile);
        file.getContent(activeUser);
    }
}
