package com.mastermindlive.android;

import java.util.SortedMap;
import java.util.TreeMap;

import android.view.View;
import android.widget.LinearLayout;

public class GuessRowManager {
	
	private int rowNum;
	private LinearLayout guessRowView;
	private SortedMap<Integer,SlotSpotView> slotSpots;
	private View guessResults;
	
	public GuessRowManager(int argRowNum, LinearLayout argGuessRowView){
		this.rowNum = argRowNum;
		this.guessRowView = argGuessRowView;
		this.slotSpots = new TreeMap<Integer, SlotSpotView>();
		
	}

	public View getGuessResults() {
		return guessResults;
	}

	public void setGuessResults(View guessResults) {
		this.guessResults = guessResults;
	}

	public int getRowNum() {
		return rowNum;
	}

	public void setRowNum(int rowNum) {
		this.rowNum = rowNum;
	}

	public LinearLayout getGuessRowView() {
		return guessRowView;
	}

	public void setGuessRowView(LinearLayout guessRowView) {
		this.guessRowView = guessRowView;
	}

	public SortedMap<Integer, SlotSpotView> getSlotSpots() {
		return slotSpots;
	}

	public void setSlotSpots(SortedMap<Integer, SlotSpotView> slotSpots) {
		this.slotSpots = slotSpots;
	}
	
	public boolean allSlotsFill(){
		for(Integer slotNum : this.slotSpots.keySet()){
			SlotSpotView ss = this.slotSpots.get(slotNum);
			if(ss.isEmpty())
				return false;
		}
		
		return true;
	}
}
