package pt.tecnico.mydrive.presentation;

import pt.tecnico.mydrive.exception.InvalidTokenException;

public class KeyCommand extends MyDriveCommand {

    public KeyCommand(Shell sh) {
        super(sh, "token", "switch between active sessions");
    }
    
	void execute(String[] args) {
		try {
			if(args.length == 0) {
				MydriveShell mds = (MydriveShell) shell();
		        mds.print("Token: "+ String.valueOf(mds.getToken())); //FIXME: Am I supposed to do this?
		        //get username through token without domain
			} else if(args.length == 1) {
				//set user as new current user
			} else {
				throw new RuntimeException("USAGE: " + name() + " token [<username>]");
			}
		} catch (InvalidTokenException e){
	        shell().println("You need to login first!");
	    }
	}

}
