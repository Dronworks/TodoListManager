package il.ac.huji.todolist;

public class TodoConstants {
	public static final int BROWSER_ACTIVATION_REQUEST = 2; // request code
	public static final String DBERROR = "DB Error: ";
	public static final String ONLNE_DB_MESSAGE = "Parse message:";
	public static final String ONLINE_DB_COLUMN_TITLE = "title";
	public static final String ONLINE_DB_COLUMN_DUE = "due";
	public static final String DATABASE_TABLE = "todo";
	public static final String DATABASE_NAME = "todo_db";
	public static final String ONLINE_DATABASE_NAME = "todo";
	public static final int DATABASE_VERSION = 1;
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_TITLE = "title";
	public static final String COLUMN_DUE = "due";
	public static final String COLUMN_DONE = "is_done";
	public static final String COLUMN_ONLINE_ID = "online_id";
	
	public static final String NEW_DB = "create table " + DATABASE_TABLE
			+ " (" + COLUMN_ID
			+ " integer primary key autoincrement, " + COLUMN_TITLE
			+ " text not null, " + COLUMN_DUE + " long not null, " + COLUMN_DONE + " int not null);";
}
