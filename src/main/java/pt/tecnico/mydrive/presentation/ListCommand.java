package pt.tecnico.mydrive.presentation;

/*Exceptions*/
import pt.tecnico.mydrive.exception.InvalidTokenException;

/* Services */
import pt.tecnico.mydrive.service.ListDirectoryService;
import pt.tecnico.mydrive.service.dto.FileDto;

/*Other stuff*/
import pt.tecnico.mydrive.domain.FileSystem;
import pt.tecnico.mydrive.domain.Session;
import pt.tecnico.mydrive.domain.Directory;

public class ListCommand extends MyDriveCommand {

    public ListCommand(Shell sh) { super(sh, "list", "ListCommand a directory"); }
    public void execute(String[] args) {
      ListDirectoryService lds;
      /* Check if argument length is at least the size of the obligatory */
      try{
        if( args.length <= 0 ){
          MydriveShell mds = (MydriveShell) shell();
          lds = new ListDirectoryService(mds.getToken(), ".");
          lds.execute();
        }
        else if( args.length>1 ){
          throw new RuntimeException("USAGE: "+name()+" [path]");
        }
        else{
          MydriveShell mds = (MydriveShell) shell();
          lds = new ListDirectoryService(mds.getToken(),args[0]);
          lds.execute();
        }
        for( FileDto f : lds.result() ){
          shell().println(f.getId()+" "+f.getName()+" "+ f.getPermissions()+" "+f.getLastMod());
        }
      }catch (InvalidTokenException e){
        shell().println("You need to login first!");
      }
    }
}
