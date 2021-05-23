package syu.wyx.zz.tetris;

import java.lang.reflect.Method;

import com.google.ads.*;
//import com.waps.AdView;
//import com.waps.AppConnect;
//import cn.domob.android.ads.DomobAdListener;
//import cn.domob.android.ads.DomobAdView;
//import net.youmi.android.AdManager;
//import net.youmi.android.AdView;
//import net.youmi.android.appoffers.YoumiOffersManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.WindowManager.LayoutParams;  
import syu.wyx.zz.tetris.R;
import syu.wyx.zz.tetris.TetrisView;
import syu.wyx.zz.tetris.param.TetrisConst;

@SuppressLint("ParserError")
public class TetrisActivity extends Activity {
	private ImageButton bnStartButton;
	private ImageButton bnStopButton;
	private ImageButton bnSound;
	private ImageButton bnQuestion;
	private ImageButton bnExit;
	private ImageButton bnLoad;
	private RadioGroup rgBlock;
	private RadioButton bnBlockRadio;
	private RadioButton bnHeartRadio;
	private RadioButton bnChocalateRadio;
	private RadioButton bnFruitRadio;
	private RadioButton bnSweetRadio;
	private TextView scoreText;
	private TextView levelText;
	private TextView titleText;
	private TetrisView tetrisView;
	private int iPause = 0; //0:start 1:pause 2:restart
	private AudioManager audioManager;
	private GestureDetector gestureDetector;
    private Screen screen; 
    //whether start or not
    private Boolean bStart = Boolean.FALSE;
    //sound
    private Boolean bSound = Boolean.TRUE;
    //popup window
    private PopupWindow helpWindow;
    private TextView helpText;

    private Boolean bExitFlag = Boolean.TRUE;
    
    // about screen
    private float mScreenDensity;
    private double mDiagonalPixels;
    private double mScreenSize;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(new Panel(this));
        setContentView(R.layout.relative_layout);
        
        // get size
        getRealSize();
        Log.i(TetrisConst.LOGTAB, "size=" + mScreenSize);
        
		findViews();
		setListeners();
		setSoundOn();
		setHelpWindow();
		tetrisView = (TetrisView)findViewById(R.id.tetris);
		setScoreText();
		setLevelText();
		setTitleText();
		tetrisView.setFocusable(true);
		audioManager = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
		tetrisView.setAudioManager(audioManager);	
        gestureDetector = new GestureDetector(this,onGestureListener);
        //Get screen size
        screen = GestureUtils.getScreenPix(this);
        helpWindow = createHelpWindow();
      
        /* waps
        AppConnect.getInstance(this); 
        LinearLayout container =(LinearLayout)findViewById(R.id.AdLinearLayout);
        new AdView(this,container).DisplayAd();
		*/
       	
        /* admob */
        AdView adView = (AdView)this.findViewById(R.id.adView);    
        adView.loadAd(new AdRequest());
		
        /*domob
    	RelativeLayout mAdContainer;
    	DomobAdView mAdview320x50;

		mAdContainer = (RelativeLayout) findViewById(R.id.adcontainer);
		//蛻帛ｻｺ荳�ｸｪ320x50逧�ｹｿ蜻碍iew
//		mAdview320x50 = new DomobAdView(this, "56OJz5IYuMqVAA4RJk", DomobAdView.INLINE_SIZE_320X50);
		mAdview320x50 = new DomobAdView(this, "56OJyM1ouMGoaSnvCK", DomobAdView.INLINE_SIZE_320X50);
		mAdview320x50.setKeyword("game");
		
		//隶ｾ鄂ｮ蟷ｿ蜻革iew逧�尅蜷ｬ蝎ｨ縲�		mAdview320x50.setOnAdListener(new DomobAdListener() {
			@Override
			public void onReceivedFreshAd(DomobAdView adview) {
				Log.i("DomobSDKDemo", "onReceivedFreshAd");
			}
			@Override
			public void onFailedToReceiveFreshAd(DomobAdView adview) {
				Log.i("DomobSDKDemo", "onFailedToReceiveFreshAd");
			}
			@Override
			public void onLandingPageOpening() {
				Log.i("DomobSDKDemo", "onLandingPageOpening");
			}
			@Override
			public void onLandingPageClose() {
				Log.i("DomobSDKDemo", "onLandingPageClose");
			}
		});
		//蟆�ｹｿ蜻碍iew蠅槫刈蛻ｰ隗�崟荳ｭ縲�		mAdContainer.addView(mAdview320x50);
		*/
        
        //youmi ad bar
        /*
        AdManager.init(this,"6634ecf73659ba21", "da1f1298a7934a8a", 30, false);
        LinearLayout adViewLayout = (LinearLayout) findViewById(R.id.adViewLayout); 
        adViewLayout.addView(new AdView(this), 
        		new LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 
        				LinearLayout.LayoutParams.WRAP_CONTENT));
		*/
        
	}

    
    @SuppressLint("ParserError")
	@Override
    public void onPause() {
    	super.onPause();
    	tetrisView.stopBkSound();
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	tetrisView.resumeBkSound();
    }
    
    @Override
    public void onStop() {
    	super.onStop();
    	if (bExitFlag){
    		Log.i("TETRIS", "Stop");
    		//exitProg();
    	}
    }
    
    @SuppressLint({ "ParserError", "ParserError" })
	@Override
    public void onDestroy() {
    	super.onDestroy();
    	if (bExitFlag) {
    		Log.i("TETRIS", "Destroy");
    		exitProg();
    	}
    }
	//Find views
    private void findViews() {
    	bnStartButton = (ImageButton)findViewById(R.id.start);
    	bnStopButton = (ImageButton)findViewById(R.id.stop);
    	bnSound = (ImageButton)findViewById(R.id.soundoff);
    	bnQuestion = (ImageButton)findViewById(R.id.question);
    	bnExit = (ImageButton)findViewById(R.id.exit);
    	bnLoad = (ImageButton)findViewById(R.id.load);
    	rgBlock = (RadioGroup)findViewById(R.id.menu);
    	bnBlockRadio = (RadioButton)findViewById(R.id.block);
    	bnHeartRadio = (RadioButton)findViewById(R.id.heart);
    	bnChocalateRadio = (RadioButton)findViewById(R.id.chocalate);
    	bnFruitRadio = (RadioButton)findViewById(R.id.fruit);
    	bnSweetRadio = (RadioButton)findViewById(R.id.sweet);
    	bnStartButton.setEnabled(Boolean.TRUE);
    	bnStopButton.setEnabled(Boolean.FALSE);
    	scoreText = (TextView)findViewById(R.id.current_score);
    	levelText = (TextView)findViewById(R.id.current_level);
    	titleText = (TextView)findViewById(R.id.title);
    	//Disable the stop and sound button
    	bnStopButton.setEnabled(Boolean.FALSE);
    	//bnSound.setEnabled(Boolean.FALSE);
    	
    	rgBlock.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// TODO Auto-generated method stub
			if (checkedId == bnBlockRadio.getId()) {
				tetrisView.setBlockCategory(0);
			}
			else if (checkedId == bnHeartRadio.getId()) {
				tetrisView.setBlockCategory(1);
			}
			else if (checkedId == bnChocalateRadio.getId()) {
				tetrisView.setBlockCategory(2);
			}
			else if (checkedId == bnFruitRadio.getId()) {
				tetrisView.setBlockCategory(3);
			}
			else if (checkedId == bnSweetRadio.getId()) {
				tetrisView.setBlockCategory(4);
			}
		}
	});
	}
	
	//Listeners	
    private void setListeners() {
		bnStartButton.setOnClickListener(startTetris);
		bnStopButton.setOnClickListener(stopTetris);
		bnExit.setOnClickListener(exitTetris);
		bnLoad.setOnClickListener(loadTetrisScore);
	}
	
	//Sound setting
	private void setSoundOn() {
		bnSound.setOnClickListener(soundTetris);
	}
	
	//Help window
	private void setHelpWindow() {
		bnQuestion.setOnClickListener(helpTetris);
	}
	
	//score text setting	
	private void setScoreText() {
		tetrisView.setScoreText(scoreText);
	}

	//level text setting
	private void setLevelText() {
		tetrisView.setLevelText(levelText);
	}

	//title text setting	
	private void setTitleText() {
		titleText.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD); 
		titleText.setTypeface(Typeface.DEFAULT, Typeface.ITALIC);
		titleText.setText("TETRIS");
		
	}
	//Start click
	private OnClickListener startTetris = new OnClickListener() {
		public void onClick(View v) {
			if (iPause == 0) { //start->pause
				bnStopButton.setEnabled(Boolean.TRUE);
				//bnSound.setEnabled(Boolean.TRUE);
				((ImageButton)v).setImageDrawable(getResources()
						.getDrawable(R.drawable.pause_button_green));                              
				tetrisView.onTetrisStart();
				bStart = Boolean.TRUE;
				iPause = 1;
				tetrisView.playBkSound();
			}
			else if (iPause == 1) { //pause->restart
				((ImageButton)v).setImageDrawable(getResources()
						.getDrawable(R.drawable.restart_button)); 	
				tetrisView.onTetrisPause();
				bStart = Boolean.FALSE;
				iPause = 2;
			}
			else if (iPause == 2) { //restart->pause
				((ImageButton)v).setImageDrawable(getResources()
						.getDrawable(R.drawable.pause_button_green)); 
				tetrisView.onTetrisRestart();
				bStart = Boolean.TRUE;
				iPause = 1;
				tetrisView.resumeBkSound();
			}
			bnStopButton.setEnabled(Boolean.TRUE);
		}
	};

	//Stop button
	private OnClickListener stopTetris = new OnClickListener() {
		public void onClick(View v) {
			bnStartButton.setImageDrawable(getResources()
					.getDrawable(R.drawable.start_button)); 
			bnStartButton.setEnabled(Boolean.TRUE);
			bnStopButton.setEnabled(Boolean.FALSE);
			tetrisView.onTetrisStop();
			bStart = Boolean.FALSE;
			iPause = 0;
			tetrisView.stopBkSound();
		}
	};
	
	//SoundOn Button
	private OnClickListener soundTetris = new OnClickListener() {
		public void onClick(View v) {
			if (bSound) { //sound on->sound off, image is changed to sound on
				((ImageButton)v).setImageDrawable(getResources()
						.getDrawable(R.drawable.soundon));  
				bSound = Boolean.FALSE;
				tetrisView.setSoundOn(bSound);
				tetrisView.pauseBkSound();
			}
			else { //sound off->on, image is changed to sound off
				((ImageButton)v).setImageDrawable(getResources()
						.getDrawable(R.drawable.soundoff)); 
				bSound = Boolean.TRUE;
				tetrisView.setSoundOn(bSound);	
				tetrisView.resumeBkSound();
			}
		}
	};
	
	//Question Button
	private OnClickListener helpTetris = new OnClickListener() {
		public void onClick(View v) {
			if(helpWindow.isShowing()) {  
				helpWindow.dismiss();  
			} 
			else {  
				helpText.setText(getHelpText());  
				helpWindow.showAsDropDown(helpText,100,200);  
			}		
		}
	};	
	
	//Help window creating
	public PopupWindow createHelpWindow() {
		LinearLayout tv = (LinearLayout) getLayoutInflater().inflate(  
							R.layout.helpwindow, null);  
		helpText = (TextView)tv.findViewById(R.id.help); 
		PopupWindow popupWindow = new PopupWindow(this);  
		popupWindow.setWidth(500);
		popupWindow.setHeight(180);
		popupWindow.setWindowLayoutMode(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);  
		popupWindow.setContentView(tv);  

		return popupWindow;  
	}
	//Exit button
	private OnClickListener exitTetris = new OnClickListener() {
		public void onClick(View v) {
            AlertDialog.Builder dialog=new AlertDialog.Builder(TetrisActivity.this);
            dialog.setTitle("TETRIS").setIcon(android.R.drawable.ic_dialog_info)
              	.setMessage("Exit the Tetris?")
              	.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				   @Override
				   public void onClick(DialogInterface dialog, int which) {
						exitProg();
				   }
              	}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
              		public void onClick(DialogInterface dialog, int which) {
              			dialog.cancel();
              		}	           
              	}).create().show();
		}
	};
	//Load Button 
	public OnClickListener loadTetrisScore = new OnClickListener() {
		public void onClick(View v) {	
			createScoreWindow(0, 0);
		}
	};
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }
    
    //move finger
	GestureDetector.OnGestureListener onGestureListener = new GestureDetector.SimpleOnGestureListener(){
		 
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                float velocityY) {
        	if (!bStart){
        		return true;
        	}
        	
            float x = e2.getX() - e1.getX();
            float y = e2.getY() - e1.getY();
            //Must > 1/4 Screen size
            float x_limit = screen.widthPixels / 5;
            float y_limit = screen.heightPixels / 5;
            float x_abs = Math.abs(x);
            float y_abs = Math.abs(y);
            if(x_abs >= y_abs){
                //gesture left or right
                if(x > x_limit || x < -x_limit){
                    if(x>0){
                        //right
                        tetrisView.moveBlockRight();
                    }else if(x<0){
                        //left
                        tetrisView.moveBlockLeft();
                    }
                }
            }else{
                //gesture down or up
                if(y > y_limit || y < -y_limit){
                	if (tetrisView.getBServiceBall()) {
                		return true;
                	}
                    if(y>0){
                        //down
                        tetrisView.moveBlockDown();
                    }else if(y<0){
                        //up;
                        tetrisView.rotateBlock();
                    }
                }
            }
            return true;
        }
        
        //Click screen to rotate the block
        @Override
        public boolean onSingleTapUp(MotionEvent event) {
        	if (tetrisView.getShowBeforeStart()) {
				bnStopButton.setEnabled(Boolean.TRUE);
				//bnSound.setEnabled(Boolean.TRUE);
				((ImageButton)bnStartButton).setImageDrawable(getResources()
						.getDrawable(R.drawable.pause_button_green));                              
				tetrisView.onTetrisStart();
				bStart = Boolean.TRUE;
				iPause = 1;
				tetrisView.playBkSound();
        	}
        	if (!bStart || tetrisView.getBServiceBall()){
        		return true;
        	}
        	tetrisView.rotateBlock();
        	return true;
        }
        /**
         * Long press        */
        @Override
        public void onLongPress(MotionEvent e) {
        	if (!bStart || tetrisView.getBServiceBall()){
        		return;
        	}
        	while (!tetrisView.moveBlockDown()) {
        		try {
        			Thread.sleep(50);
        		} catch (InterruptedException e1) {
        			// TODO Auto-generated catch block
        			return;
        		}
        	}
            return;
        }


    };
    
    //Help window texts
    private String getHelpText() {
    	String sText = null;
    	sText = "\r\n" +
    	
    			"SWIPE DOWN:  Down\r\n" +
    			"SWIPE UP:         Rotate\r\n" +
    			"SINGLE CLICK:  Rotate\r\n" +
    			"SWIPE LEFT:      Left\r\n" +
    			"SWIPE RIGHT:   Right\r\n" +
    			"LONG CLICK:    Down to the bottom";

    	
    	/*
				"蜷台ｸ区ｻ大勘: 蜷台ｸ欺r\n" +
				"蜷台ｸ頑ｻ大勘: 鄙ｻ霓ｬ\r\n" +
				"蜊募�螻丞ｹ� 鄙ｻ霓ｬ\r\n" +	
				"蜷大ｷｦ貊大勘: 蜷大ｷｦ\r\n" +	
				"蜷大承貊大勘: 蜷大承\r\n" +	
				"髟ｿ謖牙ｱ丞ｹ� 逶ｴ謗･蛻ｰ蠎表r\n";
		*/
    	
    	return sText;
    }
	//Score window creating
	public void createScoreWindow(int tetrisScore, int gameLevel) {
		bExitFlag = Boolean.FALSE;
        Intent scoreIntent = new Intent(this, syu.wyx.zz.tetris.TetrisScoreActivity.class);
        scoreIntent.putExtra("score", Integer.toString(tetrisScore)); 
        scoreIntent.putExtra("level", Integer.toString(gameLevel)); 
        startActivityForResult(scoreIntent, 1);
	}

	public void loadTetrisScore(int tetrisScore, int gameLevel) {
		bExitFlag = Boolean.FALSE;
        Intent scoreIntent = new Intent(this, syu.wyx.zz.tetris.TetrisScoreActivity.class);
        scoreIntent.putExtra("score", Integer.toString(tetrisScore)); 
        scoreIntent.putExtra("level", Integer.toString(gameLevel)); 
        startActivityForResult(scoreIntent, 1);
	}
	
	//Do with the return value
	protected void onActivityResult(int requestCode, int resultCode,  
			 Intent data){ 
        try {      
        String sValue = data.getStringExtra("score"); 

        if (sValue.equals("ok")) {
        	// youmi
        	/*
            YoumiOffersManager.init(this, "6634ecf73659ba21", "da1f1298a7934a8a");
            YoumiOffersManager.showOffers(TetrisActivity.this,
    				YoumiOffersManager.TYPE_REWARDLESS_APPLIST);
    		*/
        	//waps
        	//AppConnect.getInstance(this).showOffers(this);
        	
        }
        } catch (Exception ex) {
        	Log.e(TetrisConst.LOGTAB, "onActivityResult " + ex.getMessage());
        }
		bExitFlag = Boolean.TRUE;
		Log.i("TETRIS", "onActivityResult");
	}

	//Exit program
	private void exitProg() {
        
		Intent intent=new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		 
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		this.startActivity(intent);
		//AppConnect.getInstance(this).finalize(); //for waps
		System.exit(0);
	}
	
    private void show(String value){
        Toast.makeText(this, (CharSequence)value, Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("NewApi")
    private void getRealSize() {     
    	DisplayMetrics dm = new DisplayMetrics();
    	getWindowManager().getDefaultDisplay().getMetrics(dm);
    	TetrisConst.iScreenHeight = dm.heightPixels;
    	TetrisConst.iScreenWidth = dm.widthPixels;
    	mScreenDensity = dm.density;
    	mDiagonalPixels = Math.sqrt(Math.pow(dm.widthPixels, 2) + 
    			Math.pow(dm.heightPixels, 2));
    	mScreenSize = mDiagonalPixels / (160 * dm.density);
    	TetrisConst.changeSize(mScreenSize);
    }
}
