package com.offcn.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;


import java.io.IOException;

public class HttpClitTest {
    public static void main(String[] args) throws IOException {
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet("http://www.baidu.com");
        HttpResponse response  = httpClient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        String s = EntityUtils.toString(entity);
        System.out.println(s);
    }
}
