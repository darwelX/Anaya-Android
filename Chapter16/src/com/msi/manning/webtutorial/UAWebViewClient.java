package com.msi.manning.webtutorial;

import android.content.Context;
import android.util.Log;
import android.graphics.Bitmap;
import android.webkit.*;
import android.widget.Toast;

public class UAWebViewClient extends WebViewClient{

	private String tag = "UAWebViewClient";
	private Context context;
	
	
	public UAWebViewClient(Context context) {
		super();
		this.context = context;
	}
	
	public void onPageStarted(WebView wv,String url,Bitmap favicon) {
		super.onPageStarted(wv,url,favicon);
		Log.i(tag,"onPageStarted[" + url + "]");
		if (!url.equals(WebTutorial.STARTING_PAGE)) {
			WTApplication app = (WTApplication) context;
			String toSearch = app.getSearchTerm();
			if (toSearch != null && toSearch.trim().length() > 0) {
				Toast.makeText(context,"Searching for " + toSearch,Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	public void onPageFinished(WebView wv,String url) {
		super.onPageFinished(wv,url);
		Log.i(tag,"onPageFinished");
		if (!url.equals(WebTutorial.STARTING_PAGE)) {
			WTApplication app = (WTApplication) context;
			String toSearch = app.getSearchTerm();
			if (toSearch != null && toSearch.trim().length() > 0) {
				
				int count = wv.findAll(app.getSearchTerm());
				Log.i(tag,"term found [" + count + "] times.");
				Toast.makeText(app, count + " occurences of " + toSearch + ".",Toast.LENGTH_SHORT).show();
			}
		}
	}
}
