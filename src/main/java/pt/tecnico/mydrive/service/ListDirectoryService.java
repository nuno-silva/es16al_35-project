package pt.tecnico.mydrive.service;


public class ListDirectoryService extends MyDriveService {

    private String dirName;
    private byte userPerm;

    public ListDirectoryService(String dirName, byte userPerm) {
        this.dirName = dirName;
        this.userPerm = userPerm;
    }

    @Override
    protected void dispatch() {
    }
        /* TODO */
}
