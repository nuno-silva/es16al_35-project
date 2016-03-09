package pt.tecnico.mydrive.domain;

public class Link extends Link_Base {
    
    public Link(String name,byte perm,long id,String path) {
        init(name,perm,id);
        setPath(path);
    }
    
}
