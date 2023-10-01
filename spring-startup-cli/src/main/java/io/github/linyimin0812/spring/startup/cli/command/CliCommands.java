package io.github.linyimin0812.spring.startup.cli.command;

import io.github.linyimin0812.spring.startup.recompile.ModifiedFileProcessor;
import io.github.linyimin0812.spring.startup.recompile.ModifiedFileWatcher;
import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;
import picocli.CommandLine;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author linyimin
 **/
@CommandLine.Command(
        name = "",
        subcommands = {
                ClearScreen.class,
                Reload.class,
                Config.class
        }
)
public class CliCommands implements Runnable {

    private LineReader reader;
    private PrintWriter out;

    private final Configuration configuration = new Configuration();

    private final ModifiedFileWatcher watcher;
    private final ModifiedFileProcessor processor;

    public CliCommands() throws IOException {
        this.processor = new ModifiedFileProcessor();
        this.watcher = new ModifiedFileWatcher(this.processor);
    }

    public void setReader(LineReader reader){
        out = reader.getTerminal().writer();
        this.reader = reader;
    }

    public void run() {
        out.println(new CommandLine(this).getUsageMessage());
    }

    public Terminal getTerminal() {
        return this.reader.getTerminal();
    }

    public LineReader getReader() {
        return this.reader;
    }

    public ModifiedFileProcessor getProcessor() {
        return processor;
    }

    public String getBranch() {
        return configuration.getBranch();
    }

    public void setBranch(String branch) {
        this.configuration.setBranch(branch);
    }

    public String getHost() {
        return configuration.getHost();
    }

    public void setHost(String host) {
        this.configuration.setHost(host);
    }

    public Integer getPort() {
        return configuration.getPort();
    }

    public void setPort(Integer port) {
        this.configuration.setPort(port);
    }

    public void close() throws IOException {
        this.out.close();
        this.watcher.close();
        this.reader.getTerminal().close();
    }
}
