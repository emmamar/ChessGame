package emma.basic.chessgameus.ChessBoard.BoardDefinitions;

import java.util.ArrayList;

import emma.basic.chessgameus.ChessBoard.BoardDefinitions.ChessPiecesDefinitions.ChessPiece;

public class History {
	ArrayList<BoardMatrix> history;

	public History() {
		history = new ArrayList<BoardMatrix>();
	}

	public void addToHistory(BoardMatrix board) {
		BoardMatrix copy = board.deepCopySelf();
		history.add(copy);
	}

	public void deleteLastHistory() {
		history.remove(history.size() - 1);
	}

	public ArrayList<BoardMatrix> getHistory() {
		return history;
	}

	public BoardMatrix getLastInHistory() {
		BoardMatrix copyOfBoard = history.get(history.size() - 1).deepCopySelf();
		return copyOfBoard;
	}

	public int getSize() {
		return history.size();
	}
	
	public String[] toStringArray(){
		String[] str = new String[history.size()];
		for(int i = 0; i < str.length; i++){
		    str[i] = history.get(i).toString();
		}
		return str;
	}

}
