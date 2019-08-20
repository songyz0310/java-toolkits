package com.songyz.toolkits.file.download;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class DownloadJob extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(DownloadJob.class);

    private File file; // 本地文件
    private String url = null;// 远程文件地址
    private long offset = 0; // 偏移量
    private long length = 0; // 分配给本线程的下载字节数

    private RandomAccessFile randomAccessFile;
    private CountDownLatch downLatch;
    private CloseableHttpClient httpClient;

    public DownloadJob(String url, File file, long offset, long length, CountDownLatch downLatch,
            CloseableHttpClient httpClient) {

        this.url = url;
        this.file = file;
        this.offset = offset;
        this.length = length;
        this.downLatch = downLatch;
        this.httpClient = httpClient;
    }

    public void run() {
        try {
            HttpGet httpGet = new HttpGet(this.url);
            httpGet.addHeader("Range", "bytes=" + this.offset + "-" + (this.offset + this.length - 1));
            CloseableHttpResponse response = httpClient.execute(httpGet);

            BufferedInputStream bis = new BufferedInputStream(response.getEntity().getContent());

            randomAccessFile = new RandomAccessFile(file, "rw");
            byte[] buff = new byte[1024 * 8];
            int bytesRead;
            while ((bytesRead = bis.read(buff, 0, buff.length)) != -1) {
                randomAccessFile.seek(this.offset);
                randomAccessFile.write(buff, 0, bytesRead);
                this.offset = this.offset + bytesRead;
            }
            randomAccessFile.close();
            bis.close();
        }
        catch (IOException e) {
            logger.error("DownloadJob run exception:", e);
        }
        finally {
            if (Objects.nonNull(downLatch)) {
                downLatch.countDown();
                logger.info("DownloadJob:[{}] is run finish !", downLatch.getCount());
            }
        }
    }

}
