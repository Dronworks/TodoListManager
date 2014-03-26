package il.ac.huji.todolist;

public class TodoTask {
	private int day;
	private int month;
	private int year;
	private String task;
	private String number;
	private boolean checked;
	private long rowId;
	private String onlineID;
	
	public TodoTask(int d, int m, int y, String todo, String num, boolean chk, long rID, String oid){
		day = d;
		month = m;
		year = y;
		task = todo;
		number = num;
		checked = chk;
		rowId = rID;
		onlineID = oid;
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
	
	public String getNumber(){
		return number;
	}
	
	
	public boolean isChecked(){
		return checked;
	}
	
	public void setChecked(boolean chk){
		checked = chk;
	}
	
	public long getRowId(){
		return rowId;
	}
	
	public String getOnlineID(){
		return onlineID;
	}
}

