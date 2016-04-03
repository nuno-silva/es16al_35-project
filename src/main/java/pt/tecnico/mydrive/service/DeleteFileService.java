package pt.tecnico.mydrive.service;

import pt.tecnico.mydrive.exception.MydriveException;

public class DeleteFileService extends MyDriveService {

    private String fileName;
    private long token;

    public DeleteFileService(long token, String fileName) {
        this.token = token;
        this.fileName = fileName;
    }

    @Override
    protected void dispatch() throws MydriveException {
        // TODO
    }
}
