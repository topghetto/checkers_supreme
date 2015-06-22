package uk.ac.kcl.www;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Intent;


public class MainActivity extends Activity
{
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
				Intent intent = new Intent(this, SelectDifficulty.class);
				// Load the SinglePlayer Activity.
				startActivity(intent);
		}
		public void startMultiplayer(View view)
		{
				// Prepares the Intent for the Multiplayer Activity.
				Intent intent = new Intent(this, MultiplayerGame.class);
				// Load the Multiplayer Activity.
				startActivity(intent);
		}
		public void startSpectate(View view)
		{
				// Prepares the Intent for the Multiplayer Activity.
				Intent intent = new Intent(this, SpectateGame.class);
				// Load the Multiplayer Activity.
				startActivity(intent);
		}
		public void exitGame(View view)
		{
				// Closes the entire game.
				finish();
		}
}
