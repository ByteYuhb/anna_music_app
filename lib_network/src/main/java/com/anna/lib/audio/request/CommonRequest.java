package com.anna.lib.audio.request;

import java.io.File;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;


public class CommonRequest {
    /**创建一个Post的请求，不包括headers
     * @return
     */
    public static Request createPostRequest(String url, RequestParams params){
        return createPostRequest(url,params,null);
    }
    /**创建一个Post的请求，包括headers
     * @param url
     * @param params body的参数集合
     * @param headers header的参数集合
     * @return
     */
    public static Request createPostRequest(String url, RequestParams params, RequestParams headers){
        FormBody.Builder mFormBodyBuilder = new FormBody.Builder();
        if(params!=null){
            for(Map.Entry<String,String> entry:params.urlParams.entrySet()){
                mFormBodyBuilder.add(entry.getKey(),entry.getValue());
            }
        }
        Headers.Builder mHeadersBuilder = new Headers.Builder();
        if(headers!=null){
            for(Map.Entry<String,String> entry:headers.urlParams.entrySet()){
                mHeadersBuilder.add(entry.getKey(),entry.getValue());
            }
        }
        return new Request.Builder()
                .url(url)
                .headers(mHeadersBuilder.build())
                .post(mFormBodyBuilder.build())
                .build();
    }

    /**创建一个不包含header的get请求
     * @return
     */
    public static Request createGetRequest(String url,RequestParams params){
        return createGetRequest(url,params,null);
    }
    /**创建一个Get的请求，包括headers
     * @return
     */
    public static Request createGetRequest(String url,RequestParams params,RequestParams headers){
        StringBuilder stringBuilder = new StringBuilder(url).append("?");
        if(params != null){
            for(Map.Entry<String,String> entry:params.urlParams.entrySet()){
                stringBuilder.append(entry.getKey()).append("=").append(entry.getValue());
            }
        }
        Headers.Builder mHeadersBuilder = new Headers.Builder();
        if(headers!=null){
            for(Map.Entry<String,String> entry:headers.urlParams.entrySet()){
                mHeadersBuilder.add(entry.getKey(),entry.getValue());
            }
        }
        return new Request.Builder()
                .url(stringBuilder.toString())
                .headers(mHeadersBuilder.build())
                .get()
                .build();
    }
    private static final MediaType FILE_TYPE = MediaType.parse("application/octet-stream");

    /**文件上传请求
     * @return
     */
    public static Request createMultiPostRequest(String url,RequestParams params){
        MultipartBody.Builder requestBuilder = new MultipartBody.Builder();
        requestBuilder.setType(MultipartBody.FORM);
        if(params != null){
            for (Map.Entry<String, Object> entry : params.fileParams.entrySet()) {
                if (entry.getValue() instanceof File) {
                    requestBuilder.addPart(Headers.of("Content-Disposition","form-data; name=\"" + entry.getKey() + "\""),
                            RequestBody.create(FILE_TYPE, (File) entry.getValue()));
                }else if (entry.getValue() instanceof String) {
                    requestBuilder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + entry.getKey() + "\""),
                            RequestBody.create(null, (String) entry.getValue()));
                }
            }
        }
        return new Request.Builder().url(url).post(requestBuilder.build()).build();
    }

    /**文件下载请求
     * @param url
     * @return
     */
    public static Request createFileDownLoadRequest(String url,RequestParams params){
        return createGetRequest(url,params);
    }
    /**文件下载请求
     * @param url
     * @return
     */
    public static Request createFileDownLoadRequest(String url){
        return createGetRequest(url,null);
    }
}
