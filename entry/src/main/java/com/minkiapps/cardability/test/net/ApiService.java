package com.minkiapps.cardability.test.net;

import com.minkiapps.cardability.test.net.model.Joke;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface ApiService {

    @GET("jokes/random")
    @Headers("Content-Type: application/json;charset=UTF-8")
    Call<Joke> fetchJokes();
}
