package pt.tecnico.mydrive.service;

import pt.tecnico.mydrive.exception.MydriveException;
import pt.tecnico.mydrive.exception.EmptyFileNameException;
import pt.tecnico.mydrive.exception.IsNotCdAbleException;
import pt.tecnico.mydrive.exception.PermissionDeniedException;
import pt.tecnico.mydrive.domain.PlainFile;
import pt.tecnico.mydrive.domain.Session;
import pt.tecnico.mydrive.domain.FileSystem;
import pt.tecnico.mydrive.domain.Directory;
import pt.tecnico.mydrive.domain.File;

public class CreatePlainFileService extends CreateFileService {

	private String content;

    public CreatePlainFileService(String fileName, long token, String content) {
        super( fileName, token);
        this.content=content;
    }
    
    public CreatePlainFileService(String fileName, long token) {
        super( fileName, token );
        this.content="";
    }

	protected String getContent(){ return content; }

	protected void dispatchAux(){
		super.dispatch();
	}

    @Override
    protected void dispatch() throws EmptyFileNameException, IsNotCdAbleException,PermissionDeniedException {
		super.dispatch();
		FileSystem fs = getFileSystem();
		Session s = fs.getSession( getToken() );
		File f = fs.getFile( s.getWorkingPath() );
		if( !f.isCdAble() ) throw new IsNotCdAbleException();
		Directory d = (Directory) f;
		new PlainFile( fs, d, s.getUser(), getContent() );
    }
}
