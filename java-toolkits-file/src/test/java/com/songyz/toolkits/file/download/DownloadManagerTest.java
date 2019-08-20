package com.songyz.toolkits.file.download;

import org.junit.jupiter.api.Test;

public class DownloadManagerTest {

    @Test
    void testExecute() {
        String url = "https://dev.1stcs.cn/files/v2/100015/ticket_flow/2060500002/ab72873809804d088bd85a4a19933b90-i1stcs_20190628_195313.mp4";
        boolean flag = DownloadManager.execute(DownloadManager.createTask(url, "D://test//"));
        System.out.println(flag);

        System.out.println("1122");
    }

}
