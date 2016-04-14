package pt.tecnico.mydrive.service;

import pt.tecnico.mydrive.domain.Directory;
import pt.tecnico.mydrive.domain.File;
import pt.tecnico.mydrive.domain.FileSystem;
import pt.tecnico.mydrive.domain.PlainFile;
import pt.tecnico.mydrive.domain.Session;
import pt.tecnico.mydrive.exception.FileNotFoundException;
import pt.tecnico.mydrive.exception.InvalidTokenException;
import pt.tecnico.mydrive.exception.IsNotCdAbleException;
import pt.tecnico.mydrive.exception.MydriveException;
import pt.tecnico.mydrive.exception.UnknownPathException;

public class DeletePlainFileService extends MyDriveService {

    private String fileName;
    private String path;
    private long token;

    public DeletePlainFileService(long token, String fileName) {
        this.token = token;
        this.fileName = fileName;
    }

    @Override
    protected void dispatch() throws MydriveException {
        FileSystem fs = getFileSystem();
        Session session = getFileSystem().getSession(token);
        if (session.isExpired())
            throw new InvalidTokenException(token);
        try {
            if (path.substring(0, 1).matches("/")) {//path is absolute
                fs.getFile(path);
                session.setWorkingPath(path);
            } else {                                  //path is relative to current Directory
                String fullPath = session.getWorkingPath();
                fullPath.concat(path);
                fs.getFile(fullPath);
                session.setWorkingPath(fullPath);
            }

        } catch (UnknownPathException e) {
            throw new UnknownPathException(path);
        } catch (FileNotFoundException e) {
            throw new UnknownPathException(path);
        }
        File f = fs.getFile(session.getWorkingPath());
        if (!f.isCdAble()) throw new IsNotCdAbleException();
        String fullpathtofile = session.getWorkingPath() + "/" + fileName;
        PlainFile plainfile = (PlainFile) fs.getFile(fullpathtofile);
        plainfile.remove();
        
    }
}
