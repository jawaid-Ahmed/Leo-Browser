package com.jawaid.liobrowser.Interfaces;

import com.jawaid.liobrowser.models.News;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("top-headlines")
    Call<News> getNews(
            @Query("country") String country,
            @Query("apiKey") String apiKey,
            @Query("language") String language

    );

    @GET("everything")
    Call<News> getEverything(
            @Query("q") String q,
            @Query("language") String language,
            @Query("sortby") String sortby,
            @Query("apiKey") String apikey


    );

}
