package com.example.mycolorchooser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Rect;
import android.preference.PreferenceManager;
import android.util.Log;

public class GameState extends Observable {
	
	/*
	 First Screen : 
	 */
	
	
	
	public static final String HIGHEST_SCORE_KEY = "highest_score_key";
	public static final String HIGHEST_LEVEL_KEY = "higest_level_key";  
	
	public static int GB_CELL_WIDTH_MAX_NUM = 4;
	public static int GB_CELL_HEIGHT_MAX_NUM = 4;
	public static int GB_MAX_PENDING_OBJ = 16;
	public static int GB_NUM_PENDING_OBJ = 7;
	

	public static final int COLOR_TYPE = 1;  
	public static final int PENDING_SEQ_TYPE = 2;
	public static final int PENDING_MAP_TYPE = 3;
	public static final int PENDING_SEQ_SIZE = 4;
	
	public static final int GS_WAIT_START = 1;
	public static final int GS_RUNNING = 2; 
	public static final int GS_GAMEOVER = 3;
	public static final int GS_REACH_DEST = 4;
	public static final int GS_OPTION_MENU = 5;
	public static final int GS_RESUMING = 6;
	
	Rect mPauseButtonRect; 
	
	public static final int GAME_EASY = 1;
	public static final int GAME_NORMAL = 2; 
	public static final int GAME_HARD = 3; 
	
	public static final String G_DIFF_KEY = "game_diff_key";
	public static final int G_DIFF_DEFAULT = GAME_EASY;
	
	public static final String G_SND_ONOFF_KEY = "sound_on_key";
	public static final boolean G_SND_ONOFF_DEFAULT = true;
	
	
	private int gameDifficulty;
    private int currLevel; 
    private int currGStage;
    
    
    private int mThisRoundScore = 0;
    private int mPrevRoundScore = 0;
    
	private static final String TAG = "GState";
	
	private ArrayList<Integer> colorIndexList; 
	// index to the location in the game map. 
	private ArrayList<Integer> pendingMap;   
	// color sequence 
	
	private ArrayList<Integer> pendingSequence;  
	
    
	
	Level levelData;
	
	Context mContext;
	
	long gameFullPlayingTime; 
	long levelStartTime; 
	boolean isFirstPagePassed; 
	volatile AtomicBoolean bGameHasEnded;  
	
	
	
	public int getFullPlayingTime(){
		return (int)(gameFullPlayingTime/1000);
	}
	
