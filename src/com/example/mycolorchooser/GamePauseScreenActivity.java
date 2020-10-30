package com.example.mycolorchooser;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class GamePauseScreenActivity extends Activity {
	Button resumeButton; 
	Button againButton; 
	Button mainMenuButton; 
	
	
	
	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pause_opt);
		
		resumeButton = (Button)findViewById(R.id.pause_resume_btn);
		againButton = (Button)findViewById(R.id.pause_again_btn);
		mainMenuButton = (Button)findViewById(R.id.pause_main_btn);
		
		
		resumeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
            	Intent intent = new Intent(GamePauseScreenActivity.this, ColorChooserActivity.class);
                intent.setClass(getApplicationContext(), ColorChooserActivity.class);
                intent.putExtra("start_type", GameConstant.RESUME_GAME);
                startActivity(intent);     
            	finish();
            }
		});
		
		
		againButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
            	Intent intent = new Intent(GamePauseScreenActivity.this, ColorChooserActivity.class);
                intent.setClass(getApplicationContext(), ColorChooserActivity.class);
                intent.putExtra("start_type", GameConstant.RESUME_SAME_ROUND);
                startActivity(intent);     
            	finish();
            	
            }
		});
		
		
		mainMenuButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
            	Intent intent = new Intent(GamePauseScreenActivity.this, MainMenuActivity.class);
            	intent.setClass(getApplicationContext(), MainMenuActivity.class);
            	startActivity(intent);
            	finish();
            }
		});
	} // onCreate() 
}
