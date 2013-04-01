package emma.basic.chessgameus.ChessBoard.BoardDefinitions;

import java.util.ArrayList;
import java.util.Iterator;

import emma.basic.chessgameus.ChessBoard.BoardDefinitions.ChessPiecesDefinitions.*;

//todo: make everything a square rather than startSqu.getRow(), start column.

// BoardMatrix stores board state and checks validity of moves.
public class BoardMatrix implements Iterable<ChessPiece> {
	private ChessBoardRepresentation board;
	private ArrayList<ChessPiece> takenBlack;
	private ArrayList<ChessPiece> takenWhite;

	// constructors:
	public BoardMatrix() { // inital board
		board = new ChessBoardRepresentation();
		takenBlack = new ArrayList<ChessPiece>(16);
		takenWhite = new ArrayList<ChessPiece>(16);
	}

	public BoardMatrix(ChessBoardRepresentation bor, ArrayList<ChessPiece> tB,
			ArrayList<ChessPiece> tW) {
		board = bor;
		takenBlack = tB;
		takenWhite = tW;
	}
	
	public BoardMatrix(String boardString) {
		board = new ChessBoardRepresentation(boardString);
	}

	public BoardMatrix(String boardString, String tB, String tW) {
		board = new ChessBoardRepresentation(boardString);
		takenBlack = new ArrayList<ChessPiece>(16);
		takenWhite = new ArrayList<ChessPiece>(16);
		if(tB != null){
		    String[] piecesB = tB.split(" ");
		    for (int i = 0; i < piecesB.length; i++) {
				int color = 0;
				if (piecesB[i].substring(1).equals("B")) {
					color = 1;
				}
				if (piecesB[i].substring(0, 1).equals("B")) {
					takenBlack.add(new Bishop(color, new Square(0, 0)));
				} else if (piecesB[i].substring(0, 1).equals("K")) {
					if (color == 1) {
						takenBlack.add(new King(color, new Square(0, 0)));
					} else {
						takenBlack.add(new King(color, new Square(0, 0)));
					}
				} else if (piecesB[i].substring(0, 1).equals("Q")) {
					takenBlack.add(new Queen(color, new Square(0, 0)));
				} else if (piecesB[i].substring(0, 1).equals("N")) {
					takenBlack.add(new Knight(color, new Square(0, 0)));
				} else if (piecesB[i].substring(0, 1).equals("P")) {
					takenBlack.add(new Pawn(color, new Square(0, 0)));
				} else if (piecesB[i].substring(0, 1).equals("C")) {
					takenBlack.add(new Castle(color, new Square(0, 0)));
				}
			}
		}
		if(tW != null){
	        String[] piecesW = tW.split(" ");
	        for (int i = 0; i < piecesW.length; i++) {
				int color = 0;
				if (piecesW[i].substring(1).equals("B")) {
					color = 1;
				}
				if (piecesW[i].substring(0, 1).equals("B")) {
					takenBlack.add(new Bishop(color, new Square(0, 0)));
				} else if (piecesW[i].substring(0, 1).equals("K")) {
					if (color == 1) {
						takenBlack.add(new King(color, new Square(0, 0)));
					} else {
						takenBlack.add(new King(color, new Square(0, 0)));
					}
				} else if (piecesW[i].substring(0, 1).equals("Q")) {
					takenBlack.add(new Queen(color, new Square(0, 0)));
				} else if (piecesW[i].substring(0, 1).equals("N")) {
					takenBlack.add(new Knight(color, new Square(0, 0)));
				} else if (piecesW[i].substring(0, 1).equals("P")) {
					takenBlack.add(new Pawn(color, new Square(0, 0)));
				} else if (piecesW[i].substring(0, 1).equals("C")) {
					takenBlack.add(new Castle(color, new Square(0, 0)));
				}
			}
	    }
	}

	// methods to play chess
	public boolean isPlayableMove(Square startSqu, Square endSqu, int playerTurn) {
		boolean playable = false;
		ChessPiece mChessPiece = board.get(startSqu);
		if (mChessPiece != null) {
			// the player can only move their color.
			if (playerTurn == 0 && mChessPiece.getColor() == 0
					|| playerTurn == 1 && mChessPiece.getColor() != 0) {
				playable = mChessPiece.isLegal(this, startSqu, endSqu);
			}
		}
		return playable;
	}

