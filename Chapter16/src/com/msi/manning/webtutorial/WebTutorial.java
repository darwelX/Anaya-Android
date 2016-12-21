package com.msi.manning.webtutorial;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.webkit.WebChromeClient;

public class WebTutorial extends Activity {
	
	private WebView browser = null;
	
	public static final String STARTING_PAGE = "file:///android_asset/index.html";

	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        setupBrowser();
    }
    
    
    private void setupBrowser() {
    	browser = (WebView) findViewById(R.id.browser);
    	
    	WebSettings browserSettings = browser.getSettings();
    	
    	browserSettings.setJavaScriptEnabled(true);
    	
    	browser.clearCache(true);
    	
    	browser.setWebChromeClient(new UAChrome(this));
    	
    	browser.setWebViewClient(new UAWebViewClient(this.getApplicationContext()));
    	
    	browser.addJavascriptInterface(new UAJscriptHandler(this), "unlockingandroid");
    	//browser.addJavascriptInterface(new UANOOP() {}, "unlockingandroid");
    	//browser.addJavascriptInterface(null, "unlockingandroid");
    	browser.loadUrl(STARTING_PAGE);

    }
    
    private class UANOOP {
    	
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        // add out menu options
        menu.add(0, 0, 0, "Reset");
        return true; 
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case 0:
            	browser.loadUrl(STARTING_PAGE);
            	return true;
        }
        return false;
    }
}


