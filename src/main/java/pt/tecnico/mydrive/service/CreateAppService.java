package pt.tecnico.mydrive.service;

import pt.tecnico.mydrive.domain.*;
import pt.tecnico.mydrive.exception.EmptyFileNameException;
import pt.tecnico.mydrive.exception.IsNotCdAbleException;
import pt.tecnico.mydrive.exception.PermissionDeniedException;

public class CreateAppService extends CreatePlainFileService {

    public CreateAppService(String fileName, long token, String content) {
        super(fileName, token, content);
    }

    public CreateAppService(String fileName, long token) {
        super(fileName, token, "");
    }

    @Override
    protected void dispatch() throws EmptyFileNameException, IsNotCdAbleException, PermissionDeniedException {
        super.dispatchAux();
        FileSystem fs = getFileSystem();
        Session s = fs.getSession(getToken());
        Directory workDir = s.getWorkDir();
        new App(fs, workDir, s.getUser(), getFileName(), getContent());
    }
}
