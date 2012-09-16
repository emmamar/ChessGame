package emma.basic.chessgameus.ChessBoard.BoardDefinitions.ChessPiecesDefinitions;

import emma.basic.chessgameus.R;



public class King extends ChessPiece {
	public King(int color, int row, int column) {
		super(color, row, column);
	}

	@Override
	public int getResourceName() {
		if (color == 0) {
			return R.drawable.king_white;
		} else {
			return R.drawable.king_black;
		}
	}

	@Override
	public boolean isLegal(ChessPiece[][] board, int startRow, int startColumn,
			int endRow, int endColumn) {
		Boolean isLegal = false;
		if (board[endRow][endColumn] != null
				&& board[endRow][endColumn].getColor() != this.color
				|| board[endRow][endColumn] == null) {
			if (startColumn == endColumn + 1 || startColumn == endColumn - 1
					|| startColumn == endColumn) {
				if (startRow == endRow + 1 || startRow == endRow - 1
						|| startRow == endRow) {
					isLegal = true;
				}
			}
		}
		return isLegal;
	}

}
