package pt.tecnico.mydrive.service;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import pt.ist.fenixframework.Atomic;
import pt.tecnico.mydrive.domain.FileSystem;
import pt.tecnico.mydrive.domain.User;
import pt.tecnico.mydrive.exception.InvalidUsernameException;
import pt.tecnico.mydrive.exception.MyDriveException;
import pt.tecnico.mydrive.exception.UserNotFoundException;

import java.util.Optional;

/**
 * Abstract base class from which all of the services inherit.
 */
public abstract class MyDriveService {
    private boolean _executed = false;
    protected final static Logger logger = LogManager.getRootLogger();

    protected static FileSystem getFileSystem() {
        return FileSystem.getInstance();
    }

    @Atomic
    public final void execute() throws MyDriveException {
        dispatch();
        // protect agains people trying to get the service's result without executing it!
        // could have saved a lot of time on sprint-2...
        _executed = true;
    }

    protected abstract void dispatch() throws MyDriveException;

    protected void assertExecuted() {
        if(!_executed) {
            throw new RuntimeException("Service was NOT executed!");
        }
    }
}
