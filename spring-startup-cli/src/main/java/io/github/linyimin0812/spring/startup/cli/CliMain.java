package io.github.linyimin0812.spring.startup.cli;


import io.github.linyimin0812.spring.startup.cli.command.CliCommands;
import io.github.linyimin0812.spring.startup.constant.Constants;
import io.github.linyimin0812.spring.startup.utils.GitUtil;
import io.github.linyimin0812.spring.startup.utils.StringUtil;
import org.fusesource.jansi.AnsiConsole;
import org.jline.builtins.ConfigurationPath;
import org.jline.console.SystemRegistry;
import org.jline.console.impl.Builtins;
import org.jline.console.impl.SystemRegistryImpl;
import org.jline.reader.*;
import org.jline.reader.impl.DefaultParser;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import picocli.CommandLine;
import picocli.shell.jline3.PicocliCommands;
import picocli.shell.jline3.PicocliCommands.PicocliCommandsFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;

import static io.github.linyimin0812.spring.startup.constant.Constants.OUT;

public class CliMain {

    private static CliCommands commands;
    private static LineReader reader;
    private static SystemRegistry systemRegistry;
    private static final Parser parser = new DefaultParser();

    public static void main(String[] args) {

        AnsiConsole.systemInstall();
        try {

            initCommands();

            // only for run mvn -Pnative -Dagent exec:exec@java-agent
            if (args.length > 0 && "exec:exec@java-agent".equals(args[0])) {
                forNativeTracingAgent();
                return;
            }

            executeCommand();

        } catch (Throwable t) {
            OUT.println(t.getMessage());
        } finally {
            AnsiConsole.systemUninstall();
        }
    }

    private static void  initCommands() throws IOException {

        Path path = Paths.get(System.getProperty(Constants.USER_DIR));
        Path userConfigPath = Paths.get(System.getenv(Constants.PATH));

        // set up JLine built-in commands
        Builtins builtins = new Builtins(new HashSet<>(Collections.singleton(Builtins.Command.HISTORY)), () -> path, new ConfigurationPath(path, userConfigPath), null);

        // set up picocli commands
        commands = new CliCommands();

        PicocliCommandsFactory factory = new PicocliCommandsFactory();

        PicocliCommands picocliCommands = new PicocliCommands(new CommandLine(commands, factory));
        picocliCommands.name(Constants.CLI_NAME);

        Terminal terminal = TerminalBuilder.builder().build();

        systemRegistry = new SystemRegistryImpl(parser, terminal, () -> path, null);
        systemRegistry.setCommandRegistries(builtins, picocliCommands);

        reader = LineReaderBuilder.builder()
                .terminal(terminal)
                .completer(systemRegistry.completer())
                .parser(parser)
                .variable(LineReader.LIST_MAX, 50)   // max tab completion candidates
                .build();

        builtins.setLineReader(reader);
        commands.setReader(reader);
        factory.setTerminal(terminal);
    }

    private static void executeCommand() throws IOException {
        // start the shell and process input until the user quits with Ctrl-D or input exit command
        while (true) {
            try {
                systemRegistry.cleanUp();
                String line = reader.readLine(prompt(), null, (MaskingCallback) null, null);

                systemRegistry.execute(line);

                if ("exit".equalsIgnoreCase(line)) {
                    break;
                }

            } catch (UserInterruptException | EndOfFileException e) {
                commands.close();
                return;
            } catch (Exception e) {
                systemRegistry.trace(e);
            }
        }
    }

    public static String prompt() {

        String currentBranch = GitUtil.currentBranch();

        if (StringUtil.isEmpty(currentBranch)) {
            return Constants.CLI_NAME + " > ";
        }

        return Constants.CLI_NAME + " (" + currentBranch + ") > ";
    }

    private static void forNativeTracingAgent() {

        Path file = Paths.get(System.getProperty(Constants.USER_DIR), Constants.SOURCE_DIR, "forNativeTracingAgent.java");

        new Thread(() -> {
            try {

                Files.deleteIfExists(file);

                Files.createFile(file);

                Thread.sleep(1000);

            } catch (IOException | InterruptedException ignored) {
            } finally {
                try {
                    commands.close();
                    Files.deleteIfExists(file);

                } catch (IOException ignored) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }
}