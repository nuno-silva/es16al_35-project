package pt.tecnico.mydrive.domain;

/*Exceptions*/
import pt.tecnico.mydrive.exception.PermissionDeniedException;
import java.lang.UnsupportedOperationException;

/*Other stuff*/
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuestUser extends GuestUser_Base {

  private static final Logger logger = LogManager.getLogger();

  public GuestUser(FileSystem fs) {
      super();
      init(fs, "nobody", "", "Guest", (byte) 0b11111010);
  }

  @Override
  public void remove() throws PermissionDeniedException {
      throw new PermissionDeniedException("can not delete GuestUser " + getUsername());
  }

  @Override
  public boolean isExpired(Session s){
    return false;
  }

  @Override
  public void setPassword(String password) throws PermissionDeniedException{
    throw new PermissionDeniedException("Cannot change GuestUser password!");
  }

  // Permission checking methods

  @Override
  public boolean hasWritePermission(File f) {
    return (f.getOwner().equals(this)) ? f.ownerCanWrite() : false;
  }

  @Override
  public boolean hasDeletePermission(File f) {
    return (f.getOwner().equals(this)) ? f.ownerCanDelete() : false;
  }

  @Override
  public void assertPassword(String password){
    if(super.isSetPassword()) throw new UnsupportedOperationException("Redefining Guest password");
    else super.setUncheckedPassword(password);
  }
}
