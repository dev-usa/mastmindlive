package com.mastermindlive.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * Android Activity for the initial user screen User can pick number of
 * colors/pins for game number of guesses for guessing player and number of slot
 * spots for each guess
 * 
 * TODO need to allow for selecting Human vs Human or Human vs Computer player
 * options TODO ultimately this activity will need to activate a game on the
 * server so another Human player can search and find this game using a ranking
 * system (wins, number of games played, etc)
 * 
 * @author Benjamin Maisano
 */
public class MasterMindLiveStartMenuActivity extends Activity {

	// Our private member variables for the user options for the game
	private int numberOfColors;
	private int numberOfGuesses;
	private int numberOfSlots; // spaces per guess

	@Override
	public void onCreate(Bundle savedInstanceState) {

		// call super
		super.onCreate(savedInstanceState);

		// request no title
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// set our layout xml view
		setContentView(R.layout.startmenulayout);

		// create 3 spinners for number of colors/pins, number of guesses, and
		// number of slots per guess
		Spinner numColorsSpinner = (Spinner) findViewById(R.id.numcolorspinner);
		ArrayAdapter adapter = ArrayAdapter.createFromResource(this,
				R.array.num_colors, android.R.layout.simple_spinner_item);
		adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		numColorsSpinner.setAdapter(adapter);
		numColorsSpinner
				.setOnItemSelectedListener(new NumColorItemSelectedListener());

		Spinner numGuessesSpinner = (Spinner) findViewById(R.id.numguessesspinner);
		ArrayAdapter adapter2 = ArrayAdapter.createFromResource(this,
				R.array.num_guesses, android.R.layout.simple_spinner_item);
		adapter2
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		numGuessesSpinner.setAdapter(adapter2);
		numGuessesSpinner
				.setOnItemSelectedListener(new NumGussesItemSelectedListener());

		Spinner numSlotsSpinner = (Spinner) findViewById(R.id.numslotspinner);
		ArrayAdapter adapter3 = ArrayAdapter.createFromResource(this,
				R.array.num_slots, android.R.layout.simple_spinner_item);
		adapter3
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		numSlotsSpinner.setAdapter(adapter3);
		numSlotsSpinner
				.setOnItemSelectedListener(new NumSlotsItemSelectedListener());

		//set the start game button's click listener
		findViewById(R.id.start_game).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startGame(false); // only supports computer player vs human
									// player right now
			}
		});
	}

	/**
	 * TODO create a game on the server and submit this newly created game into the searchable
	 * games pool so other user searches can find it and it can find other user games
	 * 
	 * @param humanvsHuman - TODO not implemented can only play computer right now
	 */
	private void startGame(boolean humanvsHuman) {
		//create an Intent to start up the main game activity
		Intent i = new Intent(this, MasterMindLiveMainActivity.class);
		
		//pass the user options to this intent
		i.putExtra(MasterMindLiveMainActivity.INTENT_ATTR_NUM_COLORS,
				numberOfColors);
		i.putExtra(MasterMindLiveMainActivity.INTENT_ATTR_NUM_GUESSES,
				numberOfGuesses);
		i.putExtra(MasterMindLiveMainActivity.INTENT_ATTR_NUM_SLOTS,
				numberOfSlots);
		
		startActivity(i);
	}

	public class NumColorItemSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {
			Object inputO = parent.getItemAtPosition(pos);
			if (inputO != null) {
				String numColorsStr = (String) inputO;
				numberOfColors = new Integer(numColorsStr);
			}
		}

		public void onNothingSelected(AdapterView parent) {
			// Do nothing.
		}
	}

	public class NumGussesItemSelectedListener implements
			OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {
			Object inputO = parent.getItemAtPosition(pos);
			if (inputO != null) {
				String numGuessesStr = (String) inputO;
				numberOfGuesses = new Integer(numGuessesStr);
			}
		}

		public void onNothingSelected(AdapterView parent) {
			// Do nothing.
		}
	}

	public class NumSlotsItemSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {
			Object inputO = parent.getItemAtPosition(pos);
			if (inputO != null) {
				String numSlotsStr = (String) inputO;
				numberOfSlots = new Integer(numSlotsStr);
			}
		}

		public void onNothingSelected(AdapterView parent) {
			// Do nothing.
		}
	}
}
