package emma.basic.chessgameus.ChessBoard.BoardDefinitions;

import java.util.ArrayList;

import org.apache.http.client.utils.CloneUtils;

import emma.basic.chessgameus.ChessBoard.BoardDefinitions.ChessPiecesDefinitions.*;

//todo: make everything a square rather than startSqu.getRow(), start column.

// BoardMatrix stores board state and checks validity of moves.
public class BoardMatrix {
	private ChessPiece[][] board;
	private ChessPiece takenPiece = null;
	private ChessPiece kingBlackReference;
	private ChessPiece kingWhiteReference;

	// initializes the board
	public BoardMatrix() {
		board = new ChessPiece[8][8];
		initialBoardSetup();
	}
	
	public BoardMatrix(ChessPiece[][] bor){
		board = bor;
	}

	// creates all the initial chess pieces and puts them in their starting
	// positions.
	public void initialBoardSetup() {
		// set up board
		board[0][0] = new Castle(1, new Square(false, 0, 0));
		board[0][1] = new Knight(1, new Square(false, 0, 1));
		board[0][2] = new Bishop(1, new Square(false, 0, 2));
		board[0][3] = new Queen(1, new Square(false, 0, 3));
		kingBlackReference = new King(1, new Square(false, 0, 4));
		board[0][4] = kingBlackReference;
		board[0][5] = new Bishop(1, new Square(false, 0, 5));
		board[0][6] = new Knight(1, new Square(false, 0, 6));
		board[0][7] = new Castle(1, new Square(false, 0, 7));
		for (int i = 0; i < 8; i++) {
			board[1][i] = new Pawn(1, new Square(false, 1, i));
		}

		for (int i = 2; i < 6; i++) {
			for (int j = 0; j < 8; j++) {
				board[i][j] = null;
			}
		}

		for (int i = 0; i < 8; i++) {
			board[6][i] = new Pawn(0, new Square(false, 6, i));
		}
		board[7][0] = new Castle(0, new Square(false, 7, 0));
		board[7][1] = new Knight(0, new Square(false, 7, 1));
		board[7][2] = new Bishop(0, new Square(false, 7, 2));
		board[7][3] = new Queen(0, new Square(false, 7, 3));
		kingWhiteReference = new King(0, new Square(false, 7, 4));
		board[7][4] = kingWhiteReference;
		board[7][5] = new Bishop(0, new Square(false, 7, 5));
		board[7][6] = new Knight(0, new Square(false, 7, 6));
		board[7][7] = new Castle(0, new Square(false, 7, 7));
	}

	public boolean isPlayableMove(Square startSqu, Square endSqu, int playerTurn) {
		boolean playable = false;
		ChessPiece mChessPiece = board[startSqu.getRow()][startSqu.getColumn()];
		if (mChessPiece != null) {
			// the player can only move their color.
			if (playerTurn == 0 && mChessPiece.getColor() == 0
					|| playerTurn == 1 && mChessPiece.getColor() != 0) {
				playable = mChessPiece.isLegal(this, new Square(false, startSqu.getRow(),startSqu.getColumn()),
						new Square(false, endSqu.getRow(), endSqu.getColumn()));
			}
		}
		return playable;
	}

