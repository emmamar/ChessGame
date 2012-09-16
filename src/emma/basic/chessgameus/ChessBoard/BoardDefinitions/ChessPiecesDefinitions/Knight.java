package emma.basic.chessgameus.ChessBoard.BoardDefinitions.ChessPiecesDefinitions;

import emma.basic.chessgameus.R;



public class Knight extends ChessPiece {
	public Knight(int color, int row, int column) {
		super(color, row, column);
	}

	@Override
	public int getResourceName() {
		if (color == 0) {
			return R.drawable.knight_white;
		} else {
			return R.drawable.knight_black;
		}
	}

	public boolean isLegal(ChessPiece[][] board, int startRow, int startColumn,
			int endRow, int endColumn) {
		Boolean isLegal = false;
		if (board[endRow][endColumn] != null
				&& board[endRow][endColumn].getColor() != this.color
				|| board[endRow][endColumn] == null) {
			if (Math.abs(startColumn - endColumn) == 1) {
				if (Math.abs(startRow - endRow) == 2) {
					isLegal = true;
				}
			} else if (Math.abs(startRow - endRow) == 1) {
				if (Math.abs(startColumn - endColumn) == 2) {
					isLegal = true;
				}
			}
		}
		return isLegal;
	}
}
