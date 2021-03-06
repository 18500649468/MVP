package com.example.ll.mvp.api;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by LL on 2017/7/5.
 */

public class DoubanManager {
    private static IDoubanService sDoubanService;
    public synchronized static IDoubanService creatDoubanService(){
        if (sDoubanService == null){
            Retrofit retrofit = creatRetrofit();
            sDoubanService = retrofit.create(IDoubanService.class);
        }
        return  sDoubanService;
    }

    private static Retrofit creatRetrofit() {
        OkHttpClient httpClient = new OkHttpClient().newBuilder()
                .readTimeout(20, TimeUnit.SECONDS)
                .connectTimeout(20, TimeUnit.SECONDS)
                .build();
//        OkHttpClient httpClient;
//        HttpLoggingInterceptor loggin = new HttpLoggingInterceptor();
//        loggin.setLevel(HttpLoggingInterceptor.Level.BODY);
//        httpClient = new OkHttpClient.Builder().addInterceptor(loggin).build();
        return new Retrofit.Builder().baseUrl(IDoubanService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()) // add 转换工厂用户解析json String
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(httpClient).build();

    }


}
