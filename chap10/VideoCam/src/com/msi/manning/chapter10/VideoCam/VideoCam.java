package com.msi.manning.chapter10.VideoCam;

import java.io.File;
import android.app.Activity;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoCam extends Activity implements SurfaceHolder.Callback {

        private MediaRecorder recorder = null;
        private static final String OUTPUT_FILE = "/sdcard/uatestvideo.mp4";
        private static final String TAG = "VideoCam";
        private VideoView videoView = null;
        private ImageButton startBtn = null;
        private ImageButton playRecordingBtn = null;
        private Boolean playing = false;
        private Boolean recording = false;

  
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    
        startBtn = (ImageButton) findViewById(R.id.bgnBtn);

        playRecordingBtn = (ImageButton) 
            findViewById(R.id.playRecordingBtn);
      
        videoView = (VideoView)this.findViewById(R.id.videoView);

        final SurfaceHolder holder = videoView.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        startBtn.setOnClickListener(new OnClickListener() {

            public void onClick(View view) {
               
                if(!VideoCam.this.recording & !VideoCam.this.playing)
                {
                    try 
                    {
                        beginRecording(holder);
                        playing=false;
                        recording=true;
                        startBtn.setBackgroundResource(R.drawable.stop);
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                        e.printStackTrace();
                    }
                }
                else if(VideoCam.this.recording) 
                {
                    try
                    {
                        stopRecording();
                        playing = false;
                        recording= false;
                        startBtn.setBackgroundResource(R.drawable.play);
                    }catch (Exception e) {
                        Log.e(TAG, e.toString());
                        e.printStackTrace();
                    }
                }
            }
        });

        playRecordingBtn.setOnClickListener(new OnClickListener() {

            public void onClick(View view) 
            {               
              if(!VideoCam.this.playing & !VideoCam.this.recording)
                {
                    try 
                    {
                        playRecording();
                        VideoCam.this.playing=true;
                        VideoCam.this.recording=false;
                        playRecordingBtn.setBackgroundResource(R.drawable.stop);
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                        e.printStackTrace();
                    }
                }
                else if(VideoCam.this.playing) 
                {
                    try
                    {
                        stopPlayingRecording();
                        VideoCam.this.playing = false;
                        VideoCam.this.recording= false;
                        playRecordingBtn.setBackgroundResource(R.drawable.play);
                    }catch (Exception e) {
                        Log.e(TAG, e.toString());
                        e.printStackTrace();
                    }
                }

            }
        });

    }
 
    public void surfaceCreated(SurfaceHolder holder) {
        startBtn.setEnabled(true);
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                int height) {
        Log.v(TAG, "Width x Height = " + width + "x" + height);
    }
    private void playRecording() {
        MediaController mc = new MediaController(this);
        videoView.setMediaController(mc);
        videoView.setVideoPath(OUTPUT_FILE);
        videoView.start();
    }
   
    private void stopPlayingRecording() {
        videoView.stopPlayback();
    }
    
    private void stopRecording() throws Exception {
        if (recorder != null) {
            recorder.stop();
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        if (recorder != null) {
            recorder.release();
        }
    }

    private void beginRecording(SurfaceHolder holder) throws Exception {
        if(recorder!=null)
        {
            recorder.stop();
            recorder.release();
        }

        File outFile = new File(OUTPUT_FILE);
        if(outFile.exists())
        {
            outFile.delete();
        }

        try {
            recorder = new MediaRecorder();
            recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.setVideoSize(320, 240);
            recorder.setVideoFrameRate(15);
            recorder.setMaxDuration(20000); 
            recorder.setPreviewDisplay(holder.getSurface());
            recorder.setOutputFile(OUTPUT_FILE);
            recorder.prepare();
            recorder.start();
        }
        catch(Exception e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }
    }
}