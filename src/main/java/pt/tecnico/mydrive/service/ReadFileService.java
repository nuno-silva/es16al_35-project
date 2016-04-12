package pt.tecnico.mydrive.service;

import pt.tecnico.mydrive.exception.MydriveException;

public class ReadFileService extends MyDriveService {

    private String fileName;
    private long token;

    public ReadFileService(long token, String fileName) {
        this.token = token;
        this.fileName = fileName;
    }

    @Override
    protected void dispatch() throws MydriveException {
        // TODO
    }
}
