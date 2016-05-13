package pt.tecnico.mydrive.system;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import mockit.Mock;
import mockit.MockUp;
import mockit.integration.junit4.JMockit;

import java.io.File;
import java.util.List;
import java.util.ArrayList;

import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import org.jdom2.output.Format;

import pt.tecnico.mydrive.domain.FileSystem; // Mockup
import pt.tecnico.mydrive.domain.User;
import pt.tecnico.mydrive.service.*;
import pt.tecnico.mydrive.service.dto.*;
import pt.tecnico.mydrive.exception.*;

@RunWith(JMockit.class)
public class IntegrationTest extends AbstractServiceTest {

    private static final List<String> names = new ArrayList<String>();
    private static final List<String> usernames = new ArrayList<String>();
    private static final List<String> passwords = new ArrayList<String>();
    private static final String p1 = "Nuno Silva", p2 = "Jorge Heleno", p3 = "Illya Ger." , p4= "Bernardo Branco", p5 = "Gonçalo", p6 ="Pedro Carmo";
    private static final String uname1 = "nunosilva", uname2 = "gisson", uname3 = "iluxonchik" , uname4= "bernardobranco", uname5 = "goncalo1995", uname6 ="pmcarmo1";
    private static final String importFile = "other.xml";
    private static final String password1="crappyNunoP444Word69", password2="supersecurepasssword", password3="SomethingAboutTheGameAndHisD_oO", password4="bernardobranco", password5="goncalo1995", password6="pmcarmo1";

    protected void populate() { // populate Mockup
      names.add(p1); names.add(p2); names.add(p3);names.add(p4); names.add(p5); names.add(p6);
      usernames.add(uname1); usernames.add(uname2); usernames.add(uname3); usernames.add(uname4); usernames.add(uname5); usernames.add(uname6);
      passwords.add(password1); passwords.add(password2); passwords.add(password3); passwords.add(password4); passwords.add(password5); passwords.add(password6);
    }

    @Test
    public void success() throws Exception {

      LoginService logservice = new LoginService( "nobody", "" );
      logservice.execute();

      new CreateDirectoryService( "testyDir", logservice.result() ).execute();
      new CreateAppService( "testyApp", logservice.result(), "pt.tecnico.mydrive.domain.App" ).execute();
      new CreatePlainFileService( "testyPlainFile", logservice.result(), "Somethin in this file." ).execute();
      new CreateLinkService( "testyLink", logservice.result(), "/home/nobody/testyPlainFile").execute();

      ListDirectoryService listserv = new ListDirectoryService( logservice.result() );
      listserv.execute();
      List<FileDto> results = listserv.result();
      assertEquals("Too much files here",results.size(),6);

      assertEquals( "dot(.) Name incorrect", results.get(0).getName(), "." );
      assertEquals("dot(.) permissions incorrect", results.get(0).getPermissions(), (byte) 0b11111010);
      assertNotNull("dot(.) lastMod null", results.get(0).getLastMod());
      assertEquals("dot(.) fileType incorrect", results.get(0).getType(),FileDto.FileType.DIRECTORY);

      assertEquals( "parent(..) Name incorrect", results.get(1).getName(), ".." );
      assertEquals("parent(..) permissions incorrect", results.get(1).getPermissions(), (byte) 0b11111010);
      assertNotNull("parent(..) lastMod null", results.get(1).getLastMod());
      assertEquals("parent(..) fileType incorrect", results.get(1).getType(),FileDto.FileType.DIRECTORY);

      assertEquals( "Name incorrect", results.get(3).getName(), "testyDir" );
      assertEquals("permissions incorrect", results.get(3).getPermissions(), (byte) 0b11111010);
      assertNotNull("lastMod null", results.get(3).getLastMod());
      assertEquals("fileType incorrect", results.get(3).getType(),FileDto.FileType.DIRECTORY);

      assertEquals( "Name incorrect", results.get(2).getName(), "testyApp" );
      assertEquals("permissions incorrect @ RootFile", results.get(2).getPermissions(), (byte) 0b11111010 );
      assertNotNull("lastMod null", results.get(2).getLastMod());
      assertEquals("fileType incorrect", results.get(2).getType(),FileDto.FileType.APP);

      assertEquals( "Name incorrect ", results.get(5).getName(), "testyPlainFile" );
      assertEquals("permissions incorrect", results.get(5).getPermissions(), (byte) 0b11111010);
      assertNotNull("lastMod null", results.get(5).getLastMod());
      assertEquals("fileType incorrect", results.get(5).getType(),FileDto.FileType.PLAINFILE);

      assertEquals( "Name incorrect ", results.get(4).getName(), "testyLink" );
      assertEquals("permissions incorrect", results.get(4).getPermissions(), (byte) 0b11111010);
      assertNotNull("lastMod null", results.get(4).getLastMod());
      assertEquals("fileType incorrect", results.get(4).getType(),FileDto.FileType.LINK);

      new ChangeDirectoryService( logservice.result(), ".." ).execute();
      new ChangeDirectoryService( logservice.result(), "." ).execute();
      new ChangeDirectoryService( logservice.result(), "root" ).execute();
      new ChangeDirectoryService( logservice.result(), "/home/nobody" ).execute();

      ReadFileService readservice = new ReadFileService( logservice.result(), "testyPlainFile");
      readservice.execute();
      assertEquals("Content wrong", "Somethin in this file.", readservice.result());

      new WriteFileService( logservice.result(), "testyPlainFile", "Changed it!").execute();
      readservice = new ReadFileService( logservice.result(), "testyPlainFile");
      readservice.execute();
      assertEquals("Content wrong", "Changed it!", readservice.result());

      AddVariableService addvs = new AddVariableService( logservice.result(), "$USER", "nobody/testyPlainFile");
      addvs.execute();
      assertEquals( "Variable not there!", "nobody/testyPlainFile", addvs.result().get("$USER") );

      readservice = new ReadFileService( logservice.result(), "testyLink");
      readservice.execute();
      assertEquals("Content wrong", "Changed it!", readservice.result());

      new WriteFileService( logservice.result(), "testyLink", "Changed it..again!").execute();
      readservice.execute();
      assertEquals("Content wrong", "Changed it..again!", readservice.result());

    }
}
