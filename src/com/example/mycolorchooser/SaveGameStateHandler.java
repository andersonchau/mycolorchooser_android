package com.example.mycolorchooser;

import java.util.ArrayList;
import java.util.StringTokenizer;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

public class SaveGameStateHandler {
	
	public static final String GAME_SAVE_ROUND_KEY = "gs_round_key";
	public static final String GAME_SAVE_LEVEL_KEY = "gs_level_key";
	public static final String GAME_SAVE_TIME_KEY = "gs_time_key";
	public static final String GAME_SAVE_COLOR_SEQ_KEY = "gs_color_seq_key";
	public static final String GAME_SAVE_BOARD_SEQ_KEY = "gs_board_seq_key";
	public static final String GAME_SAVE_SCORE_THIS_KEY = "gs_score_this_key";
	public static final String GAME_SAVE_SCORE_PREV_KEY = "gs_score_prev_key";
	public static final String GAME_SAVE_AVAIL_KEY = "gs_state_avail_key";
	
	private int mRound; 
	private int mLevel;
	private int mScoreThis;
	private int mScorePrev;
	private long mRemainingTime;
	
	private String mColorSeqString;
	private String mBoardPosString; 
	Context mContext;
	
	public SaveGameStateHandler(Context ctx) {
		mContext = ctx; 
	}
	
	public void setLevel(int round, int level ){ 
		mRound = round;
		mLevel = level; 
			
	}
	
	public void setRemainingTime(long remainingTime){ 
		mRemainingTime = remainingTime; 
	}
	
	public void setScore(int scoreThisRnd, int scorePrevRound) {
		mScoreThis = scoreThisRnd;
		mScorePrev = scorePrevRound;
	}
	
	
	
	public void setPlayBoard( ArrayList<Integer> cellColorSequence , ArrayList<Integer> boardPositionSeqence ){
		String str = ""; 
		for (Integer item : cellColorSequence) {
		    // System.out.println(item);
			str += (item.toString() + ",");
		}
		mColorSeqString = str;
		Log.d("Anderson" , "SetPlayBoard (1) " + mColorSeqString);
		String str2 = "";
		for (Integer item : boardPositionSeqence) {
		    // System.out.println(item);
			str2 += (item.toString() + ",");
		}
		mBoardPosString = str2;
		Log.d("Anderson" , "SetPlayBoard (2) " + mBoardPosString);
	}
	
	public void commitSave(){
		if ( mContext != null ) {
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
			Editor edit = preferences.edit();
			edit.putInt(GAME_SAVE_ROUND_KEY,mRound);
			edit.putInt(GAME_SAVE_LEVEL_KEY,mLevel);
			edit.putLong(GAME_SAVE_TIME_KEY,mRemainingTime);
			edit.putString(GAME_SAVE_COLOR_SEQ_KEY, mColorSeqString);
			edit.putString(GAME_SAVE_BOARD_SEQ_KEY, mBoardPosString);
			edit.putInt(GAME_SAVE_SCORE_THIS_KEY,mScoreThis);
			edit.putInt(GAME_SAVE_SCORE_PREV_KEY,mScorePrev);
			edit.putBoolean(GAME_SAVE_AVAIL_KEY,true);
			edit.commit();
		}
	}
	
	
	
	public void clearSaveContent(){
		if ( mContext != null ){
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
			Editor edit = preferences.edit();
			edit.putBoolean(GAME_SAVE_AVAIL_KEY,false);
			edit.commit();
		}
	}
	
	public boolean haveSavedData(){
		if ( mContext != null ){
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
			boolean bHaveSavedData = preferences.getBoolean(GAME_SAVE_AVAIL_KEY, false );
			
			return bHaveSavedData; 
		}
		return false;
	}
	
	public void loadSavedData(){
		if ( mContext != null ){			
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
			mRound = preferences.getInt(GAME_SAVE_ROUND_KEY, 0 );
			mLevel = preferences.getInt(GAME_SAVE_LEVEL_KEY, 0 );
			mRemainingTime = preferences.getLong(GAME_SAVE_TIME_KEY, 0);
			mScoreThis = preferences.getInt(GAME_SAVE_SCORE_THIS_KEY, 0 );
			mScorePrev = preferences.getInt(GAME_SAVE_SCORE_PREV_KEY, 0 );
			mColorSeqString = preferences.getString(GAME_SAVE_COLOR_SEQ_KEY, "");
			mBoardPosString = preferences.getString(GAME_SAVE_BOARD_SEQ_KEY, "");
			
			
		} 
	}
	
	public int getRound(){
		return mRound;
	}
	
	public int getLevel(){
		return mLevel;
	}
	
	public long getRemainingTime(){
		return mRemainingTime;
	}
	
	public ArrayList<Integer> getColorSequence(){
		StringTokenizer st = new StringTokenizer(mColorSeqString, ","); 
		Log.d("Anderson" , "getColorSequence() " + mColorSeqString );
		ArrayList<Integer> al = new ArrayList<Integer>();
		
		while(st.hasMoreTokens()) { 
			al.add(Integer.parseInt(st.nextToken()));
		} 
		Log.d("Anderson" , "Dumping getColorSequence() " + al);
		return al;
	}
	
	public ArrayList<Integer> getBoardPosition(){
		StringTokenizer st = new StringTokenizer(mBoardPosString, ","); 
		Log.d("Anderson" , "getColorSequence() " + mBoardPosString );
		ArrayList<Integer> al = new ArrayList<Integer>();
		
		while(st.hasMoreTokens()) { 
			al.add(Integer.parseInt(st.nextToken()));
		} 
		Log.d("Anderson" , "Dumping getBoardPosition() " + al);
		return al;
	}
	
	public int getScoreThis(){
		return mScoreThis;
	}
	
	public int getScorePrev(){
		return mScorePrev; 
	}

}
