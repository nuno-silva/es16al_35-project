package pt.tecnico.mydrive.presentation;

/*Exceptions*/
import pt.tecnico.mydrive.exception.InvalidTokenException;
import java.lang.UnsupportedOperationException;

/* Services */
import pt.tecnico.mydrive.service.ListDirectoryService;
import pt.tecnico.mydrive.service.dto.FileDto;

/*Other stuff*/



public class List extends MydriveCommand {

    public List(Shell sh) { super(sh, "list", "List a directory"); }
    public void execute(String[] args) {
      ListDirectoryService lds;
      /* Check if argument length is at least the size of the obligatory */
      try{
        if( args.length==0 ||  args[0].trim()=="."){
          MydriveShell mds = (MydriveShell) shell();
          lds = new ListDirectoryService(mds.getToken());
          lds.execute();
        }
        if( args.length>1 ){
          throw new RuntimeException("USAGE: "+name()+"list [path]");
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
