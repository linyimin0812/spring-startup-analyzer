package io.github.linyimin0812.spring.startup.cli.command;

import org.jline.utils.InfoCmp;
import picocli.CommandLine;

import java.util.concurrent.Callable;

/**
 * @author linyimin
 **/
@CommandLine.Command(
        name = "clear",
        mixinStandardHelpOptions = true,
        description = "Clears the screen", version = "1.0"
)
public class ClearScreen implements Callable<Void> {

    @CommandLine.ParentCommand
    CliCommands parent;

    ClearScreen() {}

    public Void call() {
        if (parent.getTerminal() != null) { parent.getTerminal().puts(InfoCmp.Capability.clear_screen); }
        return null;
    }
}
