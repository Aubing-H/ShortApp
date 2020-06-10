package com.example.shortapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.os.Handler;

import java.util.Random;

public class MyFrameLayout extends FrameLayout {

    private long lastClickTime = 0; // 上一次的点击事件时间
    private static final long INTERVAL = 300; //双击事件最大间隔，单位ms
    float[] num = {-30, -20, 0, 20, 30};//随机心形图片角度
    private MyClickListener.MyCallBack onClickListener;

    public MyFrameLayout(Context context){
        super(context);
    }

    public MyFrameLayout(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    protected void dispatchDraw(Canvas canvas){
        super.dispatchDraw(canvas);
    }

    public boolean dispatchTouchEvent(MotionEvent event){
        //处理点击事件
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                long current = System.currentTimeMillis();
                // 判断点击事件差
                long interval = current - lastClickTime;
                lastClickTime = current;
                if(interval < INTERVAL){
                    final ImageView imageView = new ImageView(getContext());
                    LayoutParams layoutParams = new LayoutParams(300, 300);
                    layoutParams.leftMargin = (int)event.getX() - 150;
                    layoutParams.topMargin = (int)event.getY() - 300;
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.red_like));
                    imageView.setLayoutParams(layoutParams);
                    addView(imageView);

                    //设置控件的动画效果
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.play(scale(imageView, "scaleX", 2f, 0.9f, 100, 0))
                            //缩放动画，Y轴2倍缩放至0.9倍
                            .with(scale(imageView, "scaleY", 2f, 0.9f, 100, 0))
                            //旋转动画，随机旋转角
                            .with(rotation(imageView, 0, 0, num[new Random().nextInt(4)]))
                            //渐变透明动画，透明度从0-1
                            .with(alpha(imageView, 0, 1, 100, 0))
                            //缩放动画，X轴0.9倍缩小至
                            .with(scale(imageView, "scaleX", 0.9f, 1, 50, 150))
                            //缩放动画，Y轴0.9倍缩放至
                            .with(scale(imageView, "scaleY", 0.9f, 1, 50, 150))
                            //位移动画，Y轴从0上移至600
                            .with(translationY(imageView, 0, -600, 800, 400))
                            //透明动画，从1-0
                            .with(alpha(imageView, 1, 0, 300, 400))
                            //缩放动画，X轴1至3倍
                            .with(scale(imageView, "scaleX", 1, 3f, 700, 400))
                            //缩放动画，Y轴1至3倍
                            .with(scale(imageView, "scaleY", 1, 3f, 700, 400));
                    animatorSet.start();
                    animatorSet.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            //当动画结束以后，需要把控件从父布局移除
                            removeViewInLayout(imageView);
                        }
                    });
                }
                break;

        }
        return super.dispatchTouchEvent(event);
    }

    /* 缩放动画 */
    public static ObjectAnimator scale(View view, String propertyName, float from, float to, long duration, long delay){
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, propertyName, from, to);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.setStartDelay(delay);
        objectAnimator.setDuration(duration);
        return objectAnimator;
    }

    /* 位移动画 */
    public static ObjectAnimator translationX(View view, float from, float to, long time, long delayTime) {
        ObjectAnimator translation = ObjectAnimator.ofFloat(view
                , "translationX"
                , from, to);
        translation.setInterpolator(new LinearInterpolator());
        translation.setStartDelay(delayTime);
        translation.setDuration(time);
        return translation;
    }
    public static ObjectAnimator translationY(View view, float from, float to, long time, long delayTime) {
        ObjectAnimator translation = ObjectAnimator.ofFloat(view
                , "translationY"
                , from, to);
        translation.setInterpolator(new LinearInterpolator());
        translation.setStartDelay(delayTime);
        translation.setDuration(time);
        return translation;
    }

    /* 透明度动画 */
    public static ObjectAnimator alpha(View view, float from, float to, long time, long delayTime) {
        ObjectAnimator translation = ObjectAnimator.ofFloat(view
                , "alpha"
                , from, to);
        translation.setInterpolator(new LinearInterpolator());
        translation.setStartDelay(delayTime);
        translation.setDuration(time);
        return translation;
    }

    /* 旋转动画 */
    public static ObjectAnimator rotation(View view, long time, long delayTime, float... values) {
        ObjectAnimator rotation = ObjectAnimator.ofFloat(view, "rotation", values);
        rotation.setDuration(time);
        rotation.setStartDelay(delayTime);
        rotation.setInterpolator(new TimeInterpolator() {
            @Override
            public float getInterpolation(float input) {
                return input;
            }
        });
        return rotation;
    }


    public void setOnClickListener(MyClickListener.MyCallBack callBack){
        this.onClickListener = callBack;
    }

//    public MyClickListener.MyCallBack getOnClickListener(){
//        return onClickListener;
//    }

    public static class MyClickListener implements View.OnTouchListener{

        private static int timeout = 400; //间隔400毫秒
        private int count = 0;
        private Handler handler;
        private MyCallBack myCallBack;

        public interface MyCallBack{
            void singleClick();
            void doubleClick();
        }

        public MyClickListener(MyClickListener.MyCallBack callBack){
            this.myCallBack = callBack;
            handler = new Handler();
        }

        public boolean onTouch(View v, MotionEvent event){
            if (event.getAction() == MotionEvent.ACTION_DOWN){
                count++;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (count == 1){
                            myCallBack.singleClick();
                        }else if(count >= 2){
                            myCallBack.doubleClick();
                        }
                        handler.removeCallbacksAndMessages(null);
                        count = 0;
                    }
                }, timeout);
            }
            return false; //让点击事件继续传播
        }
    }
}
