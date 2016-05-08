package pt.tecnico.mydrive.presentation;

import pt.tecnico.mydrive.service.LoginService;

public class LoginCommand extends Command{

	public LoginCommand(Shell sh) {
		super(sh, "login", "logs in a user");
	}
	@Override
	void execute(String[] args) {
		if (args.length == 0 || args.length > 2)
		    throw new RuntimeException("USAGE: "+name()+" login <username> [<password>]");
		if (args.length == 2) {
	          LoginService ls = new LoginService(args[0], args[1]);
	          ls.execute();
	          MydriveShell mds = (MydriveShell) shell();
	          mds.setToken(ls.result());
		}
		else {
			// TODO: call loginService with only one argument (userame) for logging in the guestUser
		}
	}
}
