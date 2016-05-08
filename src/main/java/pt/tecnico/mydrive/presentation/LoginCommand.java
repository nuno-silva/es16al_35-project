package pt.tecnico.mydrive.presentation;

import pt.tecnico.mydrive.service.LoginService;

public class LoginCommand extends Command{

	public LoginCommand(Shell sh) {
		super(sh, "login", "logs in a user");
	}
	@Override
	void execute(String[] args) {
		// TODO Auto-generated method stub
		if (args.length != 2)
		    throw new RuntimeException("USAGE: "+name()+" login <username> [<password>]");
		else {
	          LoginService ls = new LoginService(args[0], args[1]);
	          ls.execute();
	          MydriveShell mds = (MydriveShell) shell();
	          mds.setToken(ls.result());
		}
	}
}
