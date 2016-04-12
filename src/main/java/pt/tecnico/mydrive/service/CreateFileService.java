package pt.tecnico.mydrive.service;

import pt.tecnico.mydrive.exception.MydriveException;
import pt.tecnico.mydrive.exception.EmptyFileNameException;
import pt.tecnico.mydrive.exception.IsNotCdAbleException;
import pt.tecnico.mydrive.exception.PermissionDeniedException;
import pt.tecnico.mydrive.domain.User;
import pt.tecnico.mydrive.domain.FileSystem;
import pt.tecnico.mydrive.domain.Session;
import pt.tecnico.mydrive.domain.File;


public abstract class CreateFileService extends MyDriveService {

    private String fileName;
	private long token;

    public CreateFileService(String fileName, long token) {
        this.fileName = fileName;
        this.token = token;
    }

	protected long getToken(){ return token; }

	protected String getFileName(){ return fileName; }

    @Override
    protected void dispatch() throws EmptyFileNameException, IsNotCdAbleException,PermissionDeniedException{
		if( fileName.trim() == "" )
			throw new EmptyFileNameException();
		/* Retrieve the user through the token received */
		FileSystem fs = getFileSystem();
		Session s=fs.getSession( token );
		File f = fs.getFile( s.getWorkingPath() );
		User u = s.getUser();
		if( ! f.isCdAble() ) throw new IsNotCdAbleException();
		if( ! f.checkWritePermission( u ) )
			throw new PermissionDeniedException( "creating file.");		
		}
}
