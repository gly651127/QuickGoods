package com.zbar.lib;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.zbar.lib.camera.CameraManager;
import com.zbar.lib.decode.CaptureActivityHandler;
import com.zbar.lib.decode.InactivityTimer;

import java.io.IOException;

/**
 * 二维码扫描
 */
public class CaptureActivity extends Activity implements Callback {
    public static final String KEY_DECODE_RESULT = "result";
    public static final String ACTION_DECODE_INTENT = "saoma";
    public static Activity activity;
    private CaptureActivityHandler handler;
    private boolean hasSurface;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.50f;

    private boolean vibrate;
    private int x = 0;
    private int y = 0;
    private int cropWidth = 0;
    private int cropHeight = 0;
    private RelativeLayout mContainer = null;
    private RelativeLayout mCropLayout = null;
    private boolean isNeedCapture = false;

    public boolean isNeedCapture() {
        return isNeedCapture;
    }

    public void setNeedCapture(boolean isNeedCapture) {
        this.isNeedCapture = isNeedCapture;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getCropWidth() {
        return cropWidth;
    }

    public void setCropWidth(int cropWidth) {
        this.cropWidth = cropWidth;
    }

    public int getCropHeight() {
        return cropHeight;
    }

    public void setCropHeight(int cropHeight) {
        this.cropHeight = cropHeight;
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        activity = this;
        // 初始化CameraManager
        CameraManager.init(getApplication());
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);

        mContainer = findViewById(R.id.capture_containter);
        mCropLayout = findViewById(R.id.capture_crop_layout);
        //获得屏幕的宽高
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int screenWith = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mCropLayout.getLayoutParams();
        params.width = (int) (screenWith * 0.7f);
        params.height = (int) (screenHeight * 0.7f);
        mCropLayout.setLayoutParams(params);
    }

    boolean flag = true;

    protected void light() {
        if (flag == true) {
            flag = false;
            // 打开
            CameraManager.get().openLight();
        } else {
            flag = true;
            // 关闭
            CameraManager.get().offLight();
        }

    }

    public void onClick(View view) {
        if (view.getId() == R.id.btn_close) {
            finish();
            inactivityTimer.onActivity();
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onResume() {
        super.onResume();
        SurfaceView surfaceView = findViewById(R.id.capture_preview);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    public void handleDecode(String result) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
//        finish();
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
        Intent intent = new Intent();
        intent.putExtra(KEY_DECODE_RESULT, result);
        intent.setAction(ACTION_DECODE_INTENT);
        lbm.sendBroadcast(intent);
        handler.sendEmptyMessageDelayed(R.id.restart_preview, 1000);
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
            Point point = CameraManager.get().getCameraResolution();
            int width = point.y;
            int height = point.x;
            int x = mCropLayout.getLeft() * width / mContainer.getWidth();
            int y = mCropLayout.getTop() * height / mContainer.getHeight();
            int cropWidth = mCropLayout.getWidth() * width / mContainer.getWidth();
            int cropHeight = mCropLayout.getHeight() * height / mContainer.getHeight();

            setX(x);
            setY(y);
            setCropWidth(cropWidth);
            setCropHeight(cropHeight);
            setNeedCapture(true);


        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(CaptureActivity.this);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public Handler getHandler() {
        return handler;
    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };
}