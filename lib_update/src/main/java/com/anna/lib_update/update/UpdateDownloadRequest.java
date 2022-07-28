package com.anna.lib_update.update;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

public class UpdateDownloadRequest implements Runnable{
    private int startPos = 0;
    private String downLoadUrl;
    private String localFilePath;

    private UpdateDownloadListener downloadListener;
    private DownloadResponseHandler downloadHandler;
    private boolean isDownloading = false;
    private int contentLength;
    public UpdateDownloadRequest(String url, String localFilePath,UpdateDownloadListener listener){
        this.downLoadUrl = url;
        this.localFilePath = localFilePath;
        this.downloadListener = listener;
        downloadHandler = new DownloadResponseHandler();
        isDownloading = true;
    }
    @Override
    public void run() {
        try {
            makeRequest();
        } catch (IOException e) {
            if (downloadHandler != null) {
                downloadHandler.sendFailureMessage(FailureCode.IO);
            }
        } catch (InterruptedException e) {
            if (downloadHandler != null) {
                downloadHandler.sendFailureMessage(FailureCode.Interrupted);
            }
        }
    }

    private void makeRequest() throws IOException, InterruptedException {
        if (!Thread.currentThread().isInterrupted()) {
            try {
                URL url = new URL(downLoadUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(5000);
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Connection", "Keep-Alive");
                urlConnection.connect();
                contentLength = urlConnection.getContentLength();
                if (!Thread.currentThread().isInterrupted()) {
                    if (downloadHandler != null) {
                        downloadHandler.sendResponseMessage(urlConnection.getInputStream());//取得与远程文件的流
                    }
                }
            }
            catch (Exception e){
                if (!Thread.currentThread().isInterrupted()) {
                    throw e;
                }
            }

        }

    }

    public boolean isDownloading() {
        return isDownloading;
    }

    public void stopDownloading() {
        isDownloading = false;
    }


    /**********************************************************
     * @文件名称：UpdateDownloadRequest.java
     * @文件作者：anna
     * @文件描述：下载消息转发器
     **********************************************************/

    public class DownloadResponseHandler{
        protected static final int SUCCESS_MESSAGE = 0;
        protected static final int FAILURE_MESSAGE = 1;
        protected static final int START_MESSAGE = 2;
        protected static final int FINISH_MESSAGE = 3;
        protected static final int NETWORK_OFF = 4;
        private Handler handler;

        @SuppressLint("HandlerLeak")
        public DownloadResponseHandler() {
            if(Looper.myLooper()!=null){
                handler = new Handler(){
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        handleSelfMessage(msg);
                    }
                };
            }
        }

        public void onFinish() {
            downloadListener.onFinished(mCompleteSize, "");
        }

        public void onFailure(FailureCode failureCode) {
            downloadListener.onFailure();
        }

        private void sendPausedMessage() {
            sendMessage(obtainMessage(PAUSED_MESSAGE, null));
        }

        private void sendProgressChangedMessage(int progress) {
            sendMessage(obtainMessage(PROGRESS_CHANGED, new Object[] { progress }));
        }

        protected void sendFailureMessage(FailureCode failureCode) {
            sendMessage(obtainMessage(FAILURE_MESSAGE, new Object[] { failureCode }));
        }

        protected void handlePausedMessage() {
            downloadListener.onPaused(progress, mCompleteSize, "");
        }

        protected void handleProgressChangedMessage(int progress) {
            downloadListener.onProgressChanged(progress, "");
        }

        protected void handleFailureMessage(FailureCode failureCode) {
            onFailure(failureCode);
        }

        protected void sendFinishMessage() {
            sendMessage(obtainMessage(FINISH_MESSAGE, null));
        }
        // Methods which emulate android's Handler and Message methods
        protected void handleSelfMessage(Message msg) {
            Object[] response;
            switch (msg.what) {
                case FAILURE_MESSAGE:
                    response = (Object[]) msg.obj;
                    handleFailureMessage((FailureCode) response[0]);
                    break;
                case PROGRESS_CHANGED:
                    response = (Object[]) msg.obj;
                    handleProgressChangedMessage(((Integer) response[0]).intValue());
                    break;
                case PAUSED_MESSAGE:
                    handlePausedMessage();
                    break;
                case FINISH_MESSAGE:
                    onFinish();
                    break;
            }
        }
        protected void sendMessage(Message msg) {
            if (handler != null) {
                handler.sendMessage(msg);
            } else {
                handleSelfMessage(msg);
            }
        }

        protected Message obtainMessage(int responseMessage, Object response) {
            Message msg = null;
            if (handler != null) {
                msg = this.handler.obtainMessage(responseMessage, response);
            } else {
                msg = Message.obtain();
                msg.what = responseMessage;
                msg.obj = response;
            }
            return msg;
        }

        private String getTwoPointFloatStr(float value) {
            DecimalFormat fnum = new DecimalFormat("0.00");
            return fnum.format(value);
        }

        private int mCompleteSize = 0;
        private int progress = 0;
        private static final int PROGRESS_CHANGED = 5;
        private static final int PAUSED_MESSAGE = 7;

        void sendResponseMessage(InputStream is) {
            RandomAccessFile randomAccessFile = null;
            mCompleteSize = 0;
            try {
                byte[] buffer = new byte[1024];
                int length = -1;
                int limit = 0;
                randomAccessFile = new RandomAccessFile(localFilePath, "rwd");
                randomAccessFile.seek(startPos);
                boolean isPaused = false;
                while ((length = is.read(buffer)) != -1) {
                    if (isDownloading) {
                        randomAccessFile.write(buffer, 0, length);
                        mCompleteSize += length;
                        if ((startPos + mCompleteSize) < (contentLength + startPos)) {
                            progress = (int) (Float.parseFloat(getTwoPointFloatStr(
                                    (float) (startPos + mCompleteSize) / (contentLength + startPos))) * 100);
                            if (limit % 30 == 0 || progress == 100) {
                                sendProgressChangedMessage(progress); //在子线程中读取流数据，后转发到主线程中去。
                            }
                        }
                        limit++;
                    } else {
                        isPaused = true;
                        sendPausedMessage();
                        break;
                    }
                }
                stopDownloading();
                if (!isPaused) {
                    sendFinishMessage();
                }
            } catch (IOException e) {
                sendPausedMessage();
                stopDownloading();
                e.printStackTrace();
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                    if (randomAccessFile != null) {
                        randomAccessFile.close();
                    }
                } catch (IOException e) {
                    stopDownloading();
                    e.printStackTrace();
                }
            }
        }
    }
    public enum FailureCode {
        UnknownHost, Socket, SocketTimeout, ConnectTimeout, IO, HttpResponse, JSON, Interrupted
    }
}
