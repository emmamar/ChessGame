package emma.basic.chessgameus.ChessBoard.BoardDefinitions.ChessPiecesDefinitions;

import emma.basic.chessgameus.R;
import emma.basic.chessgameus.ChessBoard.BoardDefinitions.BoardMatrix;
import emma.basic.chessgameus.ChessBoard.BoardDefinitions.Square;



public class Knight extends ChessPiece {
	public Knight(int color, Square squ) {
		super(color, squ);
	}

	@Override
	public int getResourceName() {
		if (color == 0) {
			return R.drawable.knight_white;
		} else {
			return R.drawable.knight_black;
		}
	}

	public boolean isLegal(BoardMatrix boardMat, Square startSqu, Square endSqu) {
		Boolean isLegal = false;
		if (boardMat.getPieceAt(endSqu) != null
				&& boardMat.getPieceAt(endSqu).getColor() != this.color
				|| boardMat.getPieceAt(endSqu) == null) {
			if (Math.abs(startSqu.getColumn() - endSqu.getColumn()) == 1) {
				if (Math.abs(startSqu.getRow() - endSqu.getRow()) == 2) {
					isLegal = true;
				}
			} else if (Math.abs(startSqu.getRow() - endSqu.getRow()) == 1) {
				if (Math.abs(startSqu.getColumn() - endSqu.getColumn()) == 2) {
					isLegal = true;
				}
			}
		}
		return isLegal;
	}
}
