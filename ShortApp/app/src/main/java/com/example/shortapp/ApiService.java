package com.example.shortapp;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;


public interface ApiService {
    // https://beiyou.bytedance.com/api/invoke/video/invoke/video
    @GET("api/invoke/video/invoke/video") //请求数据子目录
    Call<List<Article>> getArticles();
}
