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

// Yup, just a test.
import java.util.ArrayList;

public class MainActivity extends Activity
{
		private Intent intent;
		/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
				
				// A test on replicating an ArrayList.
				/*
				ArrayList<Integer> original = new ArrayList<Integer>();
				original.add(new Integer(3));
				original.add(new Integer(5));
				
				ArrayList<Integer> duplicate =  (ArrayList<Integer>) original.clone();
				duplicate.add(new Integer(23));
				
				// Oh shit, so, the original gets 23 added to it. Thank God, I tested it here before I used it in that recursion method consecutiveCaptures()
				// Using .clone() did the trick :)
				System.out.println("Now, we will output the contents of the original ArrayList.");
				System.out.println(original);
				// Yup, it works fine now :)
				System.out.println("Now, we will output the contents of the duplicate ArrayList.");
				System.out.println(duplicate);
				*/
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
}
