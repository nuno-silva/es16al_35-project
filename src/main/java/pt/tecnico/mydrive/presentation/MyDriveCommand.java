package pt.tecnico.mydrive.presentation;

public abstract class MyDriveCommand extends Command {
  public MyDriveCommand(Shell sh, String n) { super(sh, n); }
  public MyDriveCommand(Shell sh, String n, String h) { super(sh, n, h); }
}
