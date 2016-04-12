package pt.tecnico.mydrive.service;

public class ListDirectoryService extends MyDriveService {

    private String dirName;
    private long token;

    public ListDirectoryService(String dirName, long token) {
        this.dirName = dirName;
        this.token = token;
    }

    @Override
    protected void dispatch() {
    }
        /* TODO */

    public void result() { /*ArrayList<String> s=new ArrayList<String>(); return s; /* TODO */ }
}
