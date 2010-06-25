package com.mastermindlive.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TableLayout.LayoutParams;

import com.mastermindlive.android.dragndrop.DragableView;

/**
 * MasterMindLive main game activity Manages the game board and playing the game
 * 
 * @author Benjamin Maisano
 */
public class MasterMindLiveMainActivity extends Activity {

	//the variables we expect to get from Intent that invokes this Activity
	public static final String INTENT_ATTR_NUM_COLORS = "NumColors";
	public static final String INTENT_ATTR_NUM_GUESSES = "NumGuesses";
	public static final String INTENT_ATTR_NUM_SLOTS = "NumSlots";

	//Possible color/pin options
	private static final int HEX_COLOR_RED = 0xFFFF0000;
	private static final int HEX_COLOR_BLUE = 0xFF0000FF;
	private static final int HEX_COLOR_PURPLE = 0xFFFF00CC;
	private static final int HEX_COLOR_GREEN = 0xFF66FF66;
	private static final int HEX_COLOR_YELLOW = 0xFFFFFF33;
	private static final int HEX_COLOR_ORANGE = 0xFFFF9933;
	private static final int HEX_COLOR_BLACK = 0x00000000;
	
	private DragableView currentlyDraggingPin;
	private TextView statusMsg;
	private ViewGroup mainContainer;
	private List<ImageView> pinOptions;
	private int numColors;
	private int numGuesses;
	private int numSlots;
	private Resources res;
	private LinearLayout pinOptionsRow;
	private LinearLayout emptySpaceRows;
	private int currentRowNum;
	private Map<Integer, GuessRowManager> guessRows;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.gamelayout);

		// capture user inputs
		this.numColors = getIntent().getIntExtra(INTENT_ATTR_NUM_COLORS, 4);
		this.numGuesses = getIntent().getIntExtra(INTENT_ATTR_NUM_GUESSES, 10);
		this.numSlots = getIntent().getIntExtra(INTENT_ATTR_NUM_SLOTS, 4);
		this.currentRowNum = 1;
		this.guessRows = new HashMap<Integer, GuessRowManager>();
		
		// capture common layout components
		this.mainContainer = (ViewGroup) findViewById(R.id.structuredcontainer);
		this.statusMsg = (TextView) findViewById(R.id.notificationview);

		// capture resources
		this.res = getResources();

		// setup the pin color options the guesser can choose from
		setupPinOptions();

		// setup the empty slot spots for each guess
		setupSlotSpots();
		
		setupGuessButton();
	}

	private void setupGuessButton(){
		GuessRowManager grm = this.guessRows.get(currentRowNum);
		View guessResults = grm.getGuessResults();
		Button guessBtn = new Button(this);
		guessBtn.setText("Guess");
		guessBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				doGuess();
			}
		});
		grm.getGuessRowView().removeView(grm.getGuessResults());
		grm.getGuessRowView().addView(guessBtn);
		grm.setGuessResults(guessBtn);
	}
	
	public void doGuess(){
		GuessRowManager grm = this.guessRows.get(currentRowNum);

		//validate row
		if(grm.allSlotsFill() == false){
			this.statusMsg.setText("Please pick a color for all slots");
			return;
		}
		calculateGuessResults();
		
		
		Drawable guessResultsDrawable = res.getDrawable(R.drawable.guessresultsbackground);
		ImageView guessResultsView = new ImageView(this);
		guessResultsView.setImageDrawable(guessResultsDrawable);
		grm.getGuessRowView().removeView(grm.getGuessResults());
		grm.getGuessRowView().addView(guessResultsView);
		grm.setGuessResults(guessResultsView);
		
		this.currentRowNum++;
		updateGameBoard();
	}
	
	public void calculateGuessResults(){
		//TODO implement
	}
	
	public void updateGameBoard(){
		setupGuessButton();
	}
	private void setupSlotSpots() {
		// capture area to build up empty guess rows
		this.emptySpaceRows = (LinearLayout) findViewById(R.id.emptyspacesrows);

		for (int i = this.numGuesses; i > 0; i--) {
			// add a row for each guess
			LinearLayout guessRow = new LinearLayout(this);
			guessRow.setOrientation(LinearLayout.HORIZONTAL);
			guessRow.setLayoutParams(new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			this.emptySpaceRows.addView(guessRow);
			GuessRowManager grm = new GuessRowManager(i, guessRow);
			this.guessRows.put(i, grm);
				
			// within each guess row add empty slot spots
			for (int x = 1; x <= this.numSlots; x++) {
				Drawable emptyDrawable = res
						.getDrawable(R.drawable.emptyslotspot);
				ImageView emptySlotView = new ImageView(this);
				emptySlotView.setImageDrawable(emptyDrawable);
				emptySlotView.setTag("Row " + i + " Slot " + x);
				guessRow.addView(emptySlotView);
				SlotSpot ss = new SlotSpot(i, x);
				ss.setSlotView(emptySlotView);
				grm.getSlotSpots().put(x, ss);
			}
			
			Drawable guessResultsDrawable = res.getDrawable(R.drawable.guessresultsbackground);
			ImageView guessResultsView = new ImageView(this);
			guessResultsView.setImageDrawable(guessResultsDrawable);
			guessRow.addView(guessResultsView);
			grm.setGuessResults(guessResultsView);
		}
	}

	private void setupPinOptions() {
		this.pinOptionsRow = (LinearLayout) findViewById(R.id.pinoptionsrow);
		this.pinOptions = new ArrayList<ImageView>();

		for (int i = 1; i <= this.numColors; i++) {
			switch (i) {
			case 1:
				createPinOption(R.drawable.bluepin, HEX_COLOR_BLUE);
				break;
			case 2:
				createPinOption(R.drawable.redpin, HEX_COLOR_RED);
				break;
			case 3:
				createPinOption(R.drawable.greenpin, HEX_COLOR_GREEN);
				break;
			case 4:
				createPinOption(R.drawable.orangepin, HEX_COLOR_ORANGE);
				break;
			case 5:
				createPinOption(R.drawable.blackpin, HEX_COLOR_BLACK);
				break;
			case 6:
				createPinOption(R.drawable.yellowpin, HEX_COLOR_YELLOW);
				break;
			case 7:
				createPinOption(R.drawable.purplepin, HEX_COLOR_PURPLE);
				break;
			}
		}

	}

	private void createPinOption(int drawableId, int dragColor) {
		Drawable pinDrawable = res.getDrawable(drawableId);
		ImageView pinView = new ImageView(this);
		pinView.setImageDrawable(pinDrawable);
		pinView.setTag(dragColor);
		this.pinOptionsRow.addView(pinView);
		this.pinOptions.add(pinView);
	}

	public ImageView findSelectedPin(int eventX, int eventY) {
		Rect hitRec = new Rect();
		int left;
		int top;
		int right;
		int bottom;
		int width;
		int height;
		for (ImageView pinOption : this.pinOptions) {
			width = pinOption.getWidth();
			height = pinOption.getHeight();
			int[] coordinates = { 0, 0 };
			pinOption.getLocationOnScreen(coordinates);
			left = coordinates[0];
			top = coordinates[1];
			right = left + width;
			bottom = top + height;
			hitRec.set(left, top, right, bottom);

			if (hitRec.contains(eventX, eventY)) {
				return (ImageView) pinOption;
			}
		}

		return null;
	}

	public SlotSpot findSlotSpot(int eventX, int eventY, GuessRowManager grm) {
		Rect hitRec = new Rect();
		int left;
		int top;
		int right;
		int bottom;
		int width;
		int height;
		
		for (Integer slotSpotNum : grm.getSlotSpots().keySet()) {
			SlotSpot ss = grm.getSlotSpots().get(slotSpotNum);
			ImageView slotSpotView = ss.getSlotView();
			width = slotSpotView.getWidth();
			height = slotSpotView.getHeight();
			int[] coordinates = { 0, 0 };
			slotSpotView.getLocationOnScreen(coordinates);
			left = coordinates[0];
			top = coordinates[1];
			right = left + width;
			bottom = top + height;
			hitRec.set(left, top, right, bottom);
			if (hitRec.contains(eventX, eventY)) {
				return ss;
			}
		}

		return null;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int eventaction = event.getAction();
		int eventX = (int) event.getX();
		int eventY = (int) event.getY();
		StringBuffer sb = new StringBuffer();
		sb.append("eventX=" + eventX);
		sb.append("\neventY=" + eventY);

		switch (eventaction) {
		case MotionEvent.ACTION_DOWN:
			ImageView selectedPinOption = findSelectedPin(eventX, eventY);
			if (selectedPinOption == null) {
				sb.append("\nNo pin selected");
				break;
			}
			int color = (Integer) selectedPinOption.getTag();
			int xOffSetCreate = 0;
			int yOffSetCreate = 25;
			this.currentlyDraggingPin = new DragableView(this,
					selectedPinOption.getLeft() - xOffSetCreate,
					selectedPinOption.getTop() - yOffSetCreate, 10, color);

			// offset slightly after drag capture
			this.mainContainer.addView(currentlyDraggingPin);
			this.mainContainer.bringChildToFront(currentlyDraggingPin);
			break; // breaks switch
		case MotionEvent.ACTION_MOVE:
			if (this.currentlyDraggingPin != null) {
				// draw image on center of cursor
				int xOffSet = 0;
				int yOffSet = 25;
				currentlyDraggingPin.setLocation(eventX - xOffSet, eventY
						- yOffSet);
				currentlyDraggingPin.bringToFront();
				sb.append("\nDragging ");
			}
			break;
		case MotionEvent.ACTION_UP:
			if (this.currentlyDraggingPin != null) {
				GuessRowManager grm = this.guessRows.get(currentRowNum);
				if(grm != null){
					LinearLayout rowLayout = grm.getGuessRowView();
					int[] rowLocation = {0,0};
					rowLayout.getLocationOnScreen(rowLocation); //updates array x,y coordinates
					if(eventY > rowLocation[1] && eventY < rowLocation[1] + rowLayout.getHeight()){
						//up event in active row find slot spot
						SlotSpot ss = findSlotSpot(eventX, eventY, grm);
						if (ss == null) {
							sb.append("\nMissed slot");
							this.mainContainer.removeView(this.currentlyDraggingPin);
						} else {
							sb.append("\nDropped in " + ss.getSlotView().getTag());
							//update view
							ss.setColor(this.currentlyDraggingPin.getColor());
							updateSlotSpotWithDrop(ss.getColor(), ss);
							this.mainContainer.removeView(this.currentlyDraggingPin);
						}
					}else{
						sb.append("\nMissed slot");
						this.mainContainer.removeView(this.currentlyDraggingPin);
					}
				}
			} else {
				sb.append("\nNo ping selected");
			}
			this.currentlyDraggingPin = null;
			break;
		}

		this.statusMsg.setText(sb.toString());
		return true;
	}

	private void updateSlotSpotWithDrop(int color, SlotSpot ss){
		Drawable drawable = null;
		switch (color) {
		case HEX_COLOR_BLUE:
			drawable = res.getDrawable(R.drawable.bluepin);
			break;
		case HEX_COLOR_RED:
			drawable = res.getDrawable(R.drawable.redpin);
			break;
		case HEX_COLOR_GREEN:
			drawable = res.getDrawable(R.drawable.greenpin);
			break;
		case HEX_COLOR_ORANGE:
			drawable = res.getDrawable(R.drawable.orangepin);
			break;
		case HEX_COLOR_BLACK:
			drawable = res.getDrawable(R.drawable.blackpin);
			break;
		case HEX_COLOR_YELLOW:
			drawable = res.getDrawable(R.drawable.yellowpin);
			break;
		case HEX_COLOR_PURPLE:
			drawable = res.getDrawable(R.drawable.purplepin);
			break;
		}
		
		ss.getSlotView().setImageDrawable(drawable);
	}
}