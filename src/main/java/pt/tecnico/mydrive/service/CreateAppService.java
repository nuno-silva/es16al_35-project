package pt.tecnico.mydrive.service;

import java.util.List;
import java.util.ArrayList;

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
		List<String> l=d.showContent();
		for(String se : l)
			System.out.println("AAAAAAAAAAAAAND THIS FILE IS!: "+se);
		System.out.println("Creating file: "+getFileName()+" at: "+d.getName());
		new App( fs, d, s.getUser(), getFileName(), getContent() );
    }
}
