package pt.tecnico.mydrive.presentation;

import java.util.Map;
import java.util.TreeMap;

import pt.tecnico.mydrive.service.LoginService;
import pt.tecnico.mydrive.service.LogoutService;

public class MydriveShell extends Shell {
  private static final String INITIAL_USER = "nobody";

  private String activeUser;
  private long activeToken;

  protected Map<String, Long> activeSessions = new TreeMap<String, Long>();

  public static void main(String[] args) throws Exception {
    MydriveShell sh = new MydriveShell();
    sh.execute();
  }

  @Override
  protected void onQuit() {
    // logout guest
    long guestToken = getToken(INITIAL_USER);
    if(guestToken != 0) {
      LogoutService ls = new LogoutService(guestToken);
      ls.execute();
    }
    super.onQuit();
  }

  @Override
  public void execute() throws Exception {
    LoginService login = new LoginService(INITIAL_USER, "");
    login.execute();
    addSession(INITIAL_USER, login.result());
    super.execute();
  }

  public MydriveShell() {
    super("MyDrive");
    new LoginCommand(this);
    new ChangeWorkingDirectoryCommand(this);
    new ListCommand(this);
    //new ExecuteCommand(this);
    //new WriteCommand(this);
    new EnvironmentCommand(this);
    new KeyCommand(this);
    //new Import(this);
    //new Export(this);
    new ExecuteCommand(this);

  }

  public void setActiveUser(String user) {
	  activeUser = user;
    try {
      activeToken = getToken(user);
    } catch(NullPointerException e) {
      activeToken = 0;
    }
  }

  public String getActiveUser() {
	  return activeUser;
  }

  public long getActiveToken() {
	  return activeToken;
  }

  public void addSession(String username, Long token) {
	  activeSessions.put(username, token);
	  setActiveUser(username);
  }

  public long getToken(String username) {
    try {
      return activeSessions.get(username);
    } catch(NullPointerException e) {
      return 0;
    }
  }
}
