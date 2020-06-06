package com.example.shortapp;

import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class VideoPlayer extends AppCompatActivity {

    private static final String TAG = "videoPlayer";

    private SurfaceView surfaceView;
    private MediaPlayer mediaPlayer;
    private SurfaceHolder surfaceHolder;
    private String videoUrl;

    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);

        setContentView(R.layout.video_player);

        videoUrl = getIntent().getStringExtra("videoUrl");
        surfaceView = findViewById(R.id.surfaceView);
        TextView tv = findViewById(R.id.text_temp);
        tv.setText(videoUrl);

        playVideo();

        surfaceView.setOnClickListener(view -> {
            if(mediaPlayer.isPlaying()){
                mediaPlayer.pause();
            }else {
                mediaPlayer.start();
            }
        });
        // Log.d(TAG, "on click listener");
        // mediaPlayer.start();
        Log.d(TAG, "init finished");
    }

    public void playVideo(){
        if(mediaPlayer == null){
            mediaPlayer = new MediaPlayer();
            // mediaPlayer.setOnCompletionListener(mediaPlayer -> mediaPlayer.start());
            Log.d(TAG, "OnCompletionListener");
        }
        try{
            mediaPlayer.setDataSource(getApplicationContext(), Uri.parse(videoUrl));
            SurfaceHolder surfaceHolder = surfaceView.getHolder();
            Log.d(TAG, "getHolder()");
            surfaceHolder.addCallback(new MyCallback());
            Log.d(TAG, "MyCallback");

            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mediaPlayer -> mediaPlayer.setLooping(true));
            mediaPlayer.setOnVideoSizeChangedListener((mediaPlayer, i, i1) -> changeVideoSize(mediaPlayer));
            Log.d(TAG, "prepareAsync");
        }catch(IOException e){
            Log.d(TAG, "IOException: " + e.toString());
        }

    }

    public void changeVideoSize(MediaPlayer mediaPlayer){
        int videoWidth = mediaPlayer.getVideoWidth();
        int videoHeight = mediaPlayer.getVideoHeight();

        if(getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
            //竖屏模式
            float max_rate = Math.max((float)videoWidth/surfaceView.getWidth(),
                    (float)videoHeight/surfaceView.getHeight());
            videoHeight = (int)Math.ceil((float)videoHeight/max_rate);
            videoWidth = (int)Math.ceil((float)videoWidth/max_rate);
            surfaceView.setLayoutParams(new LinearLayout.LayoutParams(videoWidth, videoHeight));
        }
        else{
            //横屏模式
            ViewGroup vg = (ViewGroup)surfaceView.getParent();
            int vgw = vg.getWidth();
            int vgh = vg.getHeight();
            LinearLayout.LayoutParams lyp;
            if ((float)vgw/vgh > (float)videoWidth/videoHeight){
                int actualWidth = (int)Math.ceil((float)videoWidth * vgh / videoHeight);
                lyp = new LinearLayout.LayoutParams(actualWidth, LinearLayout.LayoutParams.MATCH_PARENT);
                lyp.leftMargin = (vgw - actualWidth) / 2;
            }
            else
                lyp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int)Math.ceil((float)videoHeight * vgw / videoWidth));
            surfaceView.setLayoutParams(lyp);
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
