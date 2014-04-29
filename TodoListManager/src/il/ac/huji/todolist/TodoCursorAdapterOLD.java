package il.ac.huji.todolist;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class TodoCursorAdapterOLD extends CursorAdapter{
	TodoDBAdapter db;

	public TodoCursorAdapterOLD(Context context, Cursor c, boolean autoRequery, TodoDBAdapter database) {
		super(context, c, autoRequery);
		db = database;
	}

	@Override
	public View newView(Context ctx, Cursor curs, ViewGroup parent) {
		// when the view will be created for first time,
		// we need to tell the adapters, how each item will look
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		View retView = inflater.inflate(R.layout.todoline_layout, parent, false);
		return retView;
	}

	@Override
	public void bindView(final View curView, Context ctx, final Cursor curs) {
		// here we are setting our data
		// that means, take the data from the cursor and put it in views
		CheckBox cb = (CheckBox) curView.findViewById(R.id.toDoChecked);
		TextView todoTitle = (TextView) curView.findViewById(R.id.txtTodoTitle);
		TextView due = (TextView) curView.findViewById(R.id.txtTodoDueDate);
		final String[] splitDate = getDateFromLong(curs.getLong(curs.getColumnIndex(TodoConstants.COLUMN_DUE))); 
		String dueText = splitDate[0] + "/" + splitDate[1] + "/" + splitDate[2];

		cb.setTag(Integer.valueOf(curs.getPosition()));
		cb.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				Integer posInt = (Integer)buttonView.getTag(); //To get the right line.
				int pos = posInt.intValue();
				curs.moveToPosition(pos);
				long rowID = curs.getLong(curs.getColumnIndex(TodoConstants.COLUMN_ID));
				String title = curs.getString(curs.getColumnIndex(TodoConstants.COLUMN_TITLE));
				long due =  curs.getLong(curs.getColumnIndex(TodoConstants.COLUMN_DUE));
				
				TextView todoTitle = (TextView) curView.findViewById(R.id.txtTodoTitle);
				TextView dueView = (TextView) curView.findViewById(R.id.txtTodoDueDate);
				if(isChecked){
					boolean x = db.updateRecord(rowID, title, due, 1);
					todoTitle.setTextColor(Color.GREEN);
					dueView.setTextColor(Color.GREEN);
				}
				else {
					db.updateRecord(rowID, title, due, 0);
					colorRedOrBlack(todoTitle, dueView, splitDate);
				}

			}
		});

		if(curs.getInt(curs.getColumnIndex(TodoConstants.COLUMN_DONE)) == 1){
			cb.setChecked(true);
		}
		else{
			cb.setChecked(false);
		}
		if(cb.isChecked()){
			todoTitle.setTextColor(Color.GREEN);
			due.setTextColor(Color.GREEN);
		}
		else {
			colorRedOrBlack(todoTitle, due, splitDate);
		}
		todoTitle.setText(curs.getString(curs.getColumnIndex(TodoConstants.COLUMN_TITLE)));
		due.setText(dueText);
	}

	private void colorRedOrBlack(TextView title, TextView due, String[] splitDate){
		if(isOverdue(splitDate)){
			title.setTextColor(Color.RED);
			due.setTextColor(Color.RED);
		}
		else{
			title.setTextColor(Color.BLACK);
			due.setTextColor(Color.BLACK);
		}
	}
	
	private static boolean isOverdue(String[] due){
		int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		int currentMonth = Calendar.getInstance().get(Calendar.MONTH)+1;
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		if(Integer.valueOf(due[2]) < currentYear || Integer.valueOf(due[1]) < currentMonth || Integer.valueOf(due[0]) < currentDay)
			return true;
		return false;
	}

	private String[] getDateFromLong(long date) {
		Date dt = new Date(date);
		SimpleDateFormat f = new SimpleDateFormat("dd-mm-yyyy", Locale.getDefault());
		String s = f.format(dt);
		String[] retDate =  s.split("-");
		return retDate;
	}
	
	
	
}
