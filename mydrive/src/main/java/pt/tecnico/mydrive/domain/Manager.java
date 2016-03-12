package pt.tecnico.mydrive.domain;

import pt.ist.fenixframework.FenixFramework;

/*
    Uses the Singleton pattern.
*/

public class Manager extends Manager_Base {

    // TODO: not Reflection safe
    public static Manager getInstance() {
        Manager man = FenixFramework.getDomainRoot().getManager();
        if (man == null) {
            man = new Manager();
        }
        return man;
    }

    private Manager() {
        super();
        FenixFramework.getDomainRoot().setManager(this);
    }
    
}
