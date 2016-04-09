package pt.tecnico.mydrive.service;

import pt.tecnico.mydrive.exception.MydriveException;
import pt.tecnico.mydrive.exception.EmptyFileNameException;

public class CreateDirectoryService extends CreateFileService {

    public CreateDirectoryService(String fileName,String owner,String parent) {
        super( fileName, owner, parent );
    }

    @Override
    protected void dispatch() throws EmptyFileNameException {
		super.dispatch();
    }
}
