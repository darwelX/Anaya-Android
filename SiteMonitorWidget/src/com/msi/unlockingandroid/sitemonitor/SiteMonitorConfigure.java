package com.msi.unlockingandroid.sitemonitor;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;



public class SiteMonitorConfigure extends Activity implements OnClickListener {
    
	private final String tag = "SiteMonitorConfig";
	private int widgetId = -1;
	
	private EditText etSiteName = null;
	private EditText etSiteURL = null;
	private EditText etSiteHomePageURL = null;
	private TextView tvSiteMessage = null;
	
	
	private SiteMonitorModel smm = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // TODO: handle restoring saveInstanceState if not null

        // wire up GUI
        etSiteName = (EditText) findViewById(R.id.etSiteName);
        etSiteURL = (EditText) findViewById(R.id.etSiteURL);
        etSiteHomePageURL = (EditText) findViewById(R.id.etSiteHomePageURL);
        tvSiteMessage = (TextView) findViewById(R.id.tvSiteMessage);
        
        final Button btnSaveSite = (Button) findViewById(R.id.btnSaveSite);
        btnSaveSite.setOnClickListener(this);
        final Button btnVisitSite = (Button) findViewById(R.id.btnVisitSite);
        btnVisitSite.setOnClickListener(this);
        
        // get widget id
        widgetId = getIntent().getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,widgetId);
        
        // lookup to see if we have any info on this widget
        smm = SiteMonitorModel.getWidgetData(this, widgetId);
        if (smm != null) {
        	etSiteName.setText(smm.getName());
        	etSiteURL.setText(smm.getUrl());
        	etSiteHomePageURL.setText(smm.getHomepageUrl());
        	tvSiteMessage.setText(smm.getMessage());
        }
    }
    
    private void saveInfo() {
		Log.i(tag,"Saving site info");
		String name = etSiteName.getText().toString();
		String url = etSiteURL.getText().toString();
		String homepageUrl = etSiteHomePageURL.getText().toString();
		if (name.trim().length() == 0 || url.trim().length() == 0) {
			return;
		}
		
		Log.i(tag,"About to save");
		// save data
		if (smm == null) {
			Log.i(tag,"smm is null, create a new one here");
		    smm = new SiteMonitorModel(name,url,homepageUrl,"UNKNOWN",SiteMonitorModel.getFormattedDate(),"nothing yet");
		} 
		
		smm.setName(name);
		smm.setUrl(url);
		smm.setHomepageUrl(homepageUrl);
		
		SiteMonitorModel.saveWidgetData(etSiteURL.getContext(), widgetId, smm);

    }
    
    public void onClick(View v) {
    	switch (v.getId()) {
    		case R.id.btnSaveSite: {
    			saveInfo();
    			// update the widget's display
    			SiteMonitorWidgetImpl.UpdateOneWidget(v.getContext(), widgetId);
    			
    			// let the widgetprovider know we're done and happy
    			Intent ret = new Intent();
    			ret.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,widgetId);
    			setResult(Activity.RESULT_OK,ret);
    			
    			
    			
    			// let's ask for an update and also enable the alarm
    			Intent askForUpdate = new Intent(SiteMonitorBootstrap.ALARM_ACTION);
    			this.sendBroadcast(askForUpdate);
    			SiteMonitorBootstrap.setAlarm(this);
    			finish();
    		}
    		break;
    		case R.id.btnVisitSite: {
    			saveInfo();
    			Intent visitSite = new Intent(Intent.ACTION_VIEW);
    			visitSite.setData(Uri.parse(smm.getHomepageUrl()));
    			startActivity(visitSite);
    		}
    		break;
    	}
    }
    
    public void onDestroy() {
    	super.onDestroy();
    	Log.i(tag,"onDestroy");
    	if (!isFinishing()) {
    		Log.i(tag,"canceling");
			Intent ret = new Intent();
			ret.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,widgetId);
			setResult(Activity.RESULT_CANCELED,ret);    	}
    }

    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        // add out menu options
        menu.add(0, 0, 0, "Refresh All Sites Now");
        menu.add(0, 1, 0, "Enable Auto Refresh");
        menu.add(0, 2, 0, "Disable Auto Refresh");
        
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
	            {
	            	Log.i(tag,"Start Service to refresh our entries");
	            	Intent intent = new Intent(this.getApplicationContext(),SiteMonitorService.class);
	            	this.startService(intent);
	            	finish();
	            }
	            return true;
            case 1:
	            {
	            	Log.i(tag,"Set alarm");
	            	SiteMonitorBootstrap.setAlarm(this);
	            	finish();
	            }
	            return true;
            case 2:
	            {
	            	Log.i(tag,"Clear alarm");
	            	SiteMonitorBootstrap.clearAlarm(this);
	            	finish();
	            }
	            return true;
         }
        return false;
    }
    
    
}

