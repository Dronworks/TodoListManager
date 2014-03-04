package il.ac.huji.todolist;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ToDoAdapter extends BaseAdapter{

	ArrayList<String> names;// = {"Desert", "JellyFish", "Koala", "Lighthouse", "Penguins", "Tulips", "Chrysanthenium", "Hydrangeas"};
	Context context; //which activity will get the cells we create.(has the list view for the cells);
	LayoutInflater inflater;//To retrieve the cells from xml layout(duplicate the layout);
	
	public ToDoAdapter(ArrayList<String> arr, Context c){
		names = arr;
		context = c;
		inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);//use object which should be the context in the activity. (tern xml stuff into java objects).
	}
	
	
	@Override
	public int getCount() {
		return names.size();
	}

	@Override
	public Object getItem(int arg0) {
		return names.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		//dont need
		return 0;
	}

	@Override
	public View getView(int location, View view, ViewGroup parent) {
		//create a cell and populate it with an element of the array.
		View localView = view;
		if(localView == null){ //It there is nothing in the table
			localView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false); //first create a simple view cell by inflating a layout for each cell. simple_list_item_1 - the default android list item.
		}
		TextView name = (TextView) localView.findViewById(android.R.id.text1); //The layout had first text(from inflated local view)
		name.setText(names.get(location)); //set text from names.
		return localView;
	}

}
