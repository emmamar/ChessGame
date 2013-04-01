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
			if (startSqu.getColumn() == endSqu.getColumn() + 1 || startSqu.getColumn() == endSqu.getColumn() - 1
					|| startSqu.getColumn() == endSqu.getColumn()) {
				if (startSqu.getRow() == endSqu.getRow() + 1 || startSqu.getRow() == endSqu.getRow() - 1
						|| startSqu.getRow() == endSqu.getRow()) {
					isLegal = true;
				}
			}
		}
		return isLegal;
	}
	@Override
	public ChessPiece deepCopy() {
		return new King(color, new Square(square.getRow(), square.getColumn()));
	}
	
	public String toString(){
		if(color == 1){
			return "KB";
		}
		else{
			return "KW";
		}
	}
}
