package il.ac.huji.todolist;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.database.Cursor;
import android.os.AsyncTask;

public class TodoAsyncLoader extends AsyncTask<Void, TodoTask, Void>{

	private TodoDBAdapter dbAdapter;
	private TodoAdapter2 adapter;

	public TodoAsyncLoader(TodoDBAdapter sql, TodoAdapter2 adap){
		dbAdapter = sql;
		adapter = adap;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		adapter.clear();
	}

	@Override
	protected Void doInBackground(Void... params) {
		Cursor curs = dbAdapter.getAllRecords();

		if(curs.moveToFirst()){
			do{
				String[] splitDate = getDateFromLong(curs.getLong(curs.getColumnIndex(TodoConstants.COLUMN_DUE)));
				String title = curs.getString(curs.getColumnIndex(TodoConstants.COLUMN_TITLE));
				int checked = curs.getInt(curs.getColumnIndex(TodoConstants.COLUMN_DONE));
				long id = curs.getLong(curs.getColumnIndex(TodoConstants.COLUMN_ID));
				TodoTask task = new TodoTask(Integer.parseInt(splitDate[0]), Integer.parseInt(splitDate[1]), 
						Integer.parseInt(splitDate[2]), title, checked == 1 ? true:false, id);
				publishProgress(task);
			} while(curs.moveToNext());

		}
		return null;
	}

	@Override
	protected void onProgressUpdate(TodoTask... values) {
		super.onProgressUpdate(values);
		adapter.add(values[0]);
		adapter.notifyDataSetChanged();
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
	}

	private String[] getDateFromLong(long date) {
		Date dt = new Date(date);
		SimpleDateFormat f = new SimpleDateFormat("dd-mm-yyyy", Locale.getDefault());
		String s = f.format(dt);
		String[] retDate =  s.split("-");
		return retDate;
	}

}
