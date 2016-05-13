package pt.tecnico.mydrive.domain;

public class Extension extends Extension_Base {

    protected Extension() {
        super();
    }

    public Extension(User u, String extension, String app_path) {
        init(u, extension, app_path);
    }

    protected void init(User u, String extension, String app_path) {
        setUser(u);
        setExtension(extension);
        setAppPath(app_path);
    }
}
