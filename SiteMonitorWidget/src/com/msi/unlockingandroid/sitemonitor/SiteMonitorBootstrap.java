package com.msi.unlockingandroid.sitemonitor;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

public class SiteMonitorBootstrap extends BroadcastReceiver {
	private static final String tag = "SiteMonitorBootstrap";
	public static final String ALARM_ACTION = "com.msi.unlockingandroid.sitemonitor.ALARM_ACTION";
	
	// TODO make this a preference feature of the application
	private static final long UPDATE_FREQUENCY = (1000 * 60 * 60);   // default to one hour


	
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// we are expecting to be called when the device boots up 
		// when this occurs, let's set an alarm which will ultimately launch our service
		String action = intent.getAction();
		
		Log.i(SiteMonitorBootstrap.tag,"onReceive");
		
		if (action.equals(SiteMonitorBootstrap.ALARM_ACTION)) {
			Log.i(SiteMonitorBootstrap.tag,"Alarm fired -- start the service to perform the updates");
			Intent startSvcIntent = new Intent(context,SiteMonitorService.class);
			startSvcIntent.putExtra("ALARMTRIGGERED", "YES");
			context.startService(startSvcIntent);
		}
		
		

	}
	
	
	
	
	public static void setAlarm(Context context) {
		Log.i(SiteMonitorBootstrap.tag,"setAlarm");
		AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		
		// setup pending intent
		Intent alarmIntent = new Intent(SiteMonitorBootstrap.ALARM_ACTION);
		PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		// now go ahead and set the alarm
		alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + SiteMonitorBootstrap.UPDATE_FREQUENCY, SiteMonitorBootstrap.UPDATE_FREQUENCY, pIntent);
	}
	
	public static void clearAlarm(Context context) {
		Log.i(SiteMonitorBootstrap.tag,"clearAlarm");
		AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		
		// cancel the pending intent!
		Intent alarmIntent = new Intent(SiteMonitorBootstrap.ALARM_ACTION);
		PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		alarmManager.cancel(pIntent);
		
	}

}
