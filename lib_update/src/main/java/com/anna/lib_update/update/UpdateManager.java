package com.anna.lib_update.update;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

public class UpdateManager {
    private static UpdateManager manager;
    private ThreadPoolExecutor threadPool;
    private UpdateDownloadRequest downloadRequest;

    static {
        manager = new UpdateManager();
    }
    private UpdateManager(){
        threadPool = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    }
    public static UpdateManager getInstance(){
        return manager;
    }

    public void startDownload(String downloadUrl, String localFilePath,
                              UpdateDownloadListener downloadListener){
        if (downloadRequest != null && downloadRequest.isDownloading()) {
            return;
        }
        checkLocalFilePath(localFilePath);
        if(downloadRequest == null){
            downloadRequest = new UpdateDownloadRequest(downloadUrl,localFilePath, downloadListener);
        }
        Future<?> request =  threadPool.submit(downloadRequest);
        new WeakReference<Future<?>>(request);
    }

    private void checkLocalFilePath(String localFilePath) {
        File path = new File(localFilePath.substring(0,
                localFilePath.lastIndexOf("/") + 1));
        File file = new File(localFilePath);
        if (!path.exists()) {
            path.mkdirs();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }





}
