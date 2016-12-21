package com.msi.manning.chapter10.SimpleCamera;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.MediaColumns;
import android.provider.MediaStore.Images.ImageColumns;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class SimpleCamera extends Activity implements SurfaceHolder.Callback {

    private Camera camera;
    private boolean isPreviewRunning = false;
    private SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyyMMddHHmmssSS");

    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private Uri targetResource = Media.EXTERNAL_CONTENT_URI;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Log.e(getClass().getSimpleName(), "onCreate");
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setContentView(R.layout.main);
        this.surfaceView = (SurfaceView) findViewById(R.id.surface);
        this.surfaceHolder = this.surfaceView.getHolder();
        this.surfaceHolder.addCallback(this);
        this.surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        MenuItem item = menu.add(0, 0, 0, "View Pictures");
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(Intent.ACTION_VIEW, SimpleCamera.this.targetResource);
                startActivity(intent);
                return true;
            }
        });
        return true;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    Camera.PictureCallback mPictureCallbackRaw = new Camera.PictureCallback() {

        public void onPictureTaken(byte[] data, Camera c) {
            SimpleCamera.this.camera.startPreview();
        }
    };

    Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {

        public void onShutter() {
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        ImageCaptureCallback camDemo = null;
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            try {
                String filename = this.timeStampFormat.format(new Date());
                ContentValues values = new ContentValues();
                values.put(MediaColumns.TITLE, filename);
                values.put(ImageColumns.DESCRIPTION, "Image from Android Emulator");
                Uri uri = getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, values);
                camDemo = new ImageCaptureCallback(getContentResolver().openOutputStream(uri));
            } catch (Exception ex) {
            }
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return super.onKeyDown(keyCode, event);
        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            this.camera.takePicture(this.mShutterCallback, this.mPictureCallbackRaw, camDemo);
            return true;
        }

        return false;
    }

    @Override
    protected void onResume() {
        Log.e(getClass().getSimpleName(), "onResume");
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if (this.isPreviewRunning) {
            this.camera.stopPreview();
        }
        Camera.Parameters p = this.camera.getParameters();
        p.setPreviewSize(w, h);
        this.camera.setParameters(p);
        try {
            this.camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        this.camera.startPreview();
        this.isPreviewRunning = true;
    }

    public void surfaceCreated(SurfaceHolder holder) {
        this.camera = Camera.open();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        this.camera.stopPreview();
        this.isPreviewRunning = false;
        this.camera.release();
    }
}
