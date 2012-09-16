package emma.basic.chessgameus.ChessBoard.BoardDefinitions.ChessPiecesDefinitions;

import emma.basic.chessgameus.R;



public class Pawn extends ChessPiece {
	public Pawn(int color, int row, int column) {
		super(color, row, column);
	}

	@Override
	public int getResourceName() {
		if (color == 0) {
			return R.drawable.pawn_white;
		} else {
			return R.drawable.pawn_black;
		}
	}

	@Override
	public boolean isLegal(ChessPiece[][] board, int startRow, int startColumn,
			int endRow, int endColumn) {
		Boolean isLegal = false;
		if (board[endRow][endColumn] != null
				&& board[endRow][endColumn].getColor() != this.color
				|| board[endRow][endColumn] == null) {
			if (color == 0) {// white
				if (startColumn == endColumn) {
					if (startRow == 6 && endRow == 4 || startRow == endRow + 1) {
						if (startRow == 6 && endRow == 4) {

							if (board[endRow][endColumn] == null
									&& board[endRow + 1][endColumn] == null) {
								isLegal = true;
							}
						} else {
							if (board[endRow][endColumn] == null) {
								isLegal = true;
							}
						}
					}
				} else if (startColumn == endColumn - 1
						&& startRow == endRow + 1
						|| startColumn == endColumn + 1
						&& startRow == endRow + 1) {
					if (board[endRow][endColumn] != null) {
						isLegal = true;
					}

				}

			} else {// black
				if (startColumn == endColumn) {
					if (startRow == 1 && endRow == 3 || startRow == endRow - 1) {
						if (startRow == 1 && endRow == 3) {

							if (board[endRow][endColumn] == null
									&& board[endRow - 1][endColumn] == null) {
								isLegal = true;
							}
						} else {
							if (board[endRow][endColumn] == null) {
								isLegal = true;
							}
						}
					}
				} else if (startColumn == endColumn - 1
						&& startRow == endRow - 1
						|| startColumn == endColumn + 1
						&& startRow == endRow - 1) {
					if (board[endRow][endColumn] != null) {
						isLegal = true;
					}
				}

			}
		}
		return isLegal;
	}

}
