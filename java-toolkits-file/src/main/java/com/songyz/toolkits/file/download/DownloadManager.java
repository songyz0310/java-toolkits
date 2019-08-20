package com.songyz.toolkits.file.download;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.songyz.toolkits.utility.ExecutorServiceUtil;

/**
 * 大文件下载管理工具
 * 
 * @author songyz<br>
 * @createTime 2019-08-20 10:54:03
 */
public class DownloadManager {

    private static CloseableHttpClient client;
    private static final Logger logger = LoggerFactory.getLogger(DownloadManager.class);
    private static final long UNIT_SIZE = 1024 * 1024;

    static {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(300);
        client = HttpClients.custom().setConnectionManager(cm).build();
    }

    /**
     * 创建下载任务
     * 
     * @param url 下载地址
     * @param localPath 本地地址
     * @return DownloadTask
     */
    public static DownloadTask createTask(String url, String localPath) {
        if (Objects.isNull(url) || url.isEmpty() || Objects.isNull(localPath) || localPath.isEmpty())
            return null;

        return new DownloadTask(url, localPath);
    }

    /**
     * 获取下载任务的文件大小
     * 
     * @param task
     * @return
     */
    public static long getFileSize(DownloadTask task) {
        if (Objects.isNull(task))
            return 0;

        try {
            HttpURLConnection httpConnection = (HttpURLConnection) new URL(task.getUrl()).openConnection();
            httpConnection.setRequestMethod("HEAD");

            int code = httpConnection.getResponseCode();
            if (code != 200) {
                String message = httpConnection.getResponseMessage();
                logger.error("request url:{},responseCode:{},responseMessage:{}", code, message);
                return 0;
            }
            for (int i = 1;; i++) {
                String sHeader = httpConnection.getHeaderFieldKey(i);
                if (Objects.equals(sHeader, "Content-Length")) {
                    return Long.parseLong(httpConnection.getHeaderField(sHeader));
                }
            }

        }
        catch (IOException exp) {
            logger.error("get remote file size error: url:{},exp:{}", exp);
            return 0;
        }

    }

    public static boolean execute(DownloadTask task) {
        if (Objects.isNull(task))
            return false;

        String url = task.getUrl();
        long fileSize = getFileSize(task);
        if (Objects.equals(fileSize, 0L)) {
            logger.error("execute download task error: url:{},fileSize:{}", url, 0);
            return false;
        }

        Long threadCount = (fileSize / UNIT_SIZE) + (fileSize % UNIT_SIZE != 0 ? 1 : 0);

        long offset = 0;

        CountDownLatch end = new CountDownLatch(threadCount.intValue());

        File filePath = new File(task.getLocalPath());
        if (filePath.exists() == false)
            filePath.mkdirs();

        File file = new File(task.getFilePath());
        if (file.exists() == false) {
            try {
                file.createNewFile();
            }
            catch (IOException exp) {
                logger.error("create file error: url:{},filePath:{},exp:{}", url, task.getFilePath(), exp);
                return false;
            }
        }

        if (fileSize <= UNIT_SIZE) {
            ExecutorServiceUtil.execute(new DownloadJob(url, file, offset, fileSize, end, client));
        }
        else {
            for (int i = 1; i < threadCount; i++) {
                ExecutorServiceUtil.execute(new DownloadJob(url, file, offset, UNIT_SIZE, end, client));
                offset = offset + UNIT_SIZE;
            }

            if (fileSize % UNIT_SIZE != 0) {
                long size = fileSize - UNIT_SIZE * (threadCount - 1);
                ExecutorServiceUtil.execute(new DownloadJob(url, file, offset, size, end, client));
            }
        }

        try {
            end.await();
            return true;
        }
        catch (InterruptedException exp) {
            logger.error("thread await error: url:{},exp:{}", url, exp);
            return false;
        }
    }
}
