package com.example.mycolorchooser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class OptionMainMenuActivity extends Activity {
	Button mSoundBtn; 
	Button mDifficultyBtn; 
	Button mQuitBtn; 
	
	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.option_main);
		
		mSoundBtn = (Button)findViewById(R.id.sound_btn);
		mDifficultyBtn = (Button)findViewById(R.id.difficult_btn);
		mQuitBtn = (Button)findViewById(R.id.quit_option_btn);
		
		
		mSoundBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
            	Intent intent = new Intent();
                intent.setClass(OptionMainMenuActivity.this, SoundOnOffOptionActivity.class);
                startActivity(intent);
            }
		});
		
		
		mDifficultyBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
            	Intent intent = new Intent();
                intent.setClass(OptionMainMenuActivity.this, DifficultyOptionActivity.class);
                startActivity(intent);
            }
		});
		
		
		mQuitBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
            	finish(); // back to main menu 
            }
		});
	} // onCreate() 
}
