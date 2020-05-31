package com.example.shortapp;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Article{
    /*
    * _id
    * feedurl  视频链接
    * nickname
    * description
    * likecount
    * avatar
    * */
    @SerializedName("_id")
    public String _id;
    @SerializedName("feedurl")
    public String feedurl;
    @SerializedName("nickname")
    public String nickname;
    @SerializedName("description")
    public String description;
    @SerializedName("avatar")
    public String avatar;
    @SerializedName("likecount")
    public int likecount;

}
