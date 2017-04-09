package com.example.muhammadfarhanbashir.gharkhana.helpers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by fbashir on 8/17/2016.
 */

public class RestClient {

    private static Gson gson;
    private static Retrofit retrofit;
    private static OkHttpClient.Builder httpClient;

    public RestClient(String END_POINT_STRING)
    {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient = new OkHttpClient.Builder();
        httpClient.connectTimeout(60, TimeUnit.SECONDS);
        httpClient.readTimeout(60, TimeUnit.SECONDS);

        //httpClient.cache(new Cache(context.getCacheDir(), 10 * 1024 * 1024));
        httpClient.addInterceptor(logging);
        gson = new GsonBuilder().setLenient().create();

        retrofit = new Retrofit.Builder().baseUrl(END_POINT_STRING)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient.build())
                .build();
    }


    public Retrofit getService() {
        return retrofit;
    }

}
