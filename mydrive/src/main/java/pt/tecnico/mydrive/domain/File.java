package pt.tecnico.mydrive.domain;

import pt.tecnico.mydrive.exception.InvalidFileNameException;

public abstract class File extends File_Base {

    public File() {
        super();
    }

    public File(Directory dir, String name, byte perm, long id) {
        super();
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

    @Override
    public void setName(String name) throws InvalidFileNameException {
        if( name.contains("/") || name.contains("\0") ) {
            throw new InvalidFileNameException(name);
        }
        super.setName(name);
    }

}
