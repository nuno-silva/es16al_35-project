package pt.tecnico.mydrive.domain;

import pt.tecnico.mydrive.exception.PermissionDeniedException;
import pt.tecnico.mydrive.exception.IsNotCdAbleException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuestUser extends GuestUser_Base {

  private static final Logger logger = LogManager.getLogger();

  public GuestUser(FileSystem fs) {
      super();
      init(fs, "nobody", "", "Guest", (byte) 0b11111010);
  }

  @Override
  public void init(FileSystem fs, String username, String password, String name, byte mask) throws IsNotCdAbleException {
    logger.trace("Guest User init " + username);
    setUsername(username);
    super.setPassword(password);
    setName(name);
    setMask(mask);
    File home = fs.getFile("/home");
    if (home.isCdAble()) {
        home = new Directory(fs, (Directory) home, this, username);
        setHomePath(home.getFullPath());
    } else {
        setFs(null); // remove User from FileSystem
        throw new IsNotCdAbleException("'" + home.getFullPath() + " is not cdAble. Can't create user home.");
    }
    fs.addUser(this);
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

}
