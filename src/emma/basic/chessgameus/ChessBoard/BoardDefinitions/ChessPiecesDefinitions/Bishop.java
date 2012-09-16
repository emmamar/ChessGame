package emma.basic.chessgameus.ChessBoard.BoardDefinitions.ChessPiecesDefinitions;

import emma.basic.chessgameus.R;

public class Bishop extends ChessPiece {
	public Bishop(int color, int row, int column) {
		super(color, row, column);
	}

	@Override
	public int getResourceName() {
		if (color == 0) {
			return R.drawable.bishop_white;
		} else {
			return R.drawable.bishop_black;
		}
	}

	@Override
	public boolean isLegal(ChessPiece[][] board, int startRow, int startColumn,
			int endRow, int endColumn) {
		Boolean isLegal = false;
		if (board[endRow][endColumn] != null
				&& board[endRow][endColumn].getColor() != this.color
				|| board[endRow][endColumn] == null) {
			if (startColumn != endColumn) {
				if (Math.abs(startRow - endRow) == Math.abs(startColumn
						- endColumn)) {
					isLegal = true;
					if (startRow > endRow) {
						if (startColumn > endColumn) {
							int columnDecrementer = startColumn - 1;
							for (int i = startRow - 1; i > endRow; i--) {
								if (board[i][columnDecrementer] != null) {
									isLegal = false;
								}
								columnDecrementer--;
							}
						} else {
							int columnIncrementer = startColumn + 1;
							for (int i = startRow - 1; i > endRow; i--) {
								if (board[i][columnIncrementer] != null) {
									isLegal = false;
								}
								columnIncrementer++;
							}
						}
					} else {

						if (startColumn > endColumn) {
							int columnDecrementer = startColumn - 1;
							for (int i = startRow + 1; i < endRow; i++) {
								if (board[i][columnDecrementer] != null) {
									isLegal = false;
								}
								columnDecrementer--;
							}
						} else {
							int columnIncrementer = startColumn + 1;
							for (int i = startRow + 1; i < endRow; i++) {
								if (board[i][columnIncrementer] != null) {
									isLegal = false;
								}
								columnIncrementer++;
							}
						}
					}

				}

			}
		}
		return isLegal;
	}

}
