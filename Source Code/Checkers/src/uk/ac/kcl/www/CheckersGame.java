package uk.ac.kcl.www;

import java.util.ArrayList;

import android.util.Log;

import android.app.Activity;
import android.app.ActionBar;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;

// These are all the GridView dependencies

import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.GridLayout;
import android.widget.BaseAdapter;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Adapter;
import android.widget.Toast;
import android.widget.ImageView;
import android.widget.TextView;

import android.view.ViewGroup.*;
import android.view.Gravity;
import android.view.LayoutInflater;

import 	android.graphics.drawable.ColorDrawable;

import android.widget.ProgressBar;

// I forgot to declare the class as public, and in result, it could not load this activity from the main menu
public class CheckersGame extends Activity{
	
	public GridLayout checkersBoardGL;
	public View inflateSquare;
	public ImageView imageOfSquares[][];
	public View squaresOfBoard[][];
	// Later on, I will change this to a 'char' array instead.
	public String	strCheckersBoard[][];
  // Handles all the events.
	public MultiplayerEvents playerEvents;
	// Displays whose turn it is.
	public TextView playerInfo;
	// Image of the player's piece.
	public ImageView playerImage;
	// Information about the Bot.
	public TextView loadingInfo;
	// Loading wheel for AI.
	public ProgressBar loadingWheel;
	
	
	
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
		// This will keep track of the entire board
		//strCheckersBoard = new String[8][8];
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
		
		// Create instiate the event class TextView passLoadingInfo, ProgressBar passLoadingWheel, ImageView passPlayerImage
		// playerEvents = new MultiplayerEvents(imageOfSquares, imageOfSquares, strCheckersBoard, playerInfo, loadingInfo, loadingWheel, playerImage);
		
		// It will populate the ImageViews, I guess.
		//populateBoard(strCheckersBoard);
		// Then add the squares 'squaresOfBoard' to the board
		// addSquaresToBoard()
		// Add listeners.
		
		
		// ---- Initially Populates the Checkersboard and Adds the Events to the Squares ---- \\ 
		// For each row of the checkersboard...
		/*
		for(int x = 0;x<8;x++)
		{
			final int loopX = x;
			// Grabs the LayoutInflater, and stores it in a readily available variable
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// Used to determine what checkers' will go where
				
			for(int y = 0;y<8;y++)
			{		
				final int loopY = y;
				// Inflates the 'View' with the layout of the given XML.
				inflateSquare = inflater.inflate(R.layout.gridview_item, checkersBoardGL, false);
				// Store the ImageView specified in the parsed XML layout, into a runtime ImageView for further modification.
				ImageView imageOfSquare = (ImageView) inflateSquare.findViewById(R.id.image_square);
				
				if(x < 3)
				{
					// For the first three rows, it will print light-brown checkers pieces - Add an extra param variable that represents the player number.
					addSquares(R.drawable.light_brown_piece, x, y, imageOfSquare, checkersBoardGL,inflateSquare, "2");
					
				}else if(x > 4 && x < 8){
					// For the last three rows, it will print dark-brown checkers pieces - Add an extra param variable that represents the player number.
					addSquares(R.drawable.dark_brown_piece, x, y, imageOfSquare, checkersBoardGL, inflateSquare, "1");
					
				}else{
					// For the two rows in the middle of the checkers board, it will print empty squares.
					// It does not need to set any image resources so therefore, the first parameter variable is 0 as I still needed to pass
					// in an integer due to the specification of this method - This 0 will not be used.
					// I NEED TO MODIFY THIS SO, THAT WE CAN HELP CREATE THE HELPER ARRAY
					addSquares(0, x, y, imageOfSquare, checkersBoardGL, inflateSquare, "0");
				}
				
				// This will add events to the 32 (dark brown) squares of the checkersboard because these are the only usuable squares on the board.
				// The other 32 sqaures are just for design.
				if(x % 2 == 0 && y % 2 == 1){
					// This will add the event to the square, which could be empty or occupying the checkers piece. 
					imageOfSquares[loopX][loopY].setOnClickListener(playerEvents);
					
				}else if(x % 2 == 1 && y % 2 == 0){
					// This will add the event to the square, which could be empty or occupying the checkers piece. 
					imageOfSquares[loopX][loopY].setOnClickListener(playerEvents);
				}			
			}
		}
		*/
		
