package il.ac.huji.todolist;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
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

	public TodoAdapter2(Context context, int layoutResourceId, ArrayList<TodoTask> data) {
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;
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
		status.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				changeColorOnCheckBox(holder, status, currentTask);
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
	}
}