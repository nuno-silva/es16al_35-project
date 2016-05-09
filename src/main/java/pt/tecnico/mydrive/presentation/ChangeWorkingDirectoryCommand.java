package pt.tecnico.mydrive.presentation;

/*Exceptions*/
import pt.tecnico.mydrive.exception.InvalidTokenException;

/* Services */
import pt.tecnico.mydrive.service.ListDirectoryService;
import pt.tecnico.mydrive.service.dto.FileDto;

/*Other stuff*/



public class ChangeWorkingDirectoryCommand extends MyDriveCommand {

    public ChangeWorkingDirectoryCommand(Shell sh) {
	super(sh, "cwd", "Change the current directory"); 
	
    }
    public void execute(String[] args) {
      		
		
	try{
		if (args.length > 1)
		    throw new RuntimeException("USAGE: "+name()+" cwd [path]");
		    
		String result;    
		MydriveShell mds = (MydriveShell) shell();
		
		if (args.length == 1) {
	          mds = new ChangeDirectoryService(mds.getToken(), args[0]);
		}else{ //path is emitted
	          mds = new ChangeDirectoryService(mds.getToken());
		}
		service.execute();
	        result = service.result();
		
		shell().println(result);
		
	} catch (InvalidTokenException e){
		shell().println("You need to login first!");
	}
}
