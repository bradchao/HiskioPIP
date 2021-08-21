package com.bradchao.hiskiopip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PictureInPictureParams;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Rational;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private VideoView videoView;
    private View counterView;
    private TextView counter;
    private Timer timer;
    private int i;
    private UIHandler uiHandler = new UIHandler();
    private boolean isPIP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        counterView = findViewById(R.id.counterView);
        counter = findViewById(R.id.counter);

        videoView = findViewById(R.id.videoView);

        isPIP = false;

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (isPIP) {
                    i++;
                    uiHandler.sendEmptyMessage(0);
                }
            }
        }, 0, 1000);

        playVideo();

//        videoView.setVisibility(View.VISIBLE);
        counterView.setVisibility(View.INVISIBLE);

        Log.v("bradlog", "onCreate");

    }

    private class UIHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            counter.setText("" + i);
        }
    }

    private MediaController mediaController;
    private MediaPlayer mediaPlayer;
    private void playVideo(){
        Log.v("bradlog", "playVideo()");
        mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        String uri = "android.resource://" + getPackageName() + "/" + R.raw.game;
        videoView.setVideoURI(Uri.parse(uri));
        videoView.start();
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            try {
                Rational rational = new Rational(videoView.getWidth(), videoView.getHeight());
                PictureInPictureParams mParams =
                        new PictureInPictureParams.Builder()
                                .setAspectRatio(rational)
                                .build();

                enterPictureInPictureMode(mParams);
            }catch (IllegalStateException e){
                Log.v("bradlog", e.toString());
            }
        }
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode);
        Log.v("bradlog", "isInPictureInPictureMode:" + isInPictureInPictureMode);

        counterView.setVisibility(isInPictureInPictureMode?View.VISIBLE:View.INVISIBLE);
        isPIP = isInPictureInPictureMode;
        if (isPIP){
            videoView.pause();
        }else{
            videoView.start();
        }

    }
}