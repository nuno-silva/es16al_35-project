package pt.tecnico.mydrive.service;

import pt.tecnico.mydrive.exception.MydriveException;

public class ChangeDirectoryService extends MyDriveService {

    private long token;
    private String path;

    public ChangeDirectoryService(long token, String path) {
        this.token = token;
        this.path = path;
    }

    @Override
    protected void dispatch() throws MydriveException {
        //Session session = getFileSystem().getSession(token);
        //session.setWorkingPath(path);
    }

}
