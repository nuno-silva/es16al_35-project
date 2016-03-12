package pt.tecnico.mydrive.domain;

public class PlainFile extends PlainFile_Base {

	public PlainFile(){}

    public PlainFile(String name, byte perm, long id, String content) {
        init(name, perm, id, content);
    }
    
    protected void init(String name, byte perm, long id, String content){
		super.init(name, perm, id);
		setContent(content);
	}
	

}
