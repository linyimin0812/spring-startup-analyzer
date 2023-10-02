package io.github.linyimin0812.spring.startup.cli.command;

import io.github.linyimin0812.spring.startup.utils.GitUtil;

/**
 * @author linyimin
 **/
public class Configuration {
    private String branch = GitUtil.currentBranch();

    private String host = "127.0.0.1";

    private Integer port = 5005;

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
}
