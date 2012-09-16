package emma.basic.chessgameus.ChessBoard.BoardDefinitions.ChessPiecesDefinitions;

import emma.basic.chessgameus.ChessBoard.BoardDefinitions.Square;

public abstract class ChessPiece {
	int color;
	Square square;

	public ChessPiece(int color, int row, int column) {
		this.color = color;
		square = new Square();
		square.setRow(row);
		square.setColumn(column);
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

	public abstract int getResourceName();

	public abstract boolean isLegal(ChessPiece[][] board, int startRow,
			int startColumn, int endRow, int endColumn);
}
