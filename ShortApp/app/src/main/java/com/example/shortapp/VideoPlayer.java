package com.example.shortapp;

import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import de.hdodenhof.circleimageview.CircleImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.IOException;

public class VideoPlayer extends AppCompatActivity {

    private static final String TAG = "videoPlayer";

    private static MyFrameLayout myFrameLayout;
    private static SurfaceView surfaceView;
    private static MediaPlayer mediaPlayer;
    private static CircleImageView userImage;
    private static ImageView likeImage;
    private static String videoUrl;
    private static boolean likeImageRed;
    private static int prgPosition;

    private static final String PRG = "progress";

    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        // 获取bundle数据
        try{
            if(bundle == null){
                prgPosition = 0;
                likeImageRed = false;
                Log.d(TAG, "## " + String.valueOf(prgPosition));
            }else{
                prgPosition = bundle.getInt(PRG, 0);
                likeImageRed = bundle.getBoolean("likeImage", false);
                Log.d(TAG, String.valueOf(prgPosition));
            }
        }catch(Exception e){
            Log.d(TAG, e.toString());
        }
        videoUrl = getIntent().getStringExtra("videoUrl");
        String imageUrl = getIntent().getStringExtra("imageUrl");
        // 设置界面布局并找到组件
        setContentView(R.layout.video_player);
        surfaceView = findViewById(R.id.surfaceView);
        userImage = findViewById(R.id.userImage);
        likeImage = findViewById(R.id.likeImage);
        myFrameLayout = findViewById(R.id.frame_layout);
        LinearLayout linearLayout = findViewById(R.id.linear_layout);
        ViewGroup viewGroup = (ViewGroup)linearLayout.getParent();

        linearLayout.setMinimumHeight(viewGroup.getHeight() / 2);
        // 加载用户头像，同时设置网络出故障时的错误图片和加载图片
        RequestOptions options = new RequestOptions();
        options.centerCrop()
                .placeholder(R.drawable.my_default) //图片加载中
                .error(R.drawable.net_error); //出现故障设置error图片
        Glide.with(getApplicationContext()).load(imageUrl).apply(options).into(userImage);

        playVideo(); //媒体播放的相关设置
        // 点击事件中需要重写的单击事件和双击事件
        MyFrameLayout.MyClickListener.MyCallBack myCallBack = new MyFrameLayout.MyClickListener.MyCallBack() {
            @Override
            public void singleClick() { //单击设置暂停或开始
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                }else {
                    mediaPlayer.start();
                }
            }
            @Override
            public void doubleClick() { //双击更改爱心的颜色
                if (!likeImageRed){
                    likeImage.setImageDrawable(getResources().getDrawable(R.drawable.red_like));
                    likeImageRed = !likeImageRed;
                }
            }
        };

        myFrameLayout.setOnTouchListener(new MyFrameLayout.MyClickListener(myCallBack)); //设置点击响应事件，设置ImageView相关内容
        myFrameLayout.setOnClickListener(myCallBack); //监听双击事件，添加爱心效果状态

        likeImage.setOnClickListener(view->{
            if (likeImageRed)
                likeImage.setImageDrawable(getResources().getDrawable(R.drawable.white_heart));
            else
                likeImage.setImageDrawable(getResources().getDrawable(R.drawable.red_like));
            likeImageRed = !likeImageRed;
        });
    }

    public void onRestart(){
        super.onRestart();
        playVideo();
    }

    public void onStart(){
        super.onStart();
        mediaPlayer.start();
        if (likeImageRed)
            likeImage.setImageDrawable(getResources().getDrawable(R.drawable.red_like));
    }

    public void onResume(){
        super.onResume();
    }

    protected void onPause(){
        super.onPause();
        if (mediaPlayer != null){
            prgPosition = mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();
        }
    }

    protected void onStop(){
        super.onStop();
    }

    protected void onSaveInstanceState(Bundle bundle){
        super.onSaveInstanceState(bundle);
        bundle.putInt(PRG, prgPosition);
        bundle.putBoolean("likeImage", likeImageRed);
    }

    public void playVideo(){
        if(mediaPlayer == null){
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                   if(!mp.isPlaying())
                        mediaPlayer.seekTo(prgPosition);
                        mp.start();
                }
            });
            Log.d(TAG, "OnCompletionListener");
        }
        try{
            mediaPlayer.setDataSource(getApplicationContext(), Uri.parse(videoUrl));
            SurfaceHolder surfaceHolder = surfaceView.getHolder();
            surfaceHolder.addCallback(new MyCallback());
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mediaPlayer -> mediaPlayer.setLooping(true));
            mediaPlayer.setOnVideoSizeChangedListener((mediaPlayer, i, i1) -> changeVideoSize(mediaPlayer));
        }catch(IOException e){
            Log.d(TAG, "IOException: " + e.toString());
        }
    }

    public void changeVideoSize(MediaPlayer mediaPlayer){
        int videoWidth = mediaPlayer.getVideoWidth();
        int videoHeight = mediaPlayer.getVideoHeight();

        ViewGroup vg = (ViewGroup)surfaceView.getParent();
        int vgw = vg.getWidth();
        int vgh = vg.getHeight();
        FrameLayout.LayoutParams lyp;

        if ((float)vgw/vgh > (float)videoWidth/videoHeight){
            int actualWidth = (int)Math.ceil((float)videoWidth * vgh / videoHeight);
            lyp = new FrameLayout.LayoutParams(actualWidth, FrameLayout.LayoutParams.MATCH_PARENT);
            lyp.leftMargin= (vgw - actualWidth) / 2;
        }
        else{
            int actualHeight = (int)Math.ceil((float)videoHeight * vgw / videoWidth);
            lyp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, actualHeight);
            lyp.topMargin= (vgh - actualHeight) / 2;
        }
        surfaceView.setLayoutParams(lyp);

        if(getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
            //竖屏模式
        }
        else{
            // 横屏模式
            getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN);
        }
    }

    public class MyCallback implements SurfaceHolder.Callback{

        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            mediaPlayer.setDisplay(surfaceHolder);
            Log.d(TAG, "setDisplay");
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            Log.d(TAG, "surfaceChanged");
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            if(mediaPlayer != null){
                mediaPlayer.release();
                mediaPlayer = null;
            }
            Log.d(TAG, "surfaceDestroyed");
        }
    }

}
