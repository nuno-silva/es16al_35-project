package pt.tecnico.mydrive.service;

import pt.tecnico.mydrive.domain.*;
import pt.tecnico.mydrive.exception.EmptyFileNameException;
import pt.tecnico.mydrive.exception.IsNotCdAbleException;
import pt.tecnico.mydrive.exception.PermissionDeniedException;

public class CreatePlainFileService extends CreateFileService {

    private String content;

    public CreatePlainFileService(String fileName, long token, String content) {
        super(fileName, token);
        this.content = content;
    }

    public CreatePlainFileService(String fileName, long token) {
        super(fileName, token);
        this.content = "";
    }

    protected String getContent() {
        return content;
    }

    protected void dispatchAux() {
        super.dispatch();
    }

    @Override
    protected void dispatch() throws EmptyFileNameException, IsNotCdAbleException, PermissionDeniedException {
        super.dispatch();
        FileSystem fs = getFileSystem();
        Session s = fs.getSession(getToken());
        File f = fs.getFile(s.getWorkingPath());
        if (!f.isCdAble()) throw new IsNotCdAbleException();
        Directory d = (Directory) f;
        new PlainFile(fs, d, s.getUser(), getFileName(),getContent());
    }
}
