package emma.basic.chessgameus.ChessBoard.BoardDefinitions.ChessPiecesDefinitions;

import emma.basic.chessgameus.ChessBoard.BoardDefinitions.BoardMatrix;
import emma.basic.chessgameus.ChessBoard.BoardDefinitions.Square;

public abstract class ChessPiece {
	int color;
	Square square;
	String pieceStr;

	public ChessPiece(int color, Square squ) {
		this.color = color;
		square = new Square();
		square.setRow(squ.getRow());
		square.setColumn(squ.getColumn());
	}
	
	public ChessPiece(String str){
		pieceStr = str;
	}

	public int getColor() {
		return color;
	}

	public Square getSquare() {
		return square;
	}

	public void setSquare(int row, int column) {
		square.setRow(row);
		square.setColumn(column);
	}
	
	public void setSquare(Square sq){
		square.setRow(sq.getRow());
		square.setColumn(sq.getColumn());
	}
	
	
	
	public abstract String toString();

	public abstract ChessPiece deepCopy();
	
	public abstract int getResourceName();

	public abstract boolean isLegal(BoardMatrix boardMat, Square startSqu, Square endSqu);

}
