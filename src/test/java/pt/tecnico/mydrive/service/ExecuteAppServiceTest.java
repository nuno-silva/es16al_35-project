package pt.tecnico.mydrive.service;


import org.junit.Test;
import pt.tecnico.mydrive.domain.*;
import pt.tecnico.mydrive.exception.AppExecutionExcepiton;
import pt.tecnico.mydrive.exception.MyDriveException;
import pt.tecnico.mydrive.exception.NotJavaFullyQualifiedNameException;

public class ExecuteAppServiceTest extends AbstractServiceTest {
    private final String EXAMPLE_APP = "System.out.println";
    private User theGame;
    @Override
    protected void populate() {
        FileSystem fs = FileSystem.getInstance();
        theGame = new User(fs, "TheGame", "Dr.Dresssss", "The Protegé Of The D.R.E.", (byte)0b11111111);
        File f = fs.getFile("/home/TheGame", theGame);
       // new App(fs, f, fs.getSuperUser(), "hello" , EXAMPLE_APP);
        new App(fs, f, fs.getSuperUser(), "except" , "pt.tecnico.mydrive.exception.MyDriveException");
    }

    @Test (expected = NotJavaFullyQualifiedNameException.class)
    public void failCreate() {
        FileSystem fs = FileSystem.getInstance();
        Session s = new Session(fs, theGame, "Dr.Dresssss");
        File f = fs.getFile("/home/TheGame", theGame);
        new App(fs, f, fs.getSuperUser(), "except" , "except");
    }
    // NotJavaFullyQualifiedNameException:
}
