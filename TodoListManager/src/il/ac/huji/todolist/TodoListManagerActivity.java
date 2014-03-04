package il.ac.huji.todolist;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class TodoListManagerActivity extends Activity {
	private ArrayList<String> strings = new ArrayList<String>();
	private Activity my = this;
	private ListView list;
	ToDoAdapter ad;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		strings.add("Andrey");
		strings.add("Tal");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todo_list_manager);
		list = (ListView) findViewById(R.id.lstTodoItems);
		ad = new ToDoAdapter(strings, my);
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
			final EditText totalCost = (EditText) findViewById(R.id.editNewItem);
			strings.add(totalCost.getText().toString());
			ad.notifyDataSetChanged();
		}
		return true;
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		int id = info.position;
		if(item.getItemId() == R.id.menuItemDelete){
			strings.remove(id);
			ad.notifyDataSetChanged();
			Toast.makeText(getApplicationContext(), "test " + id, Toast.LENGTH_SHORT).show();
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
//		Log.w(TEXT_SERVICES_MANAGER_SERVICE, new Integer(info.position).toString());
		int pos = info.position;
		String title = strings.get(pos);
		menu.setHeaderTitle(title);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.context, menu);
	}

}
 