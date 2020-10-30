package com.example.mycolorchooser;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.preference.PreferenceManager;

public class GameView extends SurfaceView implements Observer {

	private SurfaceHolder holder;
	private GameLoopThread gameLoopThread;
	
	
	private static final String TAG = "GameView";
	// dimension related parameters 
	public static int GB_CELL_WIDTH_MAX_NUM = 4;
	public static int GB_CELL_HEIGHT_MAX_NUM = 4;
	public static int GB_MAX_PENDING_OBJ = 16;
	public static int GB_NUM_PENDING_OBJ = 7;
	
	Paint paint;
	
	float XScaler = (float) 1.0;
	float YScaler = (float) 1.0;
	
	 private SoundPool soundPool;
	 private HashMap<Integer, Integer> soundsMap;
	
	
	GameState mGameState; // the game controller  
	
	private int screenWidth;
	private int screenHeight;
	
	
	private int matrixHeight;  
	private int matrixWidth; 
	
	private int instructionSecHeight;   
	private int instructionSecWidth;
	
	private int instructSecUpperSec; 
	private int instructSecLowerSec;

	private int cellHeight; 
	private int cellWidth; 
	
	private int hintBarHeight;
	private int hintYOffset; 
	private int hintCellHeight;
	private int hintCellWidth;
	
	private int intlCellPadding;
	
	private int hintCellPaddingSide;
	
	private int timeBarOffsetY; 
	private int timeBarHeight;
	private int timeBarWidth; 
	private int timeBarPaddingLeft; 
	
	// warning effect : 
	private static final boolean B_ENABLE_TINEBAR_WARNING = true; 
	private boolean bTimeBarDisappeared = false; 
	long lastTBDisappearTime = 0l;
	
	public static final int EROR_FRAME_NUMBER = 3;
	
	// TODO : handle atomicity problem 
	private volatile int bNextFrameWrongIndication = 0; 

	//Bitmap mStartQuitFirstBitmap = null;
	//Bitmap mStartQuitFirstBitmapScaled = null;
	
	Bitmap mBkgndBitmap = null;
	Bitmap mBkgndBitmapScaled = null; 
	
	
	Bitmap mWinBitmap = null;
	Bitmap mWinBitmapScaled = null;
	
	Bitmap mStartQuitBitmap = null;
	Bitmap mStartQuitBitmapScaled = null;
	
	Bitmap mAgainQuitBitmap = null;
	Bitmap mAgainQuitBitmapScaled = null;

	
	Bitmap mNextAgainQuitBitmap = null;
	Bitmap mNextAgainQuitBitmapScaled = null;

	Bitmap mOptionMenuBitmap = null;
	Bitmap mOptionMenuBitmapScaled = null;
	
	Bitmap mArrowBitmap = null;
	Bitmap mArrowBitmapScaled = null;
	
	// button related 
	Bitmap mStartButton = null;
	Bitmap mStartButtonScaled = null; 
	
	Bitmap mAgainButton = null; 
	Bitmap mAgainButtonScaled = null; 
	
	Bitmap mNextButton = null;
	Bitmap mNextButtonScaled = null;
	
	Bitmap mQuitButton = null;
	Bitmap mQuitButtonScaled = null;
	
	Bitmap mWinBkgndBitmapScaled = null;
	Bitmap mWinBkgndBitmap = null;
	
	Bitmap mGamePauseButtonBitmapScaled = null;
	Bitmap mGamePauseButtonBitmap = null;
	
	private static final int START_BUTTON = 1; 
	private static final int NEXT_BUTTON = 2; 
	private static final int AGAIN_BUTTON = 3;
    private static final int QUIT_BUTTON = 4;
	

	private static final int CLICK_ERROR_SOUND = 1;
	private static final int CLICK_RIGHT_SOUND = 2; 
	
	private Context mContext;
	
	private int TEXT_SIZE = 50;
	private int TEXT_SIZE_HIHT = 45;
	private int TIME_BAR_HEIGHT = 20;
	private int TIME_BAR_Y_OFFSET = 90;
	
	private boolean mShouldPlaySnd = false;
	
	Rect textBoundary = new Rect();
	int upperSectionOneTopY = 0;
	int upperSectionTwoTopY = 0;
	int upperSectionThreeTopY = 0;
	
	Typeface typeFaceDesc; 
	Typeface typeFaceSans; 
	Typeface typeFaceDigits;
	
	Rect mPauseButtonRect;

    
	
	public void playSound(int sound, float fSpeed) {
        AudioManager mgr = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
        float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
        float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volume = streamVolumeCurrent / streamVolumeMax;  
        
        soundPool.play(soundsMap.get(sound), volume, volume, 1, 0, fSpeed);
     }
	
	 
	
