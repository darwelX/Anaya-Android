package com.msi.manning.ua2efindedges;



import android.app.Activity;
import android.os.Bundle;

import android.util.Log;
import android.widget.ImageView;
import android.widget.Button;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
 

public class UA2EFindEdges extends Activity {
    
	protected ImageView imageView = null;
	private final String tag = "UA2EFindEdges";
	private Button btnAcquire;
	private Button btnFindEdges;
	// declare native methods
	public native int converttogray(Bitmap bitmapcolor, Bitmap gray);
	public native int detectedges(Bitmap bitmapgray, Bitmap bitmapedges);
	
	static {
		System.loadLibrary("ua2efindedges");
	} 
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
         
        
        btnAcquire = (Button) this.findViewById(R.id.AcquireImage);
        btnAcquire.setOnClickListener(new View.OnClickListener(){
        	public void onClick(View v){
        		Log.i(tag,"Button Clicked!");
        		try {
        			Intent action = new Intent("android.media.action.IMAGE_CAPTURE");
        			startActivityForResult(action,1);
        		} catch (Exception e) {
        			Log.e(tag,"Error occured [" + e.getMessage() + "]");
        		}
        	}
        });

        
        
        btnFindEdges = (Button) this.findViewById(R.id.ModifyImage);
        btnFindEdges.setOnClickListener(new View.OnClickListener(){
        	public void onClick(View v){
        		try {
        			// call native method to modify the image
        	        UA2EFindEdgesApp app = (UA2EFindEdgesApp) getApplication();
        	        Bitmap b = app.getBitmap();
        	        
        	        int width = b.getWidth();
        	        int height = b.getHeight();
        	        Bitmap bg = Bitmap.createBitmap(width, height, Config.ALPHA_8);
        	        Bitmap be = Bitmap.createBitmap(width, height, Config.ALPHA_8);
        	        converttogray(b,bg);
        	        detectedges(bg,be);
        	        app.setBitmap(be);
        	        imageView.setImageBitmap(be);
        	        Log.i(tag,"after set image bitmap");
        	      //  btnAcquire.setVisibility(View.VISIBLE);
        	        btnFindEdges.setVisibility(View.GONE);
        		} catch (Exception e) {
        			Log.e(tag,"Error occured [" + e.getMessage() + "]");
        		}
        	}
        });
        
        btnFindEdges.setVisibility(View.GONE);
        
        imageView = (ImageView) this.findViewById(R.id.PictureFrame);
        UA2EFindEdgesApp app = (UA2EFindEdgesApp) getApplication();
        Bitmap b = app.getBitmap();
        if (b != null) {
        	imageView.setImageBitmap(b);
        }
    }


    
    protected void onActivityResult(int requestCode,int resultCode,Intent data)
    {
    	try {
    		if (requestCode == 1) {
    			Log.i(tag,"resultCode is [" + resultCode + "]");
    			if (resultCode == RESULT_OK) {
	    			UA2EFindEdgesApp app = (UA2EFindEdgesApp) getApplication();
    				Bitmap b = app.getBitmap();
    				if (b != null) {
    					b.recycle();
    				}
    				
    			    b = (Bitmap) data.getExtras().get("data");
    			    app.setBitmap(b);
    			    if (b != null) {
    			    	imageView.setImageBitmap(b);
            	  //      btnAcquire.setVisibility(View.GONE);
            	        btnFindEdges.setVisibility(View.VISIBLE);

    			    }
    			}
    		}
    	}catch (Exception e) {
    		Log.e(tag,"onActivityResult Error [" + e.getMessage() + "]");
    	}
    	
    }
    
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        // add out menu options
        menu.add(0, 0, 0, "About Find Edges");
        return true; 
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case 0:
            	Intent aboutIntent = new Intent(this,AboutFindEdges.class);
            	startActivity(aboutIntent);
            	return true;
        }
        return false;
    }
    
}


