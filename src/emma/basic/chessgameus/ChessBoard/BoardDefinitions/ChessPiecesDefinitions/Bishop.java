package emma.basic.chessgameus.ChessBoard.BoardDefinitions.ChessPiecesDefinitions;

import emma.basic.chessgameus.R;
import emma.basic.chessgameus.ChessBoard.BoardDefinitions.BoardMatrix;
import emma.basic.chessgameus.ChessBoard.BoardDefinitions.Square;

public class Bishop extends ChessPiece {
	public Bishop(int color, Square squ) {
		super(color, squ);
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
	public boolean isLegal(BoardMatrix boardMat, Square startSqu, Square endSqu){
		Boolean isLegal = false;
		if (boardMat.getPieceAt(endSqu) != null
				&& boardMat.getPieceAt(endSqu).getColor() != this.color
				|| boardMat.getPieceAt(endSqu) == null) {
			if (startSqu.getColumn() != endSqu.getColumn()) {
				if (Math.abs(startSqu.getRow() - endSqu.getRow()) == Math.abs(startSqu.getColumn()
						- endSqu.getColumn())) {
					isLegal = true;
					if (startSqu.getRow() > endSqu.getRow()) {
						if (startSqu.getColumn() > endSqu.getColumn()) {
							int columnDecrementer = startSqu.getColumn() - 1;
							for (int i = startSqu.getRow() - 1; i > endSqu.getRow(); i--) {
								if (boardMat.getPieceAt(new Square(i, columnDecrementer)) != null) {
									isLegal = false;
								}
								columnDecrementer--;
							}
						} else {
							int columnIncrementer = startSqu.getColumn() + 1;
							for (int i = startSqu.getRow() - 1; i > endSqu.getRow(); i--) {
								if (boardMat.getPieceAt(new Square(i,columnIncrementer)) != null) {
									isLegal = false;
								}
								columnIncrementer++;
							}
						}
					} else {

						if (startSqu.getColumn() > endSqu.getColumn()) {
							int columnDecrementer = startSqu.getColumn() - 1;
							for (int i = startSqu.getRow() + 1; i < endSqu.getRow(); i++) {
								if (boardMat.getPieceAt(new Square(i, columnDecrementer)) != null) {
									isLegal = false;
								}
								columnDecrementer--;
							}
						} else {
							int columnIncrementer = startSqu.getColumn() + 1;
							for (int i = startSqu.getRow() + 1; i < endSqu.getRow(); i++) {
								if (boardMat.getPieceAt(new Square(i, columnIncrementer)) != null) {
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