	public GameView(Context context ,GameState gs ) {
		super(context);
		mContext = context;
		paint = new Paint();
		
		typeFaceDesc = Typeface.createFromAsset(mContext.getAssets(),"fonts/action_man_bold.ttf");
		typeFaceSans = Typeface.createFromAsset(mContext.getAssets(),"fonts/free_sans.ttf");
		typeFaceDigits = Typeface.createFromAsset(mContext.getAssets(),"fonts/ds_digi.ttf");
		
		
		paint.setTypeface(typeFaceDesc);
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		mShouldPlaySnd = preferences.getBoolean(GameState.G_SND_ONOFF_KEY, GameState.G_SND_ONOFF_DEFAULT );
		 
		mPauseButtonRect = new Rect();
		mPauseButtonRect.top = 0;
		mPauseButtonRect.left = 0;
		mPauseButtonRect.bottom = 0;
		mPauseButtonRect.right = 0;
		
		
		mGameState = gs;
		//gameLoopThread = new GameLoopThread(this,gs);
		holder = getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				pause();
			}

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				resume();
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width,
					int height) {}
		});
        
        // Audio Related
        soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
        soundsMap = new HashMap<Integer, Integer>();
        soundsMap.put(CLICK_RIGHT_SOUND, soundPool.load(mContext, R.raw.click, 1));
        soundsMap.put(CLICK_ERROR_SOUND, soundPool.load(mContext, R.raw.error, 1));
        
	}
	
	
	public void pause(){
		
		gameLoopThread.setRunning(false);
		boolean bRetry = true;
		while( bRetry ){
			try{
				Log.d("Anderson" , "calling gameLoopThread.join()");
				gameLoopThread.join();
				
				bRetry = false;
			} catch ( InterruptedException e ){
				Log.d("Anderson" , "Interrupt exception occurred!!!");
			}
		}
	}
	
	public void resume(){
		if ( gameLoopThread == null || !gameLoopThread.isAlive()){
			gameLoopThread = new GameLoopThread(this,mGameState); 
			gameLoopThread.setRunning(true);
			gameLoopThread.start();
		}
	}
	
	void drawPauseButton(Canvas canvas , float offsetY  , float offsetX, float maxWidth , float maxHeight ){
		//Bitmap mGamePauseButtonBitmapScaled = null;
		// Bitmap mGamePauseButtonBitmap = null;
		if ( mGamePauseButtonBitmapScaled == null ) {
			if ( mGamePauseButtonBitmap == null ){  
				mGamePauseButtonBitmap = BitmapFactory.decodeResource(this.getResources(),R.drawable.dark_btn);
			}
			mGamePauseButtonBitmapScaled = Bitmap.createScaledBitmap(mGamePauseButtonBitmap,  (int)maxWidth, (int)maxHeight , true);
			if ( mGamePauseButtonBitmap != mGamePauseButtonBitmapScaled ){
				mGamePauseButtonBitmap.recycle();
				mGamePauseButtonBitmap = null;
			}
		}
		canvas.drawBitmap(mGamePauseButtonBitmapScaled, offsetX, offsetY , paint);
	}
	
	
	void drawAnimatedHintCircle(float topOffset , float heightAvail, Canvas canvas ){
		// public void drawCircle (float cx, float cy, float radius, Paint paint)
		paint.setColor(Color.RED);
		paint.setStyle(Style.FILL);
		int nCircleToDraw = 4- (((int) (( mGameState.getTimeLeftMs() % 2000 ) / 500 ) ) % 4);  
		int radius =  (int) (heightAvail/2)/2;
		for ( int i = 0 ; i < nCircleToDraw ; i++ ){
			canvas.drawCircle(2*radius + 3*i*radius , topOffset + heightAvail*3/4 , radius, paint);
		}
	}
	
	void renderFonts(Canvas canvas ) {
		if ( canvas == null ){
			return;
		}
		
		
		if ( mGameState.getGameState() == GameState.GS_RUNNING ){
			/*
			 upperSectionOneTopY = 0; 
			 upperSectionTwoTopY = instructSecUpperSec * 7 / 10;
			 upperSectionThreeTopY = instructSecUpperSec * 8 / 10;
			 */
			
			
			paint.setStyle(Style.FILL);
			
			double totalWidthD =  (screenWidth * 9 ) / 10;
			timeBarWidth = (int)(totalWidthD * (mGameState.getTimePercentageLeft()));
		/*
			canvas.drawText("Time : " + (int)(mGameState.getTimeLeftMs()/1000)  , 15*XScaler, 45*YScaler, paint);
			canvas.drawText("Score : " + (int)(mGameState.getTotalScore() )  , canvas.getWidth()/2 , 45*YScaler, paint);
			*/
			
			//typeFaceDesc = Typeface.createFromAsset(mContext.getAssets(),"fonts/action_man_bold.ttf");
			//typeFaceDigits = Typeface.createFromAsset(mContext.getAssets(),"fonts/ds_digi.ttf");
			//typeFaceSans = Typeface.createFromAsset(mContext.getAssets(),"fonts/free_sans.ttf");
			paint.setColor(Color.BLACK);
			paint.setTypeface(typeFaceDigits);
			paint.setTextSize(upperSectionTwoTopY/3);
			canvas.drawText("Time "   , 2 , upperSectionTwoTopY, paint);
			
			paint.setTextSize(upperSectionTwoTopY);
			canvas.drawText(" " + (int)(mGameState.getTimeLeftMs()/1000)  , canvas.getWidth()/5, upperSectionTwoTopY, paint);
			
			paint.setColor(Color.BLUE);
			paint.setTypeface(typeFaceSans);
			paint.setTextSize(upperSectionTwoTopY/4);
			canvas.drawText("Score "   , canvas.getWidth()/2 , upperSectionTwoTopY, paint);
			
			paint.setTextSize(upperSectionTwoTopY);
			canvas.drawText(" "  + (int)(mGameState.getTotalScore()) , (canvas.getWidth()*6)/10, upperSectionTwoTopY, paint);
			
			paint.setColor(Color.BLUE);
			paint.setTypeface(typeFaceDesc);
			
			paint.getTextBounds("T", 0, 1, textBoundary);
			// ANSON
			int timeBarBottomY = renderTimeBar(canvas ,  upperSectionTwoTopY , upperSectionThreeTopY  );
			
			
			if ( instructSecUpperSec - upperSectionThreeTopY > 50 ){
				if ( true ){
					drawAnimatedHintCircle( (float)upperSectionThreeTopY,  (instructSecUpperSec - upperSectionThreeTopY)/2 , canvas );
					// ANSON
					int INTERNAL_PADDING = 2;
					// void drawPauseButton(Canvas canvas , float offsetY  , float offsetX, float maxWidth , float maxHeight )
					drawPauseButton( canvas , (float)(upperSectionThreeTopY + INTERNAL_PADDING) , canvas.getWidth()*3/5, canvas.getWidth()/ 4, (instructSecUpperSec - upperSectionThreeTopY) - (2*INTERNAL_PADDING));
						
					if ( mPauseButtonRect.top == 0  ) {
						mPauseButtonRect.top = (upperSectionThreeTopY + INTERNAL_PADDING);
						mPauseButtonRect.left =  canvas.getWidth()*3/5; 
						mPauseButtonRect.bottom = instructSecUpperSec - (INTERNAL_PADDING);
						mPauseButtonRect.right = canvas.getWidth()*3/5 +  canvas.getWidth()/ 4;
						//Log.d("Anderson" , "Pause Button rect :" + mPauseButtonRect.top + " " +  mPauseButtonRect.left + " " + mPauseButtonRect.bottom + " " + mPauseButtonRect.right);
					}
					
					
				} else { 
					if ( mArrowBitmapScaled == null ){
						if ( mArrowBitmap == null ){
							mArrowBitmap = BitmapFactory.decodeResource(this.getResources(),
									R.drawable.arrow);
						}
						mArrowBitmapScaled = Bitmap.createScaledBitmap(mArrowBitmap, canvas.getWidth()/4, (instructSecUpperSec - timeBarBottomY)/2 , true);
						if ( mArrowBitmap != mArrowBitmapScaled ){
							mArrowBitmap.recycle();
							mArrowBitmap = null;
						}
					}
					canvas.drawBitmap(mArrowBitmapScaled, 0, timeBarBottomY + (instructSecUpperSec - timeBarBottomY)/2 , paint);
				}
			}
			
			// TODO : render 2 rounds of 
			//int getHintCellWidth = (instructSecLowerSec - 3*5) / 2; 
			renderHintList2Row(canvas,instructSecUpperSec,instructSecLowerSec);
			
			// Log.d("xxx","Full upper sec " + instructSecUpperSec + " Time row : " + (45*YScaler) + " Timer bar : " + timeBarBottomY    ); 
			
		} else if ( mGameState.getGameState() == GameState.GS_REACH_DEST ){
			
			paint.setColor(Color.BLUE);
			paint.setStyle(Style.FILL);
			paint.setTextSize(TEXT_SIZE);
			CharSequence cs = "You've Won!"; 
			canvas.drawText( cs.toString() , (canvas.getWidth() - paint.measureText(cs.toString()))/2, canvas.getHeight()/4, paint);
			cs = "Final Score : " + mGameState.getTotalScore();
			canvas.drawText( cs.toString() , (canvas.getWidth() - paint.measureText(cs.toString()))/2, (canvas.getHeight()*2)/4, paint);
			
			cs = "Highest Score : " + mGameState.getHighScoreHistory(0);
			canvas.drawText( cs.toString() , (canvas.getWidth() - paint.measureText(cs.toString()))/2, (canvas.getHeight()*3)/4, paint);
			
			
			
		} else if ( mGameState.getGameState() == GameState.GS_GAMEOVER ){
			//Typeface tf = Typeface.create("Helvetica",Typeface.BOLD);
			paint.setColor(Color.BLUE);
			paint.setStyle(Style.FILL);
			paint.setTextSize(TEXT_SIZE);
			
			CharSequence cs = "You've Lost!"; 
			canvas.drawText( cs.toString() , (canvas.getWidth() - paint.measureText(cs.toString()))/2, canvas.getHeight()/4, paint);
			cs = "Final Score : " + mGameState.getTotalScore();
			canvas.drawText( cs.toString() , (canvas.getWidth() - paint.measureText(cs.toString()))/2, (canvas.getHeight()*2)/4, paint);
			
			cs = "Highest Score : " + mGameState.getHighScoreHistory(0);
			canvas.drawText( cs.toString() , (canvas.getWidth() - paint.measureText(cs.toString()))/2, (canvas.getHeight()*3)/4, paint);
			
			
		
		} else if ( mGameState.getGameState() == GameState.GS_WAIT_START ){
			//Typeface tf = Typeface.create("Helvetica",Typeface.BOLD);
			//paint.setTypeface(tf);
			paint.setColor(Color.BLUE);
			paint.setStyle(Style.FILL);
			paint.setTextSize(TEXT_SIZE);
			
			CharSequence cs = "Level " + ( mGameState.getCurrLevel() + 1 ); 
			canvas.drawText( cs.toString() , (canvas.getWidth() - paint.measureText(cs.toString()))/2, canvas.getHeight()/4, paint);
			
			cs = "Click To Play!"; 
			canvas.drawText( cs.toString() , (canvas.getWidth() - paint.measureText(cs.toString()))/2, (canvas.getHeight()*2)/4, paint);
			if ( mGameState.getCurrLevel() != 0 ){
				cs = "Your Score : " + mGameState.getTotalScore(); 
				canvas.drawText( cs.toString() , (canvas.getWidth() - paint.measureText(cs.toString()))/2, (canvas.getHeight()*3)/4, paint);
			}
			
		} else if ( mGameState.getGameState() == GameState.GS_OPTION_MENU ){
			
		}
	}
	
	int renderTimeBar(Canvas canvas , int upperOffset , int lowestBottom ){
		int TIME_BAR_PADDING = 2; 
		if ( canvas == null ){
			return 0;
		}
	
		double timeLeftP = mGameState.getTimePercentageLeft();
		timeBarOffsetY = upperOffset+TIME_BAR_PADDING; 
		double totalWidthD =  (screenWidth * 9 ) / 10;
		timeBarWidth = (int)(totalWidthD * (timeLeftP));
		timeBarHeight = ( lowestBottom - upperOffset) - (2*TIME_BAR_PADDING); 
		timeBarPaddingLeft = 10;
		if ( timeBarWidth > timeBarPaddingLeft ){ 
			paint.setColor(Color.RED);
			Rect rect = new Rect(); 
			rect.top =  timeBarOffsetY;  
			rect.bottom =  timeBarOffsetY + timeBarHeight;
			rect.left = timeBarPaddingLeft; 
			rect.right = timeBarWidth; 
			long timeLeft = mGameState.getTimeLeftMs();
			//Log.d("Anderson" , "Time left is " + timeLeft );
			if ( B_ENABLE_TINEBAR_WARNING && ((timeLeft % 500) > 250) && timeLeft < 5000 ) {
				
			} else {
				canvas.drawRect( rect, paint );
			}
		
		}
		return upperOffset+timeBarHeight; 
		
	}
	
	RectF getPendingCellRectangle(int idx ){
		
		RectF rect = new RectF(); 
		rect.top =  instructionSecHeight + (idx/4)* cellHeight + intlCellPadding ; 
		rect.bottom =  rect.top +  cellHeight - intlCellPadding ; 
		rect.left = (idx%4) * cellWidth + intlCellPadding ; 
		rect.right = (idx%4) * cellWidth + cellWidth - intlCellPadding;
		return rect;
	}
		
	private boolean bIsWithinThisMatrixRect(int x , int y , int idx ){
		
		RectF rect = getPendingCellRectangle(idx);
		//Log.d("Anderson" , "Comparing " + x + " " + y + " " + rect.left + " " + rect.right + " " + rect.top + " " + rect.bottom);
		if ( x >= rect.left && x <= rect.right && y >= rect.top && y <= rect.bottom ){
			return true;
		} else {
			return false;
		}
	}
	
	private void renderHintList(Canvas canvas){
		if ( canvas == null ){
			return;
		}
		for ( int i = 0 ; i < GB_MAX_PENDING_OBJ ; i++ ){
    		int pendingObjIdx = mGameState.getProperty(GameState.PENDING_SEQ_TYPE, i ); // get which color index 
    		if ( pendingObjIdx == -1 ) {
    			break;
    		}
    		paint.setColor(mGameState.getProperty(GameState.COLOR_TYPE, pendingObjIdx));
    		Rect rect = new Rect(); 
    		rect.top =  hintYOffset;  
    		rect.bottom =  hintYOffset + hintCellHeight;
    		rect.left = hintCellPaddingSide*(i+1) + hintCellWidth*(i); 
    		rect.right = hintCellPaddingSide*(i+1) + hintCellWidth*(i+1);
    		canvas.drawRect( rect, paint );
    	}
    }
	
	
	private void renderHintList2Row(Canvas canvas , int YOffset , int sectionHeight ){
		if ( canvas == null ){
			return;
		}
		int hintCellWidth = (sectionHeight - 3*hintCellPaddingSide)/2;
		
		if ( mGameState.getGameDifficulty() == GameState.GAME_NORMAL || 
				mGameState.getGameDifficulty() == GameState.GAME_HARD) {
			// only shows the first 3 items for normal and hard mode.
			for ( int i = 0 ; i < GB_MAX_PENDING_OBJ ; i++ ){
				int pendingObjIdx = mGameState.getProperty(GameState.PENDING_SEQ_TYPE, i ); // get which color index 
				if ( pendingObjIdx == -1 ) {
					break;
				}
				if ( mGameState.getGameDifficulty() == GameState.GAME_NORMAL || 
						mGameState.getGameDifficulty() == GameState.GAME_HARD ){
					if ( i >= 3 ){
						break;
					}
				}
				
				paint.setColor(mGameState.getProperty(GameState.COLOR_TYPE, pendingObjIdx));
				Rect rect = new Rect(); 
    		
				if ( i < GB_MAX_PENDING_OBJ / 2 ){
					rect.top =  YOffset + hintCellPaddingSide;  
					rect.bottom =  YOffset + hintCellPaddingSide + hintCellWidth;
					rect.left = hintCellPaddingSide*(i+1) + hintCellWidth*(i); 
					rect.right = hintCellPaddingSide*(i+1) + hintCellWidth*(i+1);
				} else {
					rect.top =  YOffset + hintCellPaddingSide + hintCellPaddingSide + hintCellWidth ;  
					rect.bottom =  YOffset + hintCellPaddingSide + hintCellWidth + hintCellPaddingSide + hintCellWidth ;
					rect.left = hintCellPaddingSide*((i-(GB_MAX_PENDING_OBJ/2))+1) + hintCellWidth*((i-(GB_MAX_PENDING_OBJ/2))); 
    				rect.right = hintCellPaddingSide*((i-(GB_MAX_PENDING_OBJ/2))+1) + hintCellWidth*((i-(GB_MAX_PENDING_OBJ/2))+1);
				}
				canvas.drawRect( rect, paint );
			}
			
		} else if  (mGameState.getGameDifficulty() == GameState.GAME_EASY ){ 
			// Total number of objects for this round : 
			// ANSON
			int totalNumObjForthisRound = mGameState.getOrigNumOfObjForThisRound(); 
			int numOfPendingItemGone = totalNumObjForthisRound - mGameState.getProperty(GameState.PENDING_SEQ_SIZE , 0); 
			for ( int i = 0 ; i < GB_MAX_PENDING_OBJ ; i++ ){
				int pendingObjIdx = mGameState.getProperty(GameState.PENDING_SEQ_TYPE, i ); // get which color index 
				if ( pendingObjIdx == -1 ) {
					break;
				}
				if ( mGameState.getGameDifficulty() == GameState.GAME_NORMAL || 
						mGameState.getGameDifficulty() == GameState.GAME_HARD ){
					if ( i >= 3 ){
						break;
					}
				}
				
				paint.setColor(mGameState.getProperty(GameState.COLOR_TYPE, pendingObjIdx));
				Rect rect = new Rect(); 
				int newI = numOfPendingItemGone + i;
				if ( newI < GB_MAX_PENDING_OBJ / 2 ){
					rect.top =  YOffset + hintCellPaddingSide;  
					rect.bottom =  YOffset + hintCellPaddingSide + hintCellWidth;
					rect.left = hintCellPaddingSide*(newI+1) + hintCellWidth*(newI); 
					rect.right = hintCellPaddingSide*(newI+1) + hintCellWidth*(newI+1);
				} else {
					rect.top =  YOffset + hintCellPaddingSide + hintCellPaddingSide + hintCellWidth ;  
					rect.bottom =  YOffset + hintCellPaddingSide + hintCellWidth + hintCellPaddingSide + hintCellWidth ;
					rect.left = hintCellPaddingSide*((newI-(GB_MAX_PENDING_OBJ/2))+1) + hintCellWidth*((newI-(GB_MAX_PENDING_OBJ/2))); 
    				rect.right = hintCellPaddingSide*((newI-(GB_MAX_PENDING_OBJ/2))+1) + hintCellWidth*((newI-(GB_MAX_PENDING_OBJ/2))+1);
				}
				canvas.drawRect( rect, paint );
			}
			
		}
    }
	
	
	
    void renderPendingObjects(Canvas canvas ){
    	if ( canvas == null ){
			return;
		}
    	int[] emptyCellIndexArray = new int[GB_MAX_PENDING_OBJ];
    	
    	// draw the background 
    	
    	RectF rectBkgnd = new RectF(0,instructionSecHeight,screenWidth,screenHeight);
    	paint.setColor(Color.rgb(200,255,200));
    	canvas.drawRect(rectBkgnd, paint);
    	
    	ArrayList whichCellIsOccupiedArray = new ArrayList(); 
    	for ( int i = 0 ; i < GB_MAX_PENDING_OBJ ; i++ ){
    		int pendingObjIdx = mGameState.getProperty(GameState.PENDING_SEQ_TYPE, i ); // get which color index 
    		if ( pendingObjIdx == -1 ) {
    			break;
    		}
    		paint.setColor(mGameState.getProperty(GameState.COLOR_TYPE, pendingObjIdx));
    		
    		int placementIdx = mGameState.getProperty(GameState.PENDING_MAP_TYPE, i ); // get which color index 
    		whichCellIsOccupiedArray.add(placementIdx);
    		RectF rect = getPendingCellRectangle(placementIdx);
    		canvas.drawRoundRect( rect, 6, 6,  paint );
    	}
    	// draw remaining white cells 
    	
    	for ( int i = 0 ; i < GB_MAX_PENDING_OBJ ; i++ )  {
    		if ( !whichCellIsOccupiedArray.contains(i) ){
    			paint.setColor(Color.WHITE);
    			RectF rect = getPendingCellRectangle(i);
        		canvas.drawRoundRect( rect, 6, 6,  paint );
    		}
    	}
    }
	
	@Override
	protected void onDraw(Canvas canvas) {
		// draw the background 
		
		switch( mGameState.getGameState() ){
		case GameState.GS_OPTION_MENU:
			
			if ( mOptionMenuBitmapScaled == null ){
				if ( mOptionMenuBitmap == null ){
					 mOptionMenuBitmap = BitmapFactory.decodeResource(this.getResources(),
				                R.drawable.option_menu);
				}
				mOptionMenuBitmapScaled = Bitmap.createScaledBitmap(mOptionMenuBitmap, canvas.getWidth(), canvas.getHeight(), true);
				if ( mOptionMenuBitmap != mOptionMenuBitmapScaled ){
					mOptionMenuBitmap.recycle();
					mOptionMenuBitmap = null;
				}
			}
			if ( canvas != null && paint != null &&  mOptionMenuBitmapScaled != null){
				paint.setColor(Color.WHITE);
				canvas.drawBitmap(mOptionMenuBitmapScaled, 0, 0, paint);
			} else {
				if ( canvas == null ) Log.d(TAG,"canvas is null");
				if ( paint == null ) Log.d(TAG,"paint is null");
				if ( mOptionMenuBitmapScaled == null ) Log.d(TAG,"mOptionMenuBitmapScaled is null");
			}
			break;
		case GameState.GS_RUNNING:
			if ( bNextFrameWrongIndication > 0  ){
				paint.setColor(Color.RED);
				bNextFrameWrongIndication  -= 1; 
			} else {
				paint.setColor(Color.WHITE);
			}
			if ( canvas != null ){
				
				canvas.drawRect(0,0,canvas.getWidth(),canvas.getHeight(), paint);
				// 	draw the hint list 
				//renderHintList(canvas);
				// 	draw the pending object 
				renderPendingObjects(canvas);
				renderFonts(canvas);
				
				
			}
			break;
			
		case GameState.GS_WAIT_START:
			bNextFrameWrongIndication = 0;
			//Log.d(TAG,"Current Level is " + mGameState.getCurrLevel() );
			if ( mGameState.getCurrLevel() == 0 ){
				drawBackground(canvas);
				drawBottomButton((int)(canvas.getHeight()*0.88),0,canvas.getHeight(),canvas.getWidth()/2,START_BUTTON,canvas);
				drawBottomButton((int)(canvas.getHeight()*0.88),canvas.getWidth()/2,canvas.getHeight(),canvas.getWidth(),QUIT_BUTTON,canvas);
				
			} else {
				drawBackground(canvas);
				drawBottomButton((int)(canvas.getHeight()*0.88),0,canvas.getHeight(),(canvas.getWidth())/3,NEXT_BUTTON,canvas);
				drawBottomButton((int)(canvas.getHeight()*0.88),(canvas.getWidth())/3,canvas.getHeight(),(canvas.getWidth()*2)/3,AGAIN_BUTTON,canvas);
				drawBottomButton((int)(canvas.getHeight()*0.88),(canvas.getWidth()*2)/3,canvas.getHeight(),canvas.getWidth(),QUIT_BUTTON,canvas);
			}
			
			renderFonts(canvas);
			break;
		case GameState.GS_REACH_DEST:
			drawWinningBackground(canvas);
			drawBottomButton((int)(canvas.getHeight()*0.88),0,canvas.getHeight(),canvas.getWidth(),QUIT_BUTTON,canvas);
	    	renderFonts(canvas);
			break;
		case GameState.GS_GAMEOVER:
			drawBackground(canvas);
			drawBottomButton((int)(canvas.getHeight()*0.88),0,canvas.getHeight(),canvas.getWidth()/2,AGAIN_BUTTON,canvas);
			drawBottomButton((int)(canvas.getHeight()*0.88),canvas.getWidth()/2,canvas.getHeight(),canvas.getWidth(),QUIT_BUTTON,canvas);
			
			if ( canvas != null && paint != null &&  mAgainQuitBitmapScaled != null){
				paint.setColor(Color.WHITE);
				canvas.drawBitmap(mAgainQuitBitmapScaled, 0, 0, paint);
			}
			renderFonts(canvas);
			break;
		}
	}
	
	 private void computeDimension(int w , int h ){
		 

		 Log.d(TAG,"Screen height , width " + w + " " + h );
		 
		 screenWidth = w;
		 screenHeight = h;
		 
		 
		 YScaler = screenHeight / 720;
		 XScaler = screenWidth / 480;
		 matrixHeight = ( screenHeight * 6 ) / 10;
		 matrixHeight += (matrixHeight % 4) ; 
		 matrixWidth = screenWidth; 
	        
		 cellHeight = matrixHeight / 4; 
		 cellWidth  = matrixWidth / 4;
	    	
		 instructionSecHeight = screenHeight - matrixHeight; 
		 instructionSecWidth = screenWidth; 
	    	
		 instructSecUpperSec = instructionSecHeight * 3 / 5;
		 instructSecLowerSec = instructionSecHeight * 2 / 5;
		 
		 upperSectionOneTopY = 0; 
		 upperSectionTwoTopY = instructSecUpperSec * 6 / 10;
		 upperSectionThreeTopY = instructSecUpperSec * 7 / 10;
		 
		/* 
		 private int TEXT_SIZE = 50;
		 private int TEXT_SIZE_HIHT = 30;
		 private int TIME_BAR_HEIGHT = 20;
		 private int TIME_BAR_Y_OFFSET = 90;
		 */
		 
	     // to be tuned later 
		 TEXT_SIZE = (int)(XScaler * TEXT_SIZE);
		 TEXT_SIZE_HIHT = (int)(YScaler * TEXT_SIZE_HIHT);
		 TIME_BAR_HEIGHT =  (int)(YScaler * TIME_BAR_HEIGHT);
		 TIME_BAR_Y_OFFSET = (int)(YScaler * TIME_BAR_Y_OFFSET);
		 
		 hintBarHeight =(int)(YScaler * 30); 
		 hintYOffset = (int)(YScaler * 120 ); 
		 hintCellHeight = (int)(YScaler * 30); 
		 hintCellWidth = (int)(XScaler *30);
		 hintCellPaddingSide = (int)(10*XScaler);
		 intlCellPadding = (int)(4*XScaler);
		 
		 timeBarOffsetY = TIME_BAR_Y_OFFSET; 
		 timeBarWidth = (screenWidth * 8 ) / 10; 
		 timeBarHeight = (int)(YScaler * 20); 
		 timeBarPaddingLeft = 10;
		 
		
	}
	
	int getMatrixIndexFromCoordinate(int x , int y ){
		int index = -1; 
		for (int i = 0 ; i < GB_MAX_PENDING_OBJ ; i++ ){
			if (bIsWithinThisMatrixRect(x,y,i) == true){
				Log.d("Anderson" , "Matrix Index " + i + " Pressed!"  );
				return i;
			}
		}
		
		return -1;
	}
	 
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh){
		super.onSizeChanged(w, h, oldw, oldh);
		//mGameOverBitmap = Bitmap.createScaledBitmap(mGameOverBitmap, w, h, true);
		computeDimension(w,h);
		mGameState.onSizeChanged(w, h);
	} 
	
	
	public static final int BUTTON_ID_NONE = 0; 
	public static final int BUTTON_ID_QUIT = 1; 
	public static final int BUTTON_ID_AGAIN = 2; 
	public static final int BUTTON_ID_NEXT = 3;
	public static final int BUTTON_ID_START = 4;
	public static final int BUTTON_ID_OPTION = 5; 
	public static final int BUTTON_ID_EASY = 6;
	public static final int BUTTON_ID_NORMAL = 7;
	public static final int BUTTON_ID_HARD = 8;
	
	private int getClickedButtonType(float x , float y , int w , int h ){ 
		float wf = (float)w;
		float wh = (float)h;
		float heightFraction = y/h;
		float widthFraction = x/w;
	
		if ( mGameState.getGameState() == GameState.GS_OPTION_MENU ){
            // full Width : 536 x 595 
			// Easy : Height : 104 - 196 , Width : 155 - 379  
			// Normal : Height : 255 - 350 , Width : 155 - 379
			// Hard : Height : 407 - 500 , Width : 155 - 379
			if ( widthFraction >= 0.28 && widthFraction <= 0.71 ){
				if ( heightFraction >= 0.17 &&  heightFraction <= 0.33 ){
					return BUTTON_ID_EASY;
				} else if ( heightFraction >= 0.42 &&  heightFraction <= 0.59 ){
					return BUTTON_ID_NORMAL;
				} else if ( heightFraction >= 0.68 &&  heightFraction <= 0.84 ){
					return BUTTON_ID_HARD;
				}
			}
			
		} else if ( mGameState.getGameState() == GameState.GS_WAIT_START ){
			if ( mGameState.getCurrLevel() == 0 ){
				// 2-button case , [START,QUIT]
				if ( heightFraction > 0.88 ){
					if ( widthFraction > 0.5 ){
						return BUTTON_ID_QUIT;
					} else {
						return BUTTON_ID_START; 
					}
				} else if ( heightFraction >= 0.7 && heightFraction <= 0.83  ){
					if ( widthFraction >= 0.3 && widthFraction <= 0.7 ){
						return BUTTON_ID_OPTION;
					}
				}
			} else {
				// 3-button case , [NEXT,AGAIN,QUIT]
				if ( heightFraction > 0.88 ){
					if ( widthFraction < 0.33 ){
						return BUTTON_ID_START;
					} else if ( widthFraction > 0.66 ){
						return BUTTON_ID_QUIT; 
					} else {
						return BUTTON_ID_AGAIN;
					}
				}
			}
			
		} else if (mGameState.getGameState() == GameState.GS_REACH_DEST  ){
			// 2-button case , [AGAIN,QUIT]
			if ( heightFraction > 0.88 ){
				/*
				if ( widthFraction < 0.33 ){
					return BUTTON_ID_START;
				} else if ( widthFraction > 0.66 ){
					return BUTTON_ID_QUIT; 
				} else {
					return BUTTON_ID_AGAIN;
				} 
				*/
				return BUTTON_ID_QUIT;
			}
		} else if (mGameState.getGameState() == GameState.GS_GAMEOVER  ){
			// 2-button case , [AGAIN,QUIT]
			if ( heightFraction > 0.88 ){
				if ( widthFraction < 0.5 ){
					return BUTTON_ID_AGAIN;
				} else {
					return BUTTON_ID_QUIT;
				}
			}
		}
		
		return BUTTON_ID_NONE; 
	}
	
	
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	float eventX = event.getX();
    	float eventY = event.getY();
    	
    	switch (event.getAction()) {
    	case MotionEvent.ACTION_DOWN:
    		if ( mGameState.getGameState() == GameState.GS_RUNNING ){

    			// boolean bIsWithinRect( int x , int y , Rect boundary){
    			if (bIsWithinRect( (int)eventX , (int)eventY, mPauseButtonRect )  == true ){
    				// Pause Button Pressed
    				mGameState.saveCurrentGameState();
    				Intent intent = new Intent(mContext , GamePauseScreenActivity.class);
    				mContext.startActivity(intent);
    				((Activity)mContext).finish();
    				return true;	
    			}
    			int cellIdx = getMatrixIndexFromCoordinate((int)eventX, (int)eventY);
    			int keyEvent = mGameState.handleUserClickOnIndex(cellIdx);
    			if ( keyEvent == GameState.CLICK_EVENT_WRONG ){
    				if ( mShouldPlaySnd ){
    					playSound(CLICK_ERROR_SOUND, 1);
    				} 
    				bNextFrameWrongIndication = EROR_FRAME_NUMBER;
    			} else if ( keyEvent == GameState.CLICK_EVENT_RIGHT){
    				if ( mShouldPlaySnd ){
    					
    					//CellDisappearAnimation.getInstance().startDisappearAnim(cellIdx,);
    					playSound(CLICK_RIGHT_SOUND, 1);
    				}
					if ( mGameState.getGameState() == GameState.GS_RUNNING &&  
        					mGameState.getGameDifficulty() == GameState.GAME_HARD ){
        				mGameState.rearrangePendingCellPosition();
        			}
    			} 
    		} else if ( mGameState.getGameState() == GameState.GS_WAIT_START || 
    			mGameState.getGameState() == GameState.GS_GAMEOVER || 
    			mGameState.getGameState() == GameState.GS_REACH_DEST ||
    			mGameState.getGameState() == GameState.GS_OPTION_MENU ){
    				int buttonType = getClickedButtonType(eventX , eventY , screenWidth , screenHeight ); 
    				Log.d(TAG,"Anderson button type " +  buttonType + " Clicked");
    				mGameState.handleButtonSelectionButtonClick(buttonType);
    		} 
    		return true;
    	case MotionEvent.ACTION_MOVE:        
    		break;
    	case MotionEvent.ACTION_UP:
    		// nothing to do
    		break;
    	default:
    		return false;
      }
