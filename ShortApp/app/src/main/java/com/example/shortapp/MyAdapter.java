package com.example.shortapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private ListItemClickListener listItemClickListener;

    public List<Article> articleList;

    public MyAdapter(ListItemClickListener itemClickListener){
        listItemClickListener = itemClickListener;
    }

    public void setData(List<Article> articles){
        articleList = articles;
    }

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_view, parent, false);
        return new MyViewHolder(view);
    }

    public void onBindViewHolder(MyViewHolder myHolder, int pos){
        myHolder.bind(pos);
    }

    public int getItemCount(){
        return articleList != null ? articleList.size() : 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private final CircleImageView circleView;
        private final ImageView imageView;
        private final TextView nameText;
        private final TextView descText;
        private final TextView likeCount;
        private final View view;

        public MyViewHolder(View v){
            super(v);
            view = v;
            circleView = v.findViewById(R.id.circleView);
            imageView = v.findViewById(R.id.videoImage);
            nameText = v.findViewById(R.id.name);
            descText = v.findViewById(R.id.description);
            likeCount = v.findViewById(R.id.likeCount);
            v.setOnClickListener(this::onClick);
        }

        @SuppressLint("CheckResult")
        public void bind(int i){
            Article item = articleList.get(i);
            RequestOptions options = new RequestOptions();
            options.centerCrop()
                    .placeholder(R.drawable.image_default)
                    .error(R.drawable.error);
            Glide.with(view).load(item.avatar).apply(options).into(circleView);
            Glide.with(view).load(item.feedurl).apply(options).into(imageView);
            nameText.setText(item.nickname);
            descText.setText(item.description);
            likeCount.setText("♥ " + String.valueOf(item.likecount));
        }

        public void onClick(View view){
            if(listItemClickListener != null){
                listItemClickListener.onItemClick(getAdapterPosition());
            }
        }
    }

    public interface ListItemClickListener{
        void onItemClick(int clickItemIndex);
    }
}
