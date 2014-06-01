package com.example.testtextadventure;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
	public final static String EXTRA_MESSAGE = "com.example.firstappagain.MESSAGE";
	private Locations location;
	private ArrayList<Items> inventory = new ArrayList<Items>();
	private ArrayList<Items> test_area_items = new ArrayList<Items>();
	
	private enum Items
	{
		NOTE,
		CANDLE,
		KEY
	}
	
	private enum Objects
	{
		DOOR,
		TREE
	}
	
	private enum Verbs
	{
		HELP,
		LOOK,
		TAKE,
		READ,
		OPEN,
		GO
	}
	
	private enum Locations
	{
		TEST_AREA,
		HOME,
		OUTSIDE
	}
	
	/** Called when Send button clicked */
	public void sendMessage(View view)
	{
		EditText editText = (EditText) findViewById(R.id.edit_message);
		String message = editText.getText().toString();
		((TextView)findViewById(R.id.main_text)).append("\n>"+message);
		String response = getResponse(message.split(" "));
		((TextView)findViewById(R.id.main_text)).append("\n"+response);
		editText.setText("");
		/*
		Intent intent = new Intent(this, DisplayMessageActivity.class);
		EditText editText = (EditText) findViewById(R.id.edit_message);
		String message = editText.getText().toString();
		intent.putExtra(EXTRA_MESSAGE, message);
		startActivity(intent);
		*/
	}
	
	private String getResponse(String[] input)
	{
		String response = "Sorry, that isnt a valid command";
		Verbs verb;
		Items item = null;
		Objects object = null;
		
		try
		{
			verb = Verbs.valueOf(input[0].toUpperCase(Locale.US));
		} catch (Exception e) 
		{
			return response;
		}
		System.out.println(verb);
		if(input.length == 2)
		{
			try
			{
				item = Items.valueOf(input[1].toUpperCase(Locale.US));
			} catch (Exception e) {}
			try
			{
				object = Objects.valueOf(input[1].toUpperCase(Locale.US));
			} catch (Exception e) {}
		}
		else if(input.length == 3)
		{
			try
			{
				item = Items.valueOf(input[2].toUpperCase(Locale.US));
			} catch (Exception e) {}
			try
			{
				object = Objects.valueOf(input[2].toUpperCase(Locale.US));
			} catch (Exception e) {}
		}
		//System.out.println(item);
		//System.out.println(object);
		//verb only switch
		if(item == null && object == null)
		{
			switch(verb)
			{
				case HELP:
					response = "This is a text adventure. Try 'look' to look around.";
					break;
				case LOOK:
					response = lookAround();
					break;
				default:
			}
		}
		else
		{
			switch(verb)
			{
				case TAKE:
					System.out.println("take "+item);
					response = takeItem(item);
					break;
				case READ:
					System.out.println("read "+item);
					response = readItem(item);
					break;
				case OPEN:
					System.out.println("open "+object);
					response = openObject(object);
					break;
				default:
			}
		}
		//verb and noun switch
		
		return response;
	}
	
	private String lookAround()
	{
		String view = "You have managed to leave the plain of existence";
		switch(location)
		{
			case TEST_AREA:
				if(inventory.contains(Items.NOTE) && inventory.contains(Items.CANDLE) && inventory.contains(Items.KEY))
					view = "You can't see anything in the pitch black room. You feel a handcuff around your left wrist. " +
							"There is a table to your right with nothing left on it. Across the room is a closed wooden door.";
				else if(inventory.contains(Items.NOTE) && inventory.contains(Items.CANDLE))
					view = "You can't see anything in the pitch black room. You feel a handcuff around your left wrist. " +
							"There is a table to your right with a small key on it. Across the room is a closed wooden door.";
				else if(inventory.contains(Items.CANDLE))
					view = "You can't see anything in the pitch black room. You feel a handcuff around your left wrist. " +
							"There is a table to your right with a note on it. Across the room is a closed wooden door.";
				else if(inventory.contains(Items.NOTE) && inventory.contains(Items.KEY))
						view = "You see a dimly lit room. You find a handcuff around your left wrist. " +
								"There is a table to your right with just a flickering candle remaining. Across the room is a closed wooden door.";
				else if(inventory.contains(Items.NOTE))
					view = "You see a dimly lit room. You find a handcuff around your left wrist. " +
							"There is a table to your right with a candle and a small key on it. Across the room is a closed wooden door.";
				else
					view = "You wake up in a dimly lit room. You find a handcuff around your left wrist. " +
						"There is a table to your right. On the table sits a candle with a note leaning against it. Across the room is a closed wooden door.";
				break;
			case OUTSIDE:
				view = "You see a large open meadow below a perfect blue sky. " +
				"In the middle of the meadow sits a single tree.";
			default:
		}
		return view;
	}
	
	private String takeItem(Items item)
	{
		String response = "There is no such item here.";
		switch(location)
		{
			case TEST_AREA:
				if(test_area_items.contains(item))
				{
					if(canTakeItem(Locations.TEST_AREA, item))
					{
						inventory.add(item);
						test_area_items.remove(item);
						response = "You take the "+item.toString().toLowerCase()+".";
						if(item == Items.NOTE)
							response = response.concat(" Beneath the note, you find a small key.");
						if(item == Items.CANDLE)
							response = response.concat(" You put out the candle to take it. Suddenly you find yourself in complete darkness.");
					} else
					{
						response = "There is no such visible item.";
					}
				}
				break;
			default:
		}
		return response;
	}
	
	private String readItem(Items item)
	{
		String response = "You can't read that!";
		switch(item)
		{
			case NOTE:
				response = "The note reads: 'Welcome to txtvntr0.0. Prepare to embark on an epic journey of might and magic!'";
				break;
			default:
		}
		if(!inventory.contains(item))
			response = "You don't have that!";
		return response;
	}
	
	private boolean canTakeItem(Locations loc, Items item)
	{
		switch(loc)
		{
			case TEST_AREA:
				if(item == Items.KEY && !inventory.contains(Items.NOTE))
					return false;
			default:
				return true;
		}
	}
	
	private String openObject(Objects object)
	{
		String response = "That cannot be opened";
		switch(object)
		{
			case DOOR:
				response = useDoor();
				break;
			default:
		}
		return response;
	}
	
	private String useDoor()
	{
		String response = "There is no door here.";
		switch(location)
		{
			case TEST_AREA:
				response = "You open the door and go through. The sun blinds you at first. " +
						"As you regain your sight, you find a large open meadow below a perfect blue sky. " +
						"In the middle of the meadow sits a single tree.";
				location = Locations.OUTSIDE;
				break;
			default:
		}
		return response;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_main);
		/*
		if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                .add(R.id.container, new PlaceholderFragment()).commit();
        }
		*/
		setupGame();
	}
	
	private void setupGame()
	{
		location = Locations.TEST_AREA;
		String start_text = lookAround();
		TextView mainTextView = (TextView)findViewById(R.id.main_text);
		if(mainTextView != null)
		{
			mainTextView.setText(start_text);
			mainTextView.setMovementMethod(new ScrollingMovementMethod());
		}
		test_area_items.add(Items.NOTE);
		test_area_items.add(Items.CANDLE);
		test_area_items.add(Items.KEY);
	}
/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_activity_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch(item.getItemId()) 
		{
			case R.id.action_search:
				openSearch();
				return true;
			case R.id.action_settings:
				openSettings();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	public void openSearch(){}
	
	public void openSettings(){}
*/
	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

}
