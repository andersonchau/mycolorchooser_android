package com.example.mycolorchooser;

import java.util.ArrayList;
import java.util.Iterator;

import android.graphics.Canvas;



public class CellDisappearAnimation {
	// Handler : to Implement cell disappearing amination 
	private static long CELL_DISAPPEAR_TIME = 800;
	
	private static CellDisappearAnimation instance; 
	
	private static ArrayList<DisappearingCellElement> mDispCellList;
	
	
	
	public static CellDisappearAnimation getInstance(){
		if ( instance == null ){
			synchronized (CellDisappearAnimation.class) {
				if ( instance == null ){
					
					return new CellDisappearAnimation();
				}
			}
		}
		return instance; 
	}

	private CellDisappearAnimation(){
		mDispCellList = new ArrayList<DisappearingCellElement>();
	}
	
	public void startDisappearAnim(int posIdx , int colorIdx ){ 
		synchronized (CellDisappearAnimation.class) {
			mDispCellList.add(new DisappearingCellElement(posIdx, colorIdx, System.currentTimeMillis()));
		}
	}
	
	public boolean isCellDisappearing(int posIdx){
		synchronized (CellDisappearAnimation.class) {
			for ( DisappearingCellElement el : mDispCellList){
				if ( posIdx == el.getPosIndex()){
					return true;
				}
			}
		}
		return false;
	}
	
	public void onDraw(GameView gv , Canvas cv ){
		// this is called by GameView
		synchronized (CellDisappearAnimation.class) {
			long currTime = System.currentTimeMillis();
			
			Iterator<DisappearingCellElement> i = mDispCellList.iterator();
			while (i.hasNext()) {
				DisappearingCellElement e = i.next(); // must be called before you can call i.remove()
				if ((currTime - e.getStartDispTime()) >= CELL_DISAPPEAR_TIME ){
					i.remove();
				}
			}
			
			for ( DisappearingCellElement el : mDispCellList){
				float proportion = (CELL_DISAPPEAR_TIME - (currTime - el.getStartDispTime()))/CELL_DISAPPEAR_TIME; 
				gv.drawSmallerCell(el.getPosIndex() , el.getColorIndex(), proportion , cv );
			}
		}
	}
	
	
	
	
	private static class DisappearingCellElement {
		private int mPosIdx; 
		private int mColorIdx; 
		private long mDispStartTime; 
		
		public DisappearingCellElement( int posIdx , int colorIdx , long startTime ){
			mPosIdx = posIdx; 
			mColorIdx = colorIdx; 
			mDispStartTime = startTime;
		}
		
		public int getPosIndex(){
			return mPosIdx;
		}
		
		public int getColorIndex(){
			return mColorIdx;
		}
		
		public long getStartDispTime(){
			return mDispStartTime;
		}
	}
	
}
