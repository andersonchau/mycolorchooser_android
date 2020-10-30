package com.example.mycolorchooser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
// Today objective : 
// [1] 
// [2] 

public class MainMenuActivity extends Activity {
	
	Button mStartBtn; 
	Button mOptionBtn; 
	Button mQuitBtn; 
	
	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_menu_layout);
		
		mStartBtn = (Button)findViewById(R.id.start_btn);
		mOptionBtn = (Button)findViewById(R.id.option_btn);
		mQuitBtn = (Button)findViewById(R.id.end_btn);
		
		
		mStartBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
            	// TODO : start the game here
            	Intent intent = new Intent();
                intent.setClass(MainMenuActivity.this, GameRoundSelectionActivity.class);
                startActivity(intent);
                finish();	
            }
		});
		 
		mOptionBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
            	Intent intent = new Intent();
                intent.setClass(MainMenuActivity.this, OptionMainMenuActivity.class);
                startActivity(intent);
                
            }
		});
		
		
		mQuitBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
            	finish();
            }
		});
	}
	
	
	
	
}
