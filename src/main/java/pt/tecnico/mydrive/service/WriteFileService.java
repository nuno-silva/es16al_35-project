package pt.tecnico.mydrive.service;

import pt.tecnico.mydrive.exception.MydriveException;

public class WriteFileService extends MyDriveService {

    private long token;
    private String fileName;
    private String content;

    public WriteFileService(long token, String fileName, String content) {
        this.token = token;
        this.fileName = fileName;
        this.content = content;
    }

    @Override
    protected void dispatch() throws MydriveException {
        
    }
}
