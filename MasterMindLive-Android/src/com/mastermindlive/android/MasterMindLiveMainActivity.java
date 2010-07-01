package com.mastermindlive.android;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

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
import com.masterminelive.core.game.GuessResponse;
import com.masterminelive.core.game.GuessResult;
import com.masterminelive.core.game.MasterMindGame;
import com.masterminelive.core.game.Piece;
import com.masterminelive.core.game.Slot;
import com.masterminelive.core.game.SlotSpot;
import com.masterminelive.core.players.Player;
import com.masterminelive.core.players.PlayerType;

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
	
	private DragableView currentlyDraggingPin;
	private TextView statusMsg;
	private ViewGroup mainContainer;
	private List<ImageView> pinOptions;
	private Resources res;
	private LinearLayout pinOptionsRow;
	private LinearLayout emptySpaceRows;
	private LinearLayout secretRow;
	
	//private int currentRowNum;
	private SortedMap<Integer, GuessRowManager> guessRows;
	private MasterMindGame mmGame = null;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.gamelayout);

		// capture user inputs, setting defaults if null
		int numColors = getIntent().getIntExtra(INTENT_ATTR_NUM_COLORS, 4);
		int numGuesses = getIntent().getIntExtra(INTENT_ATTR_NUM_GUESSES, 10);
		int numSlots = getIntent().getIntExtra(INTENT_ATTR_NUM_SLOTS, 4);
		
		//TODO really handle all combos of player and computers
		//Create one human and one computer player
		Player player1 = new Player();
		player1.setName("Player 1 Human");
		player1.setEmail("fake@email.com");
		PlayerType pt1 = new PlayerType();
		pt1.setTypeCd(PlayerType.TYPE_CD_HUMAN);
		player1.setPlayerType(pt1);
		
		//computer player will automatically setup random code/secret to guess
		Player player2 = new Player();
		player2.setName("Player 2");
		player2.setEmail("fake2@email.com");
		PlayerType pt2 = new PlayerType();
		pt2.setTypeCd(PlayerType.TYPE_CD_COMPUTER);
		player2.setPlayerType(pt2);
		
		//construct game
		this.mmGame  = new MasterMindGame(numSlots, numGuesses, numColors, player1, player2);
		
		//assume game has created code secret as computer is code creator
		//invoke the guessing state automatically
		//TODO in real live game will have to handle transition here
		this.mmGame.startCodeBreaking();
		
		//this.currentRowNum = 1;
		this.guessRows = new TreeMap<Integer, GuessRowManager>();
		
		// capture common layout components
		this.mainContainer = (ViewGroup) findViewById(R.id.structuredcontainer);
		this.statusMsg = (TextView) findViewById(R.id.notificationview);

		// capture resources
		this.res = getResources();

		// setup the pin color options the guesser can choose from
		setupPinOptions();

		// setup the empty slot spots for each guess
		setupSlotSpots();
		
		//setup secret row
		setupSecretRow();
		
		setupGuessButton();
	}

	private void setupGuessButton(){
		GuessRowManager grm = this.guessRows.get(this.mmGame.getCurrentGuessSlotNum());
		View guessResults = grm.getGuessResults();
		Button guessBtn = new Button(this);
		guessBtn.setText("Guess");
		guessBtn.setHeight(30);
		guessBtn.setWidth(55);
		guessBtn.setTextSize(12);
		guessBtn.setBackgroundColor(0xFF32FF4B);
		guessBtn.setPadding(0, 0, 0, 0);
		//guessBtn.setTextColor(0x00000000);
		guessBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				doGuess();
			}
		});
		if(guessResults != null)
			grm.getGuessRowView().removeView(guessResults);
		grm.getGuessRowView().addView(guessBtn);
		grm.setGuessResults(guessBtn);
	}
	
	public void doGuess(){
		int currentSlot= this.mmGame.getCurrentGuessSlotNum();
		GuessRowManager grm = this.guessRows.get(currentSlot);
		
		GuessResponse resp = this.mmGame.guess();
		
		//check response for errors
		if(resp.hadErrors()){
			this.statusMsg.setText(resp.getErrorMsg());
			return;
		}
		
		//remove guess button
		grm.getGuessRowView().removeView(grm.getGuessResults());
		
		/*
		Drawable guessResultsDrawable = res.getDrawable(R.drawable.guessresultsbackground);
		ImageView guessResultsView = new ImageView(this);
		guessResultsView.setImageDrawable(guessResultsDrawable);
		grm.getGuessRowView().addView(guessResultsView);
		grm.setGuessResults(guessResultsView);
		*/
		
		//Draw guess results
		//TODO draw black for right spot right color and white for right color wrong spot
		TextView tv = new TextView(this);
		Slot updatedSlot = this.mmGame.getSlot(currentSlot);
		GuessResult gr = updatedSlot.getGuessResult();
		tv.setText(gr.getResultDesc());
		grm.getGuessRowView().addView(tv);

		//check for winning guess
		if(resp.getWinningGuess()){
			//winning guess, alert user and end game
			//TODO save user stats
			//TODO change to checking game state
			this.statusMsg.setText("Correct Guess, You Win!!");
		}else{
			if(this.mmGame.getGameState() == MasterMindGame.GAME_STATE_CODE_BREAKER_RAN_OUT_OF_GUESSES){
				//end game, code breaker lost
				this.statusMsg.setText("You ran out of guesses, Game Over.");
			}else{
				//update for next guess as this guess was incorrect
				updateGameBoard();
			}

		}
	}
	
	public void updateGameBoard(){
		setupGuessButton();
	}
	private void setupSlotSpots() {
		// capture area to build up empty guess rows
		this.emptySpaceRows = (LinearLayout) findViewById(R.id.emptyspacesrows);

		for (int i = this.mmGame.getNumGuesses(); i > 0; i--) {
			// add a row for each guess
			LinearLayout guessRow = new LinearLayout(this);
			guessRow.setOrientation(LinearLayout.HORIZONTAL);
			guessRow.setLayoutParams(new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			this.emptySpaceRows.addView(guessRow);
			GuessRowManager grm = new GuessRowManager(i, guessRow);
			this.guessRows.put(i, grm);
			
			//put row number
			TextView tv = new TextView(this);
			tv.setText(i + "");
			guessRow.addView(tv);
			
			// within each guess row add empty slot spots
			for (int x = 1; x <= this.mmGame.getNumSpots(); x++) {
				Drawable emptyDrawable = res
						.getDrawable(R.drawable.emptyslotspot);
				ImageView emptySlotView = new ImageView(this);
				emptySlotView.setImageDrawable(emptyDrawable);
				emptySlotView.setTag("Row " + i + " Slot " + x);
				guessRow.addView(emptySlotView);
				SlotSpotView ss = new SlotSpotView(i, x);
				ss.setSlotView(emptySlotView);
				grm.getSlotSpots().put(x, ss);
			}
			
			/*Drawable guessResultsDrawable = res.getDrawable(R.drawable.guessresultsbackground);
			ImageView guessResultsView = new ImageView(this);
			LayoutParams lps = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			lps.setMargins(0, 0, 0, 0);
			guessResultsView.setLayoutParams(lps);
			guessResultsView.setImageDrawable(guessResultsDrawable);
			guessRow.addView(guessResultsView);
			grm.setGuessResults(guessResultsView);*/
		}
	}

	private void setupPinOptions() {
		this.pinOptionsRow = (LinearLayout) findViewById(R.id.pinoptionsrow);
		this.pinOptions = new ArrayList<ImageView>();

		TextView tv = new TextView(this);
		tv.setText("Colors: ");
		this.pinOptionsRow.addView(tv);
		
		for (Piece p : this.mmGame.getGuessOptions()) {
			switch (p.getColor()) {
			case Piece.COLOR_BLUE:
				createPinOption(R.drawable.bluepin, p);
				break;
			case Piece.COLOR_RED:
				createPinOption(R.drawable.redpin, p);
				break;
			case Piece.COLOR_GREEN:
				createPinOption(R.drawable.greenpin, p);
				break;
			case Piece.COLOR_ORANGE:
				createPinOption(R.drawable.orangepin, p);
				break;
			case Piece.COLOR_BLACK:
				createPinOption(R.drawable.blackpin, p);
				break;
			case Piece.COLOR_YELLOW:
				createPinOption(R.drawable.yellowpin, p);
				break;
			case Piece.COLOR_PURPLE:
				createPinOption(R.drawable.purplepin, p);
				break;
			}
		}

	}

	private void setupSecretRow() {
		this.secretRow = (LinearLayout) findViewById(R.id.secretrow);
		Slot secretSlot = this.mmGame.getSecretSlot();
		for (Integer slotSpot: secretSlot.getSpots().keySet()) {
			SlotSpot ss = secretSlot.getSpots().get(slotSpot);
			Piece p = ss.getPiece();
			switch (p.getColor()) {
			case Piece.COLOR_BLUE:
				createSecretPiece(R.drawable.bluepin, p);
				break;
			case Piece.COLOR_RED:
				createSecretPiece(R.drawable.redpin, p);
				break;
			case Piece.COLOR_GREEN:
				createSecretPiece(R.drawable.greenpin, p);
				break;
			case Piece.COLOR_ORANGE:
				createSecretPiece(R.drawable.orangepin, p);
				break;
			case Piece.COLOR_BLACK:
				createSecretPiece(R.drawable.blackpin, p);
				break;
			case Piece.COLOR_YELLOW:
				createSecretPiece(R.drawable.yellowpin, p);
				break;
			case Piece.COLOR_PURPLE:
				createSecretPiece(R.drawable.purplepin, p);
				break;
			}
		}

	}
	
	private void createPinOption(int drawableId, Piece piece) {
		Drawable pinDrawable = res.getDrawable(drawableId);
		ImageView pinView = new ImageView(this);
		pinView.setImageDrawable(pinDrawable);
		pinView.setTag(piece);
		this.pinOptionsRow.addView(pinView);
		this.pinOptions.add(pinView);
	}

	private void createSecretPiece(int drawableId, Piece piece) {
		Drawable pinDrawable = res.getDrawable(drawableId);
		ImageView pinView = new ImageView(this);
		pinView.setImageDrawable(pinDrawable);
		pinView.setTag(piece);
		this.secretRow.addView(pinView);
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

	public SlotSpotView findSlotSpot(int eventX, int eventY, GuessRowManager grm) {
		Rect hitRec = new Rect();
		int left;
		int top;
		int right;
		int bottom;
		int width;
		int height;
		
		for (Integer slotSpotNum : grm.getSlotSpots().keySet()) {
			SlotSpotView ss = grm.getSlotSpots().get(slotSpotNum);
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
			Piece p = (Piece) selectedPinOption.getTag();
			int xOffSetCreate = 0;
			int yOffSetCreate = 25;
			this.currentlyDraggingPin = new DragableView(this,
					selectedPinOption.getLeft() - xOffSetCreate,
					selectedPinOption.getTop() - yOffSetCreate, 10, p);

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
				GuessRowManager grm = this.guessRows.get(this.mmGame.getCurrentGuessSlotNum());
				if(grm != null){
					LinearLayout rowLayout = grm.getGuessRowView();
					int[] rowLocation = {0,0};
					rowLayout.getLocationOnScreen(rowLocation); //updates array x,y coordinates
					if(eventY > rowLocation[1] && eventY < rowLocation[1] + rowLayout.getHeight()){
						//up event in active row find slot spot
						SlotSpotView ss = findSlotSpot(eventX, eventY, grm);
						if (ss == null) {
							sb.append("\nMissed slot");
							this.mainContainer.removeView(this.currentlyDraggingPin);
						} else {
							sb.append("\nDropped in " + ss.getSlotView().getTag());
							//update view
							ss.setPiece(this.currentlyDraggingPin.getCopyOfPiece());
							this.mmGame.addGuessPiece(ss.getSlotSpotNum(), ss.getPiece());
							updateSlotSpotWithDrop(ss);
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

	private void updateSlotSpotWithDrop(SlotSpotView ss){
		Drawable drawable = null;
		int color = ss.getPiece().getColor();
		switch (color) {
		case Piece.COLOR_BLUE:
			drawable = res.getDrawable(R.drawable.bluepin);
			break;
		case Piece.COLOR_RED:
			drawable = res.getDrawable(R.drawable.redpin);
			break;
		case Piece.COLOR_GREEN:
			drawable = res.getDrawable(R.drawable.greenpin);
			break;
		case Piece.COLOR_ORANGE:
			drawable = res.getDrawable(R.drawable.orangepin);
			break;
		case Piece.COLOR_BLACK:
			drawable = res.getDrawable(R.drawable.blackpin);
			break;
		case Piece.COLOR_YELLOW:
			drawable = res.getDrawable(R.drawable.yellowpin);
			break;
		case Piece.COLOR_PURPLE:
			drawable = res.getDrawable(R.drawable.purplepin);
			break;
		}
		
		ss.getSlotView().setImageDrawable(drawable);
	}
}