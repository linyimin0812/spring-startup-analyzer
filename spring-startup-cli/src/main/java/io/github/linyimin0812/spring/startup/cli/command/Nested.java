package io.github.linyimin0812.spring.startup.cli.command;

import picocli.CommandLine;

import java.io.IOException;

/**
 * @author linyimin
 **/
@CommandLine.Command(
        name = "nested",
        mixinStandardHelpOptions = true,
        description = "Hosts more sub-subcommands"
)
public class Nested implements Runnable {

    @CommandLine.ParentCommand
    CliCommands parent;

    public void run() {
        parent.getOut().println("I'm a nested subcommand. I don't do much, but I have sub-subcommands!");
    }

    @CommandLine.Command(mixinStandardHelpOptions = true,
            description = "Multiplies two numbers.")
    public void multiply() throws IOException {
//            RecompileFileProcessor.process(client);
    }
}