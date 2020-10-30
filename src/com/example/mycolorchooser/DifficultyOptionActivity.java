package com.example.mycolorchooser;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class DifficultyOptionActivity extends Activity {
	Button mHardBtn; 
	Button mNormalBtn; 
	Button mEasyBtn; 
	
	private void saveGameLevel(int gameLevel ){
		if ( gameLevel == GameState.GAME_EASY ||
			gameLevel == GameState.GAME_NORMAL || 
			gameLevel == GameState.GAME_HARD ){	
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
			Editor edit = preferences.edit();
			Log.d("DifficultyOptionActivity", "Setting game difficulty key " + gameLevel);
			edit.putInt(GameState.G_DIFF_KEY,gameLevel);
			edit.commit();
		}
	}
	
	
	int getHighestHighestDifficultyAllowed(){ 
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		int highestLevelAllowed  = preferences.getInt(GameState.HIGHEST_LEVEL_KEY, 0); 
		return highestLevelAllowed;
	}
	
	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.difficulty_opt);
		
		mHardBtn = (Button)findViewById(R.id.hard_btn);
		mNormalBtn = (Button)findViewById(R.id.normal_btn);
		mEasyBtn = (Button)findViewById(R.id.easy_btn);
		
		
		mHardBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
            	int highestLevelAllowed = getHighestHighestDifficultyAllowed();
            	if ( highestLevelAllowed+1 >= GameState.GAME_HARD ){
            		saveGameLevel(GameState.GAME_HARD);
            		finish();
            	} else { 
            		 Toast.makeText(getApplicationContext(), "Win Normal Level first", Toast.LENGTH_LONG).show();
            	}
            }
		});
		
		
		mNormalBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
            	int highestLevelAllowed = getHighestHighestDifficultyAllowed();
            	if ( highestLevelAllowed+1 >= GameState.GAME_NORMAL ){
            		saveGameLevel(GameState.GAME_NORMAL);
            		finish();
            	} else { 
            		 Toast.makeText(getApplicationContext(), "Win Easy Level First", Toast.LENGTH_LONG).show();
            	}
            }
		});
		
		
		mEasyBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
            	saveGameLevel(GameState.GAME_EASY);
            	finish();
            }
		});
	} // onCreate() 
}
