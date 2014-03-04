package il.ac.huji.todolist;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); //To hide keyboard after addition.
		imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS); //To hide keyboard.
		if(item.getItemId() == R.id.menuItemAdd){
			final EditText addTodo = (EditText) findViewById(R.id.edtNewItem);
			String inputText = addTodo.getText().toString();
			if(inputText.length() != 0){
				strings.add(addTodo.getText().toString());
				ad.notifyDataSetChanged();
				addTodo.setText("");
			}
			else{
				Toast.makeText(this, "Task can't be empty...", Toast.LENGTH_SHORT).show();
			}
		}
		return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		int pos = info.position;
		String title = strings.get(pos);
		menu.setHeaderTitle(title); //set title for delete menu.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.context, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		int id = info.position;
		if(item.getItemId() == R.id.menuItemDelete){
			String itemToDelete = strings.get(id);
			strings.remove(id);
			ad.notifyDataSetChanged();
			Toast.makeText(getApplicationContext(), "Item: [" + itemToDelete + "] was successfully deleted!", Toast.LENGTH_SHORT).show();
		}
		return super.onContextItemSelected(item);
	}
}
 