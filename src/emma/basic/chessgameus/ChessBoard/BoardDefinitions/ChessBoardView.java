package emma.basic.chessgameus.ChessBoard.BoardDefinitions;

import emma.basic.chessgameus.R;
import emma.basic.chessgameus.ChessBoard.BoardDefinitions.ChessPiecesDefinitions.ChessPiece;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

// **********************************************************************
// ChessBoardView extends view. It is used to display the chess board. It
// should be passed a BoardMatrix to draw. The BoardMatrix tells it the 
// positions of the pieces.**********************************************
// **********************************************************************
public class ChessBoardView extends View {

	private BoardMatrix boardMatrix; // chessPiece location matrix
	private Paint mPaint;										
	private Square squareSelected;
	private boolean evenTouch = false; // even touch highlighted differently

	// constructors.
	public ChessBoardView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ChessBoardView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	// sets the board
	public void setBoard(BoardMatrix boardMat) {
		boardMatrix = boardMat;
		mPaint = new Paint();
	}

	// sets the square selected
	public void setSelectedSquare(Square square) {
		squareSelected = square;
	}

	// sets whether the touch is even or not
	public void setEvenTouch(boolean evenTouch) {
		this.evenTouch = evenTouch;
	}

	// **********************************************************************
	// Overridden ondraw method of the view class. This draws the chess board
	// initially and on every call to invalidate().**************************
	// **********************************************************************
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int width = getMeasuredWidth();
		for(ChessPiece piece: boardMatrix){
		  if (piece != null) {
		    Drawable drawableInThisPosition = getResources().getDrawable(piece.getResourceName());
			Bitmap resizedBitmap = turnDrawableIntoResizedBitmap(drawableInThisPosition, false);
			float objectToDrawHeight = (piece.getSquare().getColumn()) * (width /8);
			float objectToDrawWidth = (piece.getSquare().getRow()) * (width/8);
			canvas.drawBitmap(resizedBitmap, objectToDrawHeight,
					objectToDrawWidth, mPaint);
		  }
	    }
		
		// if a square has been selected highlight the square
		if (squareSelected != null) {
			Drawable drawableSelector;
			if (evenTouch == true) {
				drawableSelector = getResources().getDrawable(
						R.drawable.square_move);
			} else {
				drawableSelector = getResources().getDrawable(
						R.drawable.square_click);
			}
			Bitmap resizedBitmap = turnDrawableIntoResizedBitmap(
					drawableSelector, true);
			float objectToDrawHeight = (squareSelected.getColumn()) * (width/8);
			float objectToDrawWidth = (squareSelected.getRow()) * (width/8);
			canvas.drawBitmap(resizedBitmap, objectToDrawHeight,
					objectToDrawWidth, mPaint);
		}
	}

	// turnDrawableIntoResizedBitmap() takes a Drawable and a boolean and
	// resizes the drawable to the size needed for the board and converts it
	// into a bitmap. Boolean selector tells the method whether it is a piece
	// image or a slector image because resize requirements are different
	// Returns a bitmap of the resized image.
	private Bitmap turnDrawableIntoResizedBitmap(Drawable drawableSelector,
			boolean selector) {
		int screenwidth = getMeasuredWidth();
		int newWidth = (screenwidth/8);
		int newHeight = (screenwidth/8);
		if (selector) {
			newWidth = (screenwidth/8);
			newHeight = (screenwidth/8);
		}
		Bitmap bitmapSelector = ((BitmapDrawable) drawableSelector).getBitmap();
		int width = bitmapSelector.getWidth();
		int height = bitmapSelector.getHeight();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmapSelector, 0, 0, width,
				height, matrix, true);
		return resizedBitmap;
	}
}
