package il.ac.huji.todolist;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class TodoListManagerActivity extends Activity {
	private ListView list;
	private TodoCursorAdapter ad;
	private TodoDBAdapter db;
	private String selectedTaskName;
	private Long selectedTaskDue;
	private ParseACL aclObj;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todo_list_manager);
		Parse.initialize(this, "iPbCD2Q2ZPb7GZCC1sXz5Y91LQkouFdVIgtlOWSP", "FQvWBYu5b8E6WCep7Wj0UnBMhYm2P557bMwVxz2k");
//		String android_id = Secure.getString(getBaseContext().getContentResolver(), Secure.ANDROID_ID);
//		Log.w("--", android_id);
		
//		us.setUsername(android_id);
//		aclObj = new ParseACL(us);
//		Log.w("--",aclObj.toString());
//				this.deleteDatabase(TodoConstants.DATABASE_NAME);
		db = new TodoDBAdapter(this);
		db.open();

		list = (ListView) findViewById(R.id.lstTodoItems);
		ad = new TodoCursorAdapter(getApplicationContext(), getAllDBRecords(), true, db);
		list.setAdapter(ad);
		registerForContextMenu(list);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//Inflate the menu. this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.todo_list_manager_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//What happens when we click the menu.
		super.onOptionsItemSelected(item);
		if(item.getItemId() == R.id.menuItemAdd){
			Intent intent = new Intent(this, AddNewTodoItemActivity.class); 
			startActivityForResult(intent, TodoConstants.BROWSER_ACTIVATION_REQUEST);
		}
		if(item.getItemId() == R.id.menuAbout){
			Toast.makeText(getApplicationContext(), "Made by Andrey Dobrikov", Toast.LENGTH_SHORT).show();
		}
		if(item.getItemId() == R.id.exitMenu){
			db.close();
			finish();
		}
		return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		Long id = info.id;
		Cursor curs = db.getRecord(id);
		selectedTaskName = curs.getString(curs.getColumnIndex(TodoConstants.COLUMN_TITLE));
		selectedTaskDue = curs.getLong(curs.getColumnIndex(TodoConstants.COLUMN_DUE));
		menu.setHeaderTitle(selectedTaskName); //set title for delete menu.
		MenuInflater inflater = getMenuInflater();
		if(!firstWordCall(selectedTaskName).equals("-1")){
			inflater.inflate(R.menu.context_delete_call, menu);
			MenuItem mi =(MenuItem) menu.findItem(R.id.menuItemCall);
			mi.setTitle(selectedTaskName);
		}
		else{
			inflater.inflate(R.menu.context_delete, menu);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		if(item.getItemId() == R.id.menuItemDelete){
			removeTodoTask(info.id);
			removeOnlineTodoTask(selectedTaskName, selectedTaskDue);
			Toast.makeText(getApplicationContext(), "Item: [" + selectedTaskName + "] was successfully deleted!", Toast.LENGTH_SHORT).show();
		}
		if(item.getItemId() == R.id.menuItemCall){
			Intent callIntent = new Intent(Intent.ACTION_CALL);
			String callNumber = firstWordCall(selectedTaskName);
			Log.w("CALL NUM:", callNumber);
			callIntent.setData(Uri.parse("tel:" + callNumber));
			startActivity(callIntent);
		}
		return super.onContextItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent input) {
		// if the results is coming from BROWSER_ACTIVATION_REQUEST 
		String newTask;

		if (requestCode == TodoConstants.BROWSER_ACTIVATION_REQUEST) {

			// check the result code set by the activity
			if (resultCode == RESULT_OK) {
				if(input.hasExtra("Task") && input.getExtras().getString("Task") != null){
					newTask = input.getExtras().getString("Task");
					int day = input.getExtras().getInt("Day");
					int month = input.getExtras().getInt("Month");
					int year = input.getExtras().getInt("Year");
					String date1 = day + "-" + month + "-" + year;
					SimpleDateFormat f = new SimpleDateFormat("dd-mm-yyyy", Locale.getDefault());
					Date d = new Date();
					try {
						d = f.parse(date1);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					long dateInMilliseconds = d.getTime();

					addOnlineTodoTask(newTask, dateInMilliseconds);
					addTodoTask(newTask, dateInMilliseconds);
					ad.notifyDataSetChanged();
				}
			}
		}
	}

	//==============================================TOOLS FUNCTIONS===========================================
	private String firstWordCall(String title) {
		String[] split = title.split(" ",2);
		String firstWord = split[0].toLowerCase();
		if(firstWord.equals("call")){
			String rest = split[1];
			rest = rest.replaceAll("\\s","");
			if(rest.startsWith("-")) return "-1";
			rest = rest.replaceAll("-", "");
			if(rest.matches("^[0-9]+")){
				return rest;
			}
		}
		return "-1";
	}

	//==============================================DATABASE FUNCTIONS===========================================	
	private long addTodoTask(String task, long due){
		//		db.open();
		Log.w("=============",task);
		long id = db.insertRecord(task, due, 0);
		ad.changeCursor(getAllDBRecords());
		if(id < 0){
			Log.e(TodoConstants.DBERROR, "Could not add new task to the database");
			//			db.close();
			return -1;
		}
		else{
			//			db.close();
			return id;
		}
	}

	private void removeTodoTask(long rowId){
		//		db.open();
		boolean isDeleted = db.deleteRecord(rowId);
		if(!isDeleted) Log.e(TodoConstants.DBERROR, "Could not remove task from the database");
		ad.changeCursor(getAllDBRecords());
		//		db.close();
	}

	private Cursor getAllDBRecords(){
		//		db.open();
		Cursor curs = db.getAllRecords();
		//		db.close();
		return curs;
	}

	//======================================ONLINE DATABASE FUNCTIONS===========================================

	private void addOnlineTodoTask(String task, long due){
		final ParseObject onlineDBObject = new ParseObject(TodoConstants.ONLINE_DATABASE_NAME);
		onlineDBObject.put(TodoConstants.ONLINE_DB_COLUMN_TITLE, task);
		Date d = new Date(due);  
		SimpleDateFormat f = new SimpleDateFormat("dd-mm-yyyy", Locale.getDefault());
		String s = f.format(d);
//		onlineDBObject.setACL(aclObj);
		onlineDBObject.put(TodoConstants.ONLINE_DB_COLUMN_DUE, s);
		onlineDBObject.saveInBackground();
	}

	private void removeOnlineTodoTask(String title, final Long due){
		ParseQuery<ParseObject> query = ParseQuery.getQuery(TodoConstants.ONLINE_DATABASE_NAME);
		query.whereEqualTo(TodoConstants.COLUMN_TITLE, title);
		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> resultList, com.parse.ParseException findException) {
				Iterator<ParseObject> iter = resultList.iterator();
				while(iter.hasNext()){
					ParseObject toDelete = iter.next();
					String dueToTest = toDelete.getString(TodoConstants.ONLINE_DB_COLUMN_DUE);
					SimpleDateFormat f = new SimpleDateFormat("dd-mm-yyyy", Locale.getDefault());
					Date d = new Date();
					try {
						d = f.parse(dueToTest);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					long dateInMilliseconds = d.getTime();
					if(dateInMilliseconds == due){
						try {
							toDelete.delete();
						} catch (com.parse.ParseException e) {
							e.printStackTrace();
						}
						return;
					}
				}
			}
		});
	}

	@Override
	protected void onDestroy() {
		db.close();
		super.onStop();
	}
}
