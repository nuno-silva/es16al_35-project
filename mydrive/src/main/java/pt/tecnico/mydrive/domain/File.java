package pt.tecnico.mydrive.domain;

public class File extends File_Base {
    
	public  File(){}

    public File(String name,byte perm,long id) {
		init(name,perm,id);
		
    }
   	protected void init(String name,byte perm,long id){
		setName(name);
		setId(id);
		setPerm(perm);
		//still need to add DateTime lastMod
	}

}
