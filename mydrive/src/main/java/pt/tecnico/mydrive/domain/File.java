package pt.tecnico.mydrive.domain;

public abstract class File extends File_Base {
    
	public  File(){}

    public File(String name,byte perm,long id) {
		init(name,perm,id);
		
    }
	protected void init(String name,byte perm,long id){
		setName(name);
		setId(id);
		setPerm(perm);
		setIsDeleted(false);
		//still need to add DateTime lastMod
	}
	
	public void deleteFile(){
		
		setIsDeleted(true);
	}

	public boolean getIsDeleted(){
		return super.getIsDeleted();
	}

}