	public void makeMove(Square startSqu, Square endSqu, int playerTurn) {
		if (board[endSqu.getRow()][endSqu.getColumn()] != null) {
			takenPiece = board[endSqu.getRow()][endSqu.getColumn()];
		}
		board[endSqu.getRow()][endSqu.getColumn()] = board[startSqu.getRow()][startSqu.getColumn()];
		board[startSqu.getRow()][startSqu.getColumn()] = null;
		board[endSqu.getRow()][endSqu.getColumn()].setSquare(endSqu.getRow(), endSqu.getColumn());
		queenIfPawnReachesBack(this, new Square(false, endSqu.getRow(), endSqu.getColumn()));
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
	
	public BoardMatrix deepCopySelf(){
		ChessPiece[][] copyOfBoard = new ChessPiece[8][8];
		for (int i = 0; i < 8; i++) {
			System.arraycopy(board[i], 0, copyOfBoard[i], 0, 8);
		}
		BoardMatrix mat = new BoardMatrix(copyOfBoard);
		return mat;
	}

	// use to see if this move is a check move
	public boolean isInCheck(Square startSqu, Square endSqu, boolean moveAlreadyMade, int playerTurn) {
		Square kingPos;
		boolean isCheck = false;
		// copy board
		BoardMatrix copyBoardMat = this.deepCopySelf();
		// make the desired move if not already made
		if (!moveAlreadyMade) {
			copyBoardMat.setPieceAt(endSqu, copyBoardMat.getPieceAt(startSqu));
			copyBoardMat.setPieceAt(startSqu, null);
			queenIfPawnReachesBack(copyBoardMat, new Square(false, endSqu.getRow(), endSqu.getColumn()));
		}
		if (playerTurn == 0) {// white
			kingPos = new Square(false, kingWhiteReference.getSquare().getRow(),
			          kingWhiteReference.getSquare().getColumn());
			if (!moveAlreadyMade) {
				// if desired move is to move the king must find where it was
				// moved.
				if (board[startSqu.getRow()][startSqu.getColumn()] instanceof King) {
					kingPos.setRow(endSqu.getRow());
					kingPos.setColumn(endSqu.getColumn());
				}
			}
			for (int i = 0; i < 8; i++) {
				for (int j = 0; j < 8; j++) {
					if (copyBoardMat.getPieceAt(new Square(false, i, j)) != null
							&& copyBoardMat.getPieceAt(new Square(false, i, j)).getColor() != 0) {
						ChessPiece piece = copyBoardMat.getPieceAt(new Square(false, i, j));
						isCheck = piece.isLegal(copyBoardMat, new Square(false, i, j), new Square(false,
								kingPos.getRow(),
								kingPos.getColumn()));
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
			kingPos = new Square( false, kingBlackReference.getSquare().getRow(),
			          kingBlackReference.getSquare().getColumn());
			// if desired move is to move the king must find where it was
			// moved.
			if (!moveAlreadyMade) {
				if (board[startSqu.getRow()][startSqu.getColumn()] instanceof King) {
					kingPos.setRow(endSqu.getRow());
					kingPos.setColumn(endSqu.getColumn());
				}
			}
			for (int i = 0; i < 8; i++) {
				for (int j = 0; j < 8; j++) {
					if (copyBoardMat.getPieceAt(new Square(false, i, j)) != null
							&& copyBoardMat.getPieceAt(new Square(false, i, j)).getColor() == 0) {
						ChessPiece piece = copyBoardMat.getPieceAt(new Square(false, i, j));
						isCheck = piece.isLegal(copyBoardMat, new Square(false, i, j), new Square(false,
								kingPos.getRow(),
								kingPos.getColumn()));
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
	//why doesnt it use the input???????????????
	public boolean isCheckMate(Square startSqu, Square endSqu, int playerTurn) {
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
						Boolean isLegal = whiteLeft.get(i).isLegal(this,
								new Square(false, rowOfPiece, columnOfPiece), new Square(false, j, k));
						if (isLegal) {
							// move the piece then test if still in check.
							isStillCheck = isInCheck(new Square(false, rowOfPiece, columnOfPiece),
									new Square(false,j, k), false, playerTurn);
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
						Boolean isLegal = p.isLegal(this, new Square(false, rowOfPiece,
								columnOfPiece), new Square(false, j, k));
						if (isLegal) {
							isStillCheck = isInCheck(new Square(false, rowOfPiece, columnOfPiece),
									new Square(false,j, k), false, playerTurn);
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
	
	public ChessPiece getPieceAt(Square squ){
		return board[squ.getRow()][squ.getColumn()];
	}
	
	public void setPieceAt(Square squ, ChessPiece piece){
		board[squ.getRow()][squ.getColumn()] = piece;
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

}
