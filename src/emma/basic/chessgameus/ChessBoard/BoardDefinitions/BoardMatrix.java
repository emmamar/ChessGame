package emma.basic.chessgameus.ChessBoard.BoardDefinitions;

import java.util.ArrayList;

import emma.basic.chessgameus.ChessBoard.BoardDefinitions.ChessPiecesDefinitions.*;

//todo: make everything a square rather than startrow, start column.

// BoardMatrix stores board state and checks validity of moves.
public class BoardMatrix {
	private ChessPiece[][] board;
	private ChessPiece takenPiece = null;
	private ChessPiece kingBlackReference;
	private ChessPiece kingWhiteReference;

	// initializes the board
	public BoardMatrix() {
		board = new ChessPiece[8][8];
	}

	// creates all the initial chess pieces and puts them in their starting
	// positions.
	public void initialBoardSetup() {
		// set up board
		board[0][0] = new Castle(1, 0, 0);
		board[0][1] = new Knight(1, 0, 1);
		board[0][2] = new Bishop(1, 0, 2);
		board[0][3] = new Queen(1, 0, 3);
		kingBlackReference = new King(1, 0, 4);
		board[0][4] = kingBlackReference;
		board[0][5] = new Bishop(1, 0, 5);
		board[0][6] = new Knight(1, 0, 6);
		board[0][7] = new Castle(1, 0, 7);
		for (int i = 0; i < 8; i++) {
			board[1][i] = new Pawn(1, 1, i);
		}

		for (int i = 2; i < 6; i++) {
			for (int j = 0; j < 8; j++) {
				board[i][j] = null;
			}
		}

		for (int i = 0; i < 8; i++) {
			board[6][i] = new Pawn(0, 6, i);
		}
		board[7][0] = new Castle(0, 7, 0);
		board[7][1] = new Knight(0, 7, 1);
		board[7][2] = new Bishop(0, 7, 2);
		board[7][3] = new Queen(0, 7, 3);
		kingWhiteReference = new King(0, 7, 4);
		board[7][4] = kingWhiteReference;
		board[7][5] = new Bishop(0, 7, 5);
		board[7][6] = new Knight(0, 7, 6);
		board[7][7] = new Castle(0, 7, 7);
	}

	public boolean isPlayableMove(int startRow, int startColumn, int endRow,
			int endColumn, int playerTurn) {
		boolean playable = false;
		ChessPiece mChessPiece = board[startRow][startColumn];
		if (mChessPiece != null) {
			// the player can only move their color.
			if (playerTurn == 0 && mChessPiece.getColor() == 0
					|| playerTurn == 1 && mChessPiece.getColor() != 0) {
				playable = mChessPiece.isLegal(board, startRow, startColumn,
						endRow, endColumn);
			}
		}
		return playable;
	}

	public void makeMove(int startRow, int startColumn, int endRow,
			int endColumn, int playerTurn) {
		if (board[endRow][endColumn] != null) {
			takenPiece = board[endRow][endColumn];
		}
		board[endRow][endColumn] = board[startRow][startColumn];
		board[startRow][startColumn] = null;
		board[endRow][endColumn].setSquare(endRow, endColumn);
		queenIfPawnReachesBack(board, endRow, endColumn);
	}

	public ChessPiece getPieceTaken() {
		return takenPiece;
	}

	public void setPieceTaken() {
		takenPiece = null;
	}

	public void restartGame() {
		takenPiece = null;
		initialBoardSetup();
	}

	// use to see if this move is a check move
	public boolean isInCheck(int startRow, int startColumn, int endRow,
			int endColumn, boolean moveAlreadyMade, int playerTurn) {
		int kingRow;
		int kingColumn;
		boolean isCheck = false;
		// copy board
		ChessPiece[][] copyOfBoard = new ChessPiece[8][8];
		for (int i = 0; i < 8; i++) {
			System.arraycopy(board[i], 0, copyOfBoard[i], 0, 8);
		}
		// make the desired move if not already made
		if (!moveAlreadyMade) {
			copyOfBoard[endRow][endColumn] = copyOfBoard[startRow][startColumn];
			copyOfBoard[startRow][startColumn] = null;
			queenIfPawnReachesBack(copyOfBoard, endRow, endColumn);
		}
		if (playerTurn == 0) {// white
			kingRow = kingWhiteReference.getSquare().getRow();
			kingColumn = kingWhiteReference.getSquare().getColumn();
			if (!moveAlreadyMade) {
				// if desired move is to move the king must find where it was
				// moved.
				if (board[startRow][startColumn] instanceof King) {
					kingRow = endRow;
					kingColumn = endColumn;
				}
			}
			for (int i = 0; i < 8; i++) {
				for (int j = 0; j < 8; j++) {
					if (copyOfBoard[i][j] != null
							&& copyOfBoard[i][j].getColor() != 0) {
						ChessPiece piece = copyOfBoard[i][j];
						isCheck = piece.isLegal(copyOfBoard, i, j, kingRow,
								kingColumn);
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
			kingRow = kingBlackReference.getSquare().getRow();
			kingColumn = kingBlackReference.getSquare().getColumn();
			// if desired move is to move the king must find where it was
			// moved.
			if (!moveAlreadyMade) {
				if (board[startRow][startColumn] instanceof King) {
					kingRow = endRow;
					kingColumn = endColumn;
				}
			}
			for (int i = 0; i < 8; i++) {
				for (int j = 0; j < 8; j++) {
					if (copyOfBoard[i][j] != null
							&& copyOfBoard[i][j].getColor() == 0) {
						ChessPiece piece = copyOfBoard[i][j];
						isCheck = piece.isLegal(copyOfBoard, i, j, kingRow,
								kingColumn);
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
	public boolean isCheckMate(int startRow, int startColumn, int endRow,
			int endColumn, int playerTurn) {
		boolean isStillCheck = true;
		if (playerTurn == 0) { // check if player one in checkMate
			ArrayList<ChessPiece> whiteLeft = new ArrayList<ChessPiece>();
			for (int i = 0; i < 8; i++) {
				for (int j = 0; j < 8; j++) {
					if (board[i][j] != null && board[i][j].getColor() == 0) {
						whiteLeft.add(board[i][j]);
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
						Boolean isLegal = whiteLeft.get(i).isLegal(board,
								rowOfPiece, columnOfPiece, j, k);
						if (isLegal) {
							// move the piece then test if still in check.
							isStillCheck = isInCheck(rowOfPiece, columnOfPiece,
									j, k, false, playerTurn);
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
					if (board[i][j] != null && board[i][j].getColor() != 0) {
						blackLeft.add(board[i][j]);
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
						Boolean isLegal = p.isLegal(board, rowOfPiece,
								columnOfPiece, j, k);
						if (isLegal) {
							isStillCheck = isInCheck(rowOfPiece, columnOfPiece,
									j, k, false, playerTurn);
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

	public ChessPiece[][] getBoard() {
		return board;
	}

	public void setBoard(ChessPiece[][] board) {
		this.board = board;
	}

	private void queenIfPawnReachesBack(ChessPiece[][] referenceBoard,
			int endRow, int endColumn) {
		if (referenceBoard[endRow][endColumn] instanceof Pawn) {
			if (referenceBoard[endRow][endColumn].getColor() == 0
					&& endRow == 0) {
				referenceBoard[endRow][endColumn] = new Queen(0, endRow,
						endColumn);
			} else if (referenceBoard[endRow][endColumn].getColor() != 0
					&& endRow == 7) {
				referenceBoard[endRow][endColumn] = new Queen(1, endRow,
						endColumn);
			}
		}

	}

}
