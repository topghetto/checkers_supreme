package uk.ac.kcl.www;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ActionBar;
import android.os.Bundle;
import android.content.Context;

import android.widget.Button;
import android.widget.TextView;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.ProgressBar;

import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;

import 	android.graphics.drawable.ColorDrawable;



// I forgot to declare the class as public, and in result, it could not load this activity from the main menu
public class SpectateGame extends Activity{
	
	public GridLayout checkersBoardGL;
	public View inflateSquare;
	public static ImageView imageOfSquares[][];
	public View squaresOfBoard[][];
	// This will hold the String representation of the checkers board.
	public String	strCheckersBoard[][];
  // Handles all the events.
	public SpectateEvents playerEvents;
	// Displays whose turn it is.
	public TextView playerInfo;
	// Image of the player's piece.
	public ImageView playerImage;
	// Information about the Bot.
	public TextView loadingInfo;
	// Loading wheel for AI.
	public ProgressBar loadingWheel;
	
	// Start button for spectating
	public Button startBtn;
	// Information for the seekbar.
	public TextView speedInfo;
	// Progress bar.
	public SeekBar adjustSpeedBar;
	
	
	@Override
	public void onStop()
	{
		super.onStop();	
		System.out.println("The Activity has now stopped");
		
		// This will stop the Thread from running when we spontaneously return to the main menu.
		SpectateEvents.isPaused = false;
		
	}
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		// I need to create a different xml layout... Actually, I will do it now.
		setContentView(R.layout.checkers_game);
		// Grabs the layout of the checkersboard
		checkersBoardGL = (GridLayout) findViewById(R.id.checkersboard_gridlayout);
		
		// AN IMPORTANT NOTE ! IF I GET A PROBLEM WHENEVER I ADD A NEW VIEW IN THE XML
		// COMMENT checkersBoardGL --> Compile --> Run --> Enable checkersBoardGL --> Complie --> Run.
		
		// All the images of the squares of the checkers board will be stored in here.
		imageOfSquares = new ImageView[8][8];
		// All the layouts of the squares of the checkers board will be stored in here.
		squaresOfBoard = new ViewGroup[8][8];
		// This will state whose turn it is to make a move.
		playerInfo = (TextView) findViewById(R.id.playerInfo);
		
		// Grabs the ActionBar.
		ActionBar actionBar = getActionBar();
		// Hides the ActionBar.
		actionBar.hide();
		
		// Initialise the TextView for the display additional information with the loading wheel.
    loadingInfo = (TextView) findViewById(R.id.loading_info);
		// Initialise the ImageView
		playerImage = (ImageView) findViewById(R.id.player_image);
		// Initialise the ProgressBar.		
		loadingWheel = (ProgressBar) findViewById(R.id.progress_bar_for_bot);
		// We will hide the wheel on startup.
		loadingWheel.setVisibility(View.INVISIBLE); 
		
