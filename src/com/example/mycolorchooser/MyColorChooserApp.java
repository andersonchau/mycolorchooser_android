package com.example.mycolorchooser;

import android.app.Application;

public class MyColorChooserApp extends Application {
	private int mUserSelectedRound = 0;
	
	public void setUserSelectedRound(int round){
		mUserSelectedRound = round;
	}
	
	public int getUserSelectedRound(){
		return mUserSelectedRound; 
	}
	
	@Override 
    public void onCreate() { 
        super.onCreate();
        mUserSelectedRound=0;
                
    }    
}
