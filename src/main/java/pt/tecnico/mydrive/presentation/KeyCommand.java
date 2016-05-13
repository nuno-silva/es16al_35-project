package pt.tecnico.mydrive.presentation;

import pt.tecnico.mydrive.exception.InvalidTokenException;

public class KeyCommand extends MyDriveCommand {

    public KeyCommand(Shell sh) {
        super(sh, "token", "switch between active sessions");
    }
    
    @Override
	public void execute(String[] args) {
		try {
			MydriveShell mds = (MydriveShell) shell();
			if(args.length == 0) {
				String lastUser = mds.getLastUser();
		        shell().println("Token: "+ mds.getTokenFromUsername(lastUser) + " from User: "+ lastUser); 
			} else if(args.length == 1) {
				mds.setLastLogin(args[0]);
				String lastUser = mds.getLastUser();
				shell().println("Token: "+ mds.getTokenFromUsername(lastUser));
			} else {
				throw new RuntimeException("USAGE: " + name() + " token [<username>]");
			}
		} catch (InvalidTokenException e){
	        shell().println("You need to login first!");
	    }
	}

}
