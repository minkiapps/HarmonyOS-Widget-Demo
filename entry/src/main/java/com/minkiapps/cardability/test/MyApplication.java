package com.minkiapps.cardability.test;

import com.minkiapps.cardability.test.net.ApiService;
import ohos.aafwk.ability.AbilityPackage;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

import java.util.concurrent.TimeUnit;

public class MyApplication extends AbilityPackage {

    private static ApiService apiService = null;

    @Override
    public void onInitialize() {
        super.onInitialize();
    }

    public synchronized static ApiService getApiService() {
        if(apiService == null) {
            final HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            final OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(60L, TimeUnit.SECONDS)
                    .addInterceptor(loggingInterceptor)
                    .build();
            final Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.chucknorris.io/")
                    .addConverterFactory(MoshiConverterFactory.create()).client(client)
                    .build();
            apiService = retrofit.create(ApiService.class);
        }

        return apiService;
    }
}
