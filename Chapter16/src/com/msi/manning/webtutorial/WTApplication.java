package com.msi.manning.webtutorial;

import android.app.Application;

public class WTApplication extends Application {

	private String searchTerm = "";
	
	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}
	
	public String getSearchTerm() {
		return this.searchTerm;
	}
}
