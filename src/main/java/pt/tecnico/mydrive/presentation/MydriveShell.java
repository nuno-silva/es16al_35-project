package pt.tecnico.mydrive.presentation;

import java.util.Map;
import java.util.TreeMap;

import pt.tecnico.mydrive.service.LoginService;

public class MydriveShell extends Shell {
  private static final String INITIAL_USER = "nobody";
  
  private String lastUser;
  private long token;
  
  public Map<String, Long> users = new TreeMap<String, Long>();

  public static void main(String[] args) throws Exception {
    MydriveShell sh = new MydriveShell();
    sh.execute();
  }

  @Override
  public void execute() throws Exception {
    LoginService login = new LoginService(INITIAL_USER, "");
    login.execute();
    setUser(INITIAL_USER, login.result());
    setLastLogin(INITIAL_USER);
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
  }
  
  public void setLastLogin(String user) {
	  lastUser = user;
	  token = getTokenFromUsername(user);
  }
  
  public String getLastUser() {
	  return lastUser;
  }
  
  public long getToken() {
	  return token;
  }
  
  public void setUser(String username, Long token) {
	  users.put(username, token);
	  setLastLogin(username);
  }

  public long getTokenFromUsername(String username){
    return users.get(username);
  }
}
