package emma.basic.chessgameus;

import emma.basic.chessgameus.OnlineMultiplayerMenu.MultiOnlineMenu;
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
	}
}
