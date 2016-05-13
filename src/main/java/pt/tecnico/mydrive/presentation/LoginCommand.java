package pt.tecnico.mydrive.presentation;

import pt.tecnico.mydrive.service.LoginService;

public class LoginCommand extends Command{

	public LoginCommand(Shell sh) {
		super(sh, "login", "logs in a user");
	}
	@Override
	public void execute(String[] args) {
		LoginService ls;
		if (args.length == 0 || args.length > 2)
		    throw new RuntimeException("USAGE: "+name()+" login <username> [<password>]");
		if (args.length == 2)
	          ls = new LoginService(args[0], args[1]);
		else
	          ls = new LoginService(args[0], "");
        ls.execute();
        MydriveShell mds = (MydriveShell) shell();
        mds.addSession(args[0], ls.result());
	}
}
