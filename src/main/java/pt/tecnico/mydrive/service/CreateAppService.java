package pt.tecnico.mydrive.service;

import pt.tecnico.mydrive.domain.*;
import pt.tecnico.mydrive.exception.EmptyFileNameException;
import pt.tecnico.mydrive.exception.IsNotCdAbleException;
import pt.tecnico.mydrive.exception.PermissionDeniedException;

import java.util.List;

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
        File f = fs.getFile(s.getWorkingPath());
        if (!f.isCdAble()) throw new IsNotCdAbleException();
        Directory d = (Directory) f;
        List<String> l = d.showContent();
        for (String se : l)
            System.out.println("AAAAAAAAAAAAAND THIS FILE IS!: " + se);
        System.out.println("Creating file: " + getFileName() + " at: " + d.getName());
        new App(fs, d, s.getUser(), getFileName(), getContent());
    }
}
