package com.anna.lib.audio.cookie;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class SimpleCookieJar implements CookieJar {
    private final List<Cookie> allCookie = new ArrayList<>();

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        allCookie.addAll(cookies);
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        List<Cookie> list = new ArrayList<>();
        for (Cookie cookie : allCookie){
            if(cookie.matches(url)){
                list.add(cookie);
            }
        }
        return list;
    }
}
