package com.songyz.toolkits.utility;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池执行器
 * 
 * @author songyz<br>
 * @createTime 2019-08-20 14:10:30
 */
public class ExecutorServiceUtil {

    private static ThreadPoolExecutor executorPool = new ThreadPoolExecutor(//
            Runtime.getRuntime().availableProcessors(), //
            Runtime.getRuntime().availableProcessors() * 4, //
            30, TimeUnit.MINUTES, new LinkedBlockingQueue<>());

    public static void execute(Runnable command) {
        executorPool.execute(command);
    }

}