		// Grab the start button.
		startBtn = (Button) findViewById(R.id.start_btn);
		// Grab the seekbar information.
		speedInfo = (TextView) findViewById(R.id.seekbar_info);
		// Grab the seekbar...
		adjustSpeedBar = (SeekBar) findViewById(R.id.adjust_speed);
		
		
		// Defaults the thumb in the bar to be in the center.
		adjustSpeedBar.setProgress(4);
		// Add the event for SeekBar for adjustSpeedBar
		adjustSpeedBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
		{	
			// Initially 1 second.
			int seconds = 1;
			
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
			{
				// Yup, we want the minimum to be at least 1 - Proof (0 + 1 = 1).
				seconds = progress + 1;
				// Set the speed.
				speedInfo.setText((4000/seconds) + "ms");
				
			}
			public void onStartTrackingTouch(SeekBar seekBar)
			{
				// Set the speed.
				//int progress = seekBar.getProgress();
				// Set the speed.
				//speedInfo.setText(SpectateEvents.speed + "ms");
				// Yup, we want the minimum to be at least 1 - Proof (0 + 1 = 1).
				//seconds = progress + 1;
			}
			public void onStopTrackingTouch(SeekBar seekBar)
			{
				// Only when we let go...
				int duration = 4000 / seconds;
				// Interval will be the same size as the duration.
				int interval = duration;
				
				speedInfo.setText("Speed: " + (4000/seconds) + "ms");
				// Only when the user has taken his hands off the bar, we will assign the value of speed.
				SpectateEvents.speed = duration;
				// Debug purposes. Well, that works fine.
				System.out.println("Here are your current milliseconds: " + (4000/seconds));
				
				
				System.out.println("Here is the current speed " + duration + " ms and a interval of " + interval);
			}
		});
		
		// Testing trapped pieces for player 1.
		/*strCheckersBoard = new String[][]{{"[]","2","[]","2","[]","2","[]","2"},
																			{"2", "[]","2","[]","2","[]","2","[]"},
																			{"[]","2","[]","2","[]","0","[]","0"},
																			{"0", "[]","0","[]","0","[]","0","[]"},
																			{"[]","1","[]","0","[]","2","[]","0"},
																			{"0", "[]","0","[]","0","[]","2","[]"},
																			{"[]","0","[]","0","[]","0","[]","1"},
																			{"0", "[]","0","[]","0","[]","0","[]"}};*/
		
		// Testing trapped pieces for player 2.
		/*strCheckersBoard = new String[][]{{"[]","0","[]","0","[]","2","[]","0"},
																			{"0", "[]","0","[]","0","[]","0","[]"},
																			{"[]","0","[]","2","[]","0","[]","0"},
																			{"0", "[]","1","[]","0","[]","0","[]"},
																			{"[]","0","[]","0","[]","0","[]","2"},
																			{"0", "[]","0","[]","1","[]","1","[]"},
																			{"[]","1","[]","1","[]","1","[]","1"},
																			{"1", "[]","1","[]","1","[]","1","[]"}};*/
		
		strCheckersBoard = new String[][]{{"[]","2","[]","2","[]","2","[]","2"},
																			{"2", "[]","2","[]","2","[]","2","[]"},
																			{"[]","2","[]","2","[]","2","[]","2"},
																			{"0", "[]","0","[]","0","[]","0","[]"},
																			{"[]","0","[]","0","[]","0","[]","0"},
																			{"1", "[]","1","[]","1","[]","1","[]"},
																			{"[]","1","[]","1","[]","1","[]","1"},
																			{"1", "[]","1","[]","1","[]","1","[]"}};
		
		
		// instantiate the event class
		playerEvents = new SpectateEvents(imageOfSquares, imageOfSquares, strCheckersBoard, playerInfo, loadingInfo, loadingWheel, playerImage, startBtn);
		
		// Add the listener to the button.
		startBtn.setOnClickListener(playerEvents);
		
		// Add the blank ImageViews/Views to the board (i.e. the GridLayout).
		addSquaresToBoard();
		// Modify the ImageViews that are now part of the GridLayout based on the contents of 'strCheckersBoard' :).
		populateBoard(strCheckersBoard);
		// Assign the listener for the start button.
		// __Insert code here___ //
		
	}	
	public static void populateBoard(String[][] passStrCheckersBoard)
	{
		// I made this particular method static so, we can also use this method in the SpectateEvents java file.
		// This will update the look of the checkersboard depending on the contents of 'passStrCheckersBoard[][]'
		
		for(int row = 0; row < 8; row++)
		{
			for(int column=((row+1)%2); column<8; column+=2)
			{
				if(passStrCheckersBoard[row][column].contains("1")){
					
					if(passStrCheckersBoard[row][column].contains("K1")){
						
						// This will set the checkers' piece image to a king piece.
						imageOfSquares[row][column].setImageResource(R.drawable.king_dark_brown_piece);
						
					}else{
						
						// This will set the checkers' piece image to a standard piece.
						imageOfSquares[row][column].setImageResource(R.drawable.dark_brown_piece);
					}
				}
				else if(passStrCheckersBoard[row][column].contains("2")){
					
					if(passStrCheckersBoard[row][column].contains("K2")){
						
						// This will set the checkers' piece image to a king piece.
						imageOfSquares[row][column].setImageResource(R.drawable.king_light_brown_piece);
						
					}else{
						
						// This will set the checkers' piece image to a standard piece.
						imageOfSquares[row][column].setImageResource(R.drawable.light_brown_piece);
					}
				}
				else if(passStrCheckersBoard[row][column].contains("0")){
					
					// We do something because if there is an empty square, we need to clear the image of the ImageView.
					imageOfSquares[row][column].setImageResource(0);
				}
			}
		}
	}
	public void addSquaresToBoard()
	{
		// This method creates the proper layouts, and adds the blank ImageViews/Views to the board, which we will later
		// paint using the populateBoard() method.
		
		// Grabs the LayoutInflater, and stores it in a readily available variable
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		for(int row = 0; row < 8;row++)
		{
			for(int column = 0; column < 8;column++)
			{
				// Inflates the 'View' with the layout of the given XML.
				inflateSquare = inflater.inflate(R.layout.gridlayout_view, checkersBoardGL, false);
				// Store the ImageView specified in the parsed XML layout, into a runtime ImageView for further modification.
				ImageView imageOfSquare = (ImageView) inflateSquare.findViewById(R.id.image_square);
				// Store the 'View' in the multidimensional array of views.
				squaresOfBoard[row][column] = inflateSquare;
				// We will add an blank image which will later modify through the 'populateBoard()' method.
				imageOfSquares[row][column] = imageOfSquare;
				// Adds the square to the checkers board (i.e the grid layout specified in the XML) :)
				checkersBoardGL.addView(inflateSquare);		
			}
		}
	}
}
