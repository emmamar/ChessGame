package emma.basic.chessgameus.OnlineMultiplayerMenu;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import emma.basic.chessgameus.R;
import emma.basic.chessgameus.ChessBoard.ChessPlayActivity;



import android.app.Activity;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

//this activitys does not get added to the history stack.
public class JoinGame extends Activity {
	final private String SERVER = "http://128.32.37.29:81/web/chessServer/";
	final private String FILE_GET_GAMES = "getGames.php";
	final private String PARAMETER_GET_GAMES = "?getGames=getGames";
	final private String PARAMETER_CHANGE_AVAILABLE = "?changeAvailable=changeAvailable";

	ListView list;
	ArrayList<String> listInput = new ArrayList<String>();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.join_game);
		list = (ListView) findViewById(R.id.availableGamesList);
		new AsyncJoinGame().execute(SERVER + FILE_GET_GAMES
				+ PARAMETER_GET_GAMES);

	}

	// gives all the available games in a list.
	private class AsyncJoinGame extends AsyncTask<String, String, Boolean> {
		@Override
		protected Boolean doInBackground(String... params) {
			boolean result = false;
			try {
				String urlOfGamesCreated = params[0];
				XmlPullParser names = null;
				URL xmlUrl = new URL(urlOfGamesCreated);
				names = XmlPullParserFactory.newInstance().newPullParser();
				names.setInput(xmlUrl.openStream(), null);

				if (names != null) {
					processNames(names);
				}
				return result;
			} catch (Exception e) {
				Log.e("log_tag", "Error in http connection " + e.toString());
				return result;
			}
		}

		protected void processNames(XmlPullParser names)

		throws XmlPullParserException, IOException {
			int eventType = -1;
			boolean bFoundNames = false;
			// Find Score records from XML
			while (eventType != XmlResourceParser.END_DOCUMENT) {
				if (eventType == XmlResourceParser.START_TAG) {
					// Get the name of the tag (eg scores or score)
					String strName = names.getName();
					if (strName.equals("game")) {
						bFoundNames = true;
						String available = names.getAttributeValue(null,
								"available");
						if (available.equals("true")) {
							String gameName = names.getAttributeValue(null,
									"name");
							publishProgress(gameName);
						}
					}
				}
				eventType = names.next();
			}
			// Handle no questions available
			if (bFoundNames == false) {
				publishProgress();
			}
		}

		protected void onProgressUpdate(String... values) {
			listInput.add(values[0]);
			String[] listInputAsArray = listInput.toArray(new String[listInput
					.size()]);
			list.setAdapter(new ArrayAdapter<String>(JoinGame.this,
					R.layout.list_item, listInputAsArray));

			list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent,
						View itemClicked, int position, long id) {
					TextView textView = (TextView) itemClicked;
					String name = textView.getText().toString();
					new AsyncChangeAvailablity()
							.execute(SERVER + FILE_GET_GAMES
									+ PARAMETER_CHANGE_AVAILABLE, name);
					// clear list and populate again with new games.
					// listInput.clear();
					// new AsyncJoinGame().execute(SERVER + EXTENTION +
					// PARAMETER);
					Intent i = new Intent(JoinGame.this,
							ChessPlayActivity.class);
					i.putExtra("myText", name);
					i.putExtra("player", 1);
					startActivity(i);

					// always player 2
				}

			});
		}
	}

	// changes the avaiablility of the game you join..
	private class AsyncChangeAvailablity extends
			AsyncTask<String, String, Boolean> {
		@Override
		protected Boolean doInBackground(String... params) {
			Boolean success = false;
			String urlOfGamesCreated = params[0];
			urlOfGamesCreated += "&name=" + params[1];
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
}
