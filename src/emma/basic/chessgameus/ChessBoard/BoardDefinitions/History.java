package emma.basic.chessgameus.ChessBoard.BoardDefinitions;

import java.util.ArrayList;

import emma.basic.chessgameus.ChessBoard.BoardDefinitions.ChessPiecesDefinitions.ChessPiece;

public class History {
	ArrayList<ChessPiece[][]> history;

	public History() {
		history = new ArrayList<ChessPiece[][]>();
	}

	public void addToHistory(BoardMatrix board) {
		ChessPiece[][] copy = new ChessPiece[8][8];
		for (int i = 0; i < 8; i++) {
			System.arraycopy(board.getBoard()[i], 0, copy[i], 0, 8);
		}
		history.add(copy);
	}

	public void deleteLastHistory() {
		history.remove(history.size() - 1);
	}

	public ArrayList<ChessPiece[][]> getHistory() {
		return history;
	}

	public ChessPiece[][] getLastInHistory() {
		ChessPiece[][] copyOfBoard = new ChessPiece[8][8];
		for (int i = 0; i < 8; i++) {
			System.arraycopy(history.get(history.size() - 1)[i], 0,
					copyOfBoard[i], 0, 8);
		}
		return copyOfBoard;
	}

	public int getSize() {
		return history.size();
	}

}
