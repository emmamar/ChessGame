package emma.basic.chessgameus.ChessBoard.BoardDefinitions.ChessPiecesDefinitions;

import emma.basic.chessgameus.R;
import emma.basic.chessgameus.ChessBoard.BoardDefinitions.BoardMatrix;
import emma.basic.chessgameus.ChessBoard.BoardDefinitions.Square;

public class King extends ChessPiece {

	public King(int color, Square squ) {
		super(color, squ);
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
	public boolean isLegal(BoardMatrix boardMat, Square startSqu, Square endSqu) {
		Boolean isLegal = false;
		if (boardMat.getPieceAt(endSqu) != null
				&& boardMat.getPieceAt(endSqu).getColor() != this.color
				|| boardMat.getPieceAt(endSqu) == null) {
			if (startSqu.getColumn() == endSqu.getColumn() + 1
					|| startSqu.getColumn() == endSqu.getColumn() - 1
					|| startSqu.getColumn() == endSqu.getColumn()) {
				if (startSqu.getRow() == endSqu.getRow() + 1
						|| startSqu.getRow() == endSqu.getRow() - 1
						|| startSqu.getRow() == endSqu.getRow()) {
					isLegal = true;
				}
			}
			if (!getMoved()) {
				if (color == 0) {
					if (startSqu.getRow() == 7 && endSqu.getRow() == 7) {
						if (endSqu.getColumn() == 2) {
							if (boardMat.getPieceAt(7, 1) == null
									&& boardMat.getPieceAt(7, 2) == null
									&& boardMat.getPieceAt(7, 3) == null) {
								if (boardMat.getPieceAt(7, 0) instanceof Castle
										&& boardMat.getPieceAt(7, 0).getMoved() == false) {
									isLegal = true;
								}
							}
						}
						if (endSqu.getColumn() == 6) {
							if (boardMat.getPieceAt(7, 5) == null
									&& boardMat.getPieceAt(7, 6) == null) {
								if (boardMat.getPieceAt(7, 7) instanceof Castle
										&& boardMat.getPieceAt(7, 7).getMoved() == false) {
									isLegal = true;
								}
							}
						}
					}
				} else {
					if (startSqu.getRow() == 0 && endSqu.getRow() == 0) {
						if (endSqu.getColumn() == 2) {
							if (boardMat.getPieceAt(0, 1) == null
									&& boardMat.getPieceAt(0, 2) == null
									&& boardMat.getPieceAt(0, 3) == null) {
								if (boardMat.getPieceAt(0, 0) instanceof Castle
										&& boardMat.getPieceAt(0, 0).getMoved() == false) {
									isLegal = true;
								}
							}
						}
						if (endSqu.getColumn() == 6) {
							if (boardMat.getPieceAt(0, 5) == null
									&& boardMat.getPieceAt(0, 6) == null) {
								if (boardMat.getPieceAt(0, 7) instanceof Castle
										&& boardMat.getPieceAt(0, 7).getMoved() == false) {
									isLegal = true;
								}
							}
						}
					}
				}
			}
		}
		return isLegal;
	}

	@Override
	public ChessPiece deepCopy() {
		return new King(color, new Square(square.getRow(), square.getColumn()));
	}

	public String toString() {
		if (color == 1) {
			return "KB";
		} else {
			return "KW";
		}
	}
}
