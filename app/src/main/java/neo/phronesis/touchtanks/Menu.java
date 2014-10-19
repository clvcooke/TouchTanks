package neo.phronesis.touchtanks;



import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * @author colin
 *
 */
public class Menu extends ListActivity  {
	
	String classes[] = {"Play Game", "Settings", "High Scores", "Credits"};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setListAdapter(new ArrayAdapter<String>(Menu.this, android.R.layout.simple_list_item_1, classes));
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		
		String check = classes[position];
		if(check == "Play Game"){
			check = "Main_game";
		}else if(check == "High Scores"){
			check = "Highscores"; 
		}
		
		try{
			Class ourClass = Class.forName("neo.phronesis.touchtanks." + check);
			Intent ourIntent = new Intent(Menu.this, ourClass);
			startActivity(ourIntent);
		}catch (ClassNotFoundException e){
			e.printStackTrace();
		}
	}
	
	
	

	

}
