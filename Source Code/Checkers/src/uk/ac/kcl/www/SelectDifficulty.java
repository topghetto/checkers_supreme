package uk.ac.kcl.www;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Intent;


public class SelectDifficulty extends Activity
{
  // These are the button we will use to start up the SinglePlayerGame Activity, and as well as passing in a 'depth'.
  public Button easyBtn, normalBtn, hardBtn, extremelyHardBtn;
  // 
  public final static String DIFFICULTY_MODE = "Checkers.DEPTH";
  
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.select_difficulty);
    
  }
  public void startEasy(View view)
  {
    // Prepares the Intent for the Single player Activity.
    Intent intent = new Intent(this, SinglePlayerGame.class);
    // Add the depth.
    intent.putExtra(DIFFICULTY_MODE, "2");
    // Load the SinglePlayer Activity.
    startActivity(intent);
  }
  public void startNormal(View view)
  {
    // Prepares the Intent for the Single player Activity.
    Intent intent = new Intent(this, SinglePlayerGame.class);
    // Add the depth.
    intent.putExtra(DIFFICULTY_MODE, "3");
    // Load the SinglePlayer Activity.
    startActivity(intent);
  }
  public void startHard(View view)
  {
    // Prepares the Intent for the Single player Activity.
    Intent intent = new Intent(this, SinglePlayerGame.class);
    // Add the depth.
    intent.putExtra(DIFFICULTY_MODE, "7");
    // Load the SinglePlayer Activity.
    startActivity(intent);
  }
  public void startExtremelyHard(View view)
  {
    // Prepares the Intent for the Single player Activity.
    Intent intent = new Intent(this, SinglePlayerGame.class);
    // Add the depth.
    intent.putExtra(DIFFICULTY_MODE, "9");
    // Load the SinglePlayer Activity.
    startActivity(intent);
  }
}
