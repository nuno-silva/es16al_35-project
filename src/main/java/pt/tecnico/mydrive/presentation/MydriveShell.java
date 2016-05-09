package pt.tecnico.mydrive.presentation;

import pt.tecnico.mydrive.service.LoginService;

public class MydriveShell extends Shell {
  private static final String INITIAL_USER = "nobody";

  private long _token;

  public static void main(String[] args) throws Exception {
    MydriveShell sh = new MydriveShell();
    sh.execute();
  }

  @Override
  public void execute() throws Exception {
    LoginService login = new LoginService(INITIAL_USER, "");
    login.execute();
    _token = login.result();
    super.execute();
  }

  public MydriveShell() {
    super("MyDrive");
    /* Add commands from here below */
    //new LoginCommand(this);
    //new CreatePerson(this);
    //new CreateContact(this);
    //new RemovePerson(this);
    //new RemoveContact(this);
    new EnvironmentCommand(this);
    new ListCommand(this);
    new KeyCommand(this);
    //new Import(this);
    //new Export(this);
  }

  public long getToken(){
    return _token;
  }
  
  public void setToken(Long token) {
	  _token = token;
  }
}
