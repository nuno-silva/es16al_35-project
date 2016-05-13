package pt.tecnico.mydrive.presentation;

import pt.tecnico.mydrive.exception.InvalidTokenException;
import pt.tecnico.mydrive.service.ListDirectoryService;
import pt.tecnico.mydrive.service.WriteFileService;
import pt.tecnico.mydrive.service.dto.FileDto;

public class WriteCommand extends Command {

	public WriteCommand(Shell sh) {
		super(sh, "write", "writes text to a file");
	}
	
	@Override
	public void execute(String[] args) {
		WriteFileService wfs;
		if (args.length != 2)
		    throw new RuntimeException("USAGE: "+name()+" update <path> <text>");
		else {
			try{
				MydriveShell mds = (MydriveShell) shell();
				wfs = new WriteFileService(mds.getToken(), args[0], args[1]);
				wfs.execute();
			} catch (InvalidTokenException e){
				shell().println("You need to login first!");
			}
		}
	}

}