		strCheckersBoard = new String[][]{{"[]","2","[]","2","[]","2","[]","2"},
																			{"2", "[]","2","[]","2","[]","2","[]"},
																			{"[]","2","[]","2","[]","2","[]","2"},
																			{"0", "[]","0","[]","0","[]","0","[]"},
																			{"[]","0","[]","0","[]","0","[]","0"},
																			{"1", "[]","1","[]","1","[]","1","[]"},
																			{"[]","1","[]","1","[]","1","[]","1"},
																			{"1", "[]","1","[]","1","[]","1","[]"}};
		
		// Create instiate the event class TextView passLoadingInfo, ProgressBar passLoadingWheel, ImageView passPlayerImage
		playerEvents = new MultiplayerEvents(imageOfSquares, imageOfSquares, strCheckersBoard, playerInfo, loadingInfo, loadingWheel, playerImage);
		// Add the blank ImageViews/Views to the board (i.e. the GridLayout).
		addSquaresToBoard();
		// Modify the ImageViews that are now part of the GridLayout.
		populateBoard(strCheckersBoard);
		// Assign the listeners for each square (ImageView) of the board (i.e the GridLayout).
		assignEvents();
		
		System.out.println("|----------|");
		
		for(int x = 0;x<8;x++)
		{
			for(int y=0;y<8;y++)
			{
				System.out.print(strCheckersBoard[x][y]);
			}
			System.out.println("");
		}
		
		System.out.println("|----------|");
		
		System.out.println("Threading implemented.");
		
		// Now, we can add the code moving the pieces, and shit.
	}	
	public void populateBoard(String[][] passStrCheckersBoard)
	{
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
				//else{ //if(passStrCheckersBoard[row][column].contains("0") || .contains("[]")
					
					// We do nothing.
				//}
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
				inflateSquare = inflater.inflate(R.layout.gridview_item, checkersBoardGL, false);
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
	public void assignEvents()
	{
		// Assigns an event for each usuable ImageView (i.e. an empty square or an occuped square.)
		for(int row = 0; row < 8; row++)
		{
			for(int column=((row+1)%2); column<8; column+=2)
			{
				// Add an listener to each usuable square. Should be 32 squares in total.
				imageOfSquares[row][column].setOnClickListener(playerEvents);
			}
		}
	}
	public void addSquares(int resourceId, int passX, int passY, ImageView passImage, GridLayout passGridLayout, View passGridSquare, String occupySquare)
	{
		// Using non-final variables in inner classes are not permitted but, I got rid of my inner class so, it does not have to be final anymore. 
		final int x = passX, y = passY;
		
		if(x % 2 == 0 && y % 2 == 1)
		{
			// Prints on rows 0, 2, 4, 8, and columns 1, 3, 5, 7
			
			// This method used causes latency so, use the 'setImageDrawable()' method instead.
			passImage.setImageResource(resourceId);
			// Adds the square to the checkers board :)
			passGridLayout.addView(passGridSquare);
			// I NEED TO CONFIRM IF THIS ARRAY WILL ALLOW ME TO MODIFY THE IMAGEVIEWS THROUGH THIS! - Confirmed :)
			imageOfSquares[x][y] = passImage;
			// Add the layout of the square into the array for future modifications.
			squaresOfBoard[x][y] = inflateSquare;
			// Damn, this brain fog is making me super slow in thinking.
			strCheckersBoard[x][y] = occupySquare;
					
		}else if(x % 2 == 1 && y % 2 == 0){
			// Prints on rows 1, 3, 5, 7, and columns 0, 2, 4, 8
			
			// This method used causes latency so, use the 'setImageDrawable()' method instead.
			passImage.setImageResource(resourceId);
			// Adds the square to the checkers board :)
			passGridLayout.addView(passGridSquare);
			// I NEED TO CONFIRM IF THIS ARRAY WILL ALLOW ME TO MODIFY THE IMAGEVIEWS THROUGH THIS!
			imageOfSquares[x][y] = passImage;
			// Add the layout of the square into the array for future modifications.
			squaresOfBoard[x][y] = inflateSquare;
			// Damn, this brain fog is making me super slow in thinking.
			strCheckersBoard[x][y] = occupySquare;
			
			
		}else
		{
			// Adds an empty square to the checkers board :)
			passGridLayout.addView(passGridSquare);
			// I NEED TO CONFIRM IF THIS ARRAY WILL ALLOW ME TO MODIFY THE IMAGEVIEWS THROUGH THIS!
			imageOfSquares[x][y] = passImage;
			// Add the layout of the square into the array for future modifications.
			squaresOfBoard[x][y] = inflateSquare;
			// Damn, this brain fog is making me super slow in thinking. This value in the array will never be used ;)
			strCheckersBoard[x][y] = "[]";
		}
	}
}