	void loadColorListIndex(){
		colorIndexList = new ArrayList<Integer>(); 
		/*
		colorIndexList.add(Color.BLACK);
		colorIndexList.add(Color.BLUE);
		colorIndexList.add(Color.CYAN);
		colorIndexList.add(Color.RED);
		colorIndexList.add(Color.YELLOW);
		colorIndexList.add(Color.GREEN);
		colorIndexList.add(Color.MAGENTA);
		colorIndexList.add(Color.LTGRAY);
		colorIndexList.add(Color.DKGRAY);
		colorIndexList.add(Color.rgb(204,255,51));
		colorIndexList.add(Color.rgb(184,61,61));
		colorIndexList.add(Color.rgb(224,0,224));
		colorIndexList.add(Color.rgb(51,51,0));
		colorIndexList.add(Color.rgb(209,71,255));
		colorIndexList.add(Color.rgb(92,255,173));
		colorIndexList.add(Color.rgb(255,99,20));
		*/
		
		colorIndexList.add(Color.rgb(255,51,153));
		colorIndexList.add(Color.rgb(0,0,255));
		colorIndexList.add(Color.rgb(0,204,255));
		colorIndexList.add(Color.rgb(0,150,0));
		colorIndexList.add(Color.YELLOW);
		colorIndexList.add(Color.rgb(143,255,199));
		colorIndexList.add(Color.rgb(102,0,102));
		colorIndexList.add(Color.rgb(204,0,0));
		colorIndexList.add(Color.rgb(190,255,51));
		colorIndexList.add(Color.rgb(0,255,51));
		colorIndexList.add(Color.rgb(184,61,61));
		colorIndexList.add(Color.rgb(0,204,153));
		colorIndexList.add(Color.rgb(51,51,0));
		colorIndexList.add(Color.rgb(209,71,255));
		colorIndexList.add(Color.rgb(92,255,173));
		colorIndexList.add(Color.rgb(255,99,20));
		
		
	}
	
	
	void loadListWithRandomSequence( ArrayList<Integer> myList , int maxNumber , int numCandidate ){
		if ( true ){
			Log.d("Anderson" , "maxNumber and number Candidate is " + maxNumber + " " + numCandidate );
			loadColorListIndex();
		
			if ( numCandidate > maxNumber ){
				Log.e(TAG , "number of candidate is too big!");
				return; // Error case !
			} 
			ArrayList<Integer> inputAR = new ArrayList<Integer>();
			for ( int i = 0 ; i < maxNumber ; i++ ){
				inputAR.add( new Integer(i) );
			}
			Random rnd = new Random();
			for( int i = 0; i < numCandidate; i++ ) {
				int idx = rnd.nextInt(inputAR.size() );  
				myList.add(inputAR.get(idx));
				inputAR.remove(idx);
			}
		} else {
			Log.d("Anderson" , "maxNumber and number Candidate is " + maxNumber + " " + numCandidate );
			loadColorListIndex();
		
			if ( numCandidate > maxNumber ){
				Log.e(TAG , "number of candidate is too big!");
				return; // Error case !
			} 
			ArrayList<Integer> inputAR = new ArrayList<Integer>();
			for ( int i = 0 ; i < maxNumber ; i++ ){
				inputAR.add( new Integer(i) );
			}
			
			for( int i = 0; i < numCandidate; i++ ) {
				  
				myList.add(i);
				
			}
		}
	}
	
	public int getProperty( int listType , int listIdx ){
		ArrayList<Integer> activeList = null;
		switch( listType ){
		case PENDING_SEQ_SIZE:
			return pendingSequence.size();
		case COLOR_TYPE:
			activeList = colorIndexList;
			break;
		case PENDING_SEQ_TYPE:
			activeList = pendingSequence;
			break;
		case PENDING_MAP_TYPE:
			activeList = pendingMap;
			break;
		}
		
		if ( activeList == null ){ 
			return -1;
		}
		if ( activeList.size() >= listIdx + 1  ){
			return activeList.get(listIdx);
		}
		return -1;
	}
	
	
	public void initElements(){

		pendingMap = new ArrayList<Integer>();
		pendingSequence = new ArrayList<Integer>(); 
		// note : 10 is the number of color inserted 
		loadListWithRandomSequence(pendingSequence, 16, levelData.getNumOfSelector(currLevel));
		loadListWithRandomSequence(pendingMap, GB_MAX_PENDING_OBJ, levelData.getNumOfSelector(currLevel) );
		
		Log.d( TAG , "Dumping pendingSequence " + pendingSequence.toString()  );
		Log.d( TAG , "Dumping pendingMap " + pendingMap.toString()  );
	}
	// for new game 
	public GameState(Context ctx , int mode , int roundNum ){
		
		 
		mContext = ctx;
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
		boolean haveSavedGame = preferences.getBoolean(SaveGameStateHandler.GAME_SAVE_AVAIL_KEY, false );
		Log.d("Anderson" , "haveSavedGame " + haveSavedGame );
		if ((  mode == GameConstant.RESUME_GAME ||  mode == GameConstant.RESUME_SAME_ROUND )&& haveSavedGame  ){
			Log.d("Anderson" , "Resuming Game " + haveSavedGame );
			// ANSON
			SaveGameStateHandler saveHandler = new SaveGameStateHandler(mContext);
			saveHandler.loadSavedData();
			currLevel = saveHandler.getRound();
			gameDifficulty = saveHandler.getLevel();
			
			
			
			bGameHasEnded = new AtomicBoolean(false);
			isFirstPagePassed = true;
			levelData = new Level(gameDifficulty);
			loadColorListIndex();
			mPrevRoundScore = saveHandler.getScorePrev();
			levelStartTime = System.currentTimeMillis();
			if ( mode == GameConstant.RESUME_GAME ){
				mThisRoundScore = saveHandler.getScoreThis();
				pendingMap = saveHandler.getBoardPosition();
				pendingSequence = saveHandler.getColorSequence(); 
				gameFullPlayingTime = saveHandler.getRemainingTime();
			} else { 
				// restarting the with same level 
				mThisRoundScore = 0;
				initElements();
				gameFullPlayingTime = levelData.getTotalTimeOfLevelMs(currLevel);
			}
			
			currGStage = GS_RUNNING;
			handleAudioOnOffSwitch();
		}  else { 
			// new game 
			Log.d("Anderson" , "New game");
			currLevel = roundNum; 
			mThisRoundScore = 0;
			mPrevRoundScore = 0;
			isFirstPagePassed = false; 
			
			bGameHasEnded = new AtomicBoolean(false);
			gameDifficulty = getSelectedGameLevel(); 
			currGStage = GS_WAIT_START;
			
			levelData = new Level(gameDifficulty);
			EnterGameState(GS_RUNNING);
		}
				    
		
	}
	
