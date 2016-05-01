package pt.tecnico.mydrive.presentation;

import pt.tecnico.mydrive.service.ListDirectoryService;
import java.lang.UnsupportedOperationException;
import pt.tecnico.mydrive.service.dto.FileDto;
import java.nio.charset.StandardCharsets;


public class List extends MydriveCommand {

    public List(Shell sh) { super(sh, "List", "List a directory"); }
    public void execute(String[] args) {
      ListDirectoryService lds;
      /* Check if argument length is at least the size of the obligatory */
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
        shell().println(f.getId()+" "+f.getName()+" "+ f.getPermissions()+" "+f.getlastMod());
      }
    }
}
