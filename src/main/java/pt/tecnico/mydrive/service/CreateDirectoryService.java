package pt.tecnico.mydrive.service;

import pt.tecnico.mydrive.domain.Directory;
import pt.tecnico.mydrive.domain.File;
import pt.tecnico.mydrive.domain.FileSystem;
import pt.tecnico.mydrive.domain.PlainFile;
import pt.tecnico.mydrive.domain.Session;
import pt.tecnico.mydrive.domain.User;
import pt.tecnico.mydrive.exception.EmptyFileNameException;
import pt.tecnico.mydrive.exception.IsNotCdAbleException;

public class CreateDirectoryService extends CreateFileService {

    public CreateDirectoryService(String fileName, long token) {
        super(fileName, token);
    }

    @Override
    protected void dispatch() throws EmptyFileNameException {
        super.dispatch();
        FileSystem fs = getFileSystem();
        Session s = fs.getSession(getToken());
        new Directory(fs, s.getWorkDir(), s.getUser(), getFileName());

    }
}
