package il.ac.huji.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TodoDBAdapter {
	
	private final Context context;
	private TodoDBHelper dbHelper;
	private SQLiteDatabase db;
	
	public TodoDBAdapter(Context ctx){
		context = ctx;
		dbHelper = new TodoDBHelper(context);
	}
	
	private static class TodoDBHelper extends SQLiteOpenHelper
	{

		public TodoDBHelper(Context context) {
			super(context, TodoConstants.DATABASE_NAME, null, TodoConstants.DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			try{
				db.execSQL(TodoConstants.NEW_DB);
			} catch(SQLException e){
				e.printStackTrace();
			}
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IS EXISTS " + TodoConstants.DATABASE_TABLE);
			onCreate(db);
		}
		
	}
	
	public TodoDBAdapter open() throws SQLException{
		db = dbHelper.getWritableDatabase();
		return this;
	}
	
	public void close(){
		dbHelper.close();
	}
	
	public long insertRecord(String title, long due, int done){
		ContentValues initValues = new ContentValues();
		initValues.put(TodoConstants.COLUMN_TITLE, title);
		initValues.put(TodoConstants.COLUMN_DUE, due);
		initValues.put(TodoConstants.COLUMN_DONE, done);
		return db.insert(TodoConstants.DATABASE_TABLE, null, initValues);
	}
	
	public boolean deleteRecord(long rowId){
		return db.delete(TodoConstants.DATABASE_TABLE, TodoConstants.COLUMN_ID + "=" + rowId, null) > 0;
	}
	
	public Cursor getAllRecords(){
		return db.query(TodoConstants.DATABASE_TABLE, new String[] {TodoConstants.COLUMN_ID, TodoConstants.COLUMN_TITLE, TodoConstants.COLUMN_DUE, TodoConstants.COLUMN_DONE}, null, null, null, null, null);
	}
	
	public Cursor getRecord(long rowID) throws SQLException{
		Cursor curs = db.query(true, TodoConstants.DATABASE_TABLE, new String[] {TodoConstants.COLUMN_ID,  TodoConstants.COLUMN_TITLE, TodoConstants.COLUMN_DUE, TodoConstants.COLUMN_DONE}, TodoConstants.COLUMN_ID + "=" + rowID, null, null, null, null, null);
		if(curs != null){
			curs.moveToFirst();
		}
		return curs;
	}
	
	public boolean updateRecord(long rowID, String title, long dueDate, int done){
		ContentValues newValues = new ContentValues();
		newValues.put(TodoConstants.COLUMN_TITLE, title);
		newValues.put(TodoConstants.COLUMN_DUE, dueDate);
		newValues.put(TodoConstants.COLUMN_DONE, done);
		return db.update(TodoConstants.DATABASE_TABLE, newValues, TodoConstants.COLUMN_ID + "=" + rowID, null) > 0;
	}
}
