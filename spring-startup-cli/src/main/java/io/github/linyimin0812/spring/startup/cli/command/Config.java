package io.github.linyimin0812.spring.startup.cli.command;

import io.github.linyimin0812.spring.startup.utils.GitUtil;
import io.github.linyimin0812.spring.startup.utils.StringUtil;
import org.jline.reader.MaskingCallback;
import picocli.CommandLine;

/**
 * @author linyimin
 **/
@CommandLine.Command(
        name = "config",
        description = "configuration setting, use 'config -h' for more information",
        version = "1.0",
        mixinStandardHelpOptions = true
)
public class Config implements Runnable {

    @CommandLine.ParentCommand
    CliCommands parent;

    @CommandLine.Command(
            version = "1.0",
            description = "config deployment branch, remote debug jvm host and port",
            mixinStandardHelpOptions = true
    )
    public void set() {

        setBranch();
        setHost();
        setPort();

        System.out.print("[INFO] configuration setting success. configuration - ");
        System.out.printf("branch: %s, host: %s, port: %s\n", parent.getBranch(), parent.getHost(), parent.getPort());

    }

    @CommandLine.Command(
            version = "1.0",
            description = "view config information",
            mixinStandardHelpOptions = true
    )
    public void view() {
        System.out.printf("[INFO] configuration - branch: %s, host: %s, port: %s", parent.getBranch(), parent.getHost(), parent.getPort());
    }

    @Override
    public void run() {
        System.out.println("configuration setting, use 'config -h' for more information");
    }

    private void setBranch() {

        String line = parent.getReader().readLine(currentBranchPrompt(), null, (MaskingCallback) null, null);
        if (StringUtil.isNotEmpty(line)) {
            this.parent.setBranch(line);
        }
    }

    private void setHost() {
        String line = parent.getReader().readLine("jvm host (12.0.0.1) : ", null, (MaskingCallback) null, null);
        if (StringUtil.isNotEmpty(line)) {
            this.parent.setHost(line);
        }
    }

    private void setPort() {
        String line = parent.getReader().readLine("jvm port (5005) : ", null, (MaskingCallback) null, null);
        if (StringUtil.isNotEmpty(line)) {
            this.parent.setPort(Integer.parseInt(line));
        }
    }

    private String currentBranchPrompt() {

        String prompt = "deployment branch";

        String currentBranch = GitUtil.currentBranch();
        if (StringUtil.isNotEmpty(currentBranch)) {
            return prompt + " (" + currentBranch + ") : ";
        }

        return prompt + " : ";
    }
}
