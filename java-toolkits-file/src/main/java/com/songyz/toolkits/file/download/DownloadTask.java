package com.songyz.toolkits.file.download;

import java.util.Objects;

class DownloadTask {

    private String url;// 待下载的文件

    private String localPath;

    private String filePath;

    public DownloadTask(String url, String localPath) {
        this.url = url;
        this.localPath = localPath;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getFilePath() {
        if (Objects.isNull(filePath)) {
            String fileName = url.substring(url.lastIndexOf("/") + 1, url.length()).replace("%20", " ");

            filePath = localPath + fileName;
        }
        return filePath;

    }

}
