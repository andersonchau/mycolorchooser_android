package com.example.mycolorchooser;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.util.Log;

public class GameLoopThread extends Thread {
	static final long FPS = 1000; // se refiere a las imagenes por segundo, esto sirve como variable despues para controlar el "sleep"
	private GameView view;
	private boolean running = false;
	GameState mGameState;
       public GameLoopThread(GameView view , GameState gs ) {
    	     mGameState = gs;
             this.view = view;
       }
 
       public void setRunning(boolean run) {
             running = run;
       }
 
       @SuppressLint("WrongCall")
	@Override
	public void run() {
    	  
		long ticksPS = 1000 / FPS;
		long startTime;
		long sleepTime;
		while (running) {
			
			
			Canvas c = null;
			startTime = System.currentTimeMillis();
			if ( mGameState.getGameEndStatus() == true ){
				break;
			}
			
			if (mGameState.getGameState() == GameState.GS_RUNNING) {
				
				if ( mGameState.getTimeLeftMs() < 300 ) {
					mGameState.EnterGameState(GameState.GS_GAMEOVER);
				}
			}
			try {
				c = view.getHolder().lockCanvas();
				synchronized (view.getHolder()) {
					view.onDraw(c);
				}
			} finally {
				if (c != null) {
					view.getHolder().unlockCanvasAndPost(c);
				}
			}
			sleepTime = ticksPS - (System.currentTimeMillis() - startTime);
			try {
				if (sleepTime > 0)
					sleep(sleepTime);
				else
					sleep(10/100);
			} catch (Exception e) {
			}
			
		}
		Log.d("Anderson" , "gameloopThread quitting the while loop!!!!");
	}
}  