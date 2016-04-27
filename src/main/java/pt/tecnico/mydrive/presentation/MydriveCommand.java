package pt.tecnico.mydrive.presentation;

public abstract class MydriveCommand extends Command {
  public MydriveCommand(Shell sh, String n) { super(sh, n); }
  public MydriveCommand(Shell sh, String n, String h) { super(sh, n, h); }
}
