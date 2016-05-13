package pt.tecnico.mydrive.presentation;

import java.util.Map;
import pt.tecnico.mydrive.service.AddVariableService;

/**
 * Environment Command. <strong>Format:</strong> <i>env</i> [name [value]]
 * Creates or changes the value of the environment variable with the given name.
 */
public class EnvironmentCommand extends MyDriveCommand {
    public EnvironmentCommand(MydriveShell sh) {
        super(sh, "env", "Create environment variable in session");
    }

    @Override
    public void execute(String[] args) {
        MydriveShell sh = (MydriveShell) shell();
        if(args.length == 0) { // print all vars
            AddVariableService av = new AddVariableService(sh.getActiveToken());
            av.execute();
            Map<String, String> map = av.result();


            for (Map.Entry<String, String> v : map.entrySet()) {
                printVar(v.getKey(), v.getValue());
            }
        } else if(args.length == 1) { // print var
            AddVariableService av = new AddVariableService(sh.getActiveToken());
            av.execute();
            Map<String, String> map = av.result();
            String name = args[0];
            if(map.containsKey(name)) {
                printVar(name, map.get(name));
            } else {
                sh.println(name + " is undefined");
            }
        } else if(args.length == 2) { // (re)define var
            new AddVariableService(sh.getActiveToken(), args[0], args[1]).execute();
        } else {
            throw new RuntimeException("USAGE: "+name()+" [name [value]]");
        }
    }

    private void printVar(String name, String val) {
        shell().println(name + " = " + val);
    }
}
