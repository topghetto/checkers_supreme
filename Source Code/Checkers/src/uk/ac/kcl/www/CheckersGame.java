package uk.ac.kcl.www;

import java.util.ArrayList;

import android.util.Log;

import android.app.Activity;
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

import android.view.ViewGroup.*;
import android.view.Gravity;
import android.view.LayoutInflater;

import 	android.graphics.drawable.ColorDrawable;


// I forgot to declare the class as public, and in result, it could not load this activity from the main menu
public class CheckersGame extends Activity{
	
	public GridLayout checkersBoardGL;
	public View inflateSquare;
	public ImageView imageOfSquares[][];
	public View squaresOfBoard[][];
	public String	strCheckersBoard[][];
	
	public PlayerMoves playerEvents;
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		// I need to create a different xml layout... Actually, I will do it now.
		setContentView(R.layout.checkers_game);
		// Grabs the layout of the checkersboard
		GridLayout checkersBoardGL = (GridLayout) findViewById(R.id.checkersboard_gridlayout);
		// All the images of the squares of the checkers board will be stored in here.
		imageOfSquares = new ImageView[8][8];
		// All the layouts of the squares of the checkers board will be stored in here.
		squaresOfBoard = new View[8][8];
		// This will keep track of the entire board
		strCheckersBoard = new String[8][8];
		// Create instiate the event class
		playerEvents = new PlayerMoves(imageOfSquares, strCheckersBoard);
		
		// ---- Initially Populates the Checkersboard and Adds the Events to the Squares ---- \\ 
		// For each row of the checkersboard...
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
		
		// Debug purposes. The array has the values in the desired locations :)
		
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
		
