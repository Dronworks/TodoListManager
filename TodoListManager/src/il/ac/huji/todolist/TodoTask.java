package il.ac.huji.todolist;

public class TodoTask {
	private int day;
	private int month;
	private int year;
	private String task;
	private long number;
	
	public TodoTask(int d, int m, int y, String todo, long num){
		day = d;
		month = m;
		year = y;
		task = todo;
		number = num;		
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
	
	public long getNumber(){
		return number;
	}
	
}
