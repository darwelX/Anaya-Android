package com.msi.manning.ua2efindedges;

import android.app.Application;
import android.graphics.Bitmap;

public class UA2EFindEdgesApp extends Application {
	private Bitmap b;
	
	public Bitmap getBitmap() {
		return b;
	}
	public void setBitmap(Bitmap b) {
		this.b = b;

	}
}
 