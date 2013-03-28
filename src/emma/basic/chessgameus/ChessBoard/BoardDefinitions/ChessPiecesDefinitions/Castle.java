package emma.basic.chessgameus.ChessBoard.BoardDefinitions.ChessPiecesDefinitions;

import emma.basic.chessgameus.R;
import emma.basic.chessgameus.ChessBoard.BoardDefinitions.BoardMatrix;
import emma.basic.chessgameus.ChessBoard.BoardDefinitions.Square;



public class Castle extends ChessPiece {
	public Castle(int color, Square squ) {
		super(color, squ);
	}

	public int getResourceName() {
		if (color == 0) {
			return R.drawable.castle_white;
		} else {
			return R.drawable.castle_black;
		}
	}

	@Override
	public boolean isLegal(BoardMatrix boardMat, Square startSqu, Square endSqu) {
		Boolean isLegal = false;
		if (boardMat.getPieceAt(endSqu) != null
				&& boardMat.getPieceAt(endSqu).getColor() != this.color
				|| boardMat.getPieceAt(endSqu) == null) {
			if (startSqu.getColumn() == endSqu.getColumn()) {
				isLegal = true;
				if (startSqu.getRow() < endSqu.getRow()) {
					for (int i = startSqu.getRow() + 1; i < endSqu.getRow(); i++) {
						if (boardMat.getPieceAt(new Square(i, startSqu.getColumn())) != null) {
							isLegal = false;
						}
					}
				} else {
					for (int i = startSqu.getRow() - 1; i > endSqu.getRow(); i--) {
						if (boardMat.getPieceAt(new Square(i, startSqu.getColumn())) != null) {
							isLegal = false;
						}
					}
				}
			} else if (startSqu.getRow() == endSqu.getRow()) {
				isLegal = true;
				if (startSqu.getColumn() < endSqu.getColumn()) {
					for (int i = startSqu.getColumn() + 1; i < endSqu.getColumn(); i++) {
						if (boardMat.getPieceAt(new Square(startSqu.getRow(), i)) != null) {
							isLegal = false;
						}
					}
				} else {
					for (int i = startSqu.getColumn() - 1; i > endSqu.getColumn(); i--) {
						if (boardMat.getPieceAt(new Square(startSqu.getRow(), i)) != null) {
							isLegal = false;
						}
					}
				}
			}
		}
		return isLegal;

	}
	
	@Override
	public ChessPiece deepCopy() {
		return new Castle(color, new Square(square.getRow(), square.getColumn()));
	}
}