	public int getOrigNumOfObjForThisRound(){
		return levelData.getOrigNumObjFromLevel(currLevel);
	}
	
	public void setGameEnded(){
		bGameHasEnded.set(true);
	}
	
	public boolean getGameEndStatus(){
		return bGameHasEnded.get();
	}
	
	public void onSizeChanged(int w, int h ){

	}
	
	private boolean bIsIncorrectColor(int cellIdx){
		if ( pendingMap.size() > 1 ){
			for ( int i = 1 ; i < pendingMap.size() ; i++ ){
				if ( pendingMap.get(i) == cellIdx ){
					return true;
				}
			}
		}
		return false;
	}
	
	public double getTimePercentageLeft() {
		double gameFullPlayingTimeD = (double)gameFullPlayingTime;
		double timeLeftD = (double)(gameFullPlayingTime - (System.currentTimeMillis()-levelStartTime));
		//Log.d("Anderson", "getTimePercentageLeft " + gameFullPlayingTime  + " " + (double)(timeLeftD/gameFullPlayingTimeD));
		return (double)(timeLeftD /  gameFullPlayingTimeD);
	}
	
	public long getTimeLeftMs(){
		//Log.d("Anderson" , "getTimeleftMs returning " + (gameFullPlayingTime - (System.currentTimeMillis()-levelStartTime)));
		return gameFullPlayingTime - (System.currentTimeMillis()-levelStartTime);
	} 
	
	
	
	public int handleButtonSelectionButtonClick(int buttonID ){
		switch ( buttonID ){
		case GameView.BUTTON_ID_OPTION:
			EnterGameState(GS_OPTION_MENU);
			break;
		case GameView.BUTTON_ID_EASY:
			gameDifficulty = GAME_EASY;
			EnterGameState(GS_WAIT_START);
			break;
		case GameView.BUTTON_ID_NORMAL:
			gameDifficulty = GAME_NORMAL;
			EnterGameState(GS_WAIT_START); 
			break;
		case GameView.BUTTON_ID_HARD:
			gameDifficulty = GAME_HARD;
			EnterGameState(GS_WAIT_START);
			break;
		case GameView.BUTTON_ID_QUIT:
			bGameHasEnded.set(true);
			// start main page here. 
			Intent intent = new Intent();
            intent.setClass(mContext, MainMenuActivity.class);
            mContext.startActivity(intent);
			((Activity) mContext).finish();
			break;
		case GameView.BUTTON_ID_START:
			if ( currLevel == 0 ){
				levelData = new Level(gameDifficulty);
				initElements();
			}
			EnterGameState(GS_RUNNING);
			break;
		case GameView.BUTTON_ID_AGAIN:
			if (currGStage==GS_WAIT_START ){
				mThisRoundScore = 0;
				if ( currLevel > 0 ) {
					currLevel--;
				} 
				EnterGameState(GS_RUNNING);
			} else if ( currGStage == GS_REACH_DEST ){
				
				currLevel = Level.NUMBER_OF_LEVEL; 
				mThisRoundScore = 0;
				EnterGameState(GS_RUNNING);
			} else if ( currGStage == GS_GAMEOVER){
				// start from the beginning
				mThisRoundScore = 0;
				
				EnterGameState(GS_RUNNING);
			}
			break;
		case GameView.BUTTON_ID_NEXT:
			if ( currGStage == GS_WAIT_START || currGStage == GS_REACH_DEST ) {
				mPrevRoundScore += mThisRoundScore; 
				mThisRoundScore = 0; 
				EnterGameState(GS_RUNNING);
			}
			break;
		}
		return 0;
	}
	
	
	public static int CLICK_EVENT_NONE = 0;
	public static int CLICK_EVENT_RIGHT = 1; 
	public static int CLICK_EVENT_WRONG = 2;
	public static int CLICK_EVENT_GAMEOVER = 3; 
	
