package com.example.mycolorchooser;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SoundOnOffOptionActivity extends Activity {
	Button mOnBtn; 
	Button mOffBtn; 
	Button mQuitBtn; 
	
	private void saveGameSnd(boolean bIsOn ){
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
			Editor edit = preferences.edit();
			edit.putBoolean(GameState.G_SND_ONOFF_KEY,bIsOn);
			Log.d("Anderson" , "Setting Game Sound " + bIsOn );
			edit.commit();
		
	}
	
	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.snd_option);
		
		mOnBtn = (Button)findViewById(R.id.snd_on_btn);
		mOffBtn = (Button)findViewById(R.id.snd_off_btn);
		mQuitBtn = (Button)findViewById(R.id.snd_quit_btn);
		
		
		mOnBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
            	saveGameSnd(true);
            	
            	finish();
            }
		});
		
		
		mOffBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
            	saveGameSnd(false);
            	finish();
            }
		});
		
		
		mQuitBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
            	finish();
            }
		});
	} // onCreate()
	
	private void setGameSoundOn(boolean bSoundOn ){
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		Editor edit = preferences.edit();
		edit.putBoolean(GameState.G_SND_ONOFF_KEY,bSoundOn);
		Log.d("Anderson" , "Setting Game Sound " + bSoundOn );
		edit.commit();
	}
	
	
}
