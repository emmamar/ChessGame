package emma.basic.chessgameus.infopages;

import emma.basic.chessgameus.R;
import emma.basic.chessgameus.R.layout;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class Rules extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rules);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.rules, menu);
		return true;
	}

}
