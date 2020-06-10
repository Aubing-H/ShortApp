package com.example.shortapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

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
        recyclerView = findViewById(R.id.recyclerView); //绑定视图中的recycleView
        MyAdapter.ListItemClickListener listItemClickListener = clickItemIndex -> {
            // 实现接口，跳转到新的播放页面, 并传入相关参数
            Intent intent = new Intent(MainActivity.this, VideoPlayer.class);
            intent.putExtra("videoUrl", articles.get(clickItemIndex).feedurl);
            intent.putExtra("imageUrl", articles.get(clickItemIndex).avatar);
            startActivity(intent);
        };
        myAdapter = new MyAdapter(listItemClickListener); //构造myAdapter对象
        articles = new ArrayList<>();
        recyclerView.setAdapter(myAdapter); //绑定适配器
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); //设置2列的网格布局
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) { //滑动事件监听
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == RecyclerView.SCROLL_STATE_IDLE){
                    // 滑动结束
                    int pos = ((GridLayoutManager)recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                    if(pos == articles.size() - 1)
                        Toast.makeText(MainActivity.this, "没有更多视频啦~", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //获取数据
        getData();
        Log.d(MAT, "finish");
    }

    private void getData(){
        try{
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(HttpUrl.get("https://beiyou.bytedance.com/")) //json数据根目录
                    .addConverterFactory(GsonConverterFactory.create()) //使用gson解析数据
                    .build();
            ApiService apiService = retrofit.create(ApiService.class); //retrofit进行接口重构
            apiService.getArticles().enqueue(new Callback<List<Article>>() { //请求接口实现
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
