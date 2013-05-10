package emma.basic.chessgameus.ChessBoard;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.res.XmlResourceParser;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.chess_play);
		ChessBoardView iv = (ChessBoardView) findViewById(R.id.chessBoardView);
		Display display = getWindowManager().getDefaultDisplay();
		screenwidth = display.getWidth();
		if(screenwidth > display.getHeight()){
			screenwidth = display.getHeight();
			iv.getLayoutParams().width = display.getHeight();
			iv.getLayoutParams().height = display.getHeight();
		}
		else{
		  iv.getLayoutParams().width = display.getWidth();
		  iv.getLayoutParams().height = display.getWidth();
		}

		// recover from a stop
		if (savedInstanceState != null) {
			restoreMyState(savedInstanceState);
			setupView();
			if (playerNumber != -1 && !nameOfGame.equals("computer")) {
				waitForOponent();
			}
		} else {
			createMyState();
			setupView();
			// if single device, add initial board to history, if multiplayer
			// check
			// if its your move and get move from server if not
			if (playerNumber == -1) {
				history.addToHistory(boardMatrix);
			} else if (!nameOfGame.equals("computer")) {
				if (playerTurn == playerNumber) {
					waitForOponent();
				} else if (playerTurn != playerNumber) {
					// if multiplayer
					// wait for server to get new move
					getBoardServerTask = new AsyncGetBoardFromServer();
					getBoardServerTask.execute(SERVER + FILE_GET_BOARD
							+ PARAMETER_GET_BOARD + "&name=" + nameOfGame);

				}
			} else if (nameOfGame.equals("computer")) {
				Random generator = new Random();
				randomID = Integer.toString(generator.nextInt(100000));
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void waitForOponent() {
		if (!joined) {
			getJoined = new isJoined();
			progressDialog = ProgressDialog.show(this,
					"Please wait for an opponent to join", "Loading...");
			progressDialog.setCancelable(true);
			progressDialog.setButton("cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							deleteGameServer = new AsyncDeleteGameFromServer();
							deleteGameServer.execute(SERVER + FILE_GET_GAMES
									+ PARAMETER_DELETE_GAME);
							// also need to delete created game
							getJoined.cancel(true);
							finish();
						}
					});
			progressDialog.setOnCancelListener(new OnCancelListener() {
				public void onCancel(DialogInterface arg0) {
					deleteGameServer = new AsyncDeleteGameFromServer();
					deleteGameServer.execute(SERVER + FILE_GET_GAMES
							+ PARAMETER_DELETE_GAME);
					// also need to delete created game
					getJoined.cancel(true);
					finish();
				}
			});
			getJoined.execute();
		}
	}

	private class isJoined extends AsyncTask<String, Void, Void> {
		public Void doInBackground(String... params) {
			while (!joined) {
				try {
					String urlOfGamesCreated = SERVER + FILE_GET_GAMES
							+ PARAMETER_GET_GAMES;
					XmlPullParser names = null;
					URL xmlUrl = new URL(urlOfGamesCreated);
					names = XmlPullParserFactory.newInstance().newPullParser();
					names.setInput(xmlUrl.openStream(), null);

					if (names != null) {
						int eventType = -1;
						// Find Score records from XML
						while (eventType != XmlResourceParser.END_DOCUMENT) {
							if (eventType == XmlResourceParser.START_TAG) {
								// Get the name of the tag (eg scores or score)
								String strName = names.getName();
								if (strName.equals("game")) {
									String gameName = names.getAttributeValue(
											null, "name");
									if (gameName.equals(nameOfGame)) {
										String isJoined = names
												.getAttributeValue(null,
														"joined");
										if (isJoined.equals("true")) {
											joined = true;
										}
									}
								}
							}
							eventType = names.next();
						}
					}
				} catch (Exception e) {
					Log.e("log_tag", "Error in http connection " + e.toString());
				}
			}
			progressDialog.dismiss();
			return null;

		}
	}

	private void restoreMyState(Bundle savedInstanceState) {
		boardMatrix = new BoardMatrix(
				savedInstanceState.getStringArray("boardMatrix"));
		playerTurn = savedInstanceState.getInt("playerTurn");
		String[] historyTakenWhite = savedInstanceState
				.getStringArray("historyTakenWhite");
		String[] historyTakenBlack = savedInstanceState
				.getStringArray("historyTakenBlack");
		String[] historyStringArray = savedInstanceState
				.getStringArray("history");
		for (int i = 0; i < historyStringArray.length; i++) {
			String[] historyArray = { historyStringArray[i],
					historyTakenBlack[i], historyTakenWhite[i] };
			history.addToHistory(new BoardMatrix(historyArray));
		}
		joined = savedInstanceState.getBoolean("joined");
		isIllegalPutsYouInCheck = savedInstanceState
				.getBoolean("isIllegalPutsYouInCheck");
		isInCheck = savedInstanceState.getBoolean("isInCheck");
		isCheckMate = savedInstanceState.getBoolean("isCheckMate");
		winner = savedInstanceState.getInt("winner");
		nameOfGame = savedInstanceState.getString("nameOfGame");
		playerNumber = savedInstanceState.getInt("playerNumber");
		playerTurnText = (TextView) findViewById(R.id.playerTurnText);
		if (playerTurn == 0) {
			playerTurnText.setText("Player One's Turn");
		} else {
			playerTurnText.setText("Player Two's Turn");
		}
		setAlerts();
	}

	private void createMyState() {
		Bundle extras = getIntent().getExtras();
		// extras
		nameOfGame = extras != null ? extras.getString("myText")
				: "nothing passed in";
		playerNumber = extras != null ? extras.getInt("player") : -1;
		playerTurnText = (TextView) findViewById(R.id.playerTurnText);
		boardMatrix = new BoardMatrix();
	}

	private void setupView() {
		// set up view
		blackPiecesTakenGrid = (GridView) findViewById(R.id.blackPiecesTakenGrid);
		whitePiecesTakenGrid = (GridView) findViewById(R.id.whitePiecesTakenGrid);
		imageAdaptBlackPiecesTaken = new ImageAdapter(this);
		imageAdaptWhitePiecesTaken = new ImageAdapter(this);
		// set up the chess board itself and make touchable
		chess = (ChessBoardView) findViewById(R.id.chessBoardView);
		chess.setOnTouchListener(this);
		setUpButtons();
		setScreenDisplay();
		chess.setBoard(boardMatrix); // display it on the screen
	}

	// onTouch is called when the board is touched. The odd touches highlight
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == 1) {
			Square squareSelected = new Square((int) event.getY(),
					(int) event.getX(), screenwidth);
			chess.setSelectedSquare(squareSelected);

			// if its not even touch, just select square
			if (!evenTouch) {
				chess.setEvenTouch(evenTouch);
				evenTouch = true;
				lastSquareSelected = squareSelected;
			} else { // try to move to square
						//
						// If playerNumber == playerTurn then online multiplayer
						// game
						// if playerNumber == -1 then either computer game or
						// two players
						// playing on the same device
				if (playerNumber == playerTurn || playerNumber == -1) {
					boolean playable = boardMatrix.isPlayableMove(
							lastSquareSelected, squareSelected, playerTurn);
					// Check playable.
					if (playable) {
						isIllegalPutsYouInCheck = boardMatrix.isInCheck(
								lastSquareSelected, squareSelected, false,
								playerTurn);
						// check isIllegalPutsYouInCheck.
						if (!isIllegalPutsYouInCheck) {
							// move the piece.
							boardMatrix.makeMove(lastSquareSelected,
									squareSelected);
							// if playing a computer
							// tell the computer on server the move and it makes
							// it's move
							if (nameOfGame.equals("computer")) {
								getBoardEngineTask = new AsyncGetBoardFromEngine();
								getBoardEngineTask.execute(SOCKET, PORT,
										lastSquareSelected.toString()
												+ squareSelected.toString());
							} else {
								// if online game
								if (playerNumber == playerTurn) {
									// tell server the move and other player
									// makes their move
									addBoardServerTask = new AsyncAddBoardToServer();
									addBoardServerTask
											.execute(
													SERVER
															+ FILE_GET_BOARD
															+ PARAMETER_SET_BOARD
															+ "&name="
															+ nameOfGame,
													lastSquareSelected
															.toString()
															+ squareSelected
																	.toString(),
													Integer.toString(playerNumber));
								} else { // two players on same device
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
										lastSquareSelected, squareSelected,
										true, playerTurn);
								// check other player isInCheck after move
								if (isInCheck) {
									// check if this check is check mate.
									isCheckMate = boardMatrix.isCheckMate(
											lastSquareSelected, squareSelected,
											playerTurn);
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
			}
			refreshBoardState();
		}
		return false;
	}

	// if back button pressed during game, delete game from server
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// alert
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Are you sure you want to quit?");
			builder.setPositiveButton("YES",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							if (playerNumber != -1
									&& !nameOfGame.equals("computer")) {
								quitGameServer = new AsyncQuitGameFromServer();
								quitGameServer.execute(SERVER + FILE_GET_BOARD
										+ PARAMETER_QUIT_GAME);
							}
							if (nameOfGame.equals("computer")) {
								new AsyncGetBoardFromEngine().execute("quit");
							}
							finish();
						}
					});
			builder.setNegativeButton("NO",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							return;
						}
					});
			builder.show();
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

					// tell the piece on the board where they are.
					for (int i = 0; i < 8; i++) {
						for (int j = 0; j < 8; j++) {
							if (boardMatrix.getPieceAt(new Square(i, j)) != null) {
								boardMatrix.getPieceAt(new Square(i, j))
										.setSquare(i, j);
							}
						}
					}
					resetAdapters();

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
			@SuppressWarnings("deprecation")
			public void onClick(View v) {
				if (playerTurn == playerNumber || playerNumber == -1) {
					// alert
					AlertDialog.Builder builder = new AlertDialog.Builder(
							ChessPlayActivity.this);
					builder.setTitle("Are you sure you want to restart?");
					builder.setPositiveButton("YES",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									if (playerNumber != -1
											&& !nameOfGame.equals("computer")) {
										restartGameServer = new AsyncRestartGameFromServer();
										restartGameServer.execute(SERVER
												+ FILE_GET_GAMES
												+ PARAMETER_RESTART_GAME);
									} else if (nameOfGame.equals("computer")) {
										new AsyncGetBoardFromEngine()
												.execute("restart");
										restartGame();
									} else {
										restartGame();
									}
								}
							});
					builder.setNegativeButton("NO",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									return;
								}
							});
					builder.show();

				} else if (playerNumber != playerTurn) {
					AlertDialog alertDialog = new AlertDialog.Builder(
							ChessPlayActivity.this).create();
					alertDialog.setMessage("Must be your turn to restart!");
					alertDialog.setButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									return;
								}
							});
					alertDialog.show();
				}
			}
		});

		resignGame = (Button) findViewById(R.id.resignGame);
		resignGame.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// alert
				AlertDialog.Builder builder = new AlertDialog.Builder(
						ChessPlayActivity.this);
				builder.setTitle("Are you sure you want to quit?");
				builder.setPositiveButton("YES",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								if (playerNumber != -1
										&& !nameOfGame.equals("computer")) {
									quitGameServer = new AsyncQuitGameFromServer();
									quitGameServer.execute(SERVER
											+ FILE_GET_BOARD
											+ PARAMETER_QUIT_GAME);
								}
								if (nameOfGame.equals("computer")) {
									new AsyncGetBoardFromEngine()
											.execute("quit");
								}
								finish();
							}
						});
				builder.setNegativeButton("NO",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								return;
							}
						});
				builder.show();
			}
		});

	}

	private void refreshBoardState() {
		setScreenDisplay();
		setAlerts();
	}

	private void setScreenDisplay() {
		if (playerTurn == 0 && playerNumber == -1 || playerNumber != playerTurn
				&& playerTurn == 0) {
			playerTurnText.setText("Player One's Turn");
		} else if (playerTurn != 0 && playerNumber == -1
				|| playerNumber != playerTurn && playerTurn != 0) {
			playerTurnText.setText("Player Two's Turn");
		} else {
			playerTurnText.setText("Your turn");
		}
		resetAdapters();
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
			if (!isCheckMate) {
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
		}
		isInCheck = false;
		if (isCheckMate) {
	        isCheckMate = false;
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
							if (playerNumber != -1
									&& !nameOfGame.equals("computer")) {
								if (playerNumber == 0) {
									quitGameServer = new AsyncQuitGameFromServer();
									quitGameServer.execute(SERVER
											+ FILE_GET_BOARD
											+ PARAMETER_QUIT_GAME);
									finish();
								} else {
									deleteGameServer = new AsyncDeleteGameFromServer();
									deleteGameServer.execute(SERVER
											+ FILE_GET_BOARD
											+ PARAMETER_DELETE_GAME);
									finish();
								}
							}

							if (nameOfGame.equals("computer")) {
								new AsyncGetBoardFromEngine().execute("quit");
								finish();
							}
							if(playerNumber == -1){
								finish();
							}

						}
					});
			builder.setNegativeButton("PLAY AGAIN",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							if (playerNumber != -1
									&& !nameOfGame.equals("computer")) {
								if (playerNumber == 0) {
									new AsyncRestartGameFromServer()
											.execute(SERVER + FILE_GET_GAMES
													+ PARAMETER_RESTART_GAME);

									progressDialog = ProgressDialog
											.show(ChessPlayActivity.this,
													"Please wait for an opponent to decide",
													"Loading...");
									progressDialog.setCancelable(true);
									progressDialog
											.setButton(
													"cancel",
													new DialogInterface.OnClickListener() {
														public void onClick(
																DialogInterface dialog,
																int which) {
															quitGameServer = new AsyncQuitGameFromServer();
															quitGameServer
																	.execute(SERVER
																			+ FILE_GET_BOARD
																			+ PARAMETER_QUIT_GAME);
															finish();
														}
													});
									progressDialog
											.setOnCancelListener(new OnCancelListener() {
												public void onCancel(
														DialogInterface arg0) {
													quitGameServer = new AsyncQuitGameFromServer();
													quitGameServer
															.execute(SERVER
																	+ FILE_GET_BOARD
																	+ PARAMETER_QUIT_GAME);
													finish();
												}
											});
									new checkForDeletedGame().execute(SERVER
											+ "chessBoards/" + nameOfGame
											+ ".xml");

								} else {
									progressDialog = ProgressDialog
											.show(ChessPlayActivity.this,
													"Please wait for an opponent to decide",
													"Loading...");
									progressDialog.setCancelable(true);
									progressDialog
											.setButton(
													"cancel",
													new DialogInterface.OnClickListener() {
														public void onClick(
																DialogInterface dialog,
																int which) {
															deleteGameServer = new AsyncDeleteGameFromServer();
															deleteGameServer
																	.execute(SERVER
																			+ FILE_GET_GAMES
																			+ PARAMETER_DELETE_GAME);
															// also need to
															// delete created
															// game
															getJoined
																	.cancel(true);
															finish();
														}
													});
									progressDialog
											.setOnCancelListener(new OnCancelListener() {
												public void onCancel(
														DialogInterface arg0) {
													deleteGameServer = new AsyncDeleteGameFromServer();
													deleteGameServer
															.execute(SERVER
																	+ FILE_GET_GAMES
																	+ PARAMETER_DELETE_GAME);
													// also need to delete
													// created game
													getJoined.cancel(true);
													finish();
												}
											});
									new checkForRestartOrQuit().execute();
									restartGame();
								}
							}
							if (nameOfGame.equals("computer")) {
								new AsyncGetBoardFromEngine()
										.execute("restart");
								restartGame();
							}
							if(playerNumber == -1){
								restartGame();
							}
						}
					});
			builder.show();

		}
	}

	private void resetAdapters() {
		imageAdaptBlackPiecesTaken.clearAdapter();
		imageAdaptWhitePiecesTaken.clearAdapter();
		for (ChessPiece piece : boardMatrix.getWhiteTaken()) {
			imageAdaptBlackPiecesTaken.addImage(piece.getResourceName());
		}
		for (ChessPiece piece : boardMatrix.getBlackTaken()) {
			imageAdaptWhitePiecesTaken.addImage(piece.getResourceName());
		}
		blackPiecesTakenGrid.setAdapter(imageAdaptBlackPiecesTaken);
		whitePiecesTakenGrid.setAdapter(imageAdaptWhitePiecesTaken);
	}

	private void restartGame() {
		boardMatrix.restartGame();
		if (playerNumber == -1) {
			history.clear();
			history.addToHistory(boardMatrix);
		}
		isCheckMate = false;
		undo.setEnabled(false);
		chess.setBoard(boardMatrix);
		chess.invalidate();
		if (playerTurn == 1)
			playerTurn = 0;
		// what about online????
		playerTurnText.setText("Player One's Turn");
		resetAdapters();
	}

	/* ****************************************************************
	 * 
	 * these methods control syncing with the server
	 * 
	 * ****************************************************************
	 */

	private class checkForDeletedGame extends
			AsyncTask<String, String, Boolean> {
		boolean restart = true;
		boolean fileExhists = true;

		@Override
		protected Boolean doInBackground(String... params) {
			boolean result = false;

			while (restart && fileExhists) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}

				try {
					HttpURLConnection.setFollowRedirects(false);
					// note : you may also need
					// HttpURLConnection.setInstanceFollowRedirects(false)
					HttpURLConnection con = (HttpURLConnection) new URL(
							params[0]).openConnection();
					con.setRequestMethod("HEAD");

					fileExhists = (con.getResponseCode() == HttpURLConnection.HTTP_OK);
				} catch (Exception e) {
					e.printStackTrace();
					fileExhists = false;
				}

				try {
					String urlOfGamesCreated = "http://128.32.37.29:81/web/chessServer/getBoard.php"
							+ "?getBoardNoChange=getBoardNoChange&name="
							+ nameOfGame;
					XmlPullParser names = null;
					URL xmlUrl = new URL(urlOfGamesCreated);
					names = XmlPullParserFactory.newInstance().newPullParser();
					names.setInput(xmlUrl.openStream(), null);
					if (names != null) {
						processMyNames(names);
					}

				} catch (Exception e) {
				}
				if (isCancelled())
					break;
			}
			publishProgress();
			return result;
		}

		protected void processMyNames(XmlPullParser names)
				throws XmlPullParserException, IOException {
			int eventType = -1;
			while (eventType != XmlResourceParser.END_DOCUMENT) {
				if (eventType == XmlResourceParser.START_TAG) {
					String strName = names.getName();
					if (strName.equals("test")) {
						String isRestart = names.getAttributeValue(null,
								"restart");
						if (isRestart.equals("false")) {
							restart = false;
							break;
						}
					}
				}
				eventType = names.next();
			}

		}

		@SuppressWarnings("deprecation")
		protected void onProgressUpdate(String... values) {
			progressDialog.dismiss();
			if (!fileExhists) {
				AlertDialog alertDialog = new AlertDialog.Builder(
						ChessPlayActivity.this).create();
				alertDialog.setMessage("Opponent has quit the game!");
				alertDialog.setButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								return;
							}
						});
				alertDialog.show();
				finish();
			} else if (!restart) {
				AlertDialog alertDialog = new AlertDialog.Builder(
						ChessPlayActivity.this).create();
				alertDialog.setMessage("Opponent has restarted the game!");
				alertDialog.setButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								return;
							}
						});
				alertDialog.show();
			}
		}
	}

	// need a loadiing sign
	private class checkForRestartOrQuit extends
			AsyncTask<String, String, Boolean> {
		boolean restart = false;
		boolean quit = false;

		@Override
		protected Boolean doInBackground(String... params) {
			boolean result = false;

			while (!restart && !quit) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				try {
					String urlOfGamesCreated = "http://128.32.37.29:81/web/chessServer/getBoard.php"
							+ "?getBoard=getBoard&name=" + nameOfGame;
					XmlPullParser names = null;
					URL xmlUrl = new URL(urlOfGamesCreated);
					names = XmlPullParserFactory.newInstance().newPullParser();
					names.setInput(xmlUrl.openStream(), null);
					if (names != null) {
						processMyNames(names);
					}

				} catch (Exception e) {
				}
				if (isCancelled())
					break;
			}
			return result;
		}

		protected void processMyNames(XmlPullParser names)
				throws XmlPullParserException, IOException {
			int eventType = -1;
			while (eventType != XmlResourceParser.END_DOCUMENT) {
				if (eventType == XmlResourceParser.START_TAG) {
					String strName = names.getName();
					if (strName.equals("test")) {
						String isQuit = names.getAttributeValue(null, "quit");
						if (isQuit.equals("true")) {

							quit = true;
							break;
						}
						String isRestart = names.getAttributeValue(null,
								"restart");
						if (isRestart.equals("true")) {
							restart = true;
							break;
						}
					}
				}
				eventType = names.next();
			}

		}

		@SuppressWarnings("deprecation")
		protected void onPostExecute(Boolean result) {
			progressDialog.dismiss();
			if (quit) {
				AlertDialog alertDialog = new AlertDialog.Builder(
						ChessPlayActivity.this).create();
				alertDialog.setMessage("Opponent has quit the game!");
				alertDialog.setButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								return;
							}
						});
				alertDialog.show();
				deleteGameServer = new AsyncDeleteGameFromServer();
				deleteGameServer.execute(SERVER + FILE_GET_GAMES
						+ PARAMETER_DELETE_GAME);
				finish();
			} else if (restart) {
				AlertDialog alertDialog = new AlertDialog.Builder(
						ChessPlayActivity.this).create();
				alertDialog.setMessage("Opponent has restarted the game!");
				alertDialog.setButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								return;
							}
						});
				alertDialog.show();
				restartGame();
				if (playerTurn != playerNumber) {
					// if multiplayer
					// wait for server to get new move
					getBoardServerTask = new AsyncGetBoardFromServer();
					getBoardServerTask.execute(SERVER + FILE_GET_BOARD
							+ PARAMETER_GET_BOARD + "&name=" + nameOfGame);

				}
			}

		}
	}

	private class AsyncRestartGameFromServer extends
			AsyncTask<String, String, Boolean> {
		@Override
		protected Boolean doInBackground(String... params) {
			getBoardServerTask.cancel(true);

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

		protected void onPostExecute(Boolean result) {
			restartGame();
			if (playerTurn != playerNumber) {
				// if multiplayer
				// wait for server to get new move
				getBoardServerTask = new AsyncGetBoardFromServer();
				getBoardServerTask.execute(SERVER + FILE_GET_BOARD
						+ PARAMETER_GET_BOARD + "&name=" + nameOfGame);

			}
		}
	}

	private class AsyncQuitGameFromServer extends
			AsyncTask<String, String, Boolean> {
		@Override
		protected Boolean doInBackground(String... params) {
			Boolean success = false;
			getBoardServerTask.cancel(true);
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
				// getBoardServerTask = new AsyncGetBoardFromServer();
				// getBoardServerTask.execute(SERVER + FILE_GET_BOARD
				// + PARAMETER_GET_BOARD + "&name=" + nameOfGame);
			}
			return success;
		}

	}

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
			nameValuePairs
					.add(new BasicNameValuePair("mPlayerTurn", params[2]));
			// Create a new HttpClient and Post Header
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(params[0]);
			try {
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				
				@SuppressWarnings("unused")
				HttpResponse response = httpclient.execute(httppost);
			} catch (ClientProtocolException e) {
			} catch (IOException e) {
			}

			return null;
		}

		protected void onPostExecute(Boolean result) {
			getBoardServerTask = new AsyncGetBoardFromServer();
			getBoardServerTask.execute(SERVER + FILE_GET_BOARD
					+ PARAMETER_GET_BOARD + "&name=" + nameOfGame);
		}
	}

	private class AsyncGetBoardFromServer extends
			AsyncTask<String, String, Boolean> {
		String gottenMove = "";
		int gottenPlayerTurn = -1;
		boolean restart = false;
		boolean quit = false;

		@Override
		protected Boolean doInBackground(String... params) {
			boolean result = false;
			// doesnt ever stop running someones turn so you should put
			// iscancelled at some point when resign game
			while (((gottenPlayerTurn == playerNumber || gottenPlayerTurn == -1) && !restart)
					&& !quit) {
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
				if (isCancelled())
					break;
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
					if (strName.equals("test")) {
						String isQuit = names.getAttributeValue(null, "quit");
						if (isQuit.equals("true")) {
							quit = true;
							break;
						}
						String isRestart = names.getAttributeValue(null,
								"restart");
						if (isRestart.equals("true")) {
							restart = true;
							break;
						}

					} else if (strName.equals("square")) {
						gottenMove = names.getAttributeValue(null, "move");
						gottenPlayerTurn = Integer.parseInt(names
								.getAttributeValue(null, "mPlayerTurn"));
					}
				}
				eventType = names.next();
			}
			if (gottenPlayerTurn != playerNumber) {
				boardMatrix.makeMove(new Square(gottenMove.substring(0, 2)),
						new Square(gottenMove.substring(2)));
			}

		}

		@SuppressWarnings("deprecation")
		protected void onProgressUpdate(String... values) {
			if (quit) {
				AlertDialog alertDialog = new AlertDialog.Builder(
						ChessPlayActivity.this).create();
				alertDialog.setMessage("Opponent has quit the game!");
				alertDialog.setButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								deleteGameServer = new AsyncDeleteGameFromServer();
								deleteGameServer.execute(SERVER + FILE_GET_GAMES
										+ PARAMETER_DELETE_GAME);
								finish();
							}
						});
				alertDialog.show();
				
				
			} else if (restart) {
				AlertDialog alertDialog = new AlertDialog.Builder(
						ChessPlayActivity.this).create();
				alertDialog.setMessage("Opponent has restarted the game!");
				alertDialog.setButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								return;
							}
						});
				alertDialog.show();
				restartGame();

				if (playerTurn != playerNumber) {
					// if multiplayer
					// wait for server to get new move
					getBoardServerTask = new AsyncGetBoardFromServer();
					getBoardServerTask.execute(SERVER + FILE_GET_BOARD
							+ PARAMETER_GET_BOARD + "&name=" + nameOfGame);

				}
			} else {
				// change player turn
				if (playerTurn == 0) {
					playerTurn = 1;
				} else {
					playerTurn = 0;
				}

				isInCheck = boardMatrix.isInCheck(
						new Square(gottenMove.substring(0, 2)), new Square(
								gottenMove.substring(2)), true, playerTurn);
				// check other player isInCheck after move
				if (isInCheck) {
					// check if this check is check mate.
					isCheckMate = boardMatrix.isCheckMate(
							new Square(gottenMove.substring(0, 2)), new Square(
									gottenMove.substring(2)), playerTurn);
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
				refreshBoardState();
			}
		}
	}

	private class AsyncGetBoardFromEngine extends
			AsyncTask<String, String, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			boolean result = false;

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

				out.println(move + " " + randomID);
				String computerMove = in.readLine();
				out.close();
				in.close();

				mySocket.close();
				if (!computerMove.equals("quit")) {
					if (!computerMove.equals("restart")) {
						makeMove(computerMove);
					}
				}
			} catch (Exception e) {
			}
			publishProgress();
			return result;
		}

		protected void makeMove(String move) {
			Square start = new Square(move.substring(0, 2));
			Square end = new Square(move.substring(2));
			boardMatrix.makeMove(start, end);
		}

		protected void onProgressUpdate(String... values) {
			playerTurn = 0;
			chess.setBoard(boardMatrix);
			chess.invalidate();
			refreshBoardState();
		}

	}

	/* ****************************************************************
	 * 
	 * these methods control the state of the activity.
	 * 
	 * ****************************************************************
	 */

	@Override
	protected void onStart() {
		super.onStart();
		// The activity is about to become visible.
	}

	@Override
	protected void onResume() {
		super.onResume();
		// The activity has become visible (it is now "resumed").
	}

	@Override
	protected void onPause() {
		super.onPause();
		// Another activity is taking focus (this activity is about to be
		// "paused").
	}

	@Override
	protected void onStop() {
		Bundle b = new Bundle();
		onSaveInstanceState(b);
		super.onStop();

		// The activity is no longer visible (it is now "stopped")
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// Save the user's current game state
		savedInstanceState.putStringArray("boardMatrix",
				boardMatrix.toStringArray());
		savedInstanceState.putInt("playerTurn", playerTurn);
		savedInstanceState.putStringArray("history",
				history.toStringArrayBoards());
		savedInstanceState.putStringArray("historyTakenBlack",
				history.toStringArrayTakenBlack());
		savedInstanceState.putStringArray("historyTakenWhite",
				history.toStringArrayTakenWhite());
		savedInstanceState.putBoolean("isIllegalPutsYouInCheck",
				isIllegalPutsYouInCheck);
		savedInstanceState.putBoolean("isInCheck", isInCheck);
		savedInstanceState.putBoolean("isCheckMate", isCheckMate);
		savedInstanceState.putInt("winner", winner);
		savedInstanceState.putString("nameOfGame", nameOfGame);
		savedInstanceState.putInt("playerNumber", playerNumber);
		savedInstanceState.putBoolean("joined", joined);

		// Always call the superclass so it can save the view hierarchy state
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	protected void onDestroy() {

		// getBoardServerTask.cancel(true);
		// deleteGameServer.cancel(true);
		// getBoardEngineTask.cancel(true);
		// addBoardServerTask.cancel(true);
		super.onDestroy();
		// The activity is about to be destroyed.
	}

	// variable declarations
	private ChessBoardView chess; // the board view, passed a boardMatrix to
	// make moves
	private BoardMatrix boardMatrix;

	// state of game variables
	private int playerTurn = 0;
	private History history = new History();

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

	private boolean joined = false;
	private ProgressDialog progressDialog;

	// server info, used for multiplayer
	final private String SERVER = "http://128.32.37.29:81/web/chessServer/";
	final private String FILE_GET_BOARD = "getBoard.php";
	final private String FILE_GET_GAMES = "getGames.php";
	final private String PARAMETER_SET_BOARD = "?setBoard=setBoard";
	final private String PARAMETER_GET_BOARD = "?getBoard=getBoard";
	final private String PARAMETER_DELETE_GAME = "?deleteGame=deleteGame";
	final private String PARAMETER_RESTART_GAME = "?restartGame=restartGame";
	final private String PARAMETER_QUIT_GAME = "?quitBoard=quitBoard";
	final private String PARAMETER_GET_GAMES = "?getGames=getGames";

	// socket info used for computer play
	final private String SOCKET = "128.32.37.29";
	final private String PORT = "10000";

	private String randomID;
	private int screenwidth;
	

	private AsyncGetBoardFromServer getBoardServerTask;
	private AsyncDeleteGameFromServer deleteGameServer;
	private AsyncGetBoardFromEngine getBoardEngineTask;
	private AsyncAddBoardToServer addBoardServerTask;
	private AsyncRestartGameFromServer restartGameServer;
	private AsyncQuitGameFromServer quitGameServer;
	private isJoined getJoined;
}