	public void makeMove(Square startSqu, Square endSqu) {
		if (board.get(endSqu) != null) {
			if (board.get(endSqu).getColor() == 0) {
				takenWhite.add(board.get(endSqu));
			} else {
				takenBlack.add(board.get(endSqu));
			}
		}
		board.set(endSqu, board.get(startSqu));
		board.set(startSqu, null);
		board.get(endSqu).setSquare(endSqu);
		queenIfPawnReachesBack(this, endSqu);
	}

	// use to see if this move is a check move
	public boolean isInCheck(Square startSqu, Square endSqu,
			boolean moveAlreadyMade, int playerTurn) {
		Square kingPos;
		boolean isCheck = false;
		// copy board
		BoardMatrix copyBoardMat = this.deepCopySelf();
		// make the desired move if not already made
		if (!moveAlreadyMade) {
			copyBoardMat.setPieceAt(endSqu, copyBoardMat.getPieceAt(startSqu));
			copyBoardMat.setPieceAt(startSqu, null);
			queenIfPawnReachesBack(copyBoardMat, new Square(endSqu.getRow(),
					endSqu.getColumn()));
		}
		if (playerTurn == 0) {// white
			kingPos = new Square(board.getKingWhiteReference().getSquare()
					.getRow(), board.getKingWhiteReference().getSquare()
					.getColumn());
			if (!moveAlreadyMade) {
				// if desired move is to move the king must find where it was
				// moved.
				if (board.get(startSqu) instanceof King) {
					kingPos.setRow(endSqu.getRow());
					kingPos.setColumn(endSqu.getColumn());
				}
			}
			for (int i = 0; i < 8; i++) {
				for (int j = 0; j < 8; j++) {
					if (copyBoardMat.getPieceAt(new Square(i, j)) != null
							&& copyBoardMat.getPieceAt(new Square(i, j))
									.getColor() != 0) {
						ChessPiece piece = copyBoardMat.getPieceAt(new Square(
								i, j));
						isCheck = piece.isLegal(
								copyBoardMat,
								new Square(i, j),
								new Square(kingPos.getRow(), kingPos
										.getColumn()));
					}
					if (isCheck) {
						break;
					}
				}
				if (isCheck) {
					break;
				}
			}
		}
		if (playerTurn == 1) {
			kingPos = new Square(board.getKingBlackReference().getSquare()
					.getRow(), board.getKingBlackReference().getSquare()
					.getColumn());
			// if desired move is to move the king must find where it was
			// moved.
			if (!moveAlreadyMade) {
				if (board.get(startSqu) instanceof King) {
					kingPos.setRow(endSqu.getRow());
					kingPos.setColumn(endSqu.getColumn());
				}
			}
			for (int i = 0; i < 8; i++) {
				for (int j = 0; j < 8; j++) {
					if (copyBoardMat.getPieceAt(new Square(i, j)) != null
							&& copyBoardMat.getPieceAt(new Square(i, j))
									.getColor() == 0) {
						ChessPiece piece = copyBoardMat.getPieceAt(new Square(
								i, j));
						isCheck = piece.isLegal(
								copyBoardMat,
								new Square(i, j),
								new Square(kingPos.getRow(), kingPos
										.getColumn()));
					}
					if (isCheck) {
						break;
					}
				}
				if (isCheck) {
					break;
				}
			}
		}
		return isCheck;
	}

