package emma.basic.chessgameus.ChessBoard.BoardDefinitions.ChessPiecesDefinitions;

import emma.basic.chessgameus.R;
import emma.basic.chessgameus.ChessBoard.BoardDefinitions.BoardMatrix;
import emma.basic.chessgameus.ChessBoard.BoardDefinitions.Square;



public class Pawn extends ChessPiece {
	public Pawn(int color, Square squ) {
		super(color, squ);
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
	public boolean isLegal(BoardMatrix boardMat, Square startSqu, Square endSqu) {
		Boolean isLegal = false;
		if (boardMat.getPieceAt(endSqu) != null
				&& boardMat.getPieceAt(endSqu).getColor() != this.color
				|| boardMat.getPieceAt(endSqu) == null) {
			if (color == 0) {// white
				if (startSqu.getColumn() == endSqu.getColumn()) {
					if (startSqu.getRow() == 6 && endSqu.getRow() == 4 || startSqu.getRow() == endSqu.getRow() + 1) {
						if (startSqu.getRow() == 6 && endSqu.getRow() == 4) {

							if (boardMat.getPieceAt(endSqu) == null
									&& boardMat.getPieceAt(
											new Square(endSqu.getRow() + 1, endSqu.getColumn())) == null) {
								isLegal = true;
							}
						} else {
							if (boardMat.getPieceAt(endSqu) == null) {
								isLegal = true;
							}
						}
					}
				} else if (startSqu.getColumn() == endSqu.getColumn() - 1
						&& startSqu.getRow() == endSqu.getRow() + 1
						|| startSqu.getColumn() == endSqu.getColumn() + 1
						&& startSqu.getRow() == endSqu.getRow() + 1) {
					if (boardMat.getPieceAt(endSqu) != null) {
						isLegal = true;
					}

				}

			} else {// black
				if (startSqu.getColumn() == endSqu.getColumn()) {
					if (startSqu.getRow() == 1 && endSqu.getRow() == 3 || startSqu.getRow() == endSqu.getRow() - 1) {
						if (startSqu.getRow() == 1 && endSqu.getRow() == 3) {

							if (boardMat.getPieceAt(endSqu) == null
									&& boardMat.getPieceAt(
											new Square(endSqu.getRow() - 1, endSqu.getColumn())) == null) {
								isLegal = true;
							}
						} else {
							if (boardMat.getPieceAt(endSqu) == null) {
								isLegal = true;
							}
						}
					}
				} else if (startSqu.getColumn() == endSqu.getColumn() - 1
						&& startSqu.getRow() == endSqu.getRow() - 1
						|| startSqu.getColumn() == endSqu.getColumn() + 1
						&& startSqu.getRow() == endSqu.getRow() - 1) {
					if (boardMat.getPieceAt(endSqu) != null) {
						isLegal = true;
					}
				}

			}
		}
		return isLegal;
	}
	
	@Override
	public ChessPiece deepCopy() {
		return new Pawn(color, new Square(square.getRow(), square.getColumn()));
	}
}
