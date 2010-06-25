package com.mastermindlive.android;

import android.widget.ImageView;

public class SlotSpot {

	private int color;
	private ImageView slotView;
	private int rowNum;
	private int slotSpotNum;
	
	public SlotSpot(int argRowNum, int argSlotSpotNum){
		this.rowNum = argRowNum;
		this.slotSpotNum = argSlotSpotNum;
		
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public ImageView getSlotView() {
		return slotView;
	}

	public void setSlotView(ImageView slotView) {
		this.slotView = slotView;
	}

	public int getRowNum() {
		return rowNum;
	}

	public int getSlotSpotNum() {
		return slotSpotNum;
	}
	
	
}
