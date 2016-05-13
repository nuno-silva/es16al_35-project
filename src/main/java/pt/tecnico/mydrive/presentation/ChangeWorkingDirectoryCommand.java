package pt.tecnico.mydrive.presentation;

/*Exceptions*/
import pt.tecnico.mydrive.exception.InvalidTokenException;

/* Services */
import pt.tecnico.mydrive.service.ChangeDirectoryService;
import pt.tecnico.mydrive.service.dto.FileDto;

/*Other stuff*/



public class ChangeWorkingDirectoryCommand extends MyDriveCommand {

    public ChangeWorkingDirectoryCommand(Shell sh) {
	super(sh, "cwd", "Change the current directory");

    }
    public void execute(String[] args) {

	ChangeDirectoryService service;
	try{
		if (args.length > 1)
		    throw new RuntimeException("USAGE: "+name()+" cwd [path]");

		String result;
		MydriveShell mds = (MydriveShell) shell();

		if (args.length == 1) {
	          service = new ChangeDirectoryService(mds.getActiveToken(), args[0]);
		}else{ //path is emitted
	          service = new ChangeDirectoryService(mds.getActiveToken());
		}
		service.execute();
	        result = service.result();

		shell().println(result);

	} catch (InvalidTokenException e){
		shell().println("You need to login first!");
	}
    }
}
