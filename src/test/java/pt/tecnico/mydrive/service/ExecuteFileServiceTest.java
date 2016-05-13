package pt.tecnico.mydrive.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import pt.tecnico.mydrive.domain.App;
import pt.tecnico.mydrive.domain.Directory;
import pt.tecnico.mydrive.domain.FileSystem;
import pt.tecnico.mydrive.domain.Link;
import pt.tecnico.mydrive.domain.PlainFile;
import pt.tecnico.mydrive.domain.User;

import pt.tecnico.mydrive.service.*;
import pt.tecnico.mydrive.exception.*;

public class ExecuteFileServiceTest extends AbstractServiceTest {
	private static final byte DEFAULT_MASK = (byte) 0b11110000;

	@Override
	protected void populate() {
        FileSystem fs = FileSystem.getInstance();
        User u = new User(fs, "bbranco", "es2016ssssss", "Bernardo", DEFAULT_MASK);
        new PlainFile( fs, u.getHome(), u, "testyPlainFile", "/home/bbranco/testyApp");
        new App( fs, u.getHome(),u, "testyApp", "pt.tecnico.mydrive.service.CreateLinkService");
        new Link( fs,u.getHome(), u, "testyLink", "/home/bbranco/testyApp");
        new Directory( fs,u.getHome(), u, "porn" );
	}

    @Test
    public void successApp() {
      String[] nada= {"nada"};
      LoginService loginser = new LoginService("bbranco", "es2016ssssss");
      loginser.execute();
      ExecuteFileService execser = new ExecuteFileService( loginser.result(), "/home/bbranco/testyApp", nada);
      execser.execute();

    }
    @Test
    public void successPlainFile() {
      String[] nada= {};
      LoginService loginser = new LoginService("bbranco", "es2016ssssss");
      loginser.execute();
      ExecuteFileService execser = new ExecuteFileService( loginser.result(), "/home/bbranco/testyPlainFile", nada);
      execser.execute();

    }

    @Test ( expected = FileExecutionException.class)
    public void failDir() {
      String[] nada= {};
      LoginService loginser = new LoginService("bbranco", "es2016ssssss");
      loginser.execute();
      ExecuteFileService execser = new ExecuteFileService( loginser.result(), "/home/bbranco/porn", nada);
      execser.execute();

    }
    @Test
    public void successLink() {
      String[] nada= {"asdasd","wdsdw"};
      LoginService loginser = new LoginService("bbranco", "es2016ssssss");
      loginser.execute();
      ExecuteFileService execser = new ExecuteFileService( loginser.result(), "/home/bbranco/testyLink", nada);
      execser.execute();

    }

}
