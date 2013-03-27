package emma.basic.chessgameus.ChessBoard;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.*;
import java.net.*;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import emma.basic.chessgameus.R;
import emma.basic.chessgameus.ChessBoard.BoardDefinitions.*;
import emma.basic.chessgameus.ChessBoard.BoardDefinitions.ChessPiecesDefinitions.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.XmlResourceParser;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

/*Chess board is run from this activity!
 * 
 */

// to do:
// check mate for other player not working. also restart game should restart both games...
// make "your turn his turn" rather than "player 1 player 2" for multiplayer..

// ChessPlayActivity is the main chess game activity. Here the game is played. 
// ChessPlayActivity creates a BoardMatrix for storing the boards state. 
// ChessPlayActivity also creates a ChessBoardView which displays the BoardMatrix in a view.
public class ChessPlayActivity extends Activity implements OnTouchListener,
		OnKeyListener {
	private ChessBoardView chess; // the board view, passed a boardMatrix to
								  // make moves
	private BoardMatrix boardMatrix;

	// state of game variables
	private int playerTurn = 0;
	private ChessPiece takenPiece = null;
	private History history = new History();
	private Square squareSelected;
	private Square lastSquareSelected;
	private boolean evenTouch = false; // polarity of touch.
	private boolean isIllegalPutsYouInCheck = false;
	private boolean isInCheck = false;
	private boolean isCheckMate = false;
	private int winner = -1;

	// game info for multiplayer and computer play
	private String nameOfGame;
	private int playerNumber;

	// View Objects
	private TextView playerTurnText; // displays whos turn it is.
	private GridView blackPiecesTakenGrid;
	private GridView whitePiecesTakenGrid;
	private ImageAdapter imageAdaptBlackPiecesTaken;
	private ImageAdapter imageAdaptWhitePiecesTaken;
	private Button undo;
	private Button restartGame;
	private Button resignGame;

	// server info, used for multiplayer
	final private String SERVER = "http://128.32.37.29:81/web/chessServer/";
	final private String FILE_GET_BOARD = "getBoard.php";
	final private String FILE_GET_GAMES = "getGames.php";
	final private String PARAMETER_SET_BOARD = "?setBoard=setBoard";
	final private String PARAMETER_GET_BOARD = "?getBoard=getBoard";
	final private String PARAMETER_DELETE_GAME = "?deleteGame=deleteGame";

	// socket info used for computer play
	final private String SOCKET = "128.32.37.29";
	final private String PORT = "10000";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		//extras
		nameOfGame = extras != null ? extras.getString("myText")
				: "nothing passed in";
		playerNumber = extras != null ? extras.getInt("player") : -1;

		//set up view
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.chess_play);
		playerTurnText = (TextView) findViewById(R.id.playerTurnText);
		blackPiecesTakenGrid = (GridView) findViewById(R.id.blackPiecesTakenGrid);
		whitePiecesTakenGrid = (GridView) findViewById(R.id.whitePiecesTakenGrid);
		imageAdaptBlackPiecesTaken = new ImageAdapter(this);
		imageAdaptWhitePiecesTaken = new ImageAdapter(this);
		chess = (ChessBoardView) findViewById(R.id.chessBoardView);
		chess.setOnTouchListener(this);
		boardMatrix = new BoardMatrix(); //create a new initialized board
		setUpButtons();
		setScreenDisplay();
		chess.setBoard(boardMatrix); //display it on the screen

		// if single player, add initial board to history, if multiplayer check
		// if its your move and get move from server if not
		if (playerNumber == -1) {
			history.addToHistory(boardMatrix);
		} else {
			if (playerTurn != playerNumber) {
				// if multiplayer
				// wait for server to get new move
				new AsyncGetBoardFromServer().execute(SERVER + FILE_GET_BOARD
						+ PARAMETER_GET_BOARD + "&name=" + nameOfGame);

			}
		}
	}

	// onTouch is called when the board is touched. The first touch highlights
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == 1) {
			int myY = (int) event.getY();
			int myX = (int) event.getX();
			float density = getResources().getDisplayMetrics().density;
			
			squareSelected = new Square(myY, myX, density);
			chess.setSelectedSquare(squareSelected);
			if (evenTouch) {
				// If it is the players turn (-1 if not online)
				if (playerNumber == playerTurn || playerNumber == -1) {
					boolean playable = boardMatrix.isPlayableMove(
							lastSquareSelected, squareSelected, playerTurn);
					// Check playable. Playable if player moves right color, is
					// not moving a blank square and follows rules of pieces.
					if (playable) {
						isIllegalPutsYouInCheck = boardMatrix.isInCheck(
								lastSquareSelected, squareSelected, false, playerTurn);
						// check isIllegalPutsYouInCheck.
						// isIllegalPutsYouInCheck if player turies to move into
						// a check position
						if (!isIllegalPutsYouInCheck) {
							// move the piece.
							boardMatrix.makeMove(lastSquareSelected,
									squareSelected, playerTurn);
							if (nameOfGame.equals("computer")) {
								new AsyncGetBoardFromEngine().execute(SOCKET,
										PORT, lastSquareSelected.toString() +
											  squareSelected.toString());

							} else {
								if (playerNumber != -1) {// online game
									new AsyncAddBoardToServer().execute(SERVER + FILE_GET_BOARD
											+ PARAMETER_SET_BOARD + "&name=" + nameOfGame, 
											lastSquareSelected.toString() + squareSelected.toString(), 
											Integer.toString(playerNumber));
								} else {
									history.addToHistory(boardMatrix);
									undo.setEnabled(true);
								}
								// change player turn
								if (playerTurn == 0) {
									playerTurn = 1;
								} else {
									playerTurn = 0;
								}
								isInCheck = boardMatrix.isInCheck(
									lastSquareSelected, squareSelected, true, playerTurn);
								// check isInCheck. isInCheck if the move puts
								// the
								// next player in check
								if (isInCheck) {
									// check if this check is check mate.
									isCheckMate = boardMatrix.isCheckMate(
											lastSquareSelected, squareSelected, playerTurn);
									if (isCheckMate) {
										if (playerTurn == 0) {
											winner = 1;
										} else {
											winner = 0;
										}
									}
								}
								chess.setBoard(boardMatrix);
								chess.invalidate();
							}
						}
					}
				}
				chess.setEvenTouch(evenTouch);
				evenTouch = false;
			} else {
				chess.setEvenTouch(evenTouch);
				evenTouch = true;
				lastSquareSelected = squareSelected;
			}

			refreshBoardState();
		}
		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (playerNumber == 0) {
				new AsyncDeleteGameFromServer().execute(SERVER + FILE_GET_GAMES
						+ PARAMETER_DELETE_GAME);
			}
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public boolean onKey(View v, int keyCode, KeyEvent event) {
		return false;
	}

	private void setUpButtons() {
		undo = (Button) findViewById(R.id.undoMove);
		if (history.getSize() > 1 && playerNumber == -1) {
			undo.setEnabled(true);
		} else {
			undo.setEnabled(false);
		}

		undo.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// only active in multiplayer on device.
				if (playerNumber == -1 && history.getSize() > 1) {
					int mDeletedBoardSize = 0;
					int mCurrentBoardSize = 0;
					boolean pieceWasTaken = false;
					BoardMatrix deletedBoard = history.getLastInHistory();
					history.deleteLastHistory();
					boardMatrix = history.getLastInHistory();
					chess.setBoard(boardMatrix);
					chess.invalidate();
					// change player
					if (playerTurn == 0) {
						playerTurn = 1;
					} else {
						playerTurn = 0;
					}

					for (int i = 0; i < 8; i++) {
						for (int j = 0; j < 8; j++) {
							if (boardMatrix.getPieceAt(new Square(i, j)) != null) {
								boardMatrix.getPieceAt(new Square(i, j)).setSquare(i, j);
								mCurrentBoardSize++;
							}
							if (deletedBoard.getPieceAt(new Square(i, j)) != null) {
								mDeletedBoardSize++;
							}
						}
					}

					if (mCurrentBoardSize != mDeletedBoardSize) {
						pieceWasTaken = true;
					}

					if (pieceWasTaken && playerTurn == 1) {
						imageAdaptBlackPiecesTaken.removeLastImage();
						blackPiecesTakenGrid
								.setAdapter(imageAdaptBlackPiecesTaken);
					} else if (pieceWasTaken && playerTurn == 0) {
						imageAdaptWhitePiecesTaken.removeLastImage();
						whitePiecesTakenGrid
								.setAdapter(imageAdaptWhitePiecesTaken);
					}
					if (playerTurn == 1) {
						playerTurnText.setText("Player Two's Turn");
					} else {
						playerTurnText.setText("Player One's Turn");
					}

					if (!(history.getSize() > 1)) {
						undo.setEnabled(false);
					}

				}
			}
		});

		restartGame = (Button) findViewById(R.id.restartGame);

		restartGame.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				boardMatrix.restartGame();
				chess.setBoard(boardMatrix);
				chess.invalidate();
				playerTurnText.setText("Player One's Turn");
				imageAdaptBlackPiecesTaken.clearAdapter();
				blackPiecesTakenGrid.setAdapter(imageAdaptBlackPiecesTaken);
				imageAdaptWhitePiecesTaken.clearAdapter();
				whitePiecesTakenGrid.setAdapter(imageAdaptWhitePiecesTaken);
			}
		});

		resignGame = (Button) findViewById(R.id.resignGame);

		resignGame.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (playerNumber != -1) {
					new AsyncDeleteGameFromServer().execute(SERVER
							+ FILE_GET_GAMES + PARAMETER_DELETE_GAME);
				}
				finish();
			}
		});

	}

	private void refreshBoardState() {
		setScreenDisplay();
		setAlerts();

	}

	private void setScreenDisplay() {
		takenPiece = boardMatrix.getPieceTaken();
		if (playerTurn == 0 && playerNumber == -1 || playerNumber != playerTurn
				&& playerTurn == 0) {
			playerTurnText.setText("Player One's Turn");
		} else if (playerTurn != 0 && playerNumber == -1
				|| playerNumber != playerTurn && playerTurn != 0) {
			playerTurnText.setText("Player Two's Turn");
		} else {
			playerTurnText.setText("Your turn");
		}

		if (takenPiece != null && playerTurn == 0) {
			imageAdaptBlackPiecesTaken.addImage(takenPiece.getResourceName());
		} else if (takenPiece != null && playerTurn == 1) {
			imageAdaptWhitePiecesTaken.addImage(takenPiece.getResourceName());
		}
		boardMatrix.setPieceTaken();

		blackPiecesTakenGrid.setAdapter(imageAdaptBlackPiecesTaken);
		whitePiecesTakenGrid.setAdapter(imageAdaptWhitePiecesTaken);
	}

	@SuppressWarnings("deprecation")
	private void setAlerts() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		if (isIllegalPutsYouInCheck) {
			alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setMessage("Can not move here, it puts you in check!");
			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					return;
				}
			});
			alertDialog.show();
			isIllegalPutsYouInCheck = false;
		}
		if (isInCheck) {
			if (isCheckMate) {
				String message = "";
				if (winner == 0) {
					message = "Player One Wins!";
				} else {
					message = "Player Two Wins!";
				}
				builder.setTitle("Check Mate!");
				builder.setMessage(message);
				builder.setPositiveButton("HOME",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								finish();
							}
						});
				builder.setNegativeButton("PLAY AGAIN",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								boardMatrix.restartGame();
								chess.setBoard(boardMatrix);
								chess.invalidate();
								playerTurnText.setText("Player One's Turn");
								imageAdaptBlackPiecesTaken.clearAdapter();
								blackPiecesTakenGrid
										.setAdapter(imageAdaptBlackPiecesTaken);
								imageAdaptWhitePiecesTaken.clearAdapter();
								whitePiecesTakenGrid
										.setAdapter(imageAdaptWhitePiecesTaken);
							}
						});
				builder.show();

			} else {
				alertDialog.setMessage("Check!");
				alertDialog.setButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								return;
							}
						});
				alertDialog.show();
			}
			isInCheck = false;
		}
	}

	// //////methods only used if online game.

	private class AsyncDeleteGameFromServer extends
			AsyncTask<String, String, Boolean> {
		@Override
		protected Boolean doInBackground(String... params) {
			Boolean success = false;
			String urlOfGamesCreated = params[0];
			urlOfGamesCreated += "&name=" + nameOfGame;
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(urlOfGamesCreated);
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity entity = response.getEntity();
				@SuppressWarnings("unused")
				InputStream is = entity.getContent();
			} catch (Exception e) {
				Log.e("log_tag", "Error in http connection " + e.toString());
			}
			return success;
		}
	}

	private class AsyncAddBoardToServer extends
			AsyncTask<String, String, Boolean> {
		List<NameValuePair> nameValuePairs;

		@Override
		protected Boolean doInBackground(String... params) {
			nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("move", params[1]));
			nameValuePairs.add(new BasicNameValuePair("mPlayerTurn", params[2]));
			// Create a new HttpClient and Post Header
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(params[0]);
			try {
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				// Execute HTTP Post Request
				@SuppressWarnings("unused")
				HttpResponse response = httpclient.execute(httppost);
			} catch (ClientProtocolException e) {
			} catch (IOException e) {
			}
			return null;
		}

		protected void onPostExecute(Boolean result) {
			new AsyncGetBoardFromServer().execute(SERVER + FILE_GET_BOARD
					+ PARAMETER_GET_BOARD + "&name=" + nameOfGame);
		}
	}

	private class AsyncGetBoardFromServer extends
			AsyncTask<String, String, Boolean> {
		String gottenMove = "";
		int gottenPlayerTurn = -1;

		@Override
		protected Boolean doInBackground(String... params) {
			boolean result = false;
			// doesnt ever stop running someones turn so you should put
			// iscancelled at some point when resign game
			while (gottenPlayerTurn == playerNumber || gottenPlayerTurn == -1) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				try {
					String urlOfGamesCreated = params[0];
					XmlPullParser names = null;
					URL xmlUrl = new URL(urlOfGamesCreated);
					names = XmlPullParserFactory.newInstance().newPullParser();
					names.setInput(xmlUrl.openStream(), null);
					if (names != null) {
						processNames(names);
					}
				} catch (Exception e) {
				}
			}
			gottenPlayerTurn = -1;
			publishProgress();
			return result;
		}

		protected void processNames(XmlPullParser names)
				throws XmlPullParserException, IOException {
			int eventType = -1;
			while (eventType != XmlResourceParser.END_DOCUMENT) {
				if (eventType == XmlResourceParser.START_TAG) {
					String strName = names.getName();
					if (strName.equals("square")) {
						gottenMove = names.getAttributeValue(null, "move");
						gottenPlayerTurn = Integer.parseInt(names
								.getAttributeValue(null, "mPlayerTurn"));
					}
				}
				eventType = names.next();
			}

			if (gottenPlayerTurn != playerNumber) {
				boardMatrix.makeMove(new Square(gottenMove.substring(0,2)),
						new Square(gottenMove.substring(2)), playerTurn);
			}
		}

		protected void onProgressUpdate(String... values) {
			// change player turn
			if (playerTurn == 0) {
				playerTurn = 1;
			} else {
				playerTurn = 0;
			}
			chess.setBoard(boardMatrix);
			chess.invalidate();
			refreshBoardState();
		}
	}

	private class AsyncGetBoardFromEngine extends
			AsyncTask<String, String, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			boolean result = false;
			// doesnt ever stop running someones turn so you should put
			// iscancelled at some point when resign game

			try {
				String ip = params[0];
				int port = Integer.parseInt(params[1]);
				String move = params[2];
				Socket mySocket = null;
				PrintWriter out = null;
				BufferedReader in = null;

				try {
					mySocket = new Socket(ip, port);
					out = new PrintWriter(mySocket.getOutputStream(), true);
					in = new BufferedReader(new InputStreamReader(
							mySocket.getInputStream()));
				} catch (UnknownHostException e) {
					System.err.println("Don't know about host: " + ip);
					System.exit(1);
				} catch (IOException e) {
					System.err.println("Couldn't get I/O for "
							+ "the connection to: " + ip);
					System.exit(1);
				}

				out.println(move);
				String computerMove = in.readLine();
				out.close();
				in.close();

				mySocket.close();
				makeMove(computerMove);
			} catch (Exception e) {
			}

			publishProgress();
			return result;
		}

		protected void makeMove(String move) {
			Square start = new Square(move.substring(0, 2));
			Square end = new Square(move.substring(2));
			boardMatrix.makeMove(start, end, 1);
		}

		protected void onProgressUpdate(String... values) {
			playerTurn = 0;
			chess.setBoard(boardMatrix);
			chess.invalidate();
			refreshBoardState();
		}
	}

}
