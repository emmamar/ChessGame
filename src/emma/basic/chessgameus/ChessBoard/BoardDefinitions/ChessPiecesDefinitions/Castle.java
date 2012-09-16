package emma.basic.chessgameus.ChessBoard.BoardDefinitions.ChessPiecesDefinitions;

import emma.basic.chessgameus.R;



public class Castle extends ChessPiece {
	public Castle(int color, int row, int column) {
		super(color, row, column);
	}

	public int getResourceName() {
		if (color == 0) {
			return R.drawable.castle_white;
		} else {
			return R.drawable.castle_black;
		}
	}

	@Override
	public boolean isLegal(ChessPiece[][] board, int startRow, int startColumn,
			int endRow, int endColumn) {
		Boolean isLegal = false;
		if (board[endRow][endColumn] != null
				&& board[endRow][endColumn].getColor() != this.color
				|| board[endRow][endColumn] == null) {
			if (startColumn == endColumn) {
				isLegal = true;
				if (startRow < endRow) {
					for (int i = startRow + 1; i < endRow; i++) {
						if (board[i][startColumn] != null) {
							isLegal = false;
						}
					}
				} else {
					for (int i = startRow - 1; i > endRow; i--) {
						if (board[i][startColumn] != null) {
							isLegal = false;
						}
					}
				}
			} else if (startRow == endRow) {
				isLegal = true;
				if (startColumn < endColumn) {
					for (int i = startColumn + 1; i < endColumn; i++) {
						if (board[startRow][i] != null) {
							isLegal = false;
						}
					}
				} else {
					for (int i = startColumn - 1; i > endColumn; i--) {
						if (board[startRow][i] != null) {
							isLegal = false;
						}
					}
				}
			}
		}
		return isLegal;

	}
}
