package com.minkiapps.cardability.test.net;

import com.minkiapps.cardability.test.net.model.Joke;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

import java.util.List;

public interface ApiService {

    @GET("jokes/programming/random")
    @Headers("Content-Type: application/json;charset=UTF-8")
    Call<List<Joke>> fetchJokes();
}
