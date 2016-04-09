package pt.tecnico.mydrive.service;

import pt.tecnico.mydrive.exception.MydriveException;
import pt.tecnico.mydrive.exception.EmptyFileNameException;
import pt.tecnico.mydrive.exception.InvalidFileNameException;
import pt.tecnico.mydrive.domain.User;
import pt.tecnico.mydrive.domain.Directory;

public class CreateFileService extends MyDriveService {

    private String fileName;
	private byte permissions;

    public CreateFileService(String fileName,String owner,String parent) {
        this.fileName = fileName;
        permissions=getUser(owner).getByteMask();
    }

	public CreateFileService(String fileName,User owner,Directory parent) {
        this.fileName = fileName;
        permissions=owner.getByteMask();
    }

    @Override
    protected void dispatch() throws EmptyFileNameException{
		if( fileName.trim() == "" )
			throw new EmptyFileNameException();
	}
}
