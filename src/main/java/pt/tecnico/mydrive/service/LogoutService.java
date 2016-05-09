package pt.tecnico.mydrive.service;

import pt.tecnico.mydrive.domain.FileSystem;
import pt.tecnico.mydrive.domain.Session;

public class LogoutService extends MyDriveService {
    private long _token;

    public LogoutService(long token) {
        _token = token;
    }

    @Override
    public void dispatch() {
        FileSystem fs = FileSystem.getInstance();
        Session s = fs.getSession(_token);
        s.remove();
    }
}
