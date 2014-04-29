package il.ac.huji.todolist;

public class TodoTask {
	private int day;
	private int month;
	private int year;
	private String task;
	private boolean checked;
	private long rowID;
	
	public TodoTask(int d, int m, int y, String todo, boolean chk, long rI){
		day = d;
		month = m;
		year = y;
		task = todo;
		checked = chk;
		rowID = rI;
	}
	
	public String toStringDate(){
		String date = "";
		date += day +"/" + month + "/" + year;
		return date;
	}
	
	public String getTask(){
		return task;
	}
	
	public int getDay(){
		return day;
	}
	
	public int getMonth(){
		return month;
	}
	
	public int getYear(){
		return year;
	}
	
	public boolean isChecked(){
		return checked;
	}
	
	public void setChecked(boolean chk){
		checked = chk;
	}
	
	public long getRowID(){
		return rowID;
	}
	
}

