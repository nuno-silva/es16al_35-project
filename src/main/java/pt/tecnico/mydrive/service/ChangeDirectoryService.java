package pt.tecnico.mydrive.service;

/*Domain*/
import pt.tecnico.mydrive.domain.Directory;
import pt.tecnico.mydrive.domain.FileSystem;
import pt.tecnico.mydrive.domain.Session;
import pt.tecnico.mydrive.domain.User;
import pt.tecnico.mydrive.domain.File;

/*Exceptions*/
import pt.tecnico.mydrive.exception.EmptyPathException;
import pt.tecnico.mydrive.exception.InvalidTokenException;
import pt.tecnico.mydrive.exception.MydriveException;
import pt.tecnico.mydrive.exception.PermissionDeniedException;
import pt.tecnico.mydrive.exception.IsNotCdAbleException;

public class ChangeDirectoryService extends MyDriveService {

    private long token;
    private String path;

    public ChangeDirectoryService(long token, String path) {
        this.token = token;
        this.path = path;
    }

    @Override
    protected void dispatch() {
        if (path.trim() == "") {
            throw new EmptyPathException(path);
        }

        FileSystem fs = getFileSystem();
        Session session = fs.getSession(token);
        if (session.isExpired()){
            throw new InvalidTokenException(token);
        }

        if (path.charAt(0) == '/' ) {//path is absolute
                File f = fs.getFile(path);
                if(!f.isCdAble()) throw new IsNotCdAbleException();
                session.setWorkDir((Directory)f);
            }
            else { //relative
              Directory workDir = session.getWorkDir();
              File f = fs.getFile(workDir.getFullPath() + "/" + path);
              if(!f.isCdAble()) throw new IsNotCdAbleException();
              session.setWorkDir((Directory)f);
            }


        User activeUser = session.getUser();
        if (!activeUser.getStringPermissions().equals(session.getWorkDir().getStringPermissions()))
            throw new PermissionDeniedException(activeUser.getUsername() + " has no read permissions for "
                    + session.getWorkDir().getFullPath());
    }
}
