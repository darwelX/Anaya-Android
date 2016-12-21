package com.msi.unlockingandroid.sitemonitor;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.appwidget.AppWidgetManager;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SiteMonitorService extends Service {

	private static final String tag = "SiteMonitorService";
	
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//super.onStartCommand(intent, flags, startId);
		Log.i(SiteMonitorService.tag,"onStartCommand");
		
		Thread smu = new Thread(new SiteMonitorUpdater(this.getBaseContext()));
		smu.start();
		
		return Service.START_NOT_STICKY;
		
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(SiteMonitorService.tag,"onDestroy");
	}


	class SiteMonitorUpdater implements Runnable {
		private static final String tag = "SiteMonitorUpdater";
		
		private Context context;
		
		public SiteMonitorUpdater(Context context) {
			this.context = context;
		}
		
		public void run() {
			Log.i(SiteMonitorUpdater.tag,"Running update code");
			updateAllSites();
			stopSelf();
		}
		
		
		
	    private void updateAllSites() {
			Log.i(SiteMonitorUpdater.tag,"updateAllSites");
			
			try {
				// let's do the work... finally!
				AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
				ComponentName widgetComponentName = new ComponentName(context,SiteMonitorWidgetImpl.class);
				int [] widgetIds = appWidgetManager.getAppWidgetIds(widgetComponentName);
				for (int i=0 ; i< widgetIds.length; i++) {
					SiteMonitorModel smm = SiteMonitorModel.getWidgetData(context, widgetIds[i]);
					if (smm != null) {
						updateOneSite(smm,widgetIds[i]);
					} else {
						Log.i(SiteMonitorUpdater.tag,"Ignore this zombie widget!");
					}
				}
				
				// tell the provider to update all of the widgets... please
				Intent updateWidgetsIntent = new Intent(SiteMonitorWidgetImpl.UPDATE_WIDGETS);
				context.sendBroadcast(updateWidgetsIntent);
				
				Log.i(SiteMonitorUpdater.tag,"Complete!");
			} catch (Exception e) {
				Log.e(SiteMonitorUpdater.tag,"updateAlLSites::caught exception:" + e.getMessage());
				e.printStackTrace();
			}
		}
		
		
		private void updateOneSite(SiteMonitorModel smm,int widgetId) {
			try {
				Log.i(SiteMonitorUpdater.tag,"updateOneSite: [" + smm.getName() + "][" + widgetId + "]");
				
				// get update report from this site's url
				Log.i(SiteMonitorUpdater.tag,"url is [" + smm.getUrl() + "]");
				String dataFromSite = getDataFromSite(smm.getUrl());
				String[] data = dataFromSite.split("[|]");
				if (data.length == 2) {
					smm.setStatus(data[0]);
					smm.setMessage(data[1]);
				}
				smm.setStatusDate(SiteMonitorModel.getFormattedDate());
				SiteMonitorModel.saveWidgetData(context, widgetId, smm);

			} catch (Exception e) {
				Log.e(SiteMonitorUpdater.tag,"updateOneSite::caught exception:" + e.getMessage());
				e.printStackTrace();
				
			}
		}
		
		
		private String getDataFromSite(String siteUrl) {
			String ret = "BAD|unable to reach site";
			URL url;
			
			try {
				url = new URL(siteUrl);
				HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
				BufferedReader inBuf = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
				String inputLine;
				String result = "";
                int lineCount = 0; // limit the lines for the example
                while ((lineCount < 10) && ((inputLine = inBuf.readLine()) != null)) {
                    lineCount++;
                    Log.v(SiteMonitorUpdater.tag,inputLine);
                    result += inputLine;
                }

                inBuf.close();
                urlConn.disconnect();

				return result;
				
				
				
			} catch (Exception e) {
				Log.d(SiteMonitorUpdater.tag,"Error caught: " + e.getMessage());
				e.printStackTrace();
				return ret;
			}
		}
		
	}

}