	public int handleUserClickOnIndex(int cellIdx , Integer clorIdx ){
		if ( currGStage == GS_RUNNING ){
			
			//Log.d("Anderson" , "Touch Comparing " + pendingSequence.get(0).intValue() + " " + cellIdx);
			if ( pendingMap.get(0).intValue() == cellIdx ) {
				clorIdx = pendingSequence.get(0);
				pendingSequence.remove(0);
				pendingMap.remove(0);
				mThisRoundScore++;
				
				if ( pendingSequence.size() == 0 ){
					// 
					if ( currLevel + 1 >= levelData.getTotalNumOfLevel()  ){
						// All level passed !! 
						EnterGameState(GS_REACH_DEST);
					} else {
						currLevel++;
						// This level finished, but more to GO! 
						mThisRoundScore = (int)((gameFullPlayingTime - (System.currentTimeMillis()-levelStartTime))/1000);
						EnterGameState(GS_WAIT_START); 
					}
				} else {
					if ( gameDifficulty == GameState.GAME_HARD ){
						rearrangePendingCellPosition();
					}
					
					return CLICK_EVENT_RIGHT; 
				}
			} else if ( bIsIncorrectColor(cellIdx) == true ){   
				if ( mThisRoundScore + mPrevRoundScore > 0 ){
					mThisRoundScore-=1;
				}
				return CLICK_EVENT_WRONG;  // press wrong
			}
		} else if ( currGStage == GS_WAIT_START ){
			// just a click , no matter what does it click 
			
		} else if ( currGStage == GS_REACH_DEST ||
				currGStage == GS_GAMEOVER){
			((Activity) mContext).finish();
		}
		return CLICK_EVENT_NONE;
	}
	
	public void EnterGameState(int targetGameState){
		int beforeGameState = currGStage; 
		if ( beforeGameState == targetGameState ) {
			return; 
		}
		
		currGStage = targetGameState;
		Log.d("Anderson" , "Entering target game state " + targetGameState + " from " + beforeGameState + " Game Difficulty " + gameDifficulty );
		if ( currGStage == GS_WAIT_START ){
			attemptUpdateHighestScore(mThisRoundScore+mPrevRoundScore);
		} else if  ( currGStage == GS_REACH_DEST ){
			
			attemptUpdateHighestDifficulty(gameDifficulty);
			attemptUpdateHighestScore(mThisRoundScore+mPrevRoundScore);
		} else if ( currGStage == GS_GAMEOVER ) {
			attemptUpdateHighestScore(mThisRoundScore+mPrevRoundScore);
		}    else if ( currGStage == GS_RUNNING ){ 
			
			initElements();
			levelStartTime = System.currentTimeMillis(); 
			gameFullPlayingTime = levelData.getTotalTimeOfLevelMs(currLevel);
			Log.d("Anderson" , "gameFullPlayingTime is " + gameFullPlayingTime);
			handleAudioOnOffSwitch();
		} else if ( currGStage == GS_OPTION_MENU ){
			
		}
		
		// ******* 
		if ( currGStage == GS_RUNNING ){
			handleAudioOnOffSwitch();
		}
		
		
	} 
	
