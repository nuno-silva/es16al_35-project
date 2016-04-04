package pt.tecnico.mydrive.service;

import pt.tecnico.mydrive.exception.MydriveException;
import pt.tecnico.mydrive.exception.EmptyFileNameException;
import pt.tecnico.mydrive.exception.InvalidFileNameException;
import pt.tecnico.mydrive.domain.User;
import pt.tecnico.mydrive.domain.Directory;

public class CreateFileService extends MyDriveService {

    private String fileName;
    private String type;
	private String content;
	private byte permissions;

    public CreateFileService(String fileName,String type,String owner,String parent,String content) {
        this.fileName = fileName;
        this.type = type;
        permissions=getUser(owner).getByteMask();
        this.content=content;
    }

	public CreateFileService(String fileName,String type,User owner,Directory parent) {
        this.fileName = fileName;
        this.type = type;
        permissions=owner.getByteMask();
        content="";
    }
    @Override
    protected void dispatch() throws EmptyFileNameException {
		if(this.fileName=="") throw new EmptyFileNameException();
		// TODO is type gonna be a String? Ask teacher
		if(type=="link"){
			if(content.trim()=="") 
				throw new InvalidFileNameException(fileName);
			//else
				//new Link//ask teacher if this is the exception to throw!!
		}
		
    }
}
