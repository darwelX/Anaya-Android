package com.msi.manning.ua2esensebot;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.app.Activity;
import android.content.Intent;

public class AboutSenseBot extends Activity {

	 @Override
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.about);
	        
	        
	        final Button btnWeb = (Button) findViewById(R.id.VisitSite);
	        btnWeb.setOnClickListener(new View.OnClickListener(){
	        	public void onClick(View v){
	        		try {
	                    Intent webIntent = new Intent("android.intent.action.VIEW", android.net.Uri.parse("http://manning.com/ableson2"));
	                    startActivity(webIntent);
	        		} catch (Exception e) {

	        		}
	        	} 
	        });
	       
	 }
	        
}
