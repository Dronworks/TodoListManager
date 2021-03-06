package il.ac.huji.todolist;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class TodoAdapter2 extends ArrayAdapter<TodoTask>{

	Context context; 
	int layoutResourceId;    
	ArrayList<TodoTask> data = null;
	TodoDBAdapter thisDB;

	public TodoAdapter2(Context context, int layoutResourceId, ArrayList<TodoTask> data, TodoDBAdapter db) {
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;
		thisDB = db;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View localView = convertView;
		todoHolder tempholder = new todoHolder();
		
		if(localView == null)
		{
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			localView = inflater.inflate(layoutResourceId, parent, false);
			tempholder.task = (TextView) localView.findViewById(R.id.txtTodoTitle);
			tempholder.date = (TextView) localView.findViewById(R.id.txtTodoDueDate);
			tempholder.status = (CheckBox) localView.findViewById(R.id.toDoChecked);
			localView.setTag(tempholder);
		}
		else
		{
			tempholder = (todoHolder) localView.getTag();
		}
		final todoHolder holder = tempholder;

		final TodoTask currentTask = data.get(position);
		final CheckBox status = (CheckBox) holder.status;
		
		status.setChecked(currentTask.isChecked());
		status.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String date1 = currentTask.getDay() + "-" + currentTask.getMonth() + "-" + currentTask.getYear();
				SimpleDateFormat f = new SimpleDateFormat("dd-mm-yyyy", Locale.getDefault());
				Date d = new Date();
				try {
					d = f.parse(date1);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				long dateInMilliseconds = d.getTime();
				
				
				changeColorOnCheckBox(holder, status, currentTask);
				if(status.isChecked()){
					Log.w("----------", "CheckboxT");
					currentTask.setChecked(true);
					thisDB.updateRecord(currentTask.getRowID(), currentTask.getTask(), dateInMilliseconds, 1);
				}
				else{
					Log.w("----------", "CheckboxF");
					currentTask.setChecked(false);
					thisDB.updateRecord(currentTask.getRowID(), currentTask.getTask(), dateInMilliseconds, 0);
				}
			}
		});
		
		if(!holder.status.isChecked()){
			if(isOverdue(currentTask)){
				holder.task.setTextColor(Color.RED);
				holder.date.setTextColor(Color.RED);
			}
			else{
				holder.task.setTextColor(Color.BLACK);
				holder.date.setTextColor(Color.BLACK);
			}
		}
		else{
			holder.task.setTextColor(Color.GREEN);
			holder.date.setTextColor(Color.GREEN);
		}
		holder.task.setText(currentTask.getTask());
		holder.date.setText(currentTask.toStringDate());
		return localView;
	}

	private static boolean isOverdue(TodoTask task){
		int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		int currentMonth = Calendar.getInstance().get(Calendar.MONTH)+1;
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		if(task.getYear() < currentYear || task.getMonth() < currentMonth || task.getDay() < currentDay)
			return true;
		return false;
	}

	private static void changeColorOnCheckBox(todoHolder holder, CheckBox cb, TodoTask newTask){
		if (cb.isChecked()) {
			holder.task.setTextColor(Color.GREEN);
			holder.date.setTextColor(Color.GREEN);
		}
		else{
			if(isOverdue(newTask)){
				holder.task.setTextColor(Color.RED);
				holder.date.setTextColor(Color.RED);
			}
			else{
				holder.task.setTextColor(Color.BLACK);
				holder.date.setTextColor(Color.BLACK);
			}
		}
	}

	class todoHolder{
		CheckBox status;
		TextView task;
		TextView date;
		public String toString(){
			return status.toString() + task.toString() + date.toString();
		}
	}
}