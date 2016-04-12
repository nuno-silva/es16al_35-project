package pt.tecnico.mydrive.service;


import pt.tecnico.mydrive.domain.FileSystem;

public class ListDirectoryService extends MyDriveService {

	private String dirName;
	private byte userPerm;

    public ListDirectoryService(String dirName,byte userPerm) {
        this.dirName = dirName;
        this.userPerm=userPerm;
    }

    @Override
    protected void dispatch() {}
		/* TODO */
}
