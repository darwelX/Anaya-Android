package com.msi.unlockingandroid.sitemonitor;

import java.text.SimpleDateFormat;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SiteMonitorModel {
	
	private static final String tag = "SiteMonitorModel";
		
	private static final String PREFS_NAME = "com.msi.unlockingandroid.SiteMonitor"; 
	private static final String PREFS_PREFIX = "sitemonitor_";

	private String name;
	private String url;
	private String homepageUrl;
	private String status;
	private String statusDate;
	private String message;
	
	public SiteMonitorModel(String name,String url,String homepageUrl,String status,String statusDate,String message) {
		this.name = name;
		this.url = url;
		this.homepageUrl = homepageUrl;
		this.status = status;
		this.statusDate = statusDate;
		this.message = message;
	}
	
	public SiteMonitorModel(String instring) {
		Log.i(SiteMonitorModel.tag,"SiteMonitorModel(" + instring + ")");
		String[] data = instring.split("[|]");
		if (data.length == 6) {
			this.name = data[0];
			this.url = data[1];
			this.homepageUrl = data[2];
			this.status = data[3];
			this.statusDate = data[4];
			this.message = data[5];
		} else {
			this.name = "?";
			this.url = "?";
			this.homepageUrl = "?";
			this.status = "WARNING";
			this.statusDate = java.util.Calendar.getInstance().getTime().toString();
			this.message = "";
		}
	}
	
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	public String getUrl() {
		return this.url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getHomepageUrl() {
		return this.homepageUrl;
	}
	public void setHomepageUrl(String homepageUrl) {
		this.homepageUrl = homepageUrl;
	}
	
	public String getStatus() {
		return this.status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	
	public String getStatusDate() {
		return this.statusDate;
	}
	public void setStatusDate(String statusDate) {
		this.statusDate = statusDate;
	}
	
	public String getMessage() {
		return this.message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String storageString() {
		return this.name + "|" + this.url + "|" + this.homepageUrl + "|" + this.status + "|" + this.statusDate + "|" + message;
	}
	
	
	public String toString() {
		return this.storageString();
	}

	
	
	
	public static void saveWidgetData(Context context,int widgetId,SiteMonitorModel model) {
		Log.i(SiteMonitorModel.tag,"saveWidgetData(" + widgetId + "," + model.storageString() + ")");
		SharedPreferences.Editor prefsEditor = context.getSharedPreferences(PREFS_NAME, 0).edit();
		prefsEditor.putString(PREFS_PREFIX + widgetId,model.storageString());
		prefsEditor.commit();
	}
	
	public static SiteMonitorModel getWidgetData(Context context,int widget) {
		Log.i(SiteMonitorModel.tag,"getWidgetData(" + widget + ")");
		
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		String ret = prefs.getString(PREFS_PREFIX + widget,"BAD");
		if (ret.equals("BAD")) return null;
		return new SiteMonitorModel(ret);
	}
	
	public static void deleteWidgetData(Context context,int widgetId) {
		Log.i(SiteMonitorModel.tag,"deleteWidgetData(" + widgetId + ")");
		SharedPreferences.Editor prefsEditor = context.getSharedPreferences(PREFS_NAME, 0).edit();
		prefsEditor.remove(PREFS_PREFIX + widgetId);
		prefsEditor.commit();
	}
	
	public static String getFormattedDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd HH:mm");
		return sdf.format(java.util.Calendar.getInstance().getTime());
	}
}
