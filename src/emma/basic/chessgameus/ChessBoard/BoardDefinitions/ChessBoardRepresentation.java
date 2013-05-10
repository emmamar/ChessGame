package emma.basic.chessgameus.ChessBoard.BoardDefinitions;

import java.util.ArrayList;
import java.util.Iterator;

import emma.basic.chessgameus.ChessBoard.BoardDefinitions.ChessPiecesDefinitions.Bishop;
import emma.basic.chessgameus.ChessBoard.BoardDefinitions.ChessPiecesDefinitions.Castle;
import emma.basic.chessgameus.ChessBoard.BoardDefinitions.ChessPiecesDefinitions.ChessPiece;
import emma.basic.chessgameus.ChessBoard.BoardDefinitions.ChessPiecesDefinitions.King;
import emma.basic.chessgameus.ChessBoard.BoardDefinitions.ChessPiecesDefinitions.Knight;
import emma.basic.chessgameus.ChessBoard.BoardDefinitions.ChessPiecesDefinitions.Pawn;
import emma.basic.chessgameus.ChessBoard.BoardDefinitions.ChessPiecesDefinitions.Queen;

public class ChessBoardRepresentation implements Iterable<ChessPiece> {
	private ArrayList<ChessPiece> longBoard;
	private ChessPiece kingBlackReference;
	private ChessPiece kingWhiteReference;
	private ArrayList<ChessPiece> takenBlack;
	private ArrayList<ChessPiece> takenWhite;

	public ChessBoardRepresentation() {
		restartGame();
	}

	public ChessBoardRepresentation(ArrayList<ChessPiece> bor,
			ChessPiece kingB, ChessPiece kingW, ArrayList<ChessPiece> takenB,
			ArrayList<ChessPiece> takenW) {
		longBoard = bor;
		kingBlackReference = kingB;
		kingWhiteReference = kingW;
		takenBlack = takenB;
		takenWhite = takenW;
	}

	public ChessBoardRepresentation(String[] str) {
		takenBlack = new ArrayList<ChessPiece>(16);
		takenWhite = new ArrayList<ChessPiece>(16);
		longBoard = new ArrayList<ChessPiece>(64);

		String[] rows = str[0].split("\n");
		for (int i = 0; i < rows.length; i++) {
			String[] pieces = rows[i].split(" ");
			for (int j = 0; j < pieces.length; j++) {
				ChessPiece pieceFromString = convertStringToPiece(pieces[j], i,
						j);
				longBoard.add(pieceFromString);
			}
		}
		if (!str[1].equals("")) {
			String[] piecesB = str[1].split(" ");
			for (int i = 0; i < piecesB.length; i++) {
				ChessPiece pieceFromString = convertStringToPiece(piecesB[i],
						-1, -1);
				takenBlack.add(pieceFromString);
			}
		}
		if (!str[2].equals("")) {
			String[] piecesW = str[2].split(" ");
			for (int i = 0; i < piecesW.length; i++) {
				ChessPiece pieceFromString = convertStringToPiece(piecesW[i],
						-1, -1);
				takenWhite.add(pieceFromString);
			}
		}

	}

	private ChessPiece convertStringToPiece(String pieceString, int row,
			int column) {
		int color = 0;
		if (pieceString.substring(1).equals("B")) {
			color = 1;
		}
		if (pieceString.substring(0, 1).equals("B")) {
			return new Bishop(color, new Square(row, column));
		} else if (pieceString.substring(0, 1).equals("K")) {
			if (row == -1) {
				return new King(color, new Square(row, column));
			} else {
				if (color == 1) {
					kingBlackReference = new King(color,
							new Square(row, column));
					return kingBlackReference;
				} else {
					kingWhiteReference = new King(color,
							new Square(row, column));
					return kingWhiteReference;
				}
			}
		} else if (pieceString.substring(0, 1).equals("Q")) {
			return new Queen(color, new Square(row, column));
		} else if (pieceString.substring(0, 1).equals("N")) {
			return new Knight(color, new Square(row, column));
		} else if (pieceString.substring(0, 1).equals("P")) {
			return new Pawn(color, new Square(row, column));
		} else if (pieceString.substring(0, 1).equals("C")) {
			return new Castle(color, new Square(row, column));
		} else {
			return null;
		}

	}

