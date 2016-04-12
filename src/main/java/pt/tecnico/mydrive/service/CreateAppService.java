package pt.tecnico.mydrive.service;

import pt.tecnico.mydrive.exception.MydriveException;
import pt.tecnico.mydrive.exception.EmptyFileNameException;
import pt.tecnico.mydrive.exception.IsNotCdAbleException;
import pt.tecnico.mydrive.exception.PermissionDeniedException;
import pt.tecnico.mydrive.domain.App;
import pt.tecnico.mydrive.domain.Session;
import pt.tecnico.mydrive.domain.FileSystem;
import pt.tecnico.mydrive.domain.Directory;
import pt.tecnico.mydrive.domain.File;

public class CreateAppService extends CreatePlainFileService {

    public CreateAppService(String fileName, long token, String content) { super( fileName, token, content ); }
    
    public CreateAppService(String fileName, long token) { super( fileName, token, "" ); }


    @Override
    protected void dispatch() throws EmptyFileNameException, IsNotCdAbleException,PermissionDeniedException {
		super.dispatchAux();
		FileSystem fs = getFileSystem();
		Session s = fs.getSession( getToken() );
		File f = fs.getFile( s.getWorkingPath() );
		if( !f.isCdAble() ) throw new IsNotCdAbleException();
		Directory d = (Directory) f;
		new App( fs, d, s.getUser(), getContent() );
    }
}
