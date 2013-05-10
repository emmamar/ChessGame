package emma.basic.chessgameus;

import emma.basic.chessgameus.OnlineMultiplayerMenu.MultiOnlineMenu;
import emma.basic.chessgameus.infopages.About;
import emma.basic.chessgameus.infopages.Rules;
import emma.basic.chessgameus.ChessBoard.ChessPlayActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/*This is the MAIN program. It displays the first options page
 * 
 */

public class ChessGameUSActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		/** Choice to play computer. */
		Button playComputer = (Button) findViewById(R.id.playComputer);
		playComputer.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(ChessGameUSActivity.this,
						ChessPlayActivity.class);
				i.putExtra("myText", "computer"); // error if someone names
													// computer
				i.putExtra("player", 0);
				startActivity(i);
			}
		});

		/** Choice to play multi-player on device. */
		Button playMultiOnDevice = (Button) findViewById(R.id.playMultiOnDevice);
		playMultiOnDevice.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(ChessGameUSActivity.this,
						ChessPlayActivity.class);
				startActivity(i);
			}
		});

		/** Choice to play multi-player online. */
		Button playMultiOnline = (Button) findViewById(R.id.playMultiOnline);
		playMultiOnline.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(ChessGameUSActivity.this,
						MultiOnlineMenu.class);
				startActivity(i);
			}
		});
		
		
		/** Choice to play multi-player online. */
		Button rules = (Button) findViewById(R.id.rulesButton);
		rules.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(ChessGameUSActivity.this,
						Rules.class);
				startActivity(i);
			}
		});
		
		/** Choice to play multi-player online. */
		Button about = (Button) findViewById(R.id.aboutButton);
		about.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(ChessGameUSActivity.this,
						About.class);
				startActivity(i);
			}
		});
		
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
		// Another activity is taking focus (this activity is about to be
		// "paused").
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
