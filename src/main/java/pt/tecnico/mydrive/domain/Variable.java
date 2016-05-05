package pt.tecnico.mydrive.domain;

import pt.tecnico.mydrive.exception.DuplicateVariableException;

public class Variable extends Variable_Base {

    public Variable(Session s, String name, String value) {
        init(s, name, value);
    }

    protected void init(Session s, String name, String value) {
        super.setName(name);
        setValue(value);
        s.addVariable(this);
    }

    @Override
    public void setName(String name) {
        // TODO: shoud I check for empty names?
        if( getSession().hasVariable(name) ) {
            throw new DuplicateVariableException(name);
        }
        super.setName(name);
    }

}
