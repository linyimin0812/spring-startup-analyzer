package io.github.linyimin0812.spring.startup.cli.command;

import io.github.linyimin0812.spring.startup.constant.Constants;
import io.github.linyimin0812.spring.startup.jdwp.JDWPClient;
import io.github.linyimin0812.spring.startup.utils.GitUtil;
import io.github.linyimin0812.spring.startup.utils.ModuleUtil;
import io.github.linyimin0812.spring.startup.utils.ShellUtil;
import io.github.linyimin0812.spring.startup.utils.StringUtil;
import picocli.CommandLine;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author linyimin
 **/
@CommandLine.Command(
        name = "reload",
        mixinStandardHelpOptions = true,
        description = "reload modified classes",
        version = "1.0"
)
public class Reload implements Runnable {

    @CommandLine.ParentCommand
    CliCommands parent;

    private boolean isMerge = false;

    @Override
    public void run() {

        System.out.printf("[INFO] branch: %s, remote jvm host: %s, remote jvm port: %s\n", parent.getBranch(), parent.getHost(), parent.getPort());

        String originBranch = GitUtil.currentBranch();

        try {

            if (!merge(originBranch)) {
                System.out.println("[ERROR] merge branch error. try to resolve the problem and execute reload command again");
                return;
            }
            if(!compile()) {
                System.out.println("[ERROR] compile branch error. try to resolve the problem and execute reload command again");
                return;
            }
            reload();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            rollback(originBranch);
        }
    }

    private boolean merge(String originBranch) {

        System.out.println("[INFO] start merge branch ...");

        if (!GitUtil.isGit()) {
            System.out.println("[WARN] project is not managed by git, skip the merge process, use the code directly");
            return true;
        }

        String deployedBranch = this.parent.getBranch();
        if (StringUtil.isEmpty(deployedBranch)) {
            System.out.println("[WARN] branch is empty, skip the merge process, use the code directly");
            return true;
        }

        if (deployedBranch.equals(originBranch)) {
            return true;
        }

        // commit the current branch
        ShellUtil.execute(new String[] { "git", "add", "." });
        ShellUtil.execute(new String[] { "git", "commit", "-m", "deployment: for reload" });

        // checkout deployed branch
        ShellUtil.execute(new String[] { "git", "pull", deployedBranch });
        ShellUtil.execute(new String[] { "git", "checkout", deployedBranch });

        // merge branch
        ShellUtil.Result result = ShellUtil.execute(new String[] { "git", "merge", originBranch });
        System.out.println(result.content);

        this.isMerge = result.code == 0;

        return this.isMerge;
    }

    private boolean compile() {
        Path home = Paths.get(System.getProperty(Constants.USER_DIR));
        return ModuleUtil.compile(home);
    }

    private void rollback(String originBranch) {

        ShellUtil.execute(new String[] { "git", "checkout", originBranch });

        if (this.isMerge) {
            ShellUtil.execute(new String[] { "git", "checkout", originBranch });
            ShellUtil.execute(new String[] { "git", "reset", "--mixed", "HEAD^" });
        }
        this.isMerge = false;
    }

    private void reload() throws IOException {
        JDWPClient client = null;
        try {
            String host = this.parent.getHost();
            Integer port = this.parent.getPort();

            client = new JDWPClient(host, port);
            client.start();
            parent.getProcessor().process(client);

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }
}
