package io.github.linyimin0812.spring.startup.utils;

import io.github.linyimin0812.spring.startup.constant.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author linyimin
 **/
public class ShellUtil {
    public static Result execute(String[] cmdArray) {
        return execute(cmdArray, false);
    }

    public static Result execute(String[] cmdArray, boolean print) {
        try {

            Process process = Runtime.getRuntime().exec(cmdArray);

            int code = process.waitFor();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(code == 0 ? process.getInputStream() : process.getErrorStream()))) {

                StringBuilder sb = new StringBuilder();

                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\n');
                }

                String content = !sb.isEmpty() ? sb.substring(0, sb.length() - 1) : Constants.EMPTY_STRING;

                if (print) {
                    System.out.println(content);
                }

                return new Result(code, content);
            }

        } catch (IOException | InterruptedException e) {
            return new Result(-1, e.getMessage());
        }
    }

    public static class Result {

        public int code;
        public String content;

        public Result(int code, String content) {
            this.code = code;
            this.content = content;
        }

        @Override
        public String toString() {
            return "Result{" +
                    "code=" + code +
                    ", content='" + content + '\'' +
                    '}';
        }
    }

    public static void main(String[] args) {

        Result result = execute(new String[] {"git", "te"}, false);

        System.out.println(result);
    }
}
