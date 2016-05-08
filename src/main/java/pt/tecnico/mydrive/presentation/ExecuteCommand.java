package pt.tecnico.mydrive.presentation;

import pt.tecnico.mydrive.service.ExecuteAppService;

import java.util.Arrays;

/**
 * ExecuteCommand. <strong>Format:</strong> <i>do</i> path [args]
 */
public class ExecuteCommand extends MyDriveCommand {
    private static final String SHELL_STR = "do";

    private String path;
    private String[] args;
    public ExecuteCommand(Shell sh) { super(sh, SHELL_STR, "execute an App file"); }

    @Override
    void execute(String[] args) {
        if (args.length < 1) {
            throw new RuntimeException("USAGE: " + SHELL_STR + " path [args]");
        }

        path = args[0];
        this.args = Arrays.copyOfRange(args, 1, args.length);

        // TODO: treat invalid token case
        new ExecuteAppService(((MydriveShell)shell()).getToken(), this.path, this.args);
    }
}
