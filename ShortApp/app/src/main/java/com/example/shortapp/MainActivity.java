package com.example.shortapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;

import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import okhttp3.HttpUrl;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String MAT = "main_activity";
    private RecyclerView recyclerView;
    private List<Article> articles;
    private MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        MyAdapter.ListItemClickListener listItemClickListener = clickItemIndex -> {
            // 跳转到新的播放页面, 并传入相关参数
            Intent intent = new Intent(MainActivity.this, VideoPlayer.class);
            intent.putExtra("videoUrl", articles.get(clickItemIndex).feedurl);
            startActivity(intent);
        };
        myAdapter = new MyAdapter(listItemClickListener);
        articles = new ArrayList<>();
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        //获取数据
        getData();
        Log.d(MAT, "finish");
    }

    private void getData(){
        try{
            Retrofit.Builder builder = new Retrofit.Builder();
            builder.baseUrl(HttpUrl.get("https://beiyou.bytedance.com/"));
            builder.addConverterFactory(GsonConverterFactory.create());
            Retrofit retrofit = builder.build();
            ApiService apiService = retrofit.create(ApiService.class);
            apiService.getArticles().enqueue(new Callback<List<Article>>() {
                @Override
                public void onResponse(Call<List<Article>> call, Response<List<Article>> response) {
                    if(response.body() != null){
                        articles = response.body();
                        if(articles != null && articles.size() > 0){
                            myAdapter.setData(articles);
                            myAdapter.notifyDataSetChanged();
                        }
                    }
                }
                @Override
                public void onFailure(Call<List<Article>> call, Throwable t) {
                    Log.d(MAT, "net failure: "+ t.getMessage());
                }
            });
        }catch(Exception e){
            Log.d(MAT, "Exception: " + e.toString());
        }
    }
}
