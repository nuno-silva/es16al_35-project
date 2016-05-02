package pt.tecnico.mydrive.service;

import pt.tecnico.mydrive.domain.FileSystem;
import pt.tecnico.mydrive.domain.Session;
import pt.tecnico.mydrive.domain.Variable;

public class AddVariableService extends MyDriveService {
    private String _name;
    private String _value;
    private long _token;

    public AddVariableService(long token, String name, String value) {
        _name=name;
        _value=value;
        _token=token;
    }

    @Override
    protected void dispatch() {
        FileSystem fs = FileSystem.getInstance();
        Session s = fs.getSession(_token);
        new Variable(s, _name, _value);
    }
}
