package uk.ac.kcl.www;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Intent;

// Delete this later on.
import android.view.MenuInflater;
import android.view.Menu;


public class MainActivity extends Activity
{
    private Intent intent;
		/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);							
    }
		public void startSinglePlayer(View view)
		{
				// Prepares the Intent for the Single player Activity.
				Intent intent = new Intent(this, SinglePlayerGame.class);
				// Load the SinglePlayer Activity.
				startActivity(intent);
		}
		public void startMultiplayer(View view)
		{
				// Prepares the Intent for the Multiplayer Activity.
				Intent intent = new Intent(this, CheckersGame.class);
				// Load the Multiplayer Activity.
				startActivity(intent);
		}
		// Just a test and shizz.
		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
				// Inflate the menu items for use in the action bar
				MenuInflater inflater = getMenuInflater();
				inflater.inflate(R.menu.menu_item, menu);
				return super.onCreateOptionsMenu(menu);
		}
}
