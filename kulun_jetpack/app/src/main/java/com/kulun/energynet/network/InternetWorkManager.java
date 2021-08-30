package com.kulun.energynet.network;

import android.content.Context;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

//1单例；2初始化retrofit和okhttp;3调用retrofit.create()
public class InternetWorkManager {
    private volatile static InternetWorkManager myretrofit;
    private static Retrofit retrofit;
    private volatile static RetrofitApi request;
    public static InternetWorkManager getMyretrofit(){
        if (myretrofit == null){
            synchronized (InternetWorkManager.class){
                if (myretrofit == null){
                    myretrofit = new InternetWorkManager();
                }
            }
        }
        return myretrofit;
    }
    public static RetrofitApi getRequest(){
        if (request == null){
            synchronized (RetrofitApi.class){
                if (request == null){
                    request = retrofit.create(RetrofitApi.class);
                }
            }
        }
        return request;
    }
    public void init(Context activity) {
        retrofit = new Retrofit.Builder().
                addCallAdapterFactory(RxJava3CallAdapterFactory.create()).
                addConverterFactory(GsonConverterFactory.create()).
                client(
                        new OkHttpClient.Builder()
                                .addInterceptor(new HttpLogInterceptor(activity))
                                .sslSocketFactory(createSSLSocketFactory(), new TrustAllCerts())
                                .hostnameVerifier(new TrustAllHostnameVerifier())
                                .build()
                ).baseUrl(API.baseurl).build();
    }

    private SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllCerts()}, new SecureRandom());
            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ssfFactory;
    }

    private class TrustAllCerts implements X509TrustManager {
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    private class TrustAllHostnameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }
}
