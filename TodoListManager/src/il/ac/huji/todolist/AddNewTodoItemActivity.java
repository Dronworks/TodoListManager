package il.ac.huji.todolist;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

public class AddNewTodoItemActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_new_todo_item);
		setTitle("Add New Item");
		Button okButton = (Button) findViewById(R.id.btnOK);
		Button cancelButon = (Button) findViewById(R.id.btnCancel);

		okButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				EditText todoText = (EditText) findViewById(R.id.edtNewItem);
				DatePicker date = (DatePicker) findViewById(R.id.datePicker);
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); //To hide keyboard after addition.
				imm.hideSoftInputFromWindow(todoText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS); //To hide keyboard.
				if(!todoText.getText().toString().equals("")){
					Intent res = new Intent();
					res.putExtra("Task", todoText.getText().toString());
					res.putExtra("Day", date.getDayOfMonth());
					res.putExtra("Month", date.getMonth()+1);
					res.putExtra("Year", date.getYear());
					setResult(RESULT_OK, res);
					finish();
				}
				else{
					Toast.makeText(getApplicationContext(), "Task can't be empty", Toast.LENGTH_SHORT).show();
				}
			}
		});

		cancelButon.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				EditText todoText = (EditText) findViewById(R.id.edtNewItem);
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); //To hide keyboard after addition.
				imm.hideSoftInputFromWindow(todoText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS); //To hide keyboard.
				finish();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_new_todo_item, menu);
		return true;
	}

}
