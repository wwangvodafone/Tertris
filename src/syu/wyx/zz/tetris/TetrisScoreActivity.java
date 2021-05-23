package syu.wyx.zz.tetris;

import com.google.ads.*;
//import com.waps.AdView;
//import cn.domob.android.ads.DomobAdListener;
//import cn.domob.android.ads.DomobAdView;
//import net.youmi.android.AdManager;
//import net.youmi.android.AdView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import net.youmi.android.AdManager;
//import net.youmi.android.AdView;
import syu.wyx.zz.tetris.R;
import syu.wyx.zz.tetris.param.MySql;
import syu.wyx.zz.tetris.param.TetrisConst;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class TetrisScoreActivity extends Activity  {
    private ImageButton bnOk;
    private ImageButton bnCancel;
    private TextView scoreText;
    private TextView levelText;
	private TextView titleText;
    private ListView scoreListText;
    private EditText scoreNameEditText;
    private int tetrisScore;
    private int gameLevel;
    //Database
    private MySql mysql;
    private Cursor result;  
    
	private SimpleAdapter adapter ; 
    List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(new Panel(this));
        setContentView(R.layout.score_layout);
		findViews();
		setTitleText();
        Intent intent = getIntent(); 
        String sScore = intent.getStringExtra("score"); 
        tetrisScore = Integer.parseInt(sScore);       
        scoreText.setText(sScore);
        String sLevel = intent.getStringExtra("level");
        levelText.setText(sLevel);
        gameLevel = Integer.parseInt(sLevel);
		setListeners();	
		getScore();
		scoreListText.setAdapter(adapter);
        if (tetrisScore == 0 && gameLevel == 0) {
        	scoreNameEditText.setEnabled(Boolean.FALSE);
        	bnOk.setEnabled(Boolean.FALSE);
        }
		/*waps
        LinearLayout container =(LinearLayout)findViewById(R.id.AdLinearLayout1);
        new AdView(this,container).DisplayAd();
        */
		
        AdView adView = (AdView)this.findViewById(R.id.adView2);    
        adView.loadAd(new AdRequest());
        
		/*domob
    	RelativeLayout mAdContainer;
    	DomobAdView mAdview320x50;

		mAdContainer = (RelativeLayout) findViewById(R.id.adcontainer1);
		//创建一个320x50的广告View
		mAdview320x50 = new DomobAdView(this, "56OJz5IYuMqVAA4RJk", DomobAdView.INLINE_SIZE_320X50);
//		mAdview320x50 = new DomobAdView(this, "56OJyM1ouMGoaSnvCK", DomobAdView.INLINE_SIZE_320X50);
		mAdview320x50.setKeyword("game");
		
		//设置广告view的监听器。
		mAdview320x50.setOnAdListener(new DomobAdListener() {
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
		//将广告View增加到视图中。
		mAdContainer.addView(mAdview320x50);
		*/
		// youmi
        /*
        AdManager.init(this,"6634ecf73659ba21", "da1f1298a7934a8a", 30, false  );
        LinearLayout adViewLayout = (LinearLayout) findViewById(R.id.adViewLayout1); 
        adViewLayout.addView(new AdView(this), 
        		new LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 
        				LinearLayout.LayoutParams.WRAP_CONTENT));
        
        */
   }
    
	//�r���[��ݒ�
	private void findViews() {
    	bnOk = (ImageButton)findViewById(R.id.confirm);
    	bnCancel = (ImageButton)findViewById(R.id.cancel);    	
    	scoreText = (TextView)findViewById(R.id.score);
    	levelText = (TextView)findViewById(R.id.level);
    	titleText = (TextView)findViewById(R.id.load_title);
    	scoreNameEditText = (EditText)findViewById(R.id.name);
    	scoreListText = (ListView)findViewById(R.id.scoreListLable);
	}
	
	//Listener��ݒ�
	private void setListeners() {
		bnOk.setOnClickListener(okScoreWindow);
		bnCancel.setOnClickListener(cancelScoreWindow);
	}
	//scoreWindow.ok Button
	private OnClickListener okScoreWindow = new OnClickListener() {
		public void onClick(View v) {
			saveScore();
	        Intent firstIntent = new Intent(TetrisScoreActivity.this, TetrisActivity.class);
	        //firstIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
	        Bundle bundle = new Bundle();	        
	        bundle.putString("score", "ok");	         
	        firstIntent.putExtras(bundle);
	        setResult(1, firstIntent); 
	        //startActivity(firstIntent);
	        finish();
		}
	};
	
	//scoreWindow.cancel Button
	private OnClickListener cancelScoreWindow = new OnClickListener() {
		public void onClick(View v) {
	        Intent firstIntent = new Intent(TetrisScoreActivity.this, TetrisActivity.class);
//	        firstIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
	        Bundle bundle = new Bundle();	        
	        bundle.putString("score", "cancel");	         
	        firstIntent.putExtras(bundle);
	        setResult(1, firstIntent); 
	        //startActivity(firstIntent);
	        finish();
		}
	};	
	
	//Get scores
	private String getScore() {
		int count = 0;
		String sResult = "\r\n            SCORE LIST\r\n\r\n";
		try {
			mysql = new MySql(this);
			mysql.open(TetrisConst.DB_NAME);
			String sqlSelect = "SELECT _ID as _id, NAME, SCORE, LEVEL  FROM "
				+ TetrisConst.DB_NAME
				+ " ORDER BY SCORE DESC;";
	
			result = mysql.fetcharray(sqlSelect);
		    result.moveToFirst(); 
		    int score = 0;
		    int level = 0;
		    String name = null;
		    while (!result.isAfterLast()) {  
		    	name = result.getString(1);
		        score = result.getInt(2); 
		        level = result.getInt(3);
		        Map<String, Object> map = new HashMap<String, Object>();
				map.put("NAME", name);
				map.put("SCORE", Integer.toString(score));
				map.put("LEVEL", Integer.toString(level));

				list.add(map);
		        sResult += "    " + name + ":    " + Integer.toString(score) + "\r\n";
		        result.moveToNext(); 
		        if (count++ > 6) break;
		      } 
			adapter = new SimpleAdapter(this,
					(List<Map<String, Object>>) list, R.layout.listitem,
					new String[] { "NAME", "SCORE", "LEVEL" }, new int[] {
							R.id.listitem_name, R.id.listitem_score,
							R.id.listitem_level });
		    result.close();
			mysql.close();
		} 
		catch (Exception ex) {
			Log.e(TetrisConst.LOGTAB, ex.getMessage());
		}
		
		return sResult;
				
	}
	
	//Score saving
	private void saveScore() {
		Boolean bInsertFlg = Boolean.FALSE;
		Boolean bUpdateFlg = Boolean.FALSE;
		String name = scoreNameEditText.getText().toString();
		Log.i(TetrisConst.LOGTAB, name);
		if (name.equals(TetrisConst.BLANK )) {
			return;
		}
		
		try {
			mysql = new MySql(this);
			mysql.open(TetrisConst.DB_NAME);
			if (!mysql.isTableExist(TetrisConst.DB_NAME)) {
				mysql.execsql("CREATE TABLE "
						+ TetrisConst.DB_NAME
						+ "("
						+ "_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
						+ "NAME TEXT,"
						+ "SCORE INTEGER,"
						+ "LEVEL INTEGER"
						+ ")"
						);
			}
	
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
				Log.i(TetrisConst.LOGTAB, Integer.toString(score));
				Log.i(TetrisConst.LOGTAB, Integer.toString(tetrisScore));

			    if (score < tetrisScore) {
			    	bUpdateFlg = Boolean.TRUE;
			    }
			}
			if (bInsertFlg) {
				String sqlInsert = "insert into "
					+ TetrisConst.DB_NAME
					+ "(NAME,SCORE,LEVEL) values('"
					+ name + "'," + tetrisScore + "," + gameLevel + ")";
				mysql.execsql(sqlInsert);
			}
			else if (bUpdateFlg) {
				String sqlUpdate = "update "
					+ TetrisConst.DB_NAME
					+ " SET SCORE= "
					+ Integer.toString(tetrisScore)
					+ ", LEVEL= "
					+ Integer.toString(gameLevel)
					+ " WHERE NAME='"
					+ name
					+ "';";
				mysql.execsql(sqlUpdate);			
			}
			mysql.close();
		} 
		catch (Exception ex) {
			Log.e(TetrisConst.LOGTAB, ex.getMessage());
		}
	}	
	//Title
	private void setTitleText() {
		titleText.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD); 
		titleText.setTypeface(Typeface.DEFAULT, Typeface.ITALIC);
		titleText.setText("SCORE LIST");
	}
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {  
		@Override  
		public void onReceive(Context context, Intent intent) {  
			finish();  
		}  
	};  

}
