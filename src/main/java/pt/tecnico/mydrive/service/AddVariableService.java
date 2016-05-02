package pt.tecnico.mydrive.service;

import pt.tecnico.mydrive.domain.*;

public class AddVariableService extends MyDriveService {

    private String _name;
    private String _value;
    private long _token;

    public AddVariableService(long token, String name, String value) {
      _name=name;
      _value=value;
      _token=token;
    }

    public AddVariableService(long token, String name) {
      _name=name;
      _value="";
      _token=token;
    }

    @Override
    protected void dispatch(){
      /* TODO: Does stuff */
    }
}