		// Now, we can add the code moving the pieces, and shit.
	}	
	public void populateBoard()
	{
		// I need to copy the code 'onCreate' into here, and make modifications to it so, it can be method-friendly.
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

class PlayerMoves implements View.OnClickListener
{
	// Member Variables
	public int row, column;
	public View[][] squaresOfBoard;
	public String[][] strCheckersBoard;
	// Keeps track of the selected square.
	public ArrayList<View> prevSquares;
	public ArrayList<ImageView> imgOfPrevSquares;
	public int sizeOfPrev;
	
	// Constructor
	public PlayerMoves(View[][] passSquares, String[][] passCheckersBoard)
	{
		strCheckersBoard = passCheckersBoard;
		squaresOfBoard = passSquares;
		prevSquares = new ArrayList<View>();
		imgOfPrevSquares = null;
		sizeOfPrev = 0;
	}
	// I made this synchronised so, I am hoping the code runs one at a time otherwise, if I click two buttons at the same time, I have a hunch
	// that it would cause a series of problems.
	public synchronized void onClick(View v)
	{
		
		
		for(int x = 0;x<8;x++)
		{
			for(int y=0;y<8;y++)
			{
				// Firstly, we must find the row/column value of the square that initiated the event.
				if(squaresOfBoard[x][y].equals(v))
				{
					// We need to check if the selected square has a checkers piece...
					if(strCheckersBoard[x][y] == "1" || strCheckersBoard[x][y] == "2")
					{
						// And then check whether the neighbouring square(s) are empty...
						
						sizeOfPrev = prevSquares.size();
						
						// The prevSquares will initially be empty so, this statement should only run once.
						if(sizeOfPrev == 0)
						{
							// Condition = constraints and whatnot.
							// upOrDown = 1 so, x+1 i.e, go from top to bottom of the checkersboard.
							highlightSquares(x >= 0 && x <= 6, x, y, 1);
							
							/*// Initialises the prevSquares and imgOfPrevSquares array
							prevSquares = new ArrayList<View>();
							imgOfPrevSquares = new ArrayList<ImageView>();
										
							if(x >= 0 && x <= 6)
							{
								// Assigns the newly selected square as part of the new prevSquares[] array.
								//prevSquares.get(0) = squaresOfBoard[x][y];
								//squaresOfBoard[x][y].setBackground(new ColorDrawable(0xFF999966));
								//prevSquares.add(squaresOfBoard[x][y]);
								
								
								// "2" for Player 2's checkers piece ;) 	
								if(y >= 1 && strCheckersBoard[x+1][y-1] != "2" && strCheckersBoard[x+1][y-1] != "1")
								{
									// Remove possible duplicate.
									prevSquares.remove(squaresOfBoard[x][y]);
									// Highlights the selected square.
									squaresOfBoard[x][y].setBackground(new ColorDrawable(0xFF999966));
									// Add the selected square to the prevSquares ArrayList
									prevSquares.add(squaresOfBoard[x][y]);
									// Also highlights the neighbouring square to left of it.
									squaresOfBoard[x+1][y-1].setBackground(new ColorDrawable(0xFF999966));
									// Adds the neighbouring square to the prevSquares ArrayList ;)
									prevSquares.add(squaresOfBoard[x+1][y-1]);
								}
								if(y >= 0 && y <= 6 && strCheckersBoard[x+1][y+1] != "2" && strCheckersBoard[x+1][y+1] != "1")
								{
									// Remove possible duplicate.
									prevSquares.remove(squaresOfBoard[x][y]);
									// Highlights the selected square.
									squaresOfBoard[x][y].setBackground(new ColorDrawable(0xFF999966));
									// Add the selected square to the prevSquares ArrayList
									prevSquares.add(squaresOfBoard[x][y]);
									// Also highlights the neighbouring square to the right of it.
									squaresOfBoard[x+1][y+1].setBackground(new ColorDrawable(0xFF999966));
									// Adds the neighbouring square to the prevSquares ArrayList ;)
									prevSquares.add(squaresOfBoard[x+1][y+1]);
								}
								
								// This section works gracefully :)
								System.out.println("Printed from the prevSquares.size() == 0 -- The size of prevSquares is now " + prevSquares.size());
							}*/
						}else{
							// This else statement never seems to run.
							// prevSquares has data so, perform the following...
							System.out.println("The ELSE statement runs...");
							
							// Checks whether the selected square, is part of the previous selected/highlighted squares.				
							if(sizeOfPrev == 2 && !prevSquares.get(0).equals(v) && !prevSquares.get(1).equals(v))
							{
								// OK Now, it selects the pieces, yippee! and deselects, blah blah.
								
								prevSquares.get(0).setBackground(null);
								prevSquares.get(1).setBackground(null);
								
								// Condition = constraints and whatnot.
								// upOrDown = 1 so, x+1 i.e, go from top to bottom of the checkersboard.
								highlightSquares(x >= 0 && x <= 6, x, y, 1);
								
								// Debug purposes.
								System.out.println("Printed from the prevSquares.size() == 2 -- The size of prevSquares is now " + prevSquares.size());
								
								/*
								// Clear prevSquares but, double check this.
								prevSquares = new ArrayList<View>();
								// Clear imgOfPrevSquares but, double check this.
								imgOfPrevSquares = new ArrayList<ImageView>();
								// I need to figure out the initial values too for this... Long.
								
								if(x >= 0 && x <= 6)
								{
									//prevSquares.get(0) = squaresOfBoard[x][y];
									squaresOfBoard[x][y].setBackground(new ColorDrawable(0xFF999966));
									// Assigns the newly selected square as part of the new prevSquares[] array.
									prevSquares.add(squaresOfBoard[x][y]);
									
									
									if(y >= 1)
									{
										//prevSquares.get(1) = squaresOfBoard[x+1][y-1];
										squaresOfBoard[x+1][y-1].setBackground(new ColorDrawable(0xFF999966));
										// Assigns the newly selected square as part of the new prevSquares[] array.
										prevSquares.add(squaresOfBoard[x+1][y-1]);
									}
									if(y >= 0 && y <= 6)
									{
										//prevSquares.get(1) = squaresOfBoard[x+1][y-1];
										squaresOfBoard[x+1][y+1].setBackground(new ColorDrawable(0xFF999966));
										// Assigns the newly selected square as part of the new prevSquares[] array.
										prevSquares.add(squaresOfBoard[x+1][y+1]);
									}
									System.out.println("Printed from the prevSquares.size() == 2 -- The size of prevSquares is now " + prevSquares.size());
								}*/					
							}else if(sizeOfPrev == 3 && !prevSquares.get(0).equals(v) && !prevSquares.get(1).equals(v) && !prevSquares.get(2).equals(v))
							{
								prevSquares.get(0).setBackground(null);
								prevSquares.get(1).setBackground(null);
								prevSquares.get(2).setBackground(null);
								
								// Condition = constraints and whatnot.
								// upOrDown = 1 so, x+1 i.e, go from top to bottom of the checkersboard.
								highlightSquares(x >= 0 && x <= 6, x, y, 1);
								
								// Debug purposes.				
								System.out.println("Printed from the prevSquares.size() == 3 -- The size of prevSquares is now " + prevSquares.size());
								
								/*
								// Clear prevSquares but, double check this.
								prevSquares = new ArrayList<View>();
								// Clear imgOfPrevSquares but, double check this.
								imgOfPrevSquares = new ArrayList<ImageView>();
								// I need to figure out the initial values too for this... Long.
								
								if(x >= 0 && x <= 6)
								{
									//prevSquares.get(0) = squaresOfBoard[x][y];
									squaresOfBoard[x][y].setBackground(new ColorDrawable(0xFF999966));
									// Assigns the newly selected square as part of the new prevSquares[] array.
									prevSquares.add(squaresOfBoard[x][y]);
									if(y >= 1)
									{
										//prevSquares.get(1) = squaresOfBoard[x+1][y-1];
										squaresOfBoard[x+1][y-1].setBackground(new ColorDrawable(0xFF999966));
										// Assigns the newly selected square as part of the new prevSquares[] array.
										prevSquares.add(squaresOfBoard[x+1][y-1]);
									}
									if(y >= 0 && y <= 6)
									{
										//prevSquares.get(1) = squaresOfBoard[x+1][y-1];
										squaresOfBoard[x+1][y+1].setBackground(new ColorDrawable(0xFF999966));
										// Assigns the newly selected square as part of the new prevSquares[] array.
										prevSquares.add(squaresOfBoard[x+1][y+1]);
									}														
								}
								System.out.println("Printed from the prevSquares.size() == 3 -- The size of prevSquares is now " + prevSquares.size());
								*/
							}else if(false)
							{
								// This will move the piece, and blah de blah blah blah.
							}
						}
						
						
					}else{
						
						// Do nothing
					}
					
					
					//*/
					
			}	
		}		
	}	
	
	
	/*	
		for(int x = 0;x<8;x++)
		{
			for(int y = 0;y<8;y++)
			{
				// Finds the row/column value of the square that initiated the event.
				if(squaresOfBoard[x][y].equals(v))
				{
					// So, that worked.
					Log.i("The square clicked is located at ", "row " + x + " and column " + y);
					
					// Perform a move anywhere on rows 0 through 6... Just some board constraints.
					if(x >= 0 && x <= 6)
					{
						// Highlight neighboring checkers' pieces or squares...
						// if x = 0, y = 1 in squaresOfBoard[x][y],
						// We will highlight the selected checkers piece itself (in time), and as well as squaresOfBoard[x+1][y-1] and squaresOfBoard[x+1][y+2]
						squaresOfBoard[x][y].setBackground(new ColorDrawable(0xFF999966));
						
						if(y >= 1)
						{
							if(strCheckersBoard[x+1][y-1] == "0")
							{
								// Board constraints so, we don't try to pick squares that don't exist 
								//squaresOfBoard[x+1][y-1].setImageResource(R.drawable.ic_launcher);
								// The FF in 0xFF... corresponds to opacity.
								squaresOfBoard[x+1][y-1].setBackground(new ColorDrawable(0xFF999966));		
							}			
						}
						if(y >= 0 && y <= 6)
						{
							// Board constraints so, we don't try to pick squares that don't exist
							// I don't think the y >= 0 is needed but, I'll confirm this when I feel much better.
							// The FF in 0xFF... corresponds to opacity.
							squaresOfBoard[x+1][y+1].setBackground(new ColorDrawable(0xFF999966));
						}
						
						// Now, I must create a while loop until a decision is made/cancelled ;)
						
						//while(squaresOfBoard[x][y] != v)
					}
					
					
					
					
					// Now, once we have done everything we need to do, we will break the loop as we have already found the view which called the event.
					// Any further search would be useless.
					x = 8;
					y = 8;
				}
			}
		}
		*/
		
	}
	
	public void highlightSquares(boolean passCondition, int passX, int passY, int upOrDown)
	{
		int x = passX;
		int y = passY;
		// Initialises the prevSquares and imgOfPrevSquares array
		prevSquares = new ArrayList<View>();
		imgOfPrevSquares = new ArrayList<ImageView>();
					
		if(passCondition)
		{
			// Assigns the newly selected square as part of the new prevSquares[] array.
			//prevSquares.get(0) = squaresOfBoard[x][y];
			//squaresOfBoard[x][y].setBackground(new ColorDrawable(0xFF999966));
			//prevSquares.add(squaresOfBoard[x][y]);
			
			
			// "2" for Player 2's checkers piece ;) - [x+upOrDown] == [x+1] for now ;)
			if(y >= 1 && strCheckersBoard[x+upOrDown][y-1] != "2" && strCheckersBoard[x+upOrDown][y-1] != "1")
			{
				// Remove possible duplicate.
				prevSquares.remove(squaresOfBoard[x][y]);
				// Highlights the selected square.
				squaresOfBoard[x][y].setBackground(new ColorDrawable(0xFF999966));
				// Add the selected square to the prevSquares ArrayList
				prevSquares.add(squaresOfBoard[x][y]);
				// Also highlights the neighbouring square to left of it.
				squaresOfBoard[x+upOrDown][y-1].setBackground(new ColorDrawable(0xFF999966));
				// Adds the neighbouring square to the prevSquares ArrayList ;)
				prevSquares.add(squaresOfBoard[x+upOrDown][y-1]);
			}
			if(y >= 0 && y <= 6 && strCheckersBoard[x+upOrDown][y+1] != "2" && strCheckersBoard[x+upOrDown][y+1] != "1")
			{
				// Remove possible duplicate.
				prevSquares.remove(squaresOfBoard[x][y]);
				// Highlights the selected square.
				squaresOfBoard[x][y].setBackground(new ColorDrawable(0xFF999966));
				// Add the selected square to the prevSquares ArrayList
				prevSquares.add(squaresOfBoard[x][y]);
				// Also highlights the neighbouring square to the right of it.
				squaresOfBoard[x+upOrDown][y+1].setBackground(new ColorDrawable(0xFF999966));
				// Adds the neighbouring square to the prevSquares ArrayList ;)
				prevSquares.add(squaresOfBoard[x+upOrDown][y+1]);
			}
			
			// This section works gracefully :)
			//System.out.println("Printed from the prevSquares.size() == 0 -- The size of prevSquares is now " + prevSquares.size());
		}
	}
}