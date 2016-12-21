package com.msi.manning.telephonyexplorer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * IntentReceiver for NEW_OUTGOING_CALL that demonstrates catching an outgoing call, and either
 * adding DTMF tones, or aborting the call, etc.
 * 
 * If the specific number 1231231234 is dialed, it is caught and aborted.
 * 
 * @author charliecollins
 * 
 */
public class OutgoingCallReceiver extends BroadcastReceiver {

    public static final String ABORT_PHONE_NUMBER = "1231231234";

    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.v(Constants.LOGTAG, "OutgoingCallReceiver onReceive");
        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            Log.v(Constants.LOGTAG, "OutgoingCallReceiver NEW_OUTGOING_CALL received");

            // get phone number from bundle
            String phoneNumber = intent.getExtras().getString(Intent.EXTRA_PHONE_NUMBER);
            if ((phoneNumber != null) && phoneNumber.equals(OutgoingCallReceiver.ABORT_PHONE_NUMBER)) {
                Toast.makeText(context, "NEW_OUTGOING_CALL intercepted to number 123-123-1234 - aborting call",
                    Toast.LENGTH_LONG).show();
                abortBroadcast();
            }
        }
    }
}
