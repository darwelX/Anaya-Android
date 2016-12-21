package com.msi.manning.chapter9.bounceyBall;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;

public class BounceActivity extends Activity {

    protected static final int GUIUPDATEIDENTIFIER = 0x101;

    Thread myRefreshThread = null;

    BounceView myBounceView = null;

    Handler myGUIUpdateHandler = new Handler() {

        // @Override
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BounceActivity.GUIUPDATEIDENTIFIER:
                    BounceActivity.this.myBounceView.invalidate();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        this.myBounceView = new BounceView(this);
        this.setContentView(this.myBounceView);
        new Thread(new RefreshRunner()).start();
    }

    class RefreshRunner implements Runnable {

        // @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                Message message = new Message();
                message.what = BounceActivity.GUIUPDATEIDENTIFIER;
                BounceActivity.this.myGUIUpdateHandler.sendMessage(message);

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
