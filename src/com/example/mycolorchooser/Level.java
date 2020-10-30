package com.example.mycolorchooser;

import java.util.ArrayList;

import android.util.Log;

public class Level {
	public static final int NUMBER_OF_LEVEL = 5;
	
	private ArrayList<LevelData> levelDataList; 
	
	public Level(int difficulty ){
		levelDataList = new ArrayList<LevelData>();
		switch ( difficulty ) {
		
		case GameState.GAME_EASY:
		default:
			levelDataList.add(new LevelData(60,5));
			levelDataList.add(new LevelData(40,7));
			levelDataList.add(new LevelData(30,10));
			levelDataList.add(new LevelData(25,13));
			levelDataList.add(new LevelData(15,16));
			break;
		case GameState.GAME_NORMAL:
			levelDataList.add(new LevelData(40,8));
			levelDataList.add(new LevelData(30,8));
			levelDataList.add(new LevelData(30,11));
			levelDataList.add(new LevelData(20,11));
			levelDataList.add(new LevelData(15,16));
			break;
		case GameState.GAME_HARD:
			levelDataList.add(new LevelData(25,7));
			levelDataList.add(new LevelData(20,7));
			levelDataList.add(new LevelData(20,10));
			levelDataList.add(new LevelData(15,10));
			levelDataList.add(new LevelData(15,16));
			break;
		
		}
	}
	

	public int getFullPlayingTimeMs(int level){ 
		if ( levelDataList.size() < level+1){
			return -1;
		} else {
			return levelDataList.get(level).totalTimeSec*1000; 
		}
	}
	
	
	public int getNumOfSelector(int level){ 
		if ( levelDataList.size() < level+1){
			return -1;
		} else {
			return levelDataList.get(level).numPendingBlock; 
		}
	}
	
	public int getTotalNumOfLevel(){
		return levelDataList.size();
	}
    
	public long getTotalTimeOfLevelMs(int level){
		Log.d("Anderosn" , "getTOtalTimeOfLevelMs " + level + " " + ( levelDataList.get(level).totalTimeSec * 1000 ));
		return ( levelDataList.get(level).totalTimeSec * 1000 );
	}
	
	private class LevelData {
		public int totalTimeSec; 
		public int numPendingBlock; 
		
		public LevelData( int timesec, int nBlock  ){
			totalTimeSec = timesec;
			numPendingBlock = nBlock; 
		}
	}
	
	public int getOrigNumObjFromLevel( int level ){
		return levelDataList.get(level).numPendingBlock;
	}
	
}
