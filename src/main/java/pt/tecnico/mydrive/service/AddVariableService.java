package pt.tecnico.mydrive.service;

import java.util.Map;
import java.util.HashMap;
import pt.tecnico.mydrive.domain.FileSystem;
import pt.tecnico.mydrive.domain.Session;
import pt.tecnico.mydrive.domain.Variable;

public class AddVariableService extends MyDriveService {
    private String _name;
    private String _value;
    private long _token;
    private Map<String, String> _vars;

    public AddVariableService(long token) {
        this(token, null, null);
    }

    public AddVariableService(long token, String name, String value) {
        _name=name;
        _value=value;
        _token=token;
    }

    @Override
    protected void dispatch() {
        FileSystem fs = FileSystem.getInstance();
        Session s = fs.getSession(_token);

        // not the best approach, I know, but I don't want to create another service
        if(_name != null && _value != null) {
            new Variable(s, _name, _value);
        }

        _vars = new HashMap<String, String>();

        for(Variable v : s.getVariableSet()) {
            _vars.put( v.getName(), v.getValue() );
        }
    }

    public Map<String, String> result() {
        assertExecuted();
        return _vars;
    }
}
