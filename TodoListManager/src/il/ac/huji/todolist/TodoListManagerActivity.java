package il.ac.huji.todolist;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.net.Uri;
import android.os.Bundle;
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
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

public class TodoListManagerActivity extends Activity {
	private ArrayList<TodoTask> tasks;
	private ListView list;
	private TodoAdapter2 ad;
	private TodoDBAdapter db;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todo_list_manager);
		Parse.initialize(this, "iPbCD2Q2ZPb7GZCC1sXz5Y91LQkouFdVIgtlOWSP", "FQvWBYu5b8E6WCep7Wj0UnBMhYm2P557bMwVxz2k");
		tasks = new ArrayList<TodoTask>();
//		this.deleteDatabase(TodoConstants.DATABASE_NAME);
		db = new TodoDBAdapter(this);
		
		getAllDBRecords();

		list = (ListView) findViewById(R.id.lstTodoItems);
		ad = new TodoAdapter2(this, R.layout.todoline_layout, tasks);
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
			finish();
		}
		return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		int pos = info.position;
		TodoTask curTask = tasks.get(pos);
		String title = curTask.getTask();
		menu.setHeaderTitle(title); //set title for delete menu.

		MenuInflater inflater = getMenuInflater();
		if(!firstWordCall(title).equals("-1")){
			inflater.inflate(R.menu.context_delete_call, menu);
			MenuItem mi =(MenuItem) menu.findItem(R.id.menuItemCall);
			mi.setTitle(title);
		}
		else{
			inflater.inflate(R.menu.context_delete, menu);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		int taskPos = info.position;
		TodoTask currentTask = tasks.get(taskPos);
		if(item.getItemId() == R.id.menuItemDelete){
			String itemToDelete = currentTask.getTask();
			long rowId = currentTask.getRowId();
			Log.w("HEWHWHEHE", "SDFSDF");
			String onlineRowID = currentTask.getOnlineID();
			Log.w("ROW ID", onlineRowID);
			tasks.remove(taskPos);
			removeTodoTask(rowId);
			removeOnlineTodoTask(onlineRowID);
			ad.notifyDataSetChanged();
			Toast.makeText(getApplicationContext(), "Item: [" + itemToDelete + "] was successfully deleted!", Toast.LENGTH_SHORT).show();
		}
		if(item.getItemId() == R.id.menuItemCall){
			Intent callIntent = new Intent(Intent.ACTION_CALL);
			callIntent.setData(Uri.parse("tel:" + currentTask.getNumber()));
			startActivity(callIntent);
		}
		return super.onContextItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent input) {
		// if the results is coming from BROWSER_ACTIVATION_REQUEST 
		String newTask;
		String number = "-1";

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
					long milliseconds = d.getTime();

					String s = "";
					s+= milliseconds;// TODO change names

					if(newTask.toLowerCase().contains("call")){
						number = firstWordCall(newTask);
					}
					String onlineID = addOnlineTodoTask(newTask, milliseconds);
					long id = addTodoTask(newTask, milliseconds, onlineID);
					TodoTask todoLine = new TodoTask(day, month, year, newTask, number, false, id, onlineID);
					tasks.add(todoLine);
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
	private long addTodoTask(String task, long due, String oid){
		db.open();
		Log.w("=============",task);
		long id = db.insertRecord(task, due, oid);
		if(id < 0){
			Log.e(TodoConstants.DBERROR, "Could not add new task to the database");
			db.close();
			return -1;
		}
		else{
			db.close();
			return id;
		}
	}
	
	private void removeTodoTask(long rowId){
		db.open();
		boolean isDeleted = db.deleteRecord(rowId);
		if(!isDeleted) Log.e(TodoConstants.DBERROR, "Could not remove task from the database");
		db.close();
	}
	
	private void getAllDBRecords(){
		db.open();
		Cursor curs = db.getAllRecords();
		if(curs.moveToFirst()){
			do{
				long id = curs.getLong(curs.getColumnIndex(TodoConstants.COLUMN_ID));
				long date = curs.getLong(curs.getColumnIndex(TodoConstants.COLUMN_DUE));
				String onlineID = curs.getString(curs.getColumnIndex(TodoConstants.COLUMN_ONLINE_ID));
				String[] taskDue = getDateFromLong(date);
				String title = curs.getString(curs.getColumnIndex(TodoConstants.COLUMN_TITLE));
				Log.w("-------", title);
				String num = firstWordCall(title);
				tasks.add(new TodoTask(Integer.valueOf(taskDue[0]), Integer.valueOf(taskDue[1]), Integer.valueOf(taskDue[2]), title, num, false, id, onlineID));
			}while(curs.moveToNext());
		}
		db.close();
	}

	private String[] getDateFromLong(long date) {
		Date dt = new Date(date);
		SimpleDateFormat f = new SimpleDateFormat("dd-mm-yyyy", Locale.getDefault());
		String s = f.format(dt);
		String[] retDate =  s.split("-");
		return retDate;
	}
	
//======================================ONLINE DATABASE FUNCTIONS===========================================
	
	private String addOnlineTodoTask(String task, long due){
		final ParseObject onlineDBObject = new ParseObject(TodoConstants.ONLINE_DATABASE_NAME);
		onlineDBObject.put(TodoConstants.ONLINE_DB_COLUMN_TITLE, task);
		Date d = new Date(due);  
		SimpleDateFormat f = new SimpleDateFormat("dd-mm-yyyy", Locale.getDefault());
		String s = f.format(d);
		onlineDBObject.put(TodoConstants.ONLINE_DB_COLUMN_DUE, s);
		onlineDBObject.saveInBackground();

		String retval = "";
		try {
			onlineDBObject.save();
			retval = onlineDBObject.getObjectId();
		} catch (com.parse.ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
//	    onlineDBObject.saveInBackground(new SaveCallback() {
//	    	@Override
//	    	public void done(com.parse.ParseException e) {
//	            if (e == null) {
//	                // Saved successfully.
//	                Log.d(TodoConstants.ONLNE_DB_MESSAGE, "User update saved!");
//	                String id = onlineDBObject.getObjectId();
//	                thisId.id = id;
//	                Log.d(TodoConstants.ONLNE_DB_MESSAGE, "The object id is: " + id);
//	            } else {
//	                // The save failed.
//	                Log.d(TodoConstants.ONLNE_DB_MESSAGE, "User update error: " + e);
//	            }
//	        }
//
//	    });
		
		return retval;
	}
	
	private void removeOnlineTodoTask(String rowID){
		Log.w(TodoConstants.ONLNE_DB_MESSAGE, rowID);
		ParseObject.createWithoutData(TodoConstants.ONLINE_DATABASE_NAME, rowID).deleteEventually();
	}
	
	private void getAllOnlineDBRecords(){
		ParseQuery<ParseObject> query = ParseQuery.getQuery(TodoConstants.ONLINE_DATABASE_NAME);
		
		query.findInBackground(new FindCallback<ParseObject>() {
		     
			 @Override
			 public void done(List<ParseObject> objects, com.parse.ParseException e) {
		         if (e == null) {
		        	 //TODO ???
		         } else {
		        	 //TODO ???
		         }
		     }
		 });
	}
 
}
