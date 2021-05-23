package syu.wyx.zz.tetris;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;


import syu.wyx.zz.tetris.R;
import syu.wyx.zz.tetris.param.MySql;
import syu.wyx.zz.tetris.param.TetrisConst;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class TetrisView extends View {

	public class TETRISBLOCK {
		private int x;
		private int y;
		public TETRISBLOCK() {
			x = 0;
			y = 0;
		}
	} 
	private TETRISBLOCK[][] TetrisBlock = new TETRISBLOCK[4][5];
	private TETRISBLOCK[][] TetrisMoveBlock = new TETRISBLOCK[4][5];
	//�ｽ�ｽ�ｽW�ｽI�ｽ{�ｽ^�ｽ�ｽ�ｽﾌ値
	private Boolean bOriginal = Boolean.TRUE;
	private Boolean bHeart = Boolean.FALSE;
	private Boolean bChocalate = Boolean.FALSE;
	private Boolean bFruit = Boolean.FALSE;
	private Boolean bSweet = Boolean.FALSE;
	//�ｽ�ｽﾊのマ�ｽb�ｽv
	private int[][] TetrisBlockMap;
	private int[][] TetrisColorMap;
	private int[][] TetrisCategoryMap;	
	//Buffer
	private Bitmap mViewBitmap = null;
	private Bitmap mMoveBitmap = null;
	//redraw�ｽﾌマ�ｽ[�ｽN
	private Boolean bDoReDraw = Boolean.FALSE;
	//Bitmap
	private int blockViewBitmap = 0;
	private int blockMoveBitmap = 0;
	private int blockJazzBitmap = 0;
	private int blockServiceBitmap = 0;
	//Frame�ｽﾌ範囲搾ｿｽ�ｽW
	private int frameTopLeftX = 0;
	private int frameTopLeftY = 0;
	private int frameTopRightX = 0;
	private int frameTopRightY = 0;
	private int frameDownLeftX = 0;
	private int frameDownLeftY = 0;
	private int frameDownRightX = 0;
	private int frameDownRightY = 0;
	//�ｽﾚ難ｿｽ�ｽﾌ搾ｿｽ�ｽW
	private int xMPos[] = new int[4];
	private int yMPos[] = new int[4];
	//�ｽ�ｽ�ｽﾌブ�ｽ�ｽ�ｽb�ｽN�ｽ�ｽ\�ｽ�ｽ�ｽ�ｽ�ｽ�ｽ�ｽ�ｽW
	private int xVPos[] = new int[4];
	private int yVPos[] = new int[4];
	//�ｽﾚ難ｿｽ�ｽu�ｽ�ｽ�ｽb�ｽN�ｽﾌ托ｿｽ�ｽ�ｽ�ｽﾊ置
	private int xMoveBlock = 0;
	private int yMoveBlock = 0;
	//�ｽ�ｽ�ｽﾚのフ�ｽ�ｽ�ｽO
	private Boolean bFirstStart = Boolean.FALSE;
	private Boolean bFirstTimeOnDraw = Boolean.TRUE;
	//�ｽu�ｽ�ｽ�ｽb�ｽN�ｽﾌ形
	private int currMoveBlockForm = -1;
	private int currViewBlockForm = -1;
	private int currBlockState = 0;
	private int oldBlockState = 0;
	//�ｽN�ｽ�ｽ�ｽt�ｽ�ｽ�ｽO
	private Boolean bIsStart = Boolean.FALSE;
	//sound
	private SoundPool soundPool;
	private MediaPlayer player; 
	HashMap<Integer, Integer> soundPoolMap;
	HashMap<Integer, Integer> soundBkPoolMap;
	AudioManager audioManager;
	//Score
	private int tetrisScore = 0;
	private int iCurrentTetrisScore = 0;
	private TextView scoreText = null;
	private TextView levelText = null;
	//�ｽ^�ｽC�ｽ}�ｽ[
	private int timerBlockDown = 500;
	//Timer timer = new Timer();
	TetrisTimerTask tetrisTimerTask;
	Timer timer1 = null;
	Timer timer2 = null;
	Timer timer3 = null;
	Timer timer4 = null;
	Timer timer5 = null;
	Timer timer6 = null;
	Timer timer7 = null;
	Timer timer8 = null;
	Timer timer9 = null;
	Timer timer10 = null;
	Timer timerBeforeStart = null;
	TimerTask downBlockTask = null;
	//�ｽu�ｽ�ｽ�ｽb�ｽN�ｽﾌカ�ｽe�ｽS�ｽ�ｽ
	private int iBlockCategory = 0;
	private int iViewCategory = 1;
	//Game Level
	private int iGameLevel = 1;
	//Service ball
	private Boolean bServiceBall = Boolean.FALSE;
	private Boolean bBallReachBottom = Boolean.FALSE;
	private int iServiceBallNum = 0;
	//Sound Flag
	private Boolean bSoundOn = Boolean.TRUE;
	private int iSoundStreamId = 0;	
	//Popup Window
    private PopupWindow scoreWindow;
    private EditText scoreNameEditText;
    //Screen and view size
	int iScreenWidth = 0;
	int iScreenHeight = 0;
	int iViewWidth = 0;
	int iViewHeight = 0;
    //Show before start
	private Boolean bShowBeforeStart = Boolean.TRUE;
    //Database
    private MySql mysql;
    
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				if (bShowBeforeStart) {
					moveBeforeStart();
				}				
				else if (!bServiceBall) {
					moveBlockDown();
				}
				else {
					moveServiceBallDown();
				}
				break;
			}
			super.handleMessage(msg);
		}
	};
	
	//TetrisTimerTask tetrisTimerTask = new TetrisTimerTask(handler);
	
	private Paint mPaint = new Paint();
	public TetrisView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		bFirstStart = Boolean.TRUE;
		initSounds(context);
		initBitmap();
		initParams();
		//Show before starting
		showBeforeStart();
	}

	private void showBeforeStart() {
	
		timerBeforeStart = new Timer();
		tetrisTimerTask = new TetrisTimerTask(handler);
		timerBeforeStart.schedule(tetrisTimerTask, 0, 100);
		synchronized(tetrisTimerTask) {
			tetrisTimerTask.bTimerCond = Boolean.TRUE;
			tetrisTimerTask.notify();
		}
	}
	
	private void moveBeforeStart() {
		if (!bServiceBall) {
			moveBlockDown();
		} else {
			moveServiceBallDown();
		}
	}
	//�ｽ�ｽ�ｽﾗての変撰ｿｽ�ｽ�ｽ�ｽ�ｽ�ｽ�ｽ
	private void initParams() {
		int i = 0;
		int j = 0;

		//Block�ｽ�ｽ�ｽW�ｽﾌ擾ｿｽ�ｽ�ｽ
		for (i = 0; i < 4; i++) {
			for (j = 0; j < 5; j++) {
				TetrisBlock[i][j] = new TETRISBLOCK();
				TetrisMoveBlock[i][j] = new TETRISBLOCK();
			}
		}	
        
        //DB initialize
		mysql = new MySql(this.getContext());
		mysql.open(TetrisConst.DB_NAME);
		if (!mysql.isTableExist(TetrisConst.DB_NAME)) {
			createTable();
		}
		else {
			Boolean errFlg = Boolean.FALSE;
			try {
				mysql.fetcharray("SELECT LEVEL FROM " 
						+ TetrisConst.DB_NAME
						+ ";"
						);
			} catch (Exception ex) {
				Log.e(TetrisConst.LOGTAB, "No level " + ex.getMessage());
				errFlg = Boolean.TRUE;
			}
			if (errFlg) {
				try {
				mysql.execsql("DROP TABLE "
						+ TetrisConst.DB_NAME
						);
				createTable();
				Log.e(TetrisConst.LOGTAB, "Create new table for no Level column.");
				} catch (Exception ex) {
					Log.e(TetrisConst.LOGTAB, "CreateTable when no column " + ex.getMessage());
				}
			}
		}
		mysql.close();

		//Boolean variable initialize
		bFirstStart = Boolean.FALSE;
		bFirstTimeOnDraw = Boolean.TRUE;
		bIsStart = Boolean.FALSE;

		bDoReDraw = Boolean.FALSE;
		bServiceBall = Boolean.FALSE;
		bBallReachBottom = Boolean.FALSE;
		//bSoundOn = Boolean.TRUE;

		//Variable initialize
		blockViewBitmap = 0;
		blockMoveBitmap = 0;
		blockJazzBitmap = 0;
		blockServiceBitmap = 0;
		//�ｽu�ｽ�ｽ�ｽb�ｽN�ｽﾌカ�ｽe�ｽS�ｽ�ｽ
		iBlockCategory = 0;
		iViewCategory = 1;
		//Game Level
		iGameLevel = 1;
		iCurrentTetrisScore = 0;
		tetrisScore = 0;
		//�ｽﾚ難ｿｽ�ｽu�ｽ�ｽ�ｽb�ｽN�ｽﾌ托ｿｽ�ｽ�ｽ�ｽﾊ置
		xMoveBlock = 0;
		yMoveBlock = 0;
		//�ｽu�ｽ�ｽ�ｽb�ｽN�ｽﾌ形
		currMoveBlockForm = -1;
		currViewBlockForm = -1;
		currBlockState = 0;
		oldBlockState = 0;
		iServiceBallNum = 0;


	}
	
	private void initFrameScope() {		
		//Frame�ｽﾍ囲のセ�ｽb�ｽg
		frameTopLeftX = TetrisConst.TETRISMAP_LEFT;
		frameTopLeftY = TetrisConst.TETRISMAP_TOP;
		frameTopRightX = TetrisConst.TETRISMAP_RIGHT;
		frameTopRightY = TetrisConst.TETRISMAP_TOP;
		frameDownLeftX = TetrisConst.TETRISMAP_LEFT;
		frameDownLeftY = TetrisConst.TETRISMAP_DOWN;
		frameDownRightX = TetrisConst.TETRISMAP_RIGHT;
		frameDownRightY = TetrisConst.TETRISMAP_DOWN;
	}
	
	//Audio Manager�ｽ�ｽﾝ抵ｿｽ
	public void setAudioManager(AudioManager audioManager) {
		this.audioManager = audioManager;
	}
	
	//Sound�ｽ�ｽ�ｽ�ｽ�ｽ�ｽ
	public void initSounds(Context context) {
		try {
			player = MediaPlayer.create(context, R.raw.bkground);  
			player.setLooping(true);
		}
		catch(Exception ex) {
			Log.e("TETRISACTIVITY", "initSounds: " + ex.getMessage());
		}
		soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 100);
		soundPoolMap = new HashMap<Integer, Integer>();
		soundPoolMap.put(TetrisConst.SOUND_REACH_BUTTOM, soundPool.load(context, R.raw.reachbottom, 1));
		soundPoolMap.put(TetrisConst.SOUND_CLEAR_BLOCK, soundPool.load(context, R.raw.clearblock, 1));
		soundPoolMap.put(TetrisConst.SOUND_LEVEL_UP, soundPool.load(context, R.raw.levelup, 1));
		soundPoolMap.put(TetrisConst.SOUND_GAME_OVER, soundPool.load(context, R.raw.gameover, 1));
		//soundPoolMap.put(TetrisConst.SOUND_BKGROUND, soundPool.load(context, R.raw.bkground, 1));

	}
	
	//sound�ｽ�ｽ�ｽﾄ撰ｿｽ
	private void playSound(int sound, int loop) {
		if (bSoundOn) {
			float streamVolumeCurrent = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			float streamVolumeMax = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			//float volume = streamVolumeCurrent/streamVolumeMax;
			float volume = streamVolumeMax;
			try {
				soundPool.play(soundPoolMap.get(sound), volume, volume, 1, loop, 1f);
			} 
			catch(Exception ex) {
				Log.e("TETRISACTIVITY", "PlaySound: " + ex.getMessage());
			}
		}
	}
	
	//background music play
	public void playBkSound() {
		if (bSoundOn) {
			float streamVolumeCurrent = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			float streamVolumeMax = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			//float volume = streamVolumeCurrent/streamVolumeMax;
			float volume = streamVolumeMax;
			try {
				player.setVolume(volume, volume);
				player.start();
			}  
			catch(Exception ex) {
				Log.e("TETRISACTIVITY", "playBkSound: " + ex.getMessage());
			}
		}
	}	
	
	//background music pause
	public void pauseBkSound() {
		try {
			player.pause();
		} 
		catch(Exception ex) {
			Log.e("TETRISACTIVITY", "pauseBkSound: " + ex.getMessage());
		}
	}
	
	//background music resume
	public void resumeBkSound() {
		if (bSoundOn) {
			try {
				player.start();
			}
			catch(Exception ex) {
				Log.e("TETRISACTIVITY", "resumeBkSound: " + ex.getMessage());
			}
		}
	}
	
	//background music stop
	public void stopBkSound() {
		try {
			player.stop();
			player.prepare();
		}
		catch(Exception ex) {
			Log.e("TETRISACTIVITY", "stopBkSound: " + ex.getMessage());
		}
	}
	
	public void onDraw(Canvas canvas) {
		//Log.i("TETRISACTIVITY", "In the onDraw.");
		if (bFirstTimeOnDraw) {
			bFirstTimeOnDraw = Boolean.FALSE;
			if (TetrisBlockMap == null) {
				TetrisBlockMap = new int[TetrisConst.X_SIZE ][TetrisConst.Y_SIZE];
				TetrisColorMap = new int[TetrisConst.X_SIZE ][TetrisConst.Y_SIZE];
				TetrisCategoryMap = new int[TetrisConst.X_SIZE ][TetrisConst.Y_SIZE];
			}
			//�ｽ}�ｽb�ｽv�ｽﾌ擾ｿｽ�ｽ�ｽ
			for (int i = 0; i < TetrisConst.X_SIZE; i++) {
				for (int j = 0; j < TetrisConst.Y_SIZE; j++) {
					TetrisBlockMap[i][j] = 0;
					TetrisColorMap[i][j] = 0;
					TetrisCategoryMap[i][j] = 0;
				}
			}
			if (bShowBeforeStart) {
				initFrameScope();
				drawViewBlock(1);
				drawViewBlock(2);			
				drawMoveBlock();
			}
		}
		int i = 0;
		int j = 0;
		//canvas.drawColor(Color.WHITE);	
		drawTetrisFrame(canvas, mPaint);
		drawViewFrame(canvas, mPaint);

		//onTetrisStart(canvas);
 //		canvas.restore();
		if (bDoReDraw) {
			//canvas.restore();
			for (i = 0; i < 4; i++) {
				canvas.drawBitmap(mViewBitmap, xVPos[i], yVPos[i], null);
			}
			for (i = 0; i < TetrisConst.X_SIZE ; i++) {
				for (j = 0; j < TetrisConst.Y_SIZE; j++) {
					if (TetrisBlockMap[i][j] != 0) {
						Bitmap bmp = getBitmap(TetrisColorMap[i][j], TetrisCategoryMap[i][j]);
						canvas.drawBitmap(bmp, 
								TetrisConst.STARTX + i * TetrisConst.BLOCKSIZE,
								TetrisConst.STARTY + j * TetrisConst.BLOCKSIZE,
								null);
					}
				}
			}
			
		}
		//canvas.save(Canvas.ALL_SAVE_FLAG);
	}

	//Score
	public void setScoreText(TextView scoreText) {
		this.scoreText = scoreText;
		setScore();
	}

	//Level
	public void setLevelText(TextView levelText) {
		this.levelText = levelText;
		levelText.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);  
		setLevel();
	}
	
	//Set Score
	private void setScore() {
		String strScore = Integer.toString(iCurrentTetrisScore);
		scoreText.setText(strScore);
	}
	
	//Set Level
	private void setLevel() {
		String strLevel = Integer.toString(iGameLevel);
		strLevel = "Level: " + strLevel;
		levelText.setText(strLevel);
	}

	//Sound on Setting
	protected void setSoundOn(Boolean bSound) {
		bSoundOn = bSound;
	}
	
	//Start
	public void onTetrisStart() {
		synchronized(tetrisTimerTask) {
			tetrisTimerTask.bTimerCond = Boolean.FALSE;
			tetrisTimerTask.notify();
		}
		bShowBeforeStart = Boolean.FALSE;
		initParams();
		initBitmap();
		initTetris();

		bFirstStart = Boolean.FALSE;
		bIsStart = Boolean.TRUE;
		synchronized(tetrisTimerTask) {
			tetrisTimerTask.bTimerCond = Boolean.TRUE;
			tetrisTimerTask.notify();
		}
	}
	
	//Pause
	public void onTetrisPause() {
		bIsStart = Boolean.FALSE;
		synchronized(tetrisTimerTask) {
			tetrisTimerTask.bTimerCond = Boolean.FALSE;
		}
	}
	
	//Restart
	public void onTetrisRestart() {
		bIsStart = Boolean.TRUE;
		synchronized(tetrisTimerTask) {
			tetrisTimerTask.bTimerCond = Boolean.TRUE;
			tetrisTimerTask.notify();
		}
	}
	
	//Stop
	public void onTetrisStop() {
		synchronized(tetrisTimerTask) {
			tetrisTimerTask.bTimerCond = Boolean.FALSE;
		}
	}
	
	//�ｽJ�ｽe�ｽS�ｽ�ｽ�ｽF�ｽ�ｽ�ｽW�ｽI�ｽ{�ｽ^�ｽ�ｽ
	public void setBlockCategory(int category) {
		bOriginal = Boolean.FALSE;
		bHeart = Boolean.FALSE;
		bChocalate = Boolean.FALSE;
		bFruit = Boolean.FALSE;
		bSweet = Boolean.FALSE;
		
		switch (category) {
		case 0:
			bOriginal = Boolean.TRUE;
			break;
		case 1:
			bHeart = Boolean.TRUE;
			break;
		case 2:
			bChocalate = Boolean.TRUE;
			break;
		case 3:
			bFruit = Boolean.TRUE;
			break;
		case 4:
			bSweet = Boolean.TRUE;
			break;
		default:
			break;
		}
		
		return;
	}
	private void initTetris() {
		//preTetrisBlock();
		setScore();
		setLevel();
		drawViewBlock(1);
		drawViewBlock(2);
		drawMoveBlock();
		if (bFirstStart || timer1 == null) {
			//timer.schedule(tetrisTimerTask, 0, timerBlockDown);
			setTimer(iGameLevel);
		}
	}
	
	//�ｽ�ｽ�ｽﾗてのブ�ｽ�ｽ�ｽb�ｽN�ｽﾌ擾ｿｽ�ｽ�ｽ
	private  void preTetrisBlock() {
		int i = (int)(Math.random() * 10) % 7;

		switch(i) {
		case 0:
			TetrisBlock[0][0].x = 0;//�ｽ�ｽ
			TetrisBlock[0][0].y = 0;//�ｽ�ｽ
			TetrisBlock[0][1].x = 0;//�ｽ�ｽ
			TetrisBlock[0][1].y = 1;//�ｽ�ｽ
			TetrisBlock[0][2].x = 0;
			TetrisBlock[0][2].y = 2;
			TetrisBlock[0][3].x = 0;
			TetrisBlock[0][3].y = 3;
			TetrisBlock[0][4].x = 1;
			TetrisBlock[0][4].y = 4;

			TetrisBlock[1][0].x = 0;//�ｽ�ｽ�ｽ�ｽ�ｽ�ｽ�ｽ�ｽ
			TetrisBlock[1][0].y = 0;//
			TetrisBlock[1][1].x = 1;//
			TetrisBlock[1][1].y = 0;//
			TetrisBlock[1][2].x = 2;
			TetrisBlock[1][2].y = 0;
			TetrisBlock[1][3].x = 3;
			TetrisBlock[1][3].y = 0;
			TetrisBlock[1][4].x = 4;
			TetrisBlock[1][4].y = 1;

			break;
		case 1:
			TetrisBlock[0][0].x = 0;//�ｽ�ｽ
			TetrisBlock[0][0].y = 0;//�ｽ�ｽ�ｽ�ｽ
			TetrisBlock[0][1].x = 0;//�ｽ�ｽ
			TetrisBlock[0][1].y = 1;
			TetrisBlock[0][2].x = 1;
			TetrisBlock[0][2].y = 1;
			TetrisBlock[0][3].x = 0;
			TetrisBlock[0][3].y = 2;
			TetrisBlock[0][4].x = 2;
			TetrisBlock[0][4].y = 3;

			TetrisBlock[1][0].x = 0;//�ｽ�ｽ�ｽ�ｽ�ｽ�ｽ
			TetrisBlock[1][0].y = 0;//  �ｽ�ｽ
			TetrisBlock[1][1].x = 1;//
			TetrisBlock[1][1].y = 0;
			TetrisBlock[1][2].x = 2;
			TetrisBlock[1][2].y = 0;
			TetrisBlock[1][3].x = 1;
			TetrisBlock[1][3].y = 1;
			TetrisBlock[1][4].x = 3;
			TetrisBlock[1][4].y = 2;

			TetrisBlock[2][0].x = 1;//  �ｽ�ｽ
			TetrisBlock[2][0].y = 0;//�ｽ�ｽ�ｽ�ｽ
			TetrisBlock[2][1].x = 0;//  �ｽ�ｽ
			TetrisBlock[2][1].y = 1;
			TetrisBlock[2][2].x = 1;
			TetrisBlock[2][2].y = 1;
			TetrisBlock[2][3].x = 1;
			TetrisBlock[2][3].y = 2;
			TetrisBlock[2][4].x = 2;
			TetrisBlock[2][4].y = 3;

			TetrisBlock[3][0].x = 1;//  �ｽ�ｽ  
			TetrisBlock[3][0].y = 0;//�ｽ�ｽ�ｽ�ｽ�ｽ�ｽ
			TetrisBlock[3][1].x = 0;//  
			TetrisBlock[3][1].y = 1;
			TetrisBlock[3][2].x = 1;
			TetrisBlock[3][2].y = 1;
			TetrisBlock[3][3].x = 2;
			TetrisBlock[3][3].y = 1;
			TetrisBlock[3][4].x = 3;
			TetrisBlock[3][4].y = 2;

			break;
		case 2:
			TetrisBlock[0][0].x = 1;//  �ｽ�ｽ
			TetrisBlock[0][0].y = 0;//�ｽ�ｽ�ｽ�ｽ
			TetrisBlock[0][1].x = 0;//�ｽ�ｽ
			TetrisBlock[0][1].y = 1;
			TetrisBlock[0][2].x = 1;
			TetrisBlock[0][2].y = 1;
			TetrisBlock[0][3].x = 0;
			TetrisBlock[0][4].x = 2;
			TetrisBlock[0][4].y = 3;

			TetrisBlock[0][3].y = 2;
			TetrisBlock[1][0].x = 0;//�ｽ�ｽ�ｽ�ｽ  
			TetrisBlock[1][0].y = 0;//  �ｽ�ｽ�ｽ�ｽ
			TetrisBlock[1][1].x = 1;//  
			TetrisBlock[1][1].y = 0;
			TetrisBlock[1][2].x = 1;
			TetrisBlock[1][2].y = 1;
			TetrisBlock[1][3].x = 2;
			TetrisBlock[1][3].y = 1;
			TetrisBlock[1][4].x = 3;
			TetrisBlock[1][4].y = 2;

			break;
		case 3:
			TetrisBlock[0][0].x = 0;//�ｽ�ｽ
			TetrisBlock[0][0].y = 0;//�ｽ�ｽ�ｽ�ｽ
			TetrisBlock[0][1].x = 0;//  �ｽ�ｽ
			TetrisBlock[0][1].y = 1;
			TetrisBlock[0][2].x = 1;
			TetrisBlock[0][2].y = 1;
			TetrisBlock[0][3].x = 1;
			TetrisBlock[0][3].y = 2;
			TetrisBlock[0][4].x = 2;
			TetrisBlock[0][4].y = 3;

			TetrisBlock[1][0].x = 1;//  �ｽ�ｽ�ｽ�ｽ
			TetrisBlock[1][0].y = 0;//�ｽ�ｽ�ｽ�ｽ
			TetrisBlock[1][1].x = 2;//  
			TetrisBlock[1][1].y = 0;
			TetrisBlock[1][2].x = 0;
			TetrisBlock[1][2].y = 1;
			TetrisBlock[1][3].x = 1;
			TetrisBlock[1][3].y = 1;
			TetrisBlock[1][4].x = 3;
			TetrisBlock[1][4].y = 2;

			break;
		case 4:
			TetrisBlock[0][0].x = 0;//�ｽ�ｽ�ｽ�ｽ
			TetrisBlock[0][0].y = 0;//�ｽ�ｽ
			TetrisBlock[0][1].x = 1;//�ｽ�ｽ
			TetrisBlock[0][1].y = 0;
			TetrisBlock[0][2].x = 0;
			TetrisBlock[0][2].y = 1;
			TetrisBlock[0][3].x = 0;
			TetrisBlock[0][3].y = 2;
			TetrisBlock[0][4].x = 2;
			TetrisBlock[0][4].y = 3;

			TetrisBlock[1][0].x = 0;//�ｽ�ｽ�ｽ�ｽ�ｽ�ｽ
			TetrisBlock[1][0].y = 0;//    �ｽ�ｽ
			TetrisBlock[1][1].x = 1;//
			TetrisBlock[1][1].y = 0;
			TetrisBlock[1][2].x = 2;
			TetrisBlock[1][2].y = 0;
			TetrisBlock[1][3].x = 2;
			TetrisBlock[1][3].y = 1;	
			TetrisBlock[1][4].x = 3;
			TetrisBlock[1][4].y = 2;

			TetrisBlock[2][0].x = 1;//  �ｽ�ｽ
			TetrisBlock[2][0].y = 0;//  �ｽ�ｽ  
			TetrisBlock[2][1].x = 1;//�ｽ�ｽ�ｽ�ｽ
			TetrisBlock[2][1].y = 1;
			TetrisBlock[2][2].x = 0;
			TetrisBlock[2][2].y = 2;
			TetrisBlock[2][3].x = 1;
			TetrisBlock[2][3].y = 2;
			TetrisBlock[2][4].x = 2;
			TetrisBlock[2][4].y = 3;

			TetrisBlock[3][0].x = 0;//�ｽ�ｽ
			TetrisBlock[3][0].y = 0;//�ｽ�ｽ�ｽ�ｽ�ｽ�ｽ  
			TetrisBlock[3][1].x = 0;//
			TetrisBlock[3][1].y = 1;
			TetrisBlock[3][2].x = 1;
			TetrisBlock[3][2].y = 1;
			TetrisBlock[3][3].x = 2;
			TetrisBlock[3][3].y = 1;
			TetrisBlock[3][4].x = 3;
			TetrisBlock[3][4].y = 2;

			break;
		case 5:
			TetrisBlock[0][0].x = 0;//�ｽ�ｽ�ｽ�ｽ
			TetrisBlock[0][0].y = 0;//�ｽ�ｽ�ｽ�ｽ
			TetrisBlock[0][1].x = 1;
			TetrisBlock[0][1].y = 0;
			TetrisBlock[0][2].x = 0;
			TetrisBlock[0][2].y = 1;
			TetrisBlock[0][3].x = 1;
			TetrisBlock[0][3].y = 1;
			TetrisBlock[0][4].x = 2;
			TetrisBlock[0][4].y = 2;

			break;

		case 6:
			TetrisBlock[0][0].x = 0;//�ｽ�ｽ�ｽ�ｽ
			TetrisBlock[0][0].y = 0;//  �ｽ�ｽ
			TetrisBlock[0][1].x = 1;//  �ｽ�ｽ
			TetrisBlock[0][1].y = 0;
			TetrisBlock[0][2].x = 1;
			TetrisBlock[0][2].y = 1;
			TetrisBlock[0][3].x = 1;
			TetrisBlock[0][3].y = 2;
			TetrisBlock[0][4].x = 2;
			TetrisBlock[0][4].y = 3;

			TetrisBlock[1][0].x = 2;//    �ｽ�ｽ
			TetrisBlock[1][0].y = 0;//�ｽ�ｽ�ｽ�ｽ�ｽ�ｽ
			TetrisBlock[1][1].x = 0;//
			TetrisBlock[1][1].y = 1;
			TetrisBlock[1][2].x = 1;
			TetrisBlock[1][2].y = 1;
			TetrisBlock[1][3].x = 2;
			TetrisBlock[1][3].y = 1;	
			TetrisBlock[1][4].x = 3;
			TetrisBlock[1][4].y = 2;

			TetrisBlock[2][0].x = 0;//�ｽ�ｽ
			TetrisBlock[2][0].y = 0;//�ｽ�ｽ  
			TetrisBlock[2][1].x = 0;//�ｽ�ｽ�ｽ�ｽ
			TetrisBlock[2][1].y = 1;
			TetrisBlock[2][2].x = 0;
			TetrisBlock[2][2].y = 2;
			TetrisBlock[2][3].x = 1;
			TetrisBlock[2][3].y = 2;
			TetrisBlock[2][4].x = 2;
			TetrisBlock[2][4].y = 3;

			TetrisBlock[3][0].x = 0;//�ｽ�ｽ�ｽ�ｽ�ｽ�ｽ
			TetrisBlock[3][0].y = 0;//�ｽ�ｽ  
			TetrisBlock[3][1].x = 1;//
			TetrisBlock[3][1].y = 0;
			TetrisBlock[3][2].x = 2;
			TetrisBlock[3][2].y = 0;
			TetrisBlock[3][3].x = 0;
			TetrisBlock[3][3].y = 1;
			TetrisBlock[3][4].x = 3;
			TetrisBlock[3][4].y = 2;

			break;
		default:
			break;
		}
		currViewBlockForm = i;
	}

	//Bitmap�ｽｶ撰ｿｽ
	private int initBitmap() {
		if (bOriginal) {
			iViewCategory = TetrisConst.BLOCK_ORIGINAL;
			int i = (int)(Math.random() * 10) % 9;
			switch (i) {
			case 0:
				blockViewBitmap = R.drawable.russblue;
				break;
			case 1:
				blockViewBitmap = R.drawable.russgrey;
				break;
			case 2:
				blockViewBitmap = R.drawable.russorange;
				break;
			case 3:
				blockViewBitmap = R.drawable.russred;
				break;
			case 4:
				blockViewBitmap = R.drawable.russthinblue;
				break;
			case 5:
				blockViewBitmap = R.drawable.russthinblue;
				break;
			case 6:
				blockViewBitmap = R.drawable.russorange;
				break;
			case 7:
				blockViewBitmap = R.drawable.russthinpurple;
				break;
			case 8:
				blockViewBitmap = R.drawable.russwhite;
				break;
			default:
				break;
			}
		}
		else if (bHeart) {
			iViewCategory = TetrisConst.BLOCK_HEART;
			int i = (int)(Math.random() * 10) % 7;
			switch (i) {
			case 0:
				blockViewBitmap = R.drawable.heart1;
				break;
			case 1:
				blockViewBitmap = R.drawable.heart2;
				break;
			case 2:
				blockViewBitmap = R.drawable.heart3;
				break;
			case 3:
				blockViewBitmap = R.drawable.heart4;
				break;
			case 4:
				blockViewBitmap = R.drawable.heart5;
				break;
			case 5:
				blockViewBitmap = R.drawable.heart6;
				break;
			case 6:
				blockViewBitmap = R.drawable.heart7;
				break;
			default:
				break;
			}
		}
		else if (bChocalate) {
			iViewCategory = TetrisConst.BLOCK_CHOCALATE;
			int i = (int)(Math.random() * 20) % 14;
			switch (i) {
			case 0:
				blockViewBitmap = R.drawable.choca1;
				break;
			case 1:
				blockViewBitmap = R.drawable.choca2;
				break;
			case 2:
				blockViewBitmap = R.drawable.choca3;
				break;
			case 3:
				blockViewBitmap = R.drawable.choca4;
				break;
			case 4:
				blockViewBitmap = R.drawable.choca5;
				break;
			case 5:
				blockViewBitmap = R.drawable.choca6;
				break;
			case 6:
				blockViewBitmap = R.drawable.choca7;
				break;
			case 7:
				blockViewBitmap = R.drawable.choca8;
				break;
			case 8:
				blockViewBitmap = R.drawable.choca9;
				break;
			case 9:
				blockViewBitmap = R.drawable.choca10;
				break;
			case 10:
				blockViewBitmap = R.drawable.choca11;
				break;
			case 11:
				blockViewBitmap = R.drawable.choca12;
				break;
			case 12:
				blockViewBitmap = R.drawable.choca13;
				break;
			case 13:
				blockViewBitmap = R.drawable.choca14;
				break;
			default:
				break;
			}
		}
		else if (bFruit) {
			iViewCategory = TetrisConst.BLOCK_FRUIT;
			int i = (int)(Math.random() * 20) % 14;
			switch (i) {
			case 0:
				blockViewBitmap = R.drawable.fruit01;
				break;
			case 1:
				blockViewBitmap = R.drawable.fruit02;
				break;
			case 2:
				blockViewBitmap = R.drawable.fruit03;
				break;
			case 3:
				blockViewBitmap = R.drawable.fruit04;
				break;
			case 4:
				blockViewBitmap = R.drawable.fruit05;
				break;
			case 5:
				blockViewBitmap = R.drawable.fruit06;
				break;
			case 6:
				blockViewBitmap = R.drawable.fruit07;
				break;
			case 7:
				blockViewBitmap = R.drawable.fruit08;
				break;
			case 8:
				blockViewBitmap = R.drawable.fruit09;
				break;
			case 9:
				blockViewBitmap = R.drawable.fruit10;
				break;
			case 10:
				blockViewBitmap = R.drawable.fruit11;
				break;
			case 11:
				blockViewBitmap = R.drawable.fruit12;
				break;
			case 12:
				blockViewBitmap = R.drawable.fruit13;
				break;
			case 13:
				blockViewBitmap = R.drawable.fruit14;
				break;
			default:
				break;
			}
		}
		else if (bSweet) {
			iViewCategory = TetrisConst.BLOCK_SWEET;
			int i = (int)(Math.random() * 20) % 15;
			switch (i) {
			case 0:
				blockViewBitmap = R.drawable.sweets1;
				break;
			case 1:
				blockViewBitmap = R.drawable.sweets2;
				break;
			case 2:
				blockViewBitmap = R.drawable.sweets3;
				break;
			case 3:
				blockViewBitmap = R.drawable.sweets4;
				break;
			case 4:
				blockViewBitmap = R.drawable.sweets5;
				break;
			case 5:
				blockViewBitmap = R.drawable.sweets6;
				break;
			case 6:
				blockViewBitmap = R.drawable.sweets7;
				break;
			case 7:
				blockViewBitmap = R.drawable.sweets8;
				break;
			case 8:
				blockViewBitmap = R.drawable.sweets9;
				break;
			case 9:
				blockViewBitmap = R.drawable.sweets10;
				break;
			case 10:
				blockViewBitmap = R.drawable.sweets11;
				break;
			case 11:
				blockViewBitmap = R.drawable.sweets12;
				break;
			case 12:
				blockViewBitmap = R.drawable.sweets13;
				break;
			case 13:
				blockViewBitmap = R.drawable.sweets14;
				break;
			case 14 :
				blockViewBitmap = R.drawable.sweets15;
				break;
			default:
				break;
			}
		}
		return 0;
	}
	//Jazz Bitmap�ｽｶ撰ｿｽ
	private int initJazzBitmap(int category) {
		int i = category;
		switch (i) {
		case 0:
			blockJazzBitmap = R.drawable.ball1;
			break;
		case 1:
			blockJazzBitmap = R.drawable.ball2;
			break;
		case 2:
			blockJazzBitmap = R.drawable.ball3;
			break;
		case 3:
			blockJazzBitmap = R.drawable.ball4;
			break;
		case 4:
			blockJazzBitmap = R.drawable.ball5;
			break;
		case 5:
			blockJazzBitmap = R.drawable.ball6;
			break;
		case 6:
			blockJazzBitmap = R.drawable.ball7;
			break;
		case 7:
			blockJazzBitmap = R.drawable.ball8;
			break;
		default:
			break;
		}
		
		return 0;
	}
	//Service ball�ｽｶ撰ｿｽ
	private void initServiceBall() {
		int i = (int)(Math.random() * 10) % 2;
		switch (i) {
		case 0:
			blockServiceBitmap = R.drawable.ball1;
			break;
		case 1:
			blockServiceBitmap = R.drawable.ball2;
			break;
		default:
			break;
		}
		
		return;
	}
	
	//Frame�ｽﾌ描�ｽ�ｽ
	private void drawTetrisFrame(Canvas canvas, Paint mPaint) {
		mPaint.setAntiAlias(true);
		mPaint.setColor(Color.BLACK);
		//Log.i("TETRISACTIVITY", "XLength" + Integer.toString(TetrisConst.XLength));
		//Log.i("TETRISACTIVITY", "YLength" + Integer.toString(TetrisConst.YLength));
		Rect rect = new Rect(TetrisConst.STARTX, TetrisConst.STARTY, 
				TetrisConst.STARTX + TetrisConst.XLength + 2, 
				TetrisConst.STARTY + TetrisConst.YLength);
		canvas.drawRect(rect, mPaint);
		
		mPaint.setColor(Color.LTGRAY); 
		canvas.drawLine(rect.right + 1, rect.top + 2, rect.right + 1,  
				rect.bottom + 2, mPaint);  
		canvas.drawLine(rect.left + 2, rect.bottom + 1, rect.right + 2,  
				rect.bottom + 1, mPaint);
		canvas.drawLine(rect.right + 2, rect.top + 3, rect.right + 2,  
				rect.bottom + 3, mPaint);
		canvas.drawLine(rect.left + 3, rect.bottom + 2, rect.right + 3,  
				rect.bottom + 2, mPaint);


	}
	
	private void drawViewFrame(Canvas canvas, Paint mPaint) {
		mPaint.setColor(Color.BLACK);
		Rect rect = new Rect(TetrisConst.PRE_STARTX, TetrisConst.PRE_STARTY,
				TetrisConst.PRE_STARTX + TetrisConst.PRE_LENGTH + 2,
				TetrisConst.PRE_STARTY + TetrisConst.PRE_LENGTH);
		canvas.drawRect(rect, mPaint);	
		
		mPaint.setColor(Color.LTGRAY); 
		canvas.drawLine(rect.right + 1, rect.top + 2, rect.right + 1,  
				rect.bottom + 2, mPaint);  
		canvas.drawLine(rect.right + 2, rect.top + 3, rect.right + 2,  
				rect.bottom + 3, mPaint);

	}
	

	//Bitmap�ｽ�ｽ�ｽ謫ｾ
	private Bitmap getBitmap(int blockBitmap, int iCategory) {
		float nSize = 0;
		if (iCategory == TetrisConst.BLOCK_BALL) {
			//nSize = (float)(TetrisConst.XLength * 0.6/280);
			nSize = (float)0.7;
		}
		else {
			//nSize = (float)(TetrisConst.XLength * 0.4/280);
			nSize = (float)0.5;
		}
		Bitmap bmp = BitmapFactory.decodeResource(getResources(), blockBitmap);
		Matrix matrix = new Matrix();
		matrix.postScale(nSize, nSize);
		Bitmap destBmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(),
				matrix, true);
		
		return destBmp;
	}
	 
	//�ｽr�ｽ�ｽ�ｽ[�ｽp�ｽu�ｽ�ｽ�ｽb�ｽN�ｽﾌ描�ｽ�ｽ
	private void drawViewBlock(int times) {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 5; j++) {
				TetrisMoveBlock[i][j].x = TetrisBlock[i][j].x;
				TetrisMoveBlock[i][j].y = TetrisBlock[i][j].y;
			}
			currMoveBlockForm = currViewBlockForm;
			blockMoveBitmap = blockViewBitmap;
		}		
		iBlockCategory = iViewCategory;
		preTetrisBlock();
		initBitmap();
		if (times != 1) {
			Bitmap bmp = null;
			if (bOriginal) {
				bmp = getBitmap(blockViewBitmap, TetrisConst.BLOCK_ORIGINAL);
			}
			else {
				bmp = getBitmap(blockViewBitmap, 0);
			}
			mViewBitmap = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), 
					Config.ARGB_8888);
			
			Canvas mCanvas = new Canvas(mViewBitmap);
			mCanvas.drawBitmap(bmp, 0, 0, null);
			for (int i = 0; i < 4; i++) {
				xVPos[i] = TetrisConst.PRE_STARTX + 20 + TetrisConst.BLOCKSIZE * TetrisBlock[0][i].x;
				yVPos[i] = TetrisConst.PRE_STARTY + 10 + TetrisConst.BLOCKSIZE * TetrisBlock[0][i].y;
			}
			bDoReDraw = Boolean.TRUE;
		}
		
		invalidate();
	}
	
	//�ｽu�ｽ�ｽ�ｽb�ｽN�ｽ`�ｽ�ｽ
	private void drawMoveBlock() {
		xMoveBlock = TetrisConst.START_BLOCKX;
		yMoveBlock = TetrisConst.START_BLOCKY;
		currBlockState = 0;

		try {
			for (int i = 0; i < 4; i++) {
				xMPos[i] = xMoveBlock + TetrisConst.BLOCKSIZE * TetrisMoveBlock[currBlockState][i].x;
				yMPos[i] = yMoveBlock + TetrisConst.BLOCKSIZE * TetrisMoveBlock[currBlockState][i].y;
//				Log.i(TetrisConst.LOGTAB, "xMPos" + Integer.toString(xMPos[i]));
//				Log.i(TetrisConst.LOGTAB, "yMPos" + Integer.toString(yMPos[i]));
				TetrisBlockMap[(xMPos[i]-TetrisConst.TETRISMAP_LEFT)/TetrisConst.BLOCKSIZE]
								 [(yMPos[i]-TetrisConst.TETRISMAP_TOP)/TetrisConst.BLOCKSIZE] = TetrisConst.MAP_BLOCK;
				TetrisColorMap[(xMPos[i]-TetrisConst.TETRISMAP_LEFT)/TetrisConst.BLOCKSIZE]
								 [(yMPos[i]-TetrisConst.TETRISMAP_TOP)/TetrisConst.BLOCKSIZE] = blockMoveBitmap;			
				TetrisCategoryMap[(xMPos[i]-TetrisConst.TETRISMAP_LEFT)/TetrisConst.BLOCKSIZE]
								 [(yMPos[i]-TetrisConst.TETRISMAP_TOP)/TetrisConst.BLOCKSIZE] = iBlockCategory;			
			}		
		}
		catch(Exception ex) {
			Log.e("TETRISACTIVITY", "drawMoveBlock: " + ex.getMessage());
		}
		bDoReDraw = Boolean.TRUE;
		
		invalidate();
	}
	
	//�ｽu�ｽ�ｽ�ｽb�ｽN�ｽ`�ｽ�ｽ
	private void drawServiceBall() {
		xMoveBlock = TetrisConst.START_BLOCKX;
		yMoveBlock = TetrisConst.START_BLOCKY;
		currBlockState = 0;
		try {
			xMPos[0] = xMoveBlock;
			yMPos[0] = yMoveBlock;
			TetrisBlockMap[(xMoveBlock-TetrisConst.TETRISMAP_LEFT)/TetrisConst.BLOCKSIZE]
							 [(yMoveBlock-TetrisConst.TETRISMAP_TOP)/TetrisConst.BLOCKSIZE] = TetrisConst.MAP_SERVICE;
			TetrisColorMap[(xMoveBlock-TetrisConst.TETRISMAP_LEFT)/TetrisConst.BLOCKSIZE]
							 [(yMoveBlock-TetrisConst.TETRISMAP_TOP)/TetrisConst.BLOCKSIZE] = blockServiceBitmap;			
			TetrisCategoryMap[(xMoveBlock-TetrisConst.TETRISMAP_LEFT)/TetrisConst.BLOCKSIZE]
							 [(yMoveBlock-TetrisConst.TETRISMAP_TOP)/TetrisConst.BLOCKSIZE] = TetrisConst.BLOCK_BALL;			
		}
		catch(Exception ex) {
			Log.e("TETRISACTIVITY", "drawServiceBall: " + ex.getMessage());
		}
		
		bDoReDraw = Boolean.TRUE;
		
		invalidate();
	}
	
	private void drawJazzTetrisBlock() {
		int i = 0;
		int col = TetrisConst.X_SIZE - 1;
		int iIncLine = iGameLevel / 2;
		try {
			while (iIncLine-- != 0) {
				if (getRowEmptyNum(i) < 7) {
					continue;
				}
				int count = 0;
	
				while (count++ < 8) {
					i = (int)(Math.random() * 20) % 16;
					if (TetrisBlockMap[i][col] == 0) {
						TetrisBlockMap[i][col] = TetrisConst.MAP_JAZZ;
						initJazzBitmap(i % 8);
						TetrisColorMap[i][col] = blockJazzBitmap;
						//show(Integer.toString(iIncLine));
						TetrisCategoryMap[i][col] = TetrisConst.BLOCK_BALL;
					}
				}
				col--;
			}	
		}
		catch(Exception ex) {
			Log.e("TETRISACTIVITY", "drawJazzTetrisBlock: " + ex.getMessage());
		}
	}
	
	//�ｽL�ｽ[�ｽﾌ撰ｿｽ�ｽ�ｽ
	public boolean onKeyDown(int keyCode, KeyEvent msg) {
		if (!bIsStart) {
			return true;
		}
		if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
			moveBlockDown();
		}
		else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
			moveBlockLeft(4);
		}
		else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
			moveBlockRight(4);
		}
		else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
			rotateBlock();
		}
		return true;
	}
	
	//�ｽ�ｽ�ｽﾉ移難ｿｽ
	protected boolean moveBlockDown() {
		int i = 0;
		Boolean bReachButtom = Boolean.FALSE;
		try {
			//Clear the old block
		//Log.i("TETRISACTIVITY", "In the moveBlockDown " + TetrisBlockMap.toString());
			for (i = 0; i < 4; i++) {
				TetrisBlockMap[(xMPos[i]-TetrisConst.TETRISMAP_LEFT)/TetrisConst.BLOCKSIZE]
				 [(yMPos[i]-TetrisConst.TETRISMAP_TOP)/TetrisConst.BLOCKSIZE] = 0;
				TetrisColorMap[(xMPos[i]-TetrisConst.TETRISMAP_LEFT)/TetrisConst.BLOCKSIZE]
				  			 [(yMPos[i]-TetrisConst.TETRISMAP_TOP)/TetrisConst.BLOCKSIZE] = 0;
				TetrisCategoryMap[(xMPos[i]-TetrisConst.TETRISMAP_LEFT)/TetrisConst.BLOCKSIZE]
					  			 [(yMPos[i]-TetrisConst.TETRISMAP_TOP)/TetrisConst.BLOCKSIZE] = 0;
			}
			for (i = 0; i < 4; i++) {
				yMPos[i] += TetrisConst.BLOCKSIZE;
				//Log.i("TETRISACTIVITY", "In the moveBlockDown yMPos" + Integer.toString(yMPos[i]));
				//Log.i("TETRISACTIVITY", "In the moveBlockDown " + Integer.toString(frameDownRightY - TetrisConst.BLOCKSIZE));
				if (yMPos[i] > frameDownRightY - TetrisConst.BLOCKSIZE 
					|| TetrisBlockMap[(xMPos[i]-TetrisConst.TETRISMAP_LEFT)/TetrisConst.BLOCKSIZE]
					 				 [(yMPos[i]-TetrisConst.TETRISMAP_TOP)/TetrisConst.BLOCKSIZE] != 0) {
					bReachButtom = Boolean.TRUE;
				}
			}
			if (bReachButtom) {
				for (i = 0; i < 4; i++) {
					yMPos[i] -= TetrisConst.BLOCKSIZE;
				}
			}
			for (i = 0; i < 4; i++) {
				TetrisBlockMap[(xMPos[i]-TetrisConst.TETRISMAP_LEFT)/TetrisConst.BLOCKSIZE]
				 [(yMPos[i]-TetrisConst.TETRISMAP_TOP)/TetrisConst.BLOCKSIZE] = TetrisConst.MAP_BLOCK;
				TetrisColorMap[(xMPos[i]-TetrisConst.TETRISMAP_LEFT)/TetrisConst.BLOCKSIZE]
				  			 [(yMPos[i]-TetrisConst.TETRISMAP_TOP)/TetrisConst.BLOCKSIZE] = blockMoveBitmap;
				TetrisCategoryMap[(xMPos[i]-TetrisConst.TETRISMAP_LEFT)/TetrisConst.BLOCKSIZE]
					  			 [(yMPos[i]-TetrisConst.TETRISMAP_TOP)/TetrisConst.BLOCKSIZE] = iBlockCategory;
	
			}
		}
		catch(Exception ex) {
			Log.e("TETRISACTIVITY", "moveBlockDown: " + ex.getMessage());
		}
		if (bReachButtom) reachButtom();
		else yMoveBlock += TetrisConst.BLOCKSIZE;
		invalidate();
		
		return bReachButtom;
	}
	
	//Service ball down
	protected boolean moveServiceBallDown() {
		Boolean bReachButtom = Boolean.FALSE;

		int iServicePos = getLowestPos((xMoveBlock - TetrisConst.TETRISMAP_LEFT)/TetrisConst.BLOCKSIZE);
		//show(Integer.toString(iServicePos));
		try {
			//Clear the old block
			if (TetrisBlockMap[(xMoveBlock-TetrisConst.TETRISMAP_LEFT)/TetrisConst.BLOCKSIZE]
				  			 [(yMoveBlock-TetrisConst.TETRISMAP_TOP)/TetrisConst.BLOCKSIZE] == TetrisConst.MAP_SERVICE) {
				TetrisBlockMap[(xMoveBlock-TetrisConst.TETRISMAP_LEFT)/TetrisConst.BLOCKSIZE]
				 [(yMoveBlock-TetrisConst.TETRISMAP_TOP)/TetrisConst.BLOCKSIZE] = 0;
				TetrisColorMap[(xMoveBlock-TetrisConst.TETRISMAP_LEFT)/TetrisConst.BLOCKSIZE]
				  			 [(yMoveBlock-TetrisConst.TETRISMAP_TOP)/TetrisConst.BLOCKSIZE] = 0;
				TetrisCategoryMap[(xMoveBlock-TetrisConst.TETRISMAP_LEFT)/TetrisConst.BLOCKSIZE]
					  			 [(yMoveBlock-TetrisConst.TETRISMAP_TOP)/TetrisConst.BLOCKSIZE] = 0;
			}
			//show(Integer.toString(yMoveBlock));
			if (iServicePos <= (yMoveBlock - TetrisConst.TETRISMAP_TOP)/TetrisConst.BLOCKSIZE
				||	yMoveBlock == frameDownRightY - TetrisConst.BLOCKSIZE) {
				bReachButtom = Boolean.TRUE;
				bServiceBall = Boolean.FALSE;
				bBallReachBottom = Boolean.TRUE;
				//show(Integer.toString(yMoveBlock));
			}
			if (!bReachButtom) {
				yMoveBlock += TetrisConst.BLOCKSIZE;
			}
	
			if (TetrisBlockMap[(xMoveBlock-TetrisConst.TETRISMAP_LEFT)/TetrisConst.BLOCKSIZE]
				  			 [(yMoveBlock-TetrisConst.TETRISMAP_TOP)/TetrisConst.BLOCKSIZE] == 0) {		
				TetrisBlockMap[(xMoveBlock-TetrisConst.TETRISMAP_LEFT)/TetrisConst.BLOCKSIZE]
				 [(yMoveBlock-TetrisConst.TETRISMAP_TOP)/TetrisConst.BLOCKSIZE] = TetrisConst.MAP_SERVICE;
				TetrisColorMap[(xMoveBlock-TetrisConst.TETRISMAP_LEFT)/TetrisConst.BLOCKSIZE]
				  			 [(yMoveBlock-TetrisConst.TETRISMAP_TOP)/TetrisConst.BLOCKSIZE] = blockServiceBitmap;
				TetrisCategoryMap[(xMoveBlock-TetrisConst.TETRISMAP_LEFT)/TetrisConst.BLOCKSIZE]
					  			 [(yMoveBlock-TetrisConst.TETRISMAP_TOP)/TetrisConst.BLOCKSIZE] = TetrisConst.BLOCK_BALL;
	
			}
		}
		catch(Exception ex) {
			Log.e("TETRISACTIVITY", "moveServiceBallDown: " + ex.getMessage());
		}
		if (bReachButtom) reachButtom();
		invalidate();
		
		return bReachButtom;
	}
	
	//�ｽ�ｽ�ｽﾉ移難ｿｽ
	protected void moveBlockLeft() {
		if (bShowBeforeStart) {
			return;
		}
		if (bServiceBall) {
			moveBlockLeft(1);
		}
		else {
			moveBlockLeft(4);
		}
	}
	private void moveBlockLeft(int num) {
		int i = 0;
		Boolean bLeftStop = Boolean.FALSE;
		try {
			//Initialize
			for (i = 0; i < num; i++) {
				if (bServiceBall) {
					xMPos[i] = xMoveBlock;
					yMPos[i] = yMoveBlock;
				}
				else {
					xMPos[i] = xMoveBlock + TetrisConst.BLOCKSIZE * TetrisMoveBlock[currBlockState][i].x;
					yMPos[i] = yMoveBlock + TetrisConst.BLOCKSIZE * TetrisMoveBlock[currBlockState][i].y;
				}
				TetrisBlockMap[(xMPos[i]-TetrisConst.TETRISMAP_LEFT)/TetrisConst.BLOCKSIZE]
				  			 [(yMPos[i]-TetrisConst.TETRISMAP_TOP)/TetrisConst.BLOCKSIZE] = 0;			
			}
			//clear
			for (i = 0; i < num; i++) {
				TetrisBlockMap[(xMPos[i]-TetrisConst.TETRISMAP_LEFT)/TetrisConst.BLOCKSIZE]
				 [(yMPos[i]-TetrisConst.TETRISMAP_TOP)/TetrisConst.BLOCKSIZE] = 0;
				TetrisColorMap[(xMPos[i]-TetrisConst.TETRISMAP_LEFT)/TetrisConst.BLOCKSIZE]
				  			 [(yMPos[i]-TetrisConst.TETRISMAP_TOP)/TetrisConst.BLOCKSIZE] = 0;
				TetrisCategoryMap[(xMPos[i]-TetrisConst.TETRISMAP_LEFT)/TetrisConst.BLOCKSIZE]
					  			 [(yMPos[i]-TetrisConst.TETRISMAP_TOP)/TetrisConst.BLOCKSIZE] = 0;
			}
			if (xMoveBlock == frameTopLeftX) {
				bLeftStop = Boolean.TRUE;
			}
			else {
				for (i = 0; i < num; i++) {
					//xMPos[i] -= TetrisConst.BLOCKSIZE;
					if (TetrisBlockMap[(xMPos[i]-TetrisConst.TETRISMAP_LEFT - TetrisConst.BLOCKSIZE)/TetrisConst.BLOCKSIZE]
							     			 [(yMPos[i]-TetrisConst.TETRISMAP_TOP)/TetrisConst.BLOCKSIZE] != 0) {
						bLeftStop = Boolean.TRUE;				
					}
				}
			}
			if (!bLeftStop) {
				xMoveBlock -= TetrisConst.BLOCKSIZE;
				for (i = 0; i < num; i++) {
					xMPos[i] -= TetrisConst.BLOCKSIZE;
				}
			}
	
			for (i = 0; i < num; i++) {
				if (bServiceBall) {
					TetrisBlockMap[(xMPos[i]-TetrisConst.TETRISMAP_LEFT)/TetrisConst.BLOCKSIZE]
					  			 [(yMPos[i]-TetrisConst.TETRISMAP_TOP)/TetrisConst.BLOCKSIZE] = TetrisConst.MAP_SERVICE;
					TetrisColorMap[(xMPos[i]-TetrisConst.TETRISMAP_LEFT)/TetrisConst.BLOCKSIZE]
					  			  [(yMPos[i]-TetrisConst.TETRISMAP_TOP)/TetrisConst.BLOCKSIZE] = blockServiceBitmap;			
					TetrisCategoryMap[(xMPos[i]-TetrisConst.TETRISMAP_LEFT)/TetrisConst.BLOCKSIZE]
							  			 [(yMPos[i]-TetrisConst.TETRISMAP_TOP)/TetrisConst.BLOCKSIZE] = TetrisConst.BLOCK_BALL;
				}
				else {
					TetrisBlockMap[(xMPos[i]-TetrisConst.TETRISMAP_LEFT)/TetrisConst.BLOCKSIZE]
					 [(yMPos[i]-TetrisConst.TETRISMAP_TOP)/TetrisConst.BLOCKSIZE] = TetrisConst.MAP_BLOCK;
					TetrisColorMap[(xMPos[i]-TetrisConst.TETRISMAP_LEFT)/TetrisConst.BLOCKSIZE]
					  			 [(yMPos[i]-TetrisConst.TETRISMAP_TOP)/TetrisConst.BLOCKSIZE] = blockMoveBitmap;
					TetrisCategoryMap[(xMPos[i]-TetrisConst.TETRISMAP_LEFT)/TetrisConst.BLOCKSIZE]
							  			 [(yMPos[i]-TetrisConst.TETRISMAP_TOP)/TetrisConst.BLOCKSIZE] = iBlockCategory;
				}
	
			}
		}
		catch(Exception ex) {
			Log.e("TETRISACTIVITY", "moveBlockLeft: " + ex.getMessage());
		}
		invalidate();
	}
	
	//�ｽE�ｽﾉ移難ｿｽ
	protected void moveBlockRight() {
		if (bShowBeforeStart) {
			return;
		}
		if (bServiceBall) {
			moveBlockRight(1);
		}
		else {
			moveBlockRight(4);
		}
	}
	private void moveBlockRight(int num) {
		int i = 0;
		Boolean bRightStop = Boolean.FALSE;
		try {
			//Initialize
			for (i = 0; i < num; i++) {
				if (bServiceBall) {
					xMPos[i] = xMoveBlock;
					yMPos[i] = yMoveBlock;
				}
				else {
					xMPos[i] = xMoveBlock + TetrisConst.BLOCKSIZE * TetrisMoveBlock[currBlockState][i].x;
					yMPos[i] = yMoveBlock + TetrisConst.BLOCKSIZE * TetrisMoveBlock[currBlockState][i].y;
				}
				TetrisBlockMap[(xMPos[i]-TetrisConst.TETRISMAP_LEFT)/TetrisConst.BLOCKSIZE]
				  			 [(yMPos[i]-TetrisConst.TETRISMAP_TOP)/TetrisConst.BLOCKSIZE] = 0;			
			}
			//clear
			for (i = 0; i < num; i++) {
				TetrisBlockMap[(xMPos[i]-TetrisConst.TETRISMAP_LEFT)/TetrisConst.BLOCKSIZE]
				 [(yMPos[i]-TetrisConst.TETRISMAP_TOP)/TetrisConst.BLOCKSIZE] = 0;
				TetrisColorMap[(xMPos[i]-TetrisConst.TETRISMAP_LEFT)/TetrisConst.BLOCKSIZE]
				  			 [(yMPos[i]-TetrisConst.TETRISMAP_TOP)/TetrisConst.BLOCKSIZE] = 0;
				TetrisCategoryMap[(xMPos[i]-TetrisConst.TETRISMAP_LEFT)/TetrisConst.BLOCKSIZE]
					  			 [(yMPos[i]-TetrisConst.TETRISMAP_TOP)/TetrisConst.BLOCKSIZE] = 0;
	
			}
			if (!bServiceBall
				&& (xMoveBlock + TetrisConst.BLOCKSIZE * TetrisMoveBlock[currBlockState][4].x) ==
					frameTopRightX) {
				bRightStop = Boolean.TRUE;
			}
			else if (bServiceBall && xMoveBlock + TetrisConst.BLOCKSIZE == frameTopRightX) {
				bRightStop = Boolean.TRUE;
			}
			else {
				for (i = 0; i < num; i++) {
					//xMPos[i] += TetrisConst.BLOCKSIZE;

					if (TetrisBlockMap[(xMPos[i]-TetrisConst.TETRISMAP_LEFT + TetrisConst.BLOCKSIZE)/TetrisConst.BLOCKSIZE]
						   						 [(yMPos[i]-TetrisConst.TETRISMAP_TOP)/TetrisConst.BLOCKSIZE] != 0) {					
						bRightStop = Boolean.TRUE;
					}
				}
			}
			if (!bRightStop) {
				xMoveBlock += TetrisConst.BLOCKSIZE;
				for (i = 0; i < num; i++) {			
					xMPos[i] += TetrisConst.BLOCKSIZE;
				}
			}
	
	
			for (i = 0; i < num; i++) {
				if (bServiceBall) {
					TetrisBlockMap[(xMPos[i]-TetrisConst.TETRISMAP_LEFT)/TetrisConst.BLOCKSIZE]
					  			 [(yMPos[i]-TetrisConst.TETRISMAP_TOP)/TetrisConst.BLOCKSIZE] = TetrisConst.MAP_SERVICE;
					TetrisColorMap[(xMPos[i]-TetrisConst.TETRISMAP_LEFT)/TetrisConst.BLOCKSIZE]
					  			  [(yMPos[i]-TetrisConst.TETRISMAP_TOP)/TetrisConst.BLOCKSIZE] = blockServiceBitmap;				
					TetrisCategoryMap[(xMPos[i]-TetrisConst.TETRISMAP_LEFT)/TetrisConst.BLOCKSIZE]
							  			 [(yMPos[i]-TetrisConst.TETRISMAP_TOP)/TetrisConst.BLOCKSIZE] = TetrisConst.BLOCK_BALL;
				}
				else {
					TetrisBlockMap[(xMPos[i]-TetrisConst.TETRISMAP_LEFT)/TetrisConst.BLOCKSIZE]
					 [(yMPos[i]-TetrisConst.TETRISMAP_TOP)/TetrisConst.BLOCKSIZE] = TetrisConst.MAP_BLOCK;
					TetrisColorMap[(xMPos[i]-TetrisConst.TETRISMAP_LEFT)/TetrisConst.BLOCKSIZE]
					  			 [(yMPos[i]-TetrisConst.TETRISMAP_TOP)/TetrisConst.BLOCKSIZE] = blockMoveBitmap;
					TetrisCategoryMap[(xMPos[i]-TetrisConst.TETRISMAP_LEFT)/TetrisConst.BLOCKSIZE]
							  			 [(yMPos[i]-TetrisConst.TETRISMAP_TOP)/TetrisConst.BLOCKSIZE] = iBlockCategory;
				}
	
	
			}
		}
		catch(Exception ex) {
			Log.e("TETRISACTIVITY", "moveBlockRight: " + ex.getMessage());
		}
		
		invalidate();
	}
	
	//�ｽ�ｽﾉ抵ｿｽ�ｽ�ｽ
	private void reachButtom() {

		if (bShowBeforeStart) {
			//Log.i(TetrisConst.LOGTAB, "In the reachButton");
			iCurrentTetrisScore = (iCurrentTetrisScore < 2000) ? iCurrentTetrisScore + 20 : 0;
			setScore();
			setGameLevel();
			if (iGameLevel >= 3) {//�ｽ�ｽ�ｽx�ｽ�ｽ�ｽR�ｽ�ｽB�ｽ�ｽ�ｽ�ｽ�ｽ�ｽA�ｽT�ｽ[�ｽr�ｽX�ｽu�ｽ�ｽ�ｽb�ｽN�ｽ�ｽ�ｽJ�ｽn�ｽB
				bServiceBall = Boolean.TRUE;	
				initServiceBall();
				drawServiceBall();
				return;
			}	
			if (isGameOver()) {
				initParams();
				initFrameScope();
				setBlockCategory((int)(Math.random() * 10) % 5);
			}
		}
		else {
			playSound(TetrisConst.SOUND_REACH_BUTTOM, 0);
			if (isGameOver()) {
				stopBkSound();
				playSound(TetrisConst.SOUND_GAME_OVER, 0);
				onTetrisStop();
				((TetrisActivity)getContext()).createScoreWindow(iCurrentTetrisScore, iGameLevel);	
				return;
			}
			int line = clearFullRows();
			if (line != 0) {
				getScore(line);
				setScore();
				playSound(TetrisConst.SOUND_CLEAR_BLOCK, 0);
				//Game level change			
				setGameLevel();
				//Service ball
				if (iGameLevel >= 3 && iServiceBallNum++ < iGameLevel) {//�ｽ�ｽ�ｽx�ｽ�ｽ�ｽR�ｽ�ｽB�ｽ�ｽ�ｽ�ｽ�ｽ�ｽA�ｽT�ｽ[�ｽr�ｽX�ｽu�ｽ�ｽ�ｽb�ｽN�ｽ�ｽ�ｽJ�ｽn�ｽB
					bServiceBall = Boolean.TRUE;	
					initServiceBall();
					drawServiceBall();
					return;
				}
			}
		}
		drawViewBlock(2);
		drawMoveBlock();

		return;
	}
	
	// score�ｽ�ｽ�ｽv�ｽZ
	void getScore(int line)
	{
		int score = 1;

		switch (line) {
		case 1:
			score *= 20;
			break;
		case 2:
			score *= 45;
			break;
		case 3:
			score *= 60;
			break;
		case 4:
			score *= 100;
			break;
		default:
			score = 0;
			break;
		}
		
		if (bBallReachBottom) {
			score = 2 * score;
			bBallReachBottom = Boolean.FALSE;
		}
		iCurrentTetrisScore += score;
	}
    //Rotate
	protected void rotateBlock() {
		if (bShowBeforeStart) {
			return;
		}
		int i = 0;
		try {
			//Clear the Map		
			for (i = 0; i < 4; i++) {
				TetrisBlockMap[(xMPos[i]-TetrisConst.TETRISMAP_LEFT)/TetrisConst.BLOCKSIZE]
				 [(yMPos[i]-TetrisConst.TETRISMAP_TOP)/TetrisConst.BLOCKSIZE] = 0;
				TetrisColorMap[(xMPos[i]-TetrisConst.TETRISMAP_LEFT)/TetrisConst.BLOCKSIZE]
				  			 [(yMPos[i]-TetrisConst.TETRISMAP_TOP)/TetrisConst.BLOCKSIZE] = 0;
				TetrisCategoryMap[(xMPos[i]-TetrisConst.TETRISMAP_LEFT)/TetrisConst.BLOCKSIZE]
					  			 [(yMPos[i]-TetrisConst.TETRISMAP_TOP)/TetrisConst.BLOCKSIZE] = 0;
			}
			oldBlockState = currBlockState;
			//Rotate
			switch(currMoveBlockForm) {
			case TetrisConst.TETRISBLOCK_0:
				currBlockState = (currBlockState +1) % 2;
				break;
			case TetrisConst.TETRISBLOCK_1:
				currBlockState = (currBlockState +1) % 4;
				break;
			case TetrisConst.TETRISBLOCK_2:
				currBlockState = (currBlockState +1) % 2;
				break;
			case TetrisConst.TETRISBLOCK_3:
				currBlockState = (currBlockState +1) % 2;
				break;
			case TetrisConst.TETRISBLOCK_4:
				currBlockState = (currBlockState +1) % 4;
				break;
			case TetrisConst.TETRISBLOCK_5:
				break;
			case TetrisConst.TETRISBLOCK_6:
				currBlockState = (currBlockState +1) % 4;
				break;
			default:
				break;
			}
			int rightM = 0;
			int rightD = 0;
			//�ｽEmargin�ｽｴゑｿｽ�ｽ�ｽ�ｽ驍｢�ｽﾍ別のブ�ｽ�ｽ�ｽb�ｽN�ｽ�ｽ�ｽ�ｽ�ｽ阡ｻ�ｽf
			for (i = 0; i < 4; i++) {
				rightM = xMoveBlock + TetrisConst.BLOCKSIZE 
						* TetrisMoveBlock[currBlockState][i].x;
				rightD = yMoveBlock + TetrisConst.BLOCKSIZE
						* TetrisMoveBlock[currBlockState][i].y;
				if ((rightM + TetrisConst.BLOCKSIZE> TetrisConst.TETRISMAP_RIGHT)
					|| (TetrisBlockMap[(rightM-TetrisConst.TETRISMAP_LEFT)/TetrisConst.BLOCKSIZE]
					                  [(rightD-TetrisConst.TETRISMAP_TOP)/TetrisConst.BLOCKSIZE] != 0)) {
					currBlockState = oldBlockState;
					return;
				}
				
			}
			//Calculate the xMPos,yMPos
			for (i = 0; i < 4; i++) {
				xMPos[i] = xMoveBlock + TetrisConst.BLOCKSIZE * TetrisMoveBlock[currBlockState][i].x;
				yMPos[i] = yMoveBlock + TetrisConst.BLOCKSIZE * TetrisMoveBlock[currBlockState][i].y;
			}
			//Set the Map		
			for (i = 0; i < 4; i++) {
				TetrisBlockMap[(xMPos[i]-TetrisConst.TETRISMAP_LEFT)/TetrisConst.BLOCKSIZE]
				 [(yMPos[i]-TetrisConst.TETRISMAP_TOP)/TetrisConst.BLOCKSIZE] = TetrisConst.MAP_BLOCK;
				TetrisColorMap[(xMPos[i]-TetrisConst.TETRISMAP_LEFT)/TetrisConst.BLOCKSIZE]
				  			 [(yMPos[i]-TetrisConst.TETRISMAP_TOP)/TetrisConst.BLOCKSIZE] = blockMoveBitmap;
				TetrisCategoryMap[(xMPos[i]-TetrisConst.TETRISMAP_LEFT)/TetrisConst.BLOCKSIZE]
					  			 [(yMPos[i]-TetrisConst.TETRISMAP_TOP)/TetrisConst.BLOCKSIZE] = iBlockCategory;
	
			}
		}
		catch(Exception ex) {
			Log.e("TETRISACTIVITY", "rotateBlock: " + ex.getMessage());
		}
		invalidate();

	}

	//�ｽl�ｽﾟゑｿｽ�ｽs�ｽ�ｽ�ｽN�ｽ�ｽ�ｽA
	private int clearFullRows() {
		int i = 0; 
		int j = TetrisConst.Y_SIZE - 1;
		int line = 0;
		Boolean bFullRow = Boolean.TRUE;
		try {
			while (j >= 0) {
				for (i = 0; i < TetrisConst.X_SIZE; i++) {
					if (TetrisBlockMap[i][j] == 0) {
						bFullRow = Boolean.FALSE;
						break;
					}
				}
				if (bFullRow) {
					int k = 0;
					for (k = j-1; k >= 0; k--) {
						for (i = 0; i < TetrisConst.X_SIZE ; i++) {
							TetrisBlockMap[i][k+1] = TetrisBlockMap[i][k];
							TetrisColorMap[i][k+1] = TetrisColorMap[i][k];
							TetrisCategoryMap[i][k+1] = TetrisCategoryMap[i][k];
						}
					}
					for (i = 0; i < TetrisConst.X_SIZE; i++) {
						TetrisBlockMap[i][0] = 0;
						TetrisColorMap[i][0] = 0;
						TetrisCategoryMap[i][0] = 0;
					}
					j++;
					line++;
				}
				j--;
				bFullRow = Boolean.TRUE;
			}
		}
		catch(Exception ex) {
			Log.e("TETRISACTIVITY", "clearFullRows: " + ex.getMessage());
		}
		return line;
	}
	
	//Level�ｽ�ｽﾝ抵ｿｽ
	private void setGameLevel() {
		
		//Level up
		if ((iCurrentTetrisScore - tetrisScore) >= iGameLevel * TetrisConst.LEVEL_SCORE
				|| bShowBeforeStart) {
			if (bShowBeforeStart) {
				if ((iCurrentTetrisScore - tetrisScore) >= 20)
				iGameLevel = (iGameLevel == 10) ? 1 : iGameLevel + 1;
				setLevel();
			}
			else {
				iGameLevel = (iGameLevel < 10) ? iGameLevel + 1 : iGameLevel;
				setLevel();
				timerBlockDown = (timerBlockDown == 50) ? timerBlockDown : timerBlockDown - 50;	
				if (iGameLevel <= 10) {
					setTimer(iGameLevel);
				}
			}
			tetrisScore = iCurrentTetrisScore;
			if (iGameLevel >= 3) {
				drawJazzTetrisBlock();
			}
			if (!bShowBeforeStart) {
				playSound(TetrisConst.SOUND_LEVEL_UP, 0);
			}
			//Initialize the service ball number
			iServiceBallNum = 0;
		}

	}
	
	//Set Timer(stupid method, should be revised)
	private void setTimer(int iLevel) {
		if (bShowBeforeStart) {
			return;
		}
		try {
			if (tetrisTimerTask != null) {
				tetrisTimerTask.cancel();
				tetrisTimerTask = null;
			}
			tetrisTimerTask = new TetrisTimerTask(handler);
			switch(iLevel){
			case 1:
				if (timerBeforeStart != null) {
					timerBeforeStart.purge();
					timerBeforeStart = null;
				}
				if (timer2 != null) {
					timer2.purge();
					timer2 = null;
				}
				else if (timer3 != null) {
					timer3.purge();
					timer3 = null;
				}
				else if (timer4 != null) {
					timer4.purge();
					timer4 = null;
				}
				else if (timer5 != null) {
					timer5.purge();
					timer5 = null;
				}
				else if (timer6 != null) {
					timer6.purge();
					timer6 = null;
				}
				else if (timer7 != null) {
					timer7.purge();
					timer7 = null;
				}
				else if (timer8 != null) {
					timer8.purge();
					timer8 = null;
				}
				else if (timer9 != null) {
					timer9.purge();
					timer9 = null;
				}
				else if (timer10 != null) {
					timer10.purge();
					timer10 = null;
				}
				timer1 = new Timer();
				timer1.schedule(tetrisTimerTask, 0, timerBlockDown);
				break;
			case 2:
				timer1.purge();
				timer1 = null;
				timer2 = new Timer();
				timer2.schedule(tetrisTimerTask, 0, timerBlockDown);
				break;
			case 3:
				timer2.purge();
				timer2 = null;
				timer3 = new Timer();
				timer3.schedule(tetrisTimerTask, 0, timerBlockDown);
				break;
			case 4:
				timer3.purge();
				timer3 = null;
				timer4 = new Timer();
				timer4.schedule(tetrisTimerTask, 0, timerBlockDown);
				break;
			case 5:
				timer4.purge();
				timer4 = null;
				timer5 = new Timer();
				timer5.schedule(tetrisTimerTask, 0, timerBlockDown);
				break;
			case 6:
				timer5.purge();
				timer5 = null;
				timer6 = new Timer();
				timer6.schedule(tetrisTimerTask, 0, timerBlockDown);
				break;
			case 7:
				timer6.purge();
				timer6 = null;
				timer7 = new Timer();
				timer7.schedule(tetrisTimerTask, 0, timerBlockDown);
				break;
			case 8:
				timer7.purge();
				timer7 = null;
				timer8 = new Timer();
				timer8.schedule(tetrisTimerTask, 0, timerBlockDown);
				break;
			case 9:
				timer8.purge();
				timer8 = null;
				timer9 = new Timer();
				timer9.schedule(tetrisTimerTask, 0, timerBlockDown);
				break;
			case 10:
				if (timer9 != null) {
					timer9.purge();
					timer9 = null;
					timer10 = new Timer();
					timer10.schedule(tetrisTimerTask, 0, timerBlockDown);
				}
				break;
			default:
					break;
			}
			tetrisTimerTask.bTimerCond = Boolean.TRUE;
		}
		catch(Exception ex) {
			Log.e("TETRISACTIVITY", "setTimer: " + ex.getMessage());
		}
		
		return;
	}
	
	// �ｽ�ｽ�ｽ�ｽs�ｽﾌ空き単�ｽﾊ撰ｿｽ
	private int getRowEmptyNum(int row)
	{
		int i = 0;
		int num = 0;
		while (i < TetrisConst.X_SIZE) {
			if (TetrisBlockMap[i++][row] == 0) {
				num++;
			}
		}

		return num;
	}
	
	//�ｽﾅ会ｿｽ�ｽﾊの空き位置�ｽ�ｽ�ｽ謫ｾ�ｽ�ｽ�ｽ�ｽB
	private int getLowestPos(int pos)
	{
		int i;
		for ( i = TetrisConst.Y_SIZE - 1; i > 0; i--) {
			if (TetrisBlockMap[pos][i] == 0) {
				break;
			}
		}	

//		TRACE("%d %d\n", pos, i);
		return i;
	}
	
	// �ｽQ�ｽ[�ｽ�ｽ�ｽI�ｽ�ｽ�ｽｻ断
	private Boolean isGameOver()
	{
		for (int i = 0; i < TetrisConst.X_SIZE; i++) {
			if (TetrisBlockMap[i][2] != 0) {
				return Boolean.TRUE;
			}	
		}
		return Boolean.FALSE;
	}
	
	


	//scoreWindow.ok Button
	private OnClickListener okScoreWindow = new OnClickListener() {
		public void onClick(View v) {
			saveScore();
			scoreWindow.dismiss();
		}
	};
	
	//scoreWindow.cancel Button
	private OnClickListener cancelScoreWindow = new OnClickListener() {
		public void onClick(View v) {
			if (scoreNameEditText.getText().toString().equals(TetrisConst.BLANK)){
				return;
			}
			scoreWindow.dismiss();
		}
	};
	
	//Score saving
	private void saveScore() {
		Boolean bInsertFlg = Boolean.FALSE;
		Boolean bUpdateFlg = Boolean.FALSE;
		String name = scoreNameEditText.getText().toString();
		try {
			mysql.open(TetrisConst.DB_NAME);
	
			String sqlSelect = "SELECT * FROM "
				+ TetrisConst.DB_NAME
				+ " WHERE NAME='"
				+ name
				+"';";
			Cursor result = mysql.fetcharray(sqlSelect);
			if (result.getCount() == 0) {
				bInsertFlg = Boolean.TRUE; //no record				
			}
			else {
			    result.moveToFirst(); 
			    int score = 0;
			    while (!result.isAfterLast()) {  
			        score = result.getInt(2); 
			        result.moveToNext(); 
			      } 
			    result.close();
			    if (score < tetrisScore) {
			    	bUpdateFlg = Boolean.TRUE;
			    }
			}
			
			if (bInsertFlg) {
				String sqlInsert = "insert into "
					+ TetrisConst.DB_NAME
					+ "(name,score) values('"
					+ name + "'," + tetrisScore + "')";
				mysql.execsql(sqlInsert);
			}
			else if (bUpdateFlg) {
				String sqlUpdate = "update "
					+ TetrisConst.DB_NAME
					+ " SET SCORE= "
					+ Integer.toString(tetrisScore)
					+ " WHERE NAME='"
					+ name
					+ ";";
				mysql.execsql(sqlUpdate);			
			}
		}
		catch(Exception ex) {
			Log.e("TETRISACTIVITY", "saveScore: " + ex.getMessage());
		}

	}
	
	//Get the state of service ball
	public Boolean getBServiceBall() {
		return bServiceBall;
	}
	
	//Get the state of showBeforeStart
	public Boolean getShowBeforeStart() {
		return bShowBeforeStart;
	}
	//Create table
	private void createTable() {
		try {
		mysql.execsql("CREATE TABLE "
				+ TetrisConst.DB_NAME
				+ "("
				+ "_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "NAME TEXT,"
				+ "SCORE INTEGER,"
				+ "LEVEL INTEGER"
				+ ")"
				);
		} catch (Exception ex) {
			Log.e(TetrisConst.LOGTAB, "createTable" + ex.getMessage());
		}
	}
	
	//GetVersion
	private static String getAppVersionName(Context context) {  
		String versionName = "";  
		try {  
			// ---get the package info---  
			PackageManager pm = context.getPackageManager();  
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);  
			versionName = pi.versionName;  
			if (versionName == null || versionName.length() <= 0) {  
	            return "";  
			}  
		} catch (Exception e) {  
			Log.e("VersionInfo", "Exception", e);  
		}  
		 return versionName;  
		}

    private void show(String value){
        Toast.makeText(this.getContext(), (CharSequence)value, Toast.LENGTH_SHORT).show();
    }
}