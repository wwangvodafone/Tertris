package syu.wyx.zz.tetris.param;

import android.util.Log;


public class TetrisConst {
	
	public static int iScreenWidth = 0;
	public static int iScreenHeight = 0;
	public static int iViewWidth = 0;
	public static int iViewHeight = 0;
	

	//Size for HTC
	public static final int ISCREEN_WIDTH = 540;
	public static final int ISCREEN_HEIGHT = 960;
	public static final int IVIEW_WIDTH = 540;
	public static final int IVIEW_HEIGHT = 780;
	//Map size
	public static int X_SIZE = 18;
	public static int Y_SIZE = 35;
	//Game Frame size
	public static int STARTX = 55;
	public static int STARTY = 80;
	public static int XLength = 360; 
	public static int YLength = 700; 
	//View Frame size
	public static int PRE_STARTY = 85;
	public static int PRE_LENGTH = 120; //60*2
	public static int PRE_STARTX = STARTX + XLength - PRE_LENGTH;
	//Start position
	public static int START_BLOCKX = STARTX + 160;
	public static int START_BLOCKY = STARTY;
	public static int BLOCKSIZE = 20;
	//Block Map 
	public static int TETRISMAP_LEFT = STARTX;
	public static int TETRISMAP_TOP = STARTY;
	public static int TETRISMAP_RIGHT = STARTX + XLength;
	public static int TETRISMAP_DOWN = STARTY + YLength;
	//Bitmap Category
	public static final int BLOCK_ORIGINAL = 1;
	public static final int BLOCK_HEART = 2;
	public static final int BLOCK_CHOCALATE = 3;
	public static final int BLOCK_FRUIT = 4;
	public static final int BLOCK_SWEET = 5;
	public static final int BLOCK_BALL = 6;
	//Block Category
	public static final int MAP_BLOCK = 1;
	public static final int MAP_JAZZ = 2;
	public static final int MAP_SERVICE = 3;
	// Standard score for level
	public static final int LEVEL_SCORE = 150;
	//
	public static final int TETRISBLOCK_0 = 0;// 
	                                          // 
	                                          // 
	                                          // 
	
    public static final int TETRISBLOCK_1 = 1;// 
		                                      //  
	                                          // 
    
    public static final int TETRISBLOCK_2 = 2;//  
	                                          //  
	                                          // 
    
    public static final int TETRISBLOCK_3 = 3;// 
	                                          //  
	                                          //   

    public static final int TETRISBLOCK_4 = 4;//  
	                                          // 
	                                          // 

    public static final int TETRISBLOCK_5 = 5;//  
	                                          //  

    public static final int TETRISBLOCK_6 = 6;//  
	                                          //   
	                                          //   

    //sound
    public static final int SOUND_REACH_BUTTOM = 1;
    public static final int SOUND_CLEAR_BLOCK = 2;
    public static final int SOUND_LEVEL_UP = 3;
    public static final int SOUND_GAME_OVER = 4;
    public static final int SOUND_BKGROUND = 5;
    
    //database
    public static final String DB_NAME = "TETRIS_SCORE";
    
    //
    public static final String BLANK = "";
    //Log tab
    public static final String LOGTAB = "TETRISACTIVITY";
	//Change the size
	public static void changeSize(double dScreenSize) {
		
		X_SIZE = 18;
		Y_SIZE = 35;
		if (dScreenSize > 4.5) {
			STARTX = 55;
			STARTY = 80;
			XLength = 360; 
			YLength = 700; 
		}
		else {
			STARTX = 55;
			STARTY = 60;
			XLength = 360; 
			YLength = 680; 
		}
		PRE_STARTY = 85;
		PRE_LENGTH = 120;
		BLOCKSIZE = 20;		
		START_BLOCKY = STARTY;
		
		STARTX = STARTX * iScreenWidth / ISCREEN_WIDTH;
		STARTY = STARTY * iScreenHeight / ISCREEN_HEIGHT;
		XLength = XLength * iScreenWidth / ISCREEN_WIDTH;
		YLength = YLength * iScreenHeight / ISCREEN_HEIGHT;
		PRE_STARTY = PRE_STARTY * iScreenHeight / ISCREEN_HEIGHT;
		PRE_LENGTH = PRE_LENGTH * iScreenHeight / ISCREEN_HEIGHT;
		
		BLOCKSIZE = BLOCKSIZE * iScreenWidth / ISCREEN_WIDTH;
		X_SIZE = XLength / BLOCKSIZE;
		Y_SIZE = YLength / BLOCKSIZE;
		XLength = X_SIZE * BLOCKSIZE;
		YLength = Y_SIZE * BLOCKSIZE;
		
		START_BLOCKX = STARTX + BLOCKSIZE * 6;
		START_BLOCKY = START_BLOCKY * iScreenHeight / ISCREEN_HEIGHT;
		PRE_LENGTH = 5 * BLOCKSIZE;
		PRE_STARTX = STARTX + XLength - 5 * BLOCKSIZE;
		TETRISMAP_LEFT = STARTX;
		TETRISMAP_TOP = STARTY;
		TETRISMAP_RIGHT = STARTX + XLength;
		TETRISMAP_DOWN = STARTY + YLength;

		Log.i("TETRISACTIVITY", "START_BLOCKX" + Integer.toString(START_BLOCKX));	
		Log.i("TETRISACTIVITY", "START_BLOCKY" + Integer.toString(START_BLOCKY));	
		Log.i("TETRISACTIVITY", "STARTX" + Integer.toString(STARTX));		
		Log.i("TETRISACTIVITY", "X_SIZE" + Integer.toString(X_SIZE));
		Log.i("TETRISACTIVITY", "Y_SIZE" + Integer.toString(Y_SIZE));
		Log.i("TETRISACTIVITY", "XLength" + Integer.toString(XLength));
		Log.i("TETRISACTIVITY", "YLength" + Integer.toString(YLength));
		Log.i("TETRISACTIVITY", "BLOCKSIZE" + Integer.toString(BLOCKSIZE));		
		Log.i("TETRISACTIVITY", "PRE_STARTX" + Integer.toString(PRE_STARTX));
		Log.i("TETRISACTIVITY", "PRE_LENGTH" + Integer.toString(PRE_LENGTH));	
	}
}
