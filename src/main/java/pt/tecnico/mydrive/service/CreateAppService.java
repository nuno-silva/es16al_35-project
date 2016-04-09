package pt.tecnico.mydrive.service;

import pt.tecnico.mydrive.exception.MydriveException;
import pt.tecnico.mydrive.exception.EmptyFileNameException;

public class CreateAppService extends CreatePlainFileService {

    public CreateAppService(String fileName,String owner,String parent,String content) {
        super( fileName, owner, parent, content );
    }

    @Override
    protected void dispatch() throws EmptyFileNameException {
		super.dispatch();
    }
}
