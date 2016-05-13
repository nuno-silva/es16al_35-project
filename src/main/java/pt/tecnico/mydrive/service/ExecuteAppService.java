package pt.tecnico.mydrive.service;


import pt.tecnico.mydrive.domain.*;
import pt.tecnico.mydrive.exception.AppExecutionExcepiton;
import pt.tecnico.mydrive.exception.MyDriveException;

/**
 * Executes an {@link pt.tecnico.mydrive.domain.App} file
 */
public class ExecuteAppService extends MyDriveService {
    private final long token;
    private final String path;
    private final String[] args;

    public ExecuteAppService(long token, String path, String[] args) {
        this.token = token;
        this.path = path;
        this.args = args;
    }

    @Override
    protected void dispatch() throws MyDriveException {
        FileSystem fs = FileSystem.getInstance();
        Session s = fs.getSession(token);
        User u = s.getUser();
        Directory d = (Directory)s.getWorkDir();

        File f = fs.getFile(path, u);

        try {
            f.execute(u, args);
        } catch (Exception e) {
            throw new AppExecutionExcepiton(e.getMessage());
        }


    }
}