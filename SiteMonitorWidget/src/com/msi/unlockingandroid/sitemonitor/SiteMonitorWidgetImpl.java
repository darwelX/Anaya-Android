package com.msi.unlockingandroid.sitemonitor;


import android.content.Context;
import android.content.ComponentName;
import android.content.Intent;
import android.app.PendingIntent;
import android.appwidget.AppWidgetProvider;
import android.appwidget.AppWidgetManager;
import android.widget.RemoteViews;
import android.net.Uri;
import android.util.Log;
import android.graphics.Color;


public class SiteMonitorWidgetImpl extends AppWidgetProvider {
	private static final String tag = "SiteMonitor";
	public static final String UPDATE_WIDGETS = "com.msi.unlockingandroid.sitemonitor.UPDATE_WIDGETS";
	
	public static void UpdateOneWidget(Context context,int widgetNumber) {
		Log.i(SiteMonitorWidgetImpl.tag,"Update one widget!");
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		SiteMonitorModel smm = SiteMonitorModel.getWidgetData(context, widgetNumber);
		
		//Log.i(tagStatic,"Updating text view ...." + widgetNumber + " filter is [" + currentFilter + "]");
		if (smm != null) {
			Log.i(SiteMonitorWidgetImpl.tag,"Processing widget " + smm.toString());

			RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.monitor);
			if (smm.getStatus().equals("GOOD")) {
				views.setTextColor(R.id.siteName, Color.rgb(0,255,0));
				views.setTextColor(R.id.updateTime, Color.rgb(0,255,0));
				views.setTextColor(R.id.siteMessage, Color.rgb(0,255,0));
			} else if (smm.getStatus().equals("UNKNOWN")){
				views.setTextColor(R.id.siteName, Color.rgb(255,255,0));
				views.setTextColor(R.id.updateTime, Color.rgb(255,255,0));
				views.setTextColor(R.id.siteMessage, Color.rgb(255,255,0));
			} else {
				views.setTextColor(R.id.siteName, Color.rgb(255,0,0));
				views.setTextColor(R.id.updateTime, Color.rgb(255,0,0));
				views.setTextColor(R.id.siteMessage, Color.rgb(255,0,0));
			}
			views.setTextViewText(R.id.siteName, smm.getName());
			views.setTextViewText(R.id.updateTime, smm.getStatusDate());
			
			// make this thing clickable
			Intent intWidgetClicked = new Intent(context,SiteMonitorConfigure.class);
			
			intWidgetClicked.setData(Uri.parse("file:///bogus" + widgetNumber));
			intWidgetClicked.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetNumber);
			PendingIntent pi = PendingIntent.getActivity(context, 0, intWidgetClicked, 0);//PendingIntent.FLAG_ONE_SHOT);
			views.setOnClickPendingIntent(R.id.widgetLayout, pi);
			
			
			
			appWidgetManager.updateAppWidget(widgetNumber,views);	
		}
		else {
			Log.i(SiteMonitorWidgetImpl.tag,"Ignore this widget # " + widgetNumber + ".  Must be a zombie widget.");
		}
	}
	
	
	
	@Override
	public void onUpdate(Context context,AppWidgetManager appWidgetManager,int[] appWidgetIds ) {
		
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		int count = appWidgetIds.length;
		Log.i(SiteMonitorWidgetImpl.tag,"onUpdate::" + count);
		// we may have multiple instances of this widget ... make sure we hit each one ...
		for (int i=0;i<count;i++) {
			SiteMonitorWidgetImpl.UpdateOneWidget(context, appWidgetIds[i]);
		}
	}
	
	public void onDeleted(Context context,int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
		Log.i(SiteMonitorWidgetImpl.tag,"onDeleted()" + appWidgetIds.length);
		for (int i = 0;i<appWidgetIds.length;i++) {
			SiteMonitorModel.deleteWidgetData(context, appWidgetIds[i]);
		}
		checkForZombies(context);
	}
	
	private void checkForZombies(Context context) {
		Log.i(SiteMonitorWidgetImpl.tag,"checkForZombies");
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		int [] ids = appWidgetManager.getAppWidgetIds(new ComponentName(context,SiteMonitorWidgetImpl.class));
		int goodCount = 0;
		for (int i=0;i<ids.length;i++) {
			SiteMonitorModel smm = SiteMonitorModel.getWidgetData(context,ids[i]);
			if (smm != null) goodCount++;
		}
		if (goodCount == 0) {
			Log.i(SiteMonitorWidgetImpl.tag,"There are no good widgets left!  Kill alarm!");
			SiteMonitorBootstrap.clearAlarm(context);
		}
	}
	
	public void onDisabled(Context context) {
		Log.i(SiteMonitorWidgetImpl.tag,"onDisabled()");
		super.onDisabled(context);
		// kill the recurring alarm that drives our refresh process
		SiteMonitorBootstrap.clearAlarm(context);

	}
	
	public void onEnabled(Context context) {
		Log.i(SiteMonitorWidgetImpl.tag,"onEnabled");
		super.onEnabled(context);
		
		// setup the recurring alarm that drives our refresh process
		SiteMonitorBootstrap.setAlarm(context);
	}

	
	public void onReceive(Context context,Intent intent) {
		super.onReceive(context, intent);
		Log.i(SiteMonitorWidgetImpl.tag,"onReceive()::" + intent.getAction());
		
		if (intent.getAction().equals(SiteMonitorWidgetImpl.UPDATE_WIDGETS)) {

			Log.i(SiteMonitorWidgetImpl.tag,"Updating widget based on intent");
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
			int [] ids = appWidgetManager.getAppWidgetIds(new ComponentName(context,SiteMonitorWidgetImpl.class));
			onUpdate(context,appWidgetManager,ids);
		} 

	}

}
