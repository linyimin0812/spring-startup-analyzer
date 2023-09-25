package io.github.linyimin0812.spring.startup.cli.command;

import io.github.linyimin0812.spring.startup.recompile.RecompileFileProcessor;
import io.github.linyimin0812.spring.startup.recompile.RecompileFileWatcher;
import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;
import picocli.CommandLine;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author linyimin
 **/
@CommandLine.Command(name = "", subcommands = { Nested.class, ClearScreen.class })
public class CliCommands implements Runnable {

    private PrintWriter out;
    private Terminal terminal;

    private final RecompileFileWatcher watcher;
    private final RecompileFileProcessor processor;

    public CliCommands() throws IOException {
        this.processor = new RecompileFileProcessor();
        this.watcher = new RecompileFileWatcher(this.processor);
    }

    public void setReader(LineReader reader){
        out = reader.getTerminal().writer();
    }

    public void setTerminal(Terminal terminal) {
        this.terminal = terminal;
    }

    public void run() {
        out.println(new CommandLine(this).getUsageMessage());
    }

    public PrintWriter getOut() {
        return out;
    }

    public Terminal getTerminal() {
        return terminal;
    }

    public RecompileFileProcessor getProcessor() {
        return processor;
    }

    public void close() throws IOException {
        this.terminal.close();
        this.watcher.close();
    }
}
