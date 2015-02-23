package uk.ac.kcl.www;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Intent;


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
		public void startMultiplayer(View view)
		{
				// Prepares the Intent for the Multiplayer Activity.
				Intent intent = new Intent(this, CheckersGame.class);
				// Load the Multiplayer Activity.
				startActivity(intent);
		}
}
