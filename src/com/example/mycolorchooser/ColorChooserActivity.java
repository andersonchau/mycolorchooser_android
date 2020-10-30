package com.example.mycolorchooser;

import java.util.Observer;

import android.os.Build;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class ColorChooserActivity extends Activity {
	
	GameView mGameView;
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	/*	
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
      */  
		
		View decorView = getWindow().getDecorView();
		// Hide the status bar.
		int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
		decorView.setSystemUiVisibility(uiOptions);
		// Remember that you should never show the action bar if the
		// status bar is hidden, so hide that too if necessary.
		ActionBar actionBar = getActionBar();
		actionBar.hide();
		
		
		Intent intent = getIntent();
		
		int startGameType = intent.getIntExtra("start_type",GameConstant.NEW_GAME);
		int startRound = 0;
		if ( startGameType == GameConstant.NEW_GAME ){
			startRound = intent.getIntExtra("start_round",0);
		}
		// TODO : check if thereis valid data
		
		GameState gs = new GameState(this,startGameType, startRound);
		mGameView = new GameView(this,gs);
		setContentView(mGameView);
		gs.addObserver((Observer) mGameView);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_my_color_chooser, menu);
		return true;
	}
	
	@Override
	protected void onResume(){ 
		
		super.onResume();
		mGameView.resume();
		
	}
}