	// used to see if this move would be checkmate.
	// why doesnt it use the input???????????????
	public boolean isCheckMate(Square startSqu, Square endSqu, int playerTurn) {
		boolean isStillCheck = true;
		if (playerTurn == 0) { // check if player one in checkMate
			ArrayList<ChessPiece> whiteLeft = new ArrayList<ChessPiece>();
			for (int i = 0; i < 8; i++) {
				for (int j = 0; j < 8; j++) {
					if (board.get(i, j) != null
							&& board.get(i, j).getColor() == 0) {
						whiteLeft.add(board.get(i, j));
					}
				}
			}
			for (int i = 0; i < whiteLeft.size(); i++) {
				// check possible moves..
				for (int j = 0; j < 8; j++) {
					for (int k = 0; k < 8; k++) {
						int rowOfPiece = whiteLeft.get(i).getSquare().getRow();
						int columnOfPiece = whiteLeft.get(i).getSquare()
								.getColumn();
						Boolean isLegal = whiteLeft.get(i).isLegal(this,
								new Square(rowOfPiece, columnOfPiece),
								new Square(j, k));
						if (isLegal) {
							// move the piece then test if still in check.
							isStillCheck = isInCheck(new Square(rowOfPiece,
									columnOfPiece), new Square(j, k), false,
									playerTurn);
							if (!isStillCheck) {
								return false;
							}
						}
					}
				}
			}
		}
		// find all black pieces left and see if you can move them to avoid
		// check..
		else if (playerTurn == 1) {// check if player two in checkMate
			ArrayList<ChessPiece> blackLeft = new ArrayList<ChessPiece>();
			for (int i = 0; i < 8; i++) {
				for (int j = 0; j < 8; j++) {
					if (board.get(i, j) != null
							&& board.get(i, j).getColor() != 0) {
						blackLeft.add(board.get(i, j));
					}
				}
			}
			for (int i = 0; i < blackLeft.size(); i++) {
				// check possible moves..
				for (int j = 0; j < 8; j++) {
					for (int k = 0; k < 8; k++) {
						int rowOfPiece = blackLeft.get(i).getSquare().getRow();
						int columnOfPiece = blackLeft.get(i).getSquare()
								.getColumn();
						ChessPiece p = blackLeft.get(i);
						Boolean isLegal = p.isLegal(this, new Square(
								rowOfPiece, columnOfPiece), new Square(j, k));
						if (isLegal) {
							isStillCheck = isInCheck(new Square(rowOfPiece,
									columnOfPiece), new Square(j, k), false,
									playerTurn);
							if (!isStillCheck) {
								return false;
							}
						}
					}
				}
			}
		}
		return true;
	}

	private void queenIfPawnReachesBack(BoardMatrix referenceBoard,
			Square endSqu) {
		if (referenceBoard.getPieceAt(endSqu) instanceof Pawn) {
			if (referenceBoard.getPieceAt(endSqu).getColor() == 0
					&& endSqu.getRow() == 0) {
				referenceBoard.setPieceAt(endSqu, new Queen(0, endSqu));
			} else if (referenceBoard.getPieceAt(endSqu).getColor() != 0
					&& endSqu.getRow() == 7) {
				referenceBoard.setPieceAt(endSqu, new Queen(1, endSqu));
			}
		}

	}

	public void restartGame() {
		takenBlack = null;
		takenWhite = null;
		board.initialBoardSetup();
	}

	// methods to access pieces and variables
	public ChessPiece getPieceAt(Square squ) {
		return board.get(squ);
	}

	public void setPieceAt(Square squ, ChessPiece piece) {
		board.set(squ, piece);
	}

	public ArrayList<ChessPiece> getBlackTaken() {
		return takenBlack;
	}

	public ArrayList<ChessPiece> getWhiteTaken() {
		return takenWhite;
	}

	public void removeLastBlackTaken() {
		takenBlack.remove(takenBlack.size() - 1);
	}

	public void removeLastWhiteTaken() {
		takenWhite.remove(takenWhite.size() - 1);
	}

	// iterate over pieces
	public Iterator<ChessPiece> iterator() {
		Iterator<ChessPiece> iprof = board.iterator();
		return iprof;
	}

	// copy board
	public BoardMatrix deepCopySelf() {
		BoardMatrix copyMat;
		if (takenWhite != null | takenBlack != null) {
			if (takenWhite != null) {
				if (takenBlack != null) {
					copyMat = new BoardMatrix(board.deepCopy(), takenBlack,
							takenWhite);
				} else {
					copyMat = new BoardMatrix(board.deepCopy(), null,
							takenWhite);
				}
			} else {
				copyMat = new BoardMatrix(board.deepCopy(), takenBlack, null);
			}
		} else {
			copyMat = new BoardMatrix(board.deepCopy(), null, null);
		}
		return copyMat;
	}

	public String toString() {
		return board.toString();
	}

}
