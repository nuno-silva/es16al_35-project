package pt.tecnico.mydrive.presentation;

/**
 * ExecuteCommand. <strong>Format:</strong> <i>do</i> path [args]
 */
public class ExecuteCommand extends MyDriveCommand {
    public ExecuteCommand(Shell sh, String n) {
        super(sh, n);
    }

    public ExecuteCommand(Shell sh, String n, String h) {
        super(sh, n, h);
    }

    @Override
    void execute(String[] args) {

    }
}
