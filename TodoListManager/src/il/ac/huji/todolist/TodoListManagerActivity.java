package il.ac.huji.todolist;

import java.util.ArrayList;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
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

public class TodoListManagerActivity extends Activity {
	private ArrayList<TodoTask> tasks;
	private ListView list;
	TodoAdapter2 ad;
	final int BROWSER_ACTIVATION_REQUEST = 2; // request code

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tasks = new ArrayList<TodoTask>();
		setContentView(R.layout.activity_todo_list_manager);
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
			startActivityForResult(intent, BROWSER_ACTIVATION_REQUEST);
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

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		int id = info.position;
		if(item.getItemId() == R.id.menuItemDelete){
			String itemToDelete = tasks.get(id).getTask();
			tasks.remove(id);
			ad.notifyDataSetChanged();
			Toast.makeText(getApplicationContext(), "Item: [" + itemToDelete + "] was successfully deleted!", Toast.LENGTH_SHORT).show();
		}
		if(item.getItemId() == R.id.menuItemCall){
			Intent callIntent = new Intent(Intent.ACTION_CALL);
			callIntent.setData(Uri.parse("tel:" + tasks.get(id).getNumber()));
			startActivity(callIntent);
		}
		return super.onContextItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent input) {
		// if the results is coming from BROWSER_ACTIVATION_REQUEST 
		String newTask;
		String number = "-1";
		
		if (requestCode == BROWSER_ACTIVATION_REQUEST) {

			// check the result code set by the activity
			if (resultCode == RESULT_OK) {
				if(input.hasExtra("Task") && input.getExtras().getString("Task") != null){
					newTask = input.getExtras().getString("Task");
					int day = input.getExtras().getInt("Day");
					int month = input.getExtras().getInt("Month");
					int year = input.getExtras().getInt("Year");
					if(newTask.toLowerCase().contains("call")){
						number = firstWordCall(newTask);
					}
					TodoTask todoLine = new TodoTask(day, month, year, newTask, number);
					newTask += day;
					newTask += month;
					newTask += year;
					tasks.add(todoLine);
					ad.notifyDataSetChanged();
				}
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}


}
