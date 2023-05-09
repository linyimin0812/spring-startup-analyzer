package io.github.linyimin0812.profiler.common.upload;

import io.github.linyimin0812.profiler.common.logger.LogFactory;
import io.github.linyimin0812.profiler.common.settings.ProfilerSettings;
import okhttp3.*;
import org.slf4j.Logger;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static java.net.HttpURLConnection.HTTP_OK;

/**
 * @author linyimin
 * @date 2023/04/30 15:02
 * @description upload async profiler file
 **/
public class FileUploader {

    private static final Logger logger = LogFactory.getStartupLogger();

    private static final String ENDPOINT = ProfilerSettings.getProperty("java-profiler.jaeger.ui.endpoint");

    public static void main(String[] args) {
        upload("/Users/linyimin/java-profiler-boost/output/global-ug-clc-stateengine-s-20230428101928.html");
    }

    public static void upload(String filePath) {

        File file = new File(filePath);
        if (!file.exists()) {
            throw new IllegalArgumentException(filePath + " is not exist.");
        }

        try {
            byte[] content = Files.readAllBytes(file.toPath());
            upload(file.getName(), content);
        } catch (IOException e) {
            logger.error("readAllBytes from {} error.", filePath, e);
        }
    }

    public static void upload(String fileName, String content) {
        upload(fileName, content.getBytes(StandardCharsets.UTF_8));
    }

    private static void upload(String fileName, byte[] content) {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("text/plain; charset=utf-8");

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", fileName, RequestBody.create(content, mediaType))
                .build();

        Request request = new Request.Builder().url(ENDPOINT + "/api/file/receive").post(requestBody).build();

        try (Response response = client.newCall(request).execute()) {

            if (response.code() == HTTP_OK) {
                logger.info("upload {} success.", fileName);
            } else {
                logger.error("upload {} error. code: {}", fileName, response.code());
            }
        } catch (Exception e) {
            logger.error("upload {} error.", fileName, e);
        }
    }
}
