package pt.tecnico.mydrive.domain;

public abstract class File extends File_Base {

    public File() {
        super();
    }

    public File(Directory dir, String name, byte perm, long id) {
        init(dir, name, perm, id);
    }

    protected void init(Directory dir, String name, byte perm, long id){
        setDirectory(dir);
        setName(name);
        setId(id);
        setPerm(perm);
        //still need to add DateTime lastMod
    }

    public void remove() {
        setDirectory(null);
        deleteDomainObject();
    }

}
