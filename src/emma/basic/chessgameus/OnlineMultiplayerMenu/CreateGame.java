package emma.basic.chessgameus.OnlineMultiplayerMenu;

import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import emma.basic.chessgameus.R;
import emma.basic.chessgameus.ChessBoard.ChessPlayActivity;



import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/*creates a game online and then begins chessPlayActivity
 * 
 */


//this activitys does not get added to the history stack.
public class CreateGame extends Activity {
	EditText nameOfGame;
	Button createNewGame;
	final private String SERVER = "http://128.32.37.29:81/web/chessServer/";
	final private String FILE_GET_GAMES = "getGames.php";
	final private String PARAMETER_SET_GAME = "?setGame=setGame";
	String name;
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_game);

		createNewGame = (Button) findViewById(R.id.createGame);
		// must not be spaces in game name
		nameOfGame = (EditText) findViewById(R.id.enterGameNameEdit);

		createNewGame.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				name = nameOfGame.getText().toString();
				Intent i = new Intent(CreateGame.this, ChessPlayActivity.class);
				i.putExtra("myText", name);
				i.putExtra("player", 0);
				startActivity(i);
				new AsyncCreateGame().execute(SERVER + FILE_GET_GAMES
						+ PARAMETER_SET_GAME + "&name=" + name);
				// will always be player one in this situation...
			}
		});
	}

	private class AsyncCreateGame extends AsyncTask<String, Object, Boolean> {
		@Override
		protected Boolean doInBackground(String... params) {
			Boolean success = false;
			String urlOfGamesCreated = params[0];

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
        // Another activity is taking focus (this activity is about to be "paused").
    }
    @Override
    protected void onStop() {
        super.onStop();
        // The activity is no longer visible (it is now "stopped")
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // The activity is about to be destroyed.
    }

}
