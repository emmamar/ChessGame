package emma.basic.chessgameus.ChessBoard.BoardDefinitions.ChessPiecesDefinitions;

import emma.basic.chessgameus.R;
import emma.basic.chessgameus.ChessBoard.BoardDefinitions.BoardMatrix;
import emma.basic.chessgameus.ChessBoard.BoardDefinitions.Square;



public class Queen extends ChessPiece {
	public Queen(int color, Square squ) {
		super(color, squ);
	}

	@Override
	public int getResourceName() {
		if (color == 0) {
			return R.drawable.queen_white;
		} else {
			return R.drawable.queen_black;
		}
	}

	@Override
	public boolean isLegal(BoardMatrix boardMat, Square startSqu, Square endSqu) {
		Boolean isLegal = false;
		if (boardMat.getPieceAt(endSqu) != null
				&& boardMat.getPieceAt(endSqu).getColor() != this.color
				|| boardMat.getPieceAt(endSqu) == null) {
			// acts like bishop
			if (startSqu.getColumn() != endSqu.getColumn()) {
				if (Math.abs(startSqu.getRow() - endSqu.getRow()) == Math.abs(startSqu.getColumn()
						- endSqu.getColumn())) {
					isLegal = true;
					if (startSqu.getRow() > endSqu.getRow()) {
						if (startSqu.getColumn() > endSqu.getColumn()) {
							int columnDecrementer = startSqu.getColumn() - 1;
							for (int i = startSqu.getRow() - 1; i > endSqu.getRow(); i--) {
								if (boardMat.getPieceAt(new Square(false, i, columnDecrementer)) != null) {
									isLegal = false;
								}
								columnDecrementer--;
							}
						} else {
							int columnIncrementer = startSqu.getColumn() + 1;
							for (int i = startSqu.getRow() - 1; i > endSqu.getRow(); i--) {
								if (boardMat.getPieceAt(new Square(false, i, columnIncrementer)) != null) {
									isLegal = false;
								}
								columnIncrementer++;
							}
						}
					} else {

						if (startSqu.getColumn() > endSqu.getColumn()) {
							int columnDecrementer = startSqu.getColumn() - 1;
							for (int i = startSqu.getRow() + 1; i < endSqu.getRow(); i++) {
								if (boardMat.getPieceAt(new Square(false, i, columnDecrementer)) != null) {
									isLegal = false;
								}
								columnDecrementer--;
							}
						} else {
							int columnIncrementer = startSqu.getColumn() + 1;
							for (int i = startSqu.getRow() + 1; i < endSqu.getRow(); i++) {
								if (boardMat.getPieceAt(new Square(false, i, columnIncrementer)) != null) {
									isLegal = false;
								}
								columnIncrementer++;
							}
						}
					}

				}
			}
			// or acts like castle
			if (startSqu.getColumn() == endSqu.getColumn()) {
				isLegal = true;
				if (startSqu.getRow() < endSqu.getRow()) {
					for (int i = startSqu.getRow() + 1; i < endSqu.getRow(); i++) {
						if (boardMat.getPieceAt(new Square(false, i, startSqu.getColumn())) != null) {
							isLegal = false;
						}
					}
				} else {
					for (int i = startSqu.getRow() - 1; i > endSqu.getRow(); i--) {
						if (boardMat.getPieceAt(new Square(false, i, startSqu.getColumn())) != null) {
							isLegal = false;
						}
					}
				}
			} else if (startSqu.getRow() == endSqu.getRow()) {
				isLegal = true;
				if (startSqu.getColumn() < endSqu.getColumn()) {
					for (int i = startSqu.getColumn() + 1; i < endSqu.getColumn(); i++) {
						if (boardMat.getPieceAt(new Square(false, startSqu.getRow(), i)) != null) {
							isLegal = false;
						}
					}
				} else {
					for (int i = startSqu.getColumn() - 1; i > endSqu.getColumn(); i--) {
						if (boardMat.getPieceAt(new Square(false, startSqu.getRow(),i)) != null) {
							isLegal = false;
						}
					}
				}
			}
		}
		return isLegal;

	}

}