//      invalidate();
    	
      return super.onTouchEvent(event);
      
    }
	
    
     
    
	private void drawBottomButton(int top , int left , int bottom , int right , int buttonID , Canvas canvas ){
		
		
		Bitmap imgScaled = null;
		Bitmap imgOriginal = null;
		 
		int  resourceID = 0; 
		if(!( buttonID == START_BUTTON || buttonID == NEXT_BUTTON ||
			buttonID == AGAIN_BUTTON || buttonID == QUIT_BUTTON ) ){
			return;
		}
		switch( buttonID ){
		case START_BUTTON:
			imgScaled = mStartButtonScaled; 
			imgOriginal = mStartButton;  
			//resourceID
			resourceID = R.drawable.start_btn;
			break;
		case NEXT_BUTTON:
			imgScaled = mNextButtonScaled; 
			imgOriginal = mNextButton;  
			//resourceID 
			resourceID = R.drawable.next_btn;
			break;
		case AGAIN_BUTTON:
			imgScaled = mAgainButtonScaled; 
			imgOriginal = mAgainButton; 
			resourceID = R.drawable.again_btn;
			break;
		case QUIT_BUTTON:
			imgScaled = mQuitButtonScaled; 
			imgOriginal = mQuitButton;
			resourceID = R.drawable.main_btn;
			break;
		}
		
		
		if ( imgScaled == null ){
			if ( imgOriginal == null ) {
				imgOriginal= BitmapFactory.decodeResource(this.getResources(),resourceID);
				
			}
			imgScaled = Bitmap.createScaledBitmap(imgOriginal,  (int)(right-left) ,(int)(bottom-top), true);
			
		} else if ( imgScaled.getHeight() != (bottom-top) ||  imgScaled.getWidth() != (right-left)  ){
			imgScaled.recycle(); 
			imgScaled = null;
			if ( imgOriginal == null ) {
				imgOriginal= BitmapFactory.decodeResource(this.getResources(),resourceID);
			}
			imgScaled = Bitmap.createScaledBitmap(imgOriginal,  (int)(right-left) ,(int)(bottom-top), true);
		}
		if ( buttonID == START_BUTTON ){
			
			mStartButtonScaled = imgScaled;
		} else if ( buttonID == NEXT_BUTTON ){
			mNextButtonScaled = imgScaled;
		} else if ( buttonID == AGAIN_BUTTON ){
			mAgainButtonScaled = imgScaled;
		} else if ( buttonID == QUIT_BUTTON ){
			mQuitButtonScaled = imgScaled;
		}
		if ( imgScaled != imgOriginal && imgOriginal != null ){
			imgOriginal.recycle();
			imgOriginal = null;
		}
		if ( canvas != null && paint != null && imgScaled != null ){
			canvas.drawBitmap(imgScaled, left , top, paint);
		}
		
		
	}
	 
	void drawBackground(Canvas canvas){
		if ( mBkgndBitmapScaled == null ){
			if (mBkgndBitmap == null ){
				mBkgndBitmap = BitmapFactory.decodeResource(this.getResources(),R.drawable.bkgnd2);
			} 
			mBkgndBitmapScaled = Bitmap.createScaledBitmap(mBkgndBitmap, canvas.getWidth(), canvas.getHeight(), true);
		}
		if ( mBkgndBitmapScaled != mBkgndBitmap && mBkgndBitmap != null ){
			mBkgndBitmap.recycle(); 
			mBkgndBitmap = null; 
		}
		if ( canvas != null && paint != null && mBkgndBitmapScaled != null ){
			canvas.drawBitmap(mBkgndBitmapScaled, 0 , 0, paint);
		}
	}

	void drawWinningBackground(Canvas canvas){
		if ( mWinBkgndBitmapScaled == null ){
			if (mWinBkgndBitmap == null ){
				mWinBkgndBitmap = BitmapFactory.decodeResource(this.getResources(),R.drawable.win_bkgnd);
			} 
			mWinBkgndBitmapScaled = Bitmap.createScaledBitmap(mWinBkgndBitmap, canvas.getWidth(), canvas.getHeight(), true);
		}
		if ( mWinBkgndBitmapScaled != mWinBkgndBitmap && mWinBkgndBitmap != null ){
			mWinBkgndBitmap.recycle(); 
			mWinBkgndBitmap = null; 
		}
		if ( canvas != null && paint != null && mWinBkgndBitmapScaled != null ){
			canvas.drawBitmap(mWinBkgndBitmapScaled, 0 , 0, paint);
		}
	}
	
	public void update(Observable o, Object arg) {
		if ( arg != null ){
			if ( "snd_on".equals((String)arg)){
				Log.d("Anderson" , "Click Sound is on!");
				mShouldPlaySnd = true;
			} else if  ( "snd_off".equals((String)arg)){
				Log.d("Anderson" , "Click Sound is off!");
				mShouldPlaySnd = false;
			}
		}
	  }
	
	boolean bIsWithinRect( int x , int y , Rect boundary){
		if ( x >= boundary.left && x <= boundary.right && y >= boundary.top && y <= boundary.bottom ){ 
			return true;
		} 
		return false;
		
	}
	
	public void drawSmallerCell( int posIdx , int colorIdx, float proportion , Canvas cv ){
		RectF rect = getPendingCellRectangle(posIdx);
		float centerX = (rect.left + rect.right)/2; 
		float centerY = (rect.top + rect.bottom)/2;
		float height = rect.bottom - rect.top;
		float width = rect.right - rect.left; 
		RectF rectFinal = new RectF(); 
		rectFinal.top = centerY - ((height * proportion ) / 2);
		rectFinal.bottom = centerY + ((height * proportion ) / 2);
		
		rectFinal.left = centerX - ((width * proportion ) / 2);
		rectFinal.right = centerX + ((width * proportion ) / 2);
		
		cv.drawRoundRect( rectFinal, 6, 6,  paint );
		
	}
	
	
	
}
	
