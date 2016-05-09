package pt.tecnico.mydrive.presentation;

import java.util.Map;
import java.util.TreeMap;

import pt.tecnico.mydrive.service.LoginService;

public class MydriveShell extends Shell {
  private static final String INITIAL_USER = "nobody";
  
  private String lastUser;
  private long token;
  
  private Map<String, Long> users = new TreeMap<String, Long>();

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
    /* Add commands from here below */
    new LoginCommand(this);
    //new CreatePerson(this);
    //new CreateContact(this);
    //new RemovePerson(this);
    //new RemoveContact(this);
    new ChangeWorkingDirectoryCommand(this);
    new EnvironmentCommand(this);
    new ListCommand(this);
    new KeyCommand(this);
    //new Import(this);
    //new Export(this);
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
  }

  public long getTokenFromUsername(String username){
    return users.get(username);
  }
}
