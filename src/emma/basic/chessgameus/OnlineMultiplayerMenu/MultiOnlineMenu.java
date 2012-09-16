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

}
