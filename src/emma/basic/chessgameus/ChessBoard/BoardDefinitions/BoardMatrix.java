package emma.basic.chessgameus.ChessBoard.BoardDefinitions;

import java.util.ArrayList;
import java.util.Iterator;

import emma.basic.chessgameus.ChessBoard.BoardDefinitions.ChessPiecesDefinitions.*;

//todo: make everything a square rather than startSqu.getRow(), start column.

// BoardMatrix stores board state and checks validity of moves.
public class BoardMatrix extends ChessBoardRepresentation implements
		Iterable<ChessPiece> {

	// constructors:
	public BoardMatrix() { // inital board
		super();
	}

	public BoardMatrix(String[] boardString) {
		super(boardString);
	}

	public BoardMatrix(ArrayList<ChessPiece> bor, ChessPiece kingB,
			ChessPiece kingW, ArrayList<ChessPiece> takenB,
			ArrayList<ChessPiece> takenW) {
		super(bor, kingB, kingW, takenB, takenW);
	}

	// methods to play chess
	public boolean isPlayableMove(Square startSqu, Square endSqu, int playerTurn) {
		boolean playable = false;
		ChessPiece mChessPiece = getPieceAt(startSqu);
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
		if (!castleMove(this, startSqu, endSqu)) {
			if (getPieceAt(endSqu) != null) {
				if (getPieceAt(endSqu).getColor() == 0) {
					addTakenWhite(getPieceAt(endSqu));
				} else {
					addTakenBlack(getPieceAt(endSqu));
				}
			}
			setPieceAt(endSqu, getPieceAt(startSqu));
			setPieceAt(startSqu, null);
			getPieceAt(endSqu).setSquare(endSqu);
			queenIfPawnReachesBack(this, endSqu);
			getPieceAt(endSqu).setMoved();
		}
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
			if (!castleMove(copyBoardMat, new Square(startSqu.getRow(),
					startSqu.getColumn()),
					new Square(endSqu.getRow(), endSqu.getColumn()))) {
				copyBoardMat.setPieceAt(endSqu,
						copyBoardMat.getPieceAt(startSqu));
				copyBoardMat.setPieceAt(startSqu, null);
				queenIfPawnReachesBack(copyBoardMat, new Square(
						endSqu.getRow(), endSqu.getColumn()));
			}
		}
		if (playerTurn == 0) {// white
			kingPos = new Square(getKingWhiteReference().getSquare().getRow(),
					getKingWhiteReference().getSquare().getColumn());
			if (!moveAlreadyMade) {
				// if desired move is to move the king must find where it was
				// moved.
				if (getPieceAt(startSqu) instanceof King) {
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
			kingPos = new Square(getKingBlackReference().getSquare().getRow(),
					getKingBlackReference().getSquare().getColumn());
			// if desired move is to move the king must find where it was
			// moved.
			if (!moveAlreadyMade) {
				if (getPieceAt(startSqu) instanceof King) {
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
					if (getPieceAt(i, j) != null
							&& getPieceAt(i, j).getColor() == 0) {
						whiteLeft.add(getPieceAt(i, j));
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
					if (getPieceAt(i, j) != null
							&& getPieceAt(i, j).getColor() != 0) {
						blackLeft.add(getPieceAt(i, j));
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

	private boolean castleMove(BoardMatrix referenceBoard, Square startSqu,
			Square endSqu) {
		boolean castled = false;
		if (referenceBoard.getPieceAt(startSqu) instanceof King) {
			if (referenceBoard.getPieceAt(startSqu).getMoved() == false) {
				if (referenceBoard.getPieceAt(startSqu).getColor() == 0) {
					if (startSqu.getRow() == 7 && endSqu.getRow() == 7) {
						if (endSqu.getColumn() == 2) {
							if (referenceBoard.getPieceAt(7, 1) == null
									&& referenceBoard.getPieceAt(7, 2) == null
									&& referenceBoard.getPieceAt(7, 3) == null
									&& referenceBoard.getPieceAt(7, 0) instanceof Castle
									&& referenceBoard.getPieceAt(7, 0)
											.getMoved() == false) {
								referenceBoard.setPieceAt(7, 3,
										referenceBoard.getPieceAt(7, 0));
								referenceBoard.setPieceAt(7, 0, null);
								referenceBoard.getPieceAt(7, 3).setSquare(7, 3);
								referenceBoard.getPieceAt(7, 3).setMoved();
								castled = true;

							}
						} else if (endSqu.getColumn() == 6) {
							if (referenceBoard.getPieceAt(7, 5) == null
									&& referenceBoard.getPieceAt(7, 6) == null
									&& referenceBoard.getPieceAt(7, 7) instanceof Castle
									&& referenceBoard.getPieceAt(7, 7)
											.getMoved() == false) {
								referenceBoard.setPieceAt(7, 5,
										referenceBoard.getPieceAt(7, 7));
								referenceBoard.setPieceAt(7, 7, null);
								referenceBoard.getPieceAt(7, 5).setSquare(7, 5);
								referenceBoard.getPieceAt(7, 5).setMoved();
								castled = true;

							}
						}
					}
				} else {
					if (startSqu.getRow() == 0 && endSqu.getRow() == 0) {
						if (endSqu.getColumn() == 2) {
							if (referenceBoard.getPieceAt(0, 1) == null
									&& referenceBoard.getPieceAt(0, 2) == null
									&& referenceBoard.getPieceAt(0, 3) == null
									&& referenceBoard.getPieceAt(0, 0) instanceof Castle
									&& referenceBoard.getPieceAt(0, 0)
											.getMoved() == false) {
								referenceBoard.setPieceAt(0, 3,
										referenceBoard.getPieceAt(0, 0));
								referenceBoard.setPieceAt(0, 0, null);
								referenceBoard.getPieceAt(0, 3).setSquare(0, 3);
								referenceBoard.getPieceAt(0, 3).setMoved();
								castled = true;

							}
						} else if (endSqu.getColumn() == 6) {
							if (referenceBoard.getPieceAt(0, 5) == null
									&& referenceBoard.getPieceAt(0, 6) == null
									&& referenceBoard.getPieceAt(0, 7) instanceof Castle
									&& referenceBoard.getPieceAt(0, 7)
											.getMoved() == false) {
								referenceBoard.setPieceAt(0, 5,
										referenceBoard.getPieceAt(0, 7));
								referenceBoard.setPieceAt(0, 7, null);
								referenceBoard.getPieceAt(0, 5).setSquare(0, 5);
								referenceBoard.getPieceAt(0, 5).setMoved();
								castled = true;

							}
						}
					}
				}
			}
		}
		if (castled) {
			// move the king
			referenceBoard.setPieceAt(endSqu,
					referenceBoard.getPieceAt(startSqu));
			referenceBoard.setPieceAt(startSqu, null);
			referenceBoard.getPieceAt(endSqu).setSquare(endSqu);
			referenceBoard.getPieceAt(endSqu).setMoved();
		}
		return castled;
	}

	// copy board
	public BoardMatrix deepCopySelf() {
		ArrayList<ChessPiece> longBoardCopy = new ArrayList<ChessPiece>(64);
		ChessPiece newKingReferenceBlack = null;
		ChessPiece newKingReferenceWhite = null;
		for (int i = 0; i < getLongBoard().size(); i++) {
			if (getLongBoard().get(i) != null) {
				longBoardCopy.add(getLongBoard().get(i).deepCopy());
				if (getLongBoard().get(i) instanceof King) {
					if (getLongBoard().get(i).getColor() == 1) {
						newKingReferenceBlack = getLongBoard().get(i);
					} else {
						newKingReferenceWhite = getLongBoard().get(i);
					}
				}
			} else {
				longBoardCopy.add(null);
			}
		}
		ArrayList<ChessPiece> takenB = new ArrayList<ChessPiece>(16);
		for (ChessPiece piece : getBlackTaken()) {
			takenB.add(piece.deepCopy());
		}
		ArrayList<ChessPiece> takenW = new ArrayList<ChessPiece>(16);
		for (ChessPiece piece : getWhiteTaken()) {
			takenW.add(piece.deepCopy());
		}

		return new BoardMatrix(longBoardCopy, newKingReferenceBlack,
				newKingReferenceWhite, takenB, takenW);
	}

}
