package com.msi.manning.webtutorial;

import android.content.Context;
import android.widget.Toast;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

public class UAChrome extends WebChromeClient {

	private Context context;
	
	public UAChrome(Context context) {
		super();
		this.context = context;
		
	}
	
	
	public boolean onJsAlert(WebView wv,String url,String message,JsResult result) {
		Toast.makeText(wv.getContext(),message,Toast.LENGTH_SHORT).show();
		result.confirm();
		return true;
	}
}
