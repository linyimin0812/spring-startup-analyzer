package io.github.linyimin0812.spring.startup.utils;

import io.github.linyimin0812.spring.startup.constant.Constants;

/**
 * @author linyimin
 **/
public class GitUtil {
    public static boolean isGit() {
        ShellUtil.Result result = ShellUtil.execute(new String[] { "git", "status" });
        return result.code == 0;
    }

    public static String currentBranch() {
        ShellUtil.Result result = ShellUtil.execute(new String[] {"git", "branch", "--show-current"});
        if (result.code == 0) {
            return result.content;
        }

        return Constants.EMPTY_STRING;
    }
}
