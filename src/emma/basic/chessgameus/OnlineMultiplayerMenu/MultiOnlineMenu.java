package emma.basic.chessgameus.OnlineMultiplayerMenu;

import emma.basic.chessgameus.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MultiOnlineMenu extends Activity {
	Button createNew;
	Button joinCurrent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.multi_online_menu);

		createNew = (Button) findViewById(R.id.createGame);

		createNew.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(MultiOnlineMenu.this, CreateGame.class);
				startActivity(i);
			}
		});

		joinCurrent = (Button) findViewById(R.id.joinGame);

		joinCurrent.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(MultiOnlineMenu.this, JoinGame.class);
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
