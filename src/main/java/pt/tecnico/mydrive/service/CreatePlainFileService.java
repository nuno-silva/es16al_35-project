package pt.tecnico.mydrive.service;

import pt.tecnico.mydrive.exception.MydriveException;
import pt.tecnico.mydrive.exception.EmptyFileNameException;

public class CreatePlainFileService extends CreateFileService {

	private String content;

    public CreatePlainFileService(String fileName,String owner,String parent,String content) {
        super( fileName, owner, parent );
        this.content=content;
    }

    @Override
    protected void dispatch() throws EmptyFileNameException {
		super.dispatch();
    }
}
