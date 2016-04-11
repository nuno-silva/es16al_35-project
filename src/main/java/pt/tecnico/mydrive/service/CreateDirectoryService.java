package pt.tecnico.mydrive.service;

import pt.tecnico.mydrive.exception.MydriveException;
import pt.tecnico.mydrive.exception.EmptyFileNameException;

public class CreateDirectoryService extends CreateFileService {

    public CreateDirectoryService(String fileName,long token ) {
        super( fileName, token );
    }

    @Override
    protected void dispatch() throws EmptyFileNameException {
		super.dispatch();
    }
}
