package pt.tecnico.mydrive.system;

import org.junit.Test;

import pt.tecnico.mydrive.presentation.ChangeWorkingDirectoryCommand;
import pt.tecnico.mydrive.presentation.EnvironmentCommand;
import pt.tecnico.mydrive.presentation.KeyCommand;
import pt.tecnico.mydrive.presentation.ListCommand;
import pt.tecnico.mydrive.presentation.LoginCommand;
import pt.tecnico.mydrive.presentation.MydriveShell;
import pt.tecnico.mydrive.service.AbstractServiceTest;

public class SystemTest extends AbstractServiceTest {
	private MydriveShell shell;

	@Override
	protected void populate() {
		shell = new MydriveShell();
	}

    @Test
    public void success() {
        new LoginCommand(shell).execute(new String[] { "root", "***" } );
        new ChangeWorkingDirectoryCommand(shell).execute(new String[] { "/home/nobody" } );
        new LoginCommand(shell).execute(new String[] { "nobody", "" } );
        new EnvironmentCommand(shell).execute(new String[] { "$QWERTY", "/home/nobody" } );
        new ListCommand(shell).execute(new String[] { "/home" } );
        //new ExecuteCommand(shell).execute(new String[] { "other.xml" } );
        //new WriteCommand(shell).execute(new String[] { "other.xml" } );
        new KeyCommand(shell).execute(new String[] { "nobody" } );
    }
}
