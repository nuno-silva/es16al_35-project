package pt.tecnico.mydrive.service;

import java.lang.reflect.InvocationTargetException;

import pt.tecnico.mydrive.domain.File;
import pt.tecnico.mydrive.domain.FileSystem;
import pt.tecnico.mydrive.domain.Session;
import pt.tecnico.mydrive.domain.User;
import pt.tecnico.mydrive.exception.EmptyFileNameException;
import pt.tecnico.mydrive.exception.MyDriveException;

public class ExecuteFileService extends MyDriveService {
	private long token;
	private String path;
	private String[] args;
	
	public ExecuteFileService(long token, String path, String[] args) {
		this.token = token;
		this.path = path;
		this.args = args;
	}
    
	@Override
	protected void dispatch() throws MyDriveException {
        if (path.trim() == "")
            throw new EmptyFileNameException();
	    
        FileSystem fs = getFileSystem();
	    Session session = fs.getSession(token);
	    
        User activeUser = session.getUser();

        File file = fs.getFile(path, activeUser);
        
        //TODO: not really sure about this...
        try {
			file.execute(activeUser, args);
		} catch (NoSuchMethodException e) {
			System.err.println("A NoSuchMethodException was caught: " + e.getMessage());
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.err.println("A ClassNotFoundException was caught: " + e.getMessage());
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			System.err.println("A IllegalArgumentException was caught: " + e.getMessage());
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			System.err.println("A IllegalAccessException was caught: " + e.getMessage());
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			System.err.println("A InvocationTargetException was caught: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