	void handleAudioOnOffSwitch(){
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		boolean bShouldPlaySnd = preferences.getBoolean(GameState.G_SND_ONOFF_KEY, GameState.G_SND_ONOFF_DEFAULT );
		if ( bShouldPlaySnd ){
			setChanged();
			notifyObservers("snd_on");
		} else {
			setChanged();
			notifyObservers("snd_off");
		}
	}
	
	public int getTotalScore(){
		return mThisRoundScore + mPrevRoundScore;
	}
	
	public int getGameState()
	{
		return currGStage; 
	}
	 
	public int getGameDifficulty()
	{

		return gameDifficulty;
	}
	
	public int getCurrLevel(){
		return currLevel;
	}
	
	public void attemptUpdateHighestScore( int score ){ 
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		int highestScore = preferences.getInt(HIGHEST_SCORE_KEY, 0 ); 
		if ( score > highestScore ){
			Editor edit = preferences.edit();
        	edit.putInt(HIGHEST_SCORE_KEY,score);
        	edit.commit();
		}
	}
	
	
	
	public int getHighScoreHistory( int score ){
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		int highestScore = preferences.getInt(HIGHEST_SCORE_KEY, 0 );
		return highestScore; 
	} 
	
	boolean isFirstPagePassed(){
		return isFirstPagePassed;
	}
	
	void setFirstPagePassed(boolean b){
		isFirstPagePassed = b;
	}
	
	public void rearrangePendingCellPosition(){
		int numOfPendingCellLeft = pendingMap.size();
		// private ArrayList<Integer> pendingMap;
		if ( numOfPendingCellLeft == 0 ) {
			return;
		}
		ArrayList<Integer> inputAR = new ArrayList<Integer>();
		for ( int i = 0 ; i < GB_MAX_PENDING_OBJ ; i++ ){
			inputAR.add( new Integer(i) );
		}
		
		//Log.d("Anderson" , "numOfPendingCellLeft is " + numOfPendingCellLeft );
		pendingMap.clear();
		Random rnd = new Random();
		for( int i = 0; i < numOfPendingCellLeft ; i++ ) {
			//Log.d("Anderson" , "inputAR's size is " + inputAR.size() );
			int idx = rnd.nextInt(inputAR.size() );  
			pendingMap.add(inputAR.get(idx));
			inputAR.remove(idx);
		}
		// Log.d("Anderson", "pendingMap display " + pendingMap.toString() );
	}
	
	void handlePauseAction(){
		// TODO : save current status to persistent store 
	}
	
	void saveCurrentGameState(){
		// save current status to flash
		SaveGameStateHandler saveGHdler = new SaveGameStateHandler(mContext); 
		saveGHdler.setLevel(currLevel, gameDifficulty);
		long timeLeft = getTimeLeftMs();
		if ( timeLeft < 1000 ) timeLeft = 1000;
		saveGHdler.setRemainingTime(timeLeft);
		saveGHdler.setScore(mThisRoundScore,mPrevRoundScore);
		saveGHdler.setPlayBoard(pendingSequence, pendingMap);
		saveGHdler.commitSave();
	}
	
	void attemptUpdateHighestDifficulty(int thisDifficulty ){ 
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		int highestLevel  = preferences.getInt(HIGHEST_LEVEL_KEY, 0 ); 
		if ( thisDifficulty > highestLevel ){
			Editor edit = preferences.edit();
        	edit.putInt(HIGHEST_LEVEL_KEY,thisDifficulty);
        	edit.commit();
		}
	}
	// ANSON
	int  getSelectedGameLevel(){
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		int difficultyValue = preferences.getInt(G_DIFF_KEY, GameState.GAME_EASY );
		Log.d(TAG,"Difficulty level is " + difficultyValue );
		return preferences.getInt(G_DIFF_KEY, GameState.GAME_EASY );
	}
	
	
}

