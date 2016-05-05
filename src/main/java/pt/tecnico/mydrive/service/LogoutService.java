package pt.tecnico.mydrive.service;


public class LogoutService extends MyDriveService {

    private long _token;

    public LogoutService(long token) {
      _token = token;
    }

    @Override
    public void dispatch() {
      /* TODO STUFF */
    }

    public long result() {
      /*FIXME This might not be needed */
      return _token;
    }
}
