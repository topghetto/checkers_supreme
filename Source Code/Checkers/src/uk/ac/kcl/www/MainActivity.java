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
				
				//Button singlePlayerBtn = (Button) findViewById(R.id.single_player_btn);
				
				
				// Whenever I would try to implement a GridView, the application would just crash on launch, and I believe
				// it was because of this intent.
				/*intent = new Intent(this, CheckersGame.class);
				
				singlePlayerBtn.setOnClickListener(new View.OnClickListener(){
						public void onClick(View v){
								
								// It shall start the new activity, which is the 'single player' game
								startActivity(intent);
						}
				});*/
				
    }
}
