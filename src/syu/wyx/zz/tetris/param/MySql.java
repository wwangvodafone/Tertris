package syu.wyx.zz.tetris.param;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;

public class MySql {

	private Context mcontext;

	public Datahelper mdatahelper = null;
	public SQLiteDatabase msqlitedatabase;
	private static String dbname = "";

	public static class Datahelper extends SQLiteOpenHelper {
		// Constructor

		public Datahelper(Context context) {

			super(context, dbname, null, 1);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		}
	}

	public MySql(Context context) {
		mcontext = context;
	}

	// Open Database
	public void open(String db) throws SQLException {
		dbname = db;
		mdatahelper = new Datahelper(mcontext);
		msqlitedatabase = mdatahelper.getWritableDatabase();

	}

	// Get Data
	public Cursor fetcharray(String sqlstr) {
		Cursor cur = msqlitedatabase.rawQuery(sqlstr, null);
		return cur;
	}

	// Get Record Count
	public Integer getcount(String sqlstr) {
		Cursor cur = msqlitedatabase.rawQuery(sqlstr, null);
		Integer count = cur.getCount();
		cur.close();
		return count;
	}

	// Exec Sql
	public void execsql(String sqlstr) {
		msqlitedatabase.execSQL(sqlstr);

	}

	// Delete DB
	public void dropdatabase() {
		close();
		mcontext.deleteDatabase(dbname);
	}

	// Whether the DB exit or not
	public boolean checkdbexsit() {
		return false;
	}

	// Close DB
	public void close() {
		msqlitedatabase.close();
		mdatahelper.close();
	}
 /** 
  * Whether the TABLE is exist or not
  * @param sTableName 
  * @return 
  */  
  public boolean isTableExist(String sTableName){  
	  
	  Boolean result = Boolean.FALSE ;  
	  if(sTableName == null) {  
		  return result ;  
	  }  
	  Cursor cursor = null;  
	  try {  
		  String sql = "select count(*) as c from Sqlite_master " +
		  				" where type ='table' and name ='"+
		  				sTableName.trim()+"' ";  
		  cursor = msqlitedatabase.rawQuery(sql, null);  
		  if(cursor.moveToNext()) {  
			  int count = cursor.getInt(0);  
			  if(count > 0) {  
				  result = Boolean.TRUE;  
			  }  
		  }  		          
	  } catch (Exception e) {  
		        // TODO: handle exception  
	  }     
	  
	  return result;  
  }  

}
