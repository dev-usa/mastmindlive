package com.mastermindlive.android.dragndrop;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.masterminelive.core.game.Piece;

/**
 * DragableView has some helper members and methods to make it easy to do drag n drop
 * 
 * @author Benjamin Maisano
 */
public class DragableView extends View {
	private float x;
	private float y;
	private int r;
	private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Context cntxt;
	private Piece piece;
	
	public DragableView getCopy(){
		DragableView dvCopy = new DragableView(this.cntxt, this.x, this.y, this.r, this.piece );
		return dvCopy;
	}
	public DragableView(Context context, float x, float y, int r, Piece argPiece) {
		super(context);
		this.cntxt = context;
		this.piece = argPiece;
		mPaint.setColor(this.piece.getColor());
		mPaint.setAlpha(180); //slightly transparent
		this.x = x;
		this.y = y;
		this.r = r;
		LayoutParams lps = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		super.setLayoutParams(lps);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawCircle(x, y, r, mPaint);
	}

	public void setLocation(float newX, float newY) {
		this.x = newX;
		this.y = newY;
		this.invalidate();
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public int getR() {
		return r;
	}

	public void setR(int r) {
		this.r = r;
	}

	public Paint getmPaint() {
		return mPaint;
	}

	public void setmPaint(Paint mPaint) {
		this.mPaint = mPaint;
	}
	
	public Piece getCopyOfPiece(){
		Piece p = new Piece(this.piece);
		return p;
	}
	public Piece getPiece() {
		return piece;
	}
	public void setPiece(Piece piece) {
		this.piece = piece;
	}

	
	
	
}
