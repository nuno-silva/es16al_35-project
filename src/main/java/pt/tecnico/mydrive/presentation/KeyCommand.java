package pt.tecnico.mydrive.presentation;

import pt.tecnico.mydrive.exception.InvalidTokenException;

public class KeyCommand extends MyDriveCommand {

    public KeyCommand(Shell sh) {
        super(sh, "token", "switch between active sessions");
    }

    @Override
	public void execute(String[] args) {

		MydriveShell mds = (MydriveShell) shell();
		if(args.length == 0) {
			String lastUser = mds.getActiveUser();
			if(lastUser == null) {
				shell().println("No active user.");
			} else {
				shell().println("Token: "+ mds.getToken(lastUser) + " from User: "+ lastUser);
			}
		} else if(args.length == 1) {
			String username = args[0];
			if(mds.getToken(username) != 0) {
				mds.setActiveUser(username);
				shell().println("Token: "+ mds.getToken(username));
			} else {
				shell().println("No token for user "+username);
			}
		} else {
			throw new RuntimeException("USAGE: " + name() + " token [<username>]");
		}

	}

}
