package com.example.mycolorchooser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class GameRoundSelectionActivity extends Activity {
	Button levelOneBtn;
	Button levelTwoBtn;
	Button levelThreeBtn;
	Button levelFourBtn;
	Button levelFiveBtn;
	
	
	MyColorChooserApp myApp;
	
	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.round_layout);
		
		levelOneBtn = (Button)findViewById(R.id.level_1_btn);
		levelTwoBtn = (Button)findViewById(R.id.level_2_btn);
		levelThreeBtn = (Button)findViewById(R.id.level_3_btn);
		levelFourBtn = (Button)findViewById(R.id.level_4_btn);
		levelFiveBtn = (Button)findViewById(R.id.level_5_btn);
		
		myApp = (MyColorChooserApp) getApplication();
		
		levelOneBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
            	Intent intent = new Intent(GameRoundSelectionActivity.this, ColorChooserActivity.class);
                intent.setClass(getApplicationContext(), ColorChooserActivity.class);
                intent.putExtra("start_type", GameConstant.NEW_GAME);
                intent.putExtra("start_round", 0);
                startActivity(intent);
            	
            	finish();
            }
		});
		
		
		levelTwoBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

            	Intent intent = new Intent(GameRoundSelectionActivity.this, ColorChooserActivity.class);            	
                intent.setClass(getApplicationContext(), ColorChooserActivity.class);
                intent.putExtra("start_type", GameConstant.NEW_GAME);
                intent.putExtra("start_round", 1);
                startActivity(intent);     
            	finish();
            	
            }
		});
		
		
		levelThreeBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
            	Intent intent = new Intent(GameRoundSelectionActivity.this, ColorChooserActivity.class);
                intent.setClass(getApplicationContext(), ColorChooserActivity.class);
                intent.putExtra("start_type", GameConstant.NEW_GAME);
                intent.putExtra("start_round", 2);
                startActivity(intent);
            	finish();
            }
		});
		
		levelFourBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
            	Intent intent = new Intent(GameRoundSelectionActivity.this, ColorChooserActivity.class);
                intent.setClass(getApplicationContext(), ColorChooserActivity.class);
                intent.putExtra("start_type", GameConstant.NEW_GAME);
                intent.putExtra("start_round", 3);
                startActivity(intent);
            	finish();
            }
		});
		
		levelFiveBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
            	Intent intent = new Intent(GameRoundSelectionActivity.this, ColorChooserActivity.class);
                intent.setClass(getApplicationContext(), ColorChooserActivity.class);
                intent.putExtra("start_type", GameConstant.NEW_GAME);
                intent.putExtra("start_round", 4);
                startActivity(intent);
            	finish();
            }
		});
	} // onCreate() 
}