	public void restartGame() {
		takenBlack = new ArrayList<ChessPiece>(16);
		takenWhite = new ArrayList<ChessPiece>(16);

		longBoard = new ArrayList<ChessPiece>(64);
		// set up board
		longBoard.add(0, new Castle(1, new Square(0, 0)));
		longBoard.add(1, new Knight(1, new Square(0, 1)));
		longBoard.add(2, new Bishop(1, new Square(0, 2)));
		longBoard.add(3, new Queen(1, new Square(0, 3)));
		kingBlackReference = new King(1, new Square(0, 4));
		longBoard.add(4, kingBlackReference);
		longBoard.add(5, new Bishop(1, new Square(0, 5)));
		longBoard.add(6, new Knight(1, new Square(0, 6)));
		longBoard.add(7, new Castle(1, new Square(0, 7)));
		for (int i = 8; i < 16; i++) {
			longBoard.add(i, new Pawn(1, new Square(1, (i - 8))));
		}
		for (int i = 16; i < 48; i++) {
			longBoard.add(i, null);
		}
		for (int i = 48; i < 56; i++) {
			longBoard.add(i, new Pawn(0, new Square(6, (i - 48))));
		}
		longBoard.add(56, new Castle(0, new Square(7, 0)));
		longBoard.add(57, new Knight(0, new Square(7, 1)));
		longBoard.add(58, new Bishop(0, new Square(7, 2)));
		longBoard.add(59, new Queen(0, new Square(7, 3)));
		kingWhiteReference = new King(0, new Square(7, 4));
		longBoard.add(60, kingWhiteReference);
		longBoard.add(61, new Bishop(0, new Square(7, 5)));
		longBoard.add(62, new Knight(0, new Square(7, 6)));
		longBoard.add(63, new Castle(0, new Square(7, 7)));

	}

	public ArrayList<ChessPiece> getLongBoard() {
		return longBoard;
	}
	
	public ChessPiece getPieceAt(int row, int column) {
		return longBoard.get((row * 8) + column);
	}

	public void setPieceAt(int row, int column, ChessPiece piece) {
		longBoard.set(((row * 8) + column), piece);
	}

	public ChessPiece getPieceAt(Square squ) {
		return longBoard.get((squ.getRow() * 8) + squ.getColumn());
	}

	public void setPieceAt(Square squ, ChessPiece piece) {
		longBoard.set(((squ.getRow() * 8) + squ.getColumn()), piece);
	}

	public ChessPiece getKingBlackReference() {
		return kingBlackReference;
	}

	public ChessPiece getKingWhiteReference() {
		return kingWhiteReference;
	}

	public void setKingBlackReference(ChessPiece king) {
		kingBlackReference = king;
	}

	public void setKingWhiteReference(ChessPiece king) {
		kingWhiteReference = king;
	}

	public ArrayList<ChessPiece> getBlackTaken() {
		return takenBlack;
	}

	public ArrayList<ChessPiece> getWhiteTaken() {
		return takenWhite;
	}

	public void addTakenBlack(ChessPiece piece) {
		takenBlack.add(piece);
	}

	public void addTakenWhite(ChessPiece piece) {
		takenWhite.add(piece);
	}

	public ChessBoardRepresentation deepCopy() {
		ArrayList<ChessPiece> longBoardCopy = new ArrayList<ChessPiece>(64);
		ChessPiece newKingReferenceBlack = null;
		ChessPiece newKingReferenceWhite = null;
		for (int i = 0; i < longBoard.size(); i++) {
			if (longBoard.get(i) != null) {
				longBoardCopy.add(longBoard.get(i).deepCopy());
				if (longBoard.get(i) instanceof King) {
					if (longBoard.get(i).getColor() == 1) {
						newKingReferenceBlack = longBoard.get(i);
					} else {
						newKingReferenceWhite = longBoard.get(i);
					}
				}
			} else {
				longBoardCopy.add(null);
			}
		}
		ArrayList<ChessPiece> takenB = new ArrayList<ChessPiece>(16);
		for (ChessPiece piece : takenBlack) {
			takenB.add(piece.deepCopy());
		}
		ArrayList<ChessPiece> takenW = new ArrayList<ChessPiece>(16);
		for (ChessPiece piece : takenWhite) {
			takenW.add(piece.deepCopy());
		}

		return new ChessBoardRepresentation(longBoardCopy,
				newKingReferenceBlack, newKingReferenceWhite, takenB, takenW);
	}

	public Iterator<ChessPiece> iterator() {
		Iterator<ChessPiece> iprof = longBoard.iterator();
		return iprof;
	}

	public String[] toStringArray() {
		String[] str = new String[3];
		str[0] = toString();
		str[1] = toStringTakenB();
		str[2] = toStringTakenW();
		return str;
	}

	public String toString() {
		String str = "";
		int column = 0;
		for (ChessPiece w : longBoard) {
			if (column < 7) {
				if (w == null) {
					str += "00" + " ";
				} else {
					str += w.toString() + " ";
				}
				column++;
			} else if (column == 7) {
				if (w == null) {
					str += "00" + "\n";
				} else {
					str += w.toString() + "\n";
				}
				column = 0;
			}
		}
		return str;
	}

	public String toStringTakenB() {
		String str = "";
		for (int i = 0; i < takenBlack.size() - 1; i++) {
			str += takenBlack.get(i).toString() + " ";
		}
		if (takenBlack.size() > 0)
			str += takenBlack.get(takenBlack.size() - 1).toString();
		return str;
	}

	public String toStringTakenW() {
		String str = "";
		for (int i = 0; i < takenWhite.size() - 1; i++) {
			str += takenWhite.get(i).toString() + " ";
		}
		if (takenWhite.size() > 0)
			str += takenWhite.get(takenWhite.size() - 1).toString();
		return str;
	}

}
