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
	// Later on, I will change this to a 'char' array instead.
	public String	strCheckersBoard[][];
  // Handles all the events.
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
		playerEvents = new PlayerMoves(imageOfSquares, imageOfSquares, strCheckersBoard);
		
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
	public ImageView[][] imageOfSquares;
	public String[][] strCheckersBoard;
	
	// Keeps track of the selected square.
	public ArrayList<View> prevSquares;
	public ArrayList<ImageView> imgOfPrevSquares;
	public int sizeOfPrev;
	public ArrayList<Integer> xPrevAxis, yPrevAxis;
	
	public boolean isHighlighted, playerOneTurn;
	
	public int xParentPrev, yParentPrev;
	
	// Constructor
	public PlayerMoves(View[][] passSquares, ImageView[][] passImgSquares, String[][] passCheckersBoard)
	{
		// The first turn goes to player two.
		// After each turn a player makes, a boolean variable will determine when it is the others players turn.
		// e.g. playerOne = true, playerTwo = false. Then after player one's turn, the variable will
		// become playerOne = false, and playerTwo = true, and this will keep switching back-and-forth. I just realised,
		// the computer will never ever be able to click this, lol.
		playerOneTurn = false;
		
		strCheckersBoard = passCheckersBoard;
		squaresOfBoard = passSquares;
		imageOfSquares = passImgSquares;
		prevSquares = new ArrayList<View>();
		imgOfPrevSquares = null;
		sizeOfPrev = 0;
		xPrevAxis = new ArrayList<Integer>();
		yPrevAxis = new ArrayList<Integer>();
		
		
	}
	// I declared this method as synchronised hoping the code runs one at a time otherwise, because if I click two buttons at the same time, I have a hunch
	// that it may cause a series of problems.
	public synchronized void onClick(View v)
	{
		/*if(playerOneTurn == false)
		{
			// Then do the following...
			
		}else
		{
			// AI Code will go here.
		}*/
		
		for(int x = 0;x<8;x++)
		{
			// ((x+1)%2) will make it change back-and-forth from 1 to 0 after each 'x' iteration. This will allow a search for events through
			// the 32 squares that would call an event instead of searching through 64 squares (where 32 will never ever call a event).
			for(int y=((x+1)%2);y<8;y+=2)
			{
				// Firstly, we must find the row/column value of the square that initiated the event.
				if(squaresOfBoard[x][y].equals(v))
				{		
					if(playerOneTurn == true)
					{
						System.out.println("Player One's Turn.");
						// If it player one's turn...
						playerTurn("1", v, x >= 1 && x <= 7, x, y, -1, R.drawable.dark_brown_piece, false);		// Nice, it works.			
					}
					else
					{
						System.out.println("Player Two's Turn.");
						// If it is Player two's turn...
						playerTurn("2", v, x >= 0 && x <= 6, x, y, 1, R.drawable.light_brown_piece, true);	// Nice, it works.		
					}
					
					// Used to hold the value of whether the squares are highlighted or not.
					//isHighlighted = false;
					// And then check whether the neighbouring square(s) are empty :)
					//sizeOfPrev = xPrevAxis.size();    // Initially, the size will be 0 :)
					
					/*
					if(strCheckersBoard[x][y] == "1" && sizeOfPrev <= 0)
					{
						// First param variable is a condition - Player one.
						// Last param variable 'upOrDown' = 1 so, [x+upOrDown] = [x+(-1)] i.e, go from bottom to top of the checkersboard.
						highlightSquares(x >= 1 && x <= 7, x, y, -1);
						
					}else
					{
						// WORKS :) - It will check whether all the squares are highlighted, and assign the result of the operation into 'isHighlighted'
						isHighlighted = checkHighlights(v);
						
						if(isHighlighted == false)
						{
							// Works as expected :)
							removeHighlights(); 
							// Add new highlights for the newly selected square.
							highlightSquares(x >= 1 && x <= 7, x, y, -1); // upOrDown = 1 so, x-1 i.e, go from bottom to the top of the checkersboard.
							// Debug purposes.
							System.out.println("In isHighlighted==false The size of xPrevAxis = " + xPrevAxis.size() + " and yPrevAxis = " + yPrevAxis.size());				
						}
						else if(isHighlighted == true)
						{
							// Moves the checkers piece to the new location.
							movePiece(v, x, y, "1", R.drawable.dark_brown_piece);
						}	
					}
					
					if(strCheckersBoard[x][y] == "2" && sizeOfPrev <= 0)
					{
						// First param variable is a condition - Player two.
						// Last param variable 'upOrDown' = 1 so, [x+upOrDown] = [x+1] i.e, go from top to bottom of the checkersboard.
						highlightSquares(x >= 0 && x <= 6, x, y, 1);
					}else
					{
						// WORKS :) - It will check whether all the squares are highlighted, and assign the result of the operation into 'isHighlighted'
						isHighlighted = checkHighlights(v);
						
						if(isHighlighted == false)
						{
							// Works as expected :)
							removeHighlights(); 
							// Add new highlights for the newly selected square.
							highlightSquares(x >= 0 && x <= 6, x, y, 1); // upOrDown = 1 so, x+1 i.e, go from top to bottom of the checkersboard.
							// Debug purposes.
							System.out.println("In isHighlighted==false The size of xPrevAxis = " + xPrevAxis.size() + " and yPrevAxis = " + yPrevAxis.size());				
						}
						else if(isHighlighted == true)
						{
							// Moves the checkers piece to the new location.
							movePiece(v, x, y, "2", R.drawable.light_brown_piece);
						}	
					}
					*/
					
					/*
					if(sizeOfPrev <= 0)
					{
						if(strCheckersBoard[x][y] == "1")
						{
							// First param variable is a condition - Player one.
							// Last param variable 'upOrDown' = 1 so, [x+upOrDown] = [x+(-1)] i.e, go from bottom to top of the checkersboard.
							highlightSquares(x >= 1 && x <= 7, x, y, -1);
							
						}else if(strCheckersBoard[x][y] == "2")
						{
							// First param variable is a condition - Player two.
							// Last param variable 'upOrDown' = 1 so, [x+upOrDown] = [x+1] i.e, go from top to bottom of the checkersboard.
							highlightSquares(x >= 0 && x <= 6, x, y, 1);
						}
					}
					else 
					{
						// From here...
						// There is a bug when I select a piece that has one neighbouring piece, and decide to click an empty square...
						// that isn't highlighted, it will get stuck on that selection.
						isHighlighted = checkHighlights(v);
						
						if(isHighlighted == false && strCheckersBoard[xPrevAxis.get(0).intValue()][yPrevAxis.get(0).intValue()] == "1")
						{
							// Works as expected :)
							removeHighlights(); 
							if(strCheckersBoard[x][y] == "1")
							{
								// Add new highlights for the newly selected square.
								highlightSquares(x >= 1 && x <= 7, x, y, -1); // upOrDown = 1 so, x+1 i.e, go from top to bottom of the checkersboard.
							}	
							// Debug purposes.
							System.out.println("In isHighlighted==false The size of xPrevAxis = " + xPrevAxis.size() + " and yPrevAxis = " + yPrevAxis.size());
							System.out.println("First IF statement");
						}
						else if(isHighlighted == true && strCheckersBoard[xPrevAxis.get(0).intValue()][yPrevAxis.get(0).intValue()] == "1")
						{
							// Moves the checkers piece to the new location.
							movePiece(v, x, y, "1", R.drawable.dark_brown_piece);
							// Debug purposes.
							System.out.println("Second IF statement");
						}						
						if(isHighlighted == false && strCheckersBoard[xPrevAxis.get(0).intValue()][yPrevAxis.get(0).intValue()] == "2")
						{
							// Works as expected :)
							removeHighlights(); 
							// Add new highlights for the newly selected square.
							if(strCheckersBoard[x][y] == "2")
							{
								highlightSquares(x >= 0 && x <= 6, x, y, 1); // upOrDown = 1 so, x+1 i.e, go from top to bottom of the checkersboard.
								// Debug purposes.
								System.out.println("In isHighlighted==false The size of xPrevAxis = " + xPrevAxis.size() + " and yPrevAxis = " + yPrevAxis.size());
							}
							// Debug purposes.
							System.out.println("Third IF statement");
						}
						else if(isHighlighted == true && strCheckersBoard[xPrevAxis.get(0).intValue()][yPrevAxis.get(0).intValue()] == "2")
						{
							// Moves the checkers piece to the new location.
							movePiece(v, x, y, "2", R.drawable.light_brown_piece);
							// Debug purposes.
							System.out.println("Fourth IF statement");
						}
						
						// ...To here. It works, I guess. I like both the dark brown and the light brown checkers pieces.
						
										
					}*/
					
			}// if(squaresOfBoard[x][y].equals(v))	
		}		
	}	
	}// End of 'onClick'
	public void addHighlight(int passX, int passY, int upOrDown, int leftOrRight)
	{
		// leftOrRight would either be a negative value (go right), and a positive value (go left)
		int x = passX;
		int y = passY;
		
		// Remove duplicate.
		xPrevAxis.remove(new Integer(x));
		yPrevAxis.remove(new Integer(y));
		// Stores the coordinates for later use and makes sure these values are the first element in their ArrayLists ;)
		xPrevAxis.add(0, new Integer(x));
		yPrevAxis.add(0, new Integer(y));
		
		// Remove possible duplicate.
		prevSquares.remove(squaresOfBoard[x][y]);
		// Highlights the selected square.
		squaresOfBoard[x][y].setBackground(new ColorDrawable(0xFF999966));
		// Add the selected square to the prevSquares ArrayList
		prevSquares.add(squaresOfBoard[x][y]);
		// Also highlights the neighbouring square to left/right of it.
		squaresOfBoard[x+upOrDown][y+(leftOrRight)].setBackground(new ColorDrawable(0xFF999966));
		// Adds the neighbouring square to the prevSquares ArrayList ;)
		prevSquares.add(squaresOfBoard[x+upOrDown][y+(leftOrRight)]);
		
		// Stores the coordinates for later use ;)
		xPrevAxis.add(new Integer(x+upOrDown));
		yPrevAxis.add(new Integer(y+(leftOrRight)));
	}
	
	public void highlightSquares(boolean passCondition, int passX, int passY, int upOrDown)
	{
		int x = passX;
		int y = passY;
		// Initialises the prevSquares and imgOfPrevSquares array
		prevSquares = new ArrayList<View>();
		imgOfPrevSquares = new ArrayList<ImageView>();
		// Holds the co-ordinates of the 'x' and 'y' axis of the highlighted squares.
		xPrevAxis = new ArrayList<Integer>();
		yPrevAxis = new ArrayList<Integer>();
		//sizeOfPrev = xPrevAxis.size();
		
		if(passCondition)
		{
			// Only highlight the squares if the neighbbouring squares are vacant.
			if(y >= 1 && strCheckersBoard[x+upOrDown][y-1] == "0")
			{
				// WORKS - Adds an highlight to a neighbouring empty square or enemy piece (which needs to be implemented).
				addHighlight(x, y, upOrDown, -1);	// y-1
				
				
			}
			if(y >= 0 && y <= 6 && strCheckersBoard[x+upOrDown][y+1] == "0")
			{
				// WORKS - Adds an highlight to a neighbouring empty square or enemy piece (which needs to be implemented).
				addHighlight(x, y, upOrDown, 1);	// y+1
		
			}
			
			// Debug purposes - It works in the way I hoped it would :)
			for(int a = 0;a<xPrevAxis.size();a++)
			{
				System.out.print("xPrevAxis.get(" + a + ").intValue() = " + xPrevAxis.get(a).intValue() + " and ");
				System.out.println("yPrevAxis.get(" + a + ").intValue() = " + yPrevAxis.get(a).intValue());
			}
			
			// Debug purposes.
			System.out.println("Printed from the if(prevSquares.size() == " + sizeOfPrev + " -- The size of prevSquares is now " + prevSquares.size());		
		}
	}
	public void removeHighlights()
	{
			// If the square is not part of the highlighted squares, then remove the highlights of the existing square...
			for(int rm = 0;rm < sizeOfPrev;rm++)
			{
				// Remove the highlights from the highlighted squares.
				int xHighlighted = xPrevAxis.get(rm).intValue();
				int yHighlighted = yPrevAxis.get(rm).intValue();
				squaresOfBoard[xHighlighted][yHighlighted].setBackground(null);
			}
			// When I highlight a checkers piece, and then click on another square that is not part of the highlighted squares, it removes the...
			// highlights as it should do but, when I try to click on the checkers piece that was once highlighted, it does not highlight again.
			// The following code may solve this, inshallah... Well, that sorted it :) alhamdullah.
			// Holds the co-ordinates of the 'x' and 'y' axis of the highlighted squares.
			xPrevAxis = new ArrayList<Integer>();
			yPrevAxis = new ArrayList<Integer>();
	}
	public boolean checkHighlights(View v)
	{
		boolean isHighlighted = true;
		// For all highlighted squares, check whether the selected square is part of the highlighted squares.
		for(int h = 0;h < sizeOfPrev;h++)
		{
			// The coordinates of the possibly highlighted square.
			int xHighlighted = xPrevAxis.get(h).intValue();
			int yHighlighted = yPrevAxis.get(h).intValue();
			
			if(squaresOfBoard[xHighlighted][yHighlighted].equals(v))
			{
				// The selected square is part of the highlighted squares.
				isHighlighted = true;
				System.out.println("It is highlighted and xHighlighted=" + xHighlighted + " and yHighlighted=" + yHighlighted);
				// Close the loop. 
				h = sizeOfPrev;
				
			}else
			{
				// Closing the loop early fixed the problem because if x = 0 true, then x =1 is false, then isHighlighted == false will run.
				// All we need is at least one x to be false in order for x1 && x2 && x3 to be false so, any other checks would cause harm.
				// The selected square is not part of the highlighted squares.
				isHighlighted = false;
				System.out.println("It is not highlighted and xHighlighted=" + xHighlighted + " and yHighlighted=" + yHighlighted);
			}
		}
		// This will later be assigned to the global variable 'isHighlighted'
		
		return isHighlighted;
	}
	public void movePiece(View v, int passX, int passY, String strDest, int destImg)
	{
		// I need to implement this in a bit.
		// The coordinates of the root square of highlighted squares
		int x = passX, y = passY;
		int parentPrevX = xPrevAxis.get(0).intValue();
		int parentPrevY = yPrevAxis.get(0).intValue();
		
		for(int mv = 1;mv < sizeOfPrev;mv++)
		{	
			// The coordinates of the neighbouring squares of the highlighted squares.
			int prevX = xPrevAxis.get(mv).intValue();
			int prevY = yPrevAxis.get(mv).intValue();

			// If the selected piece is part of the highlighted neighbouring squares, then perform blah de blah blah.
			if(v.equals(squaresOfBoard[prevX][prevY]))
			{
				
				System.out.println("We successfully moved the piece :)");
				// Clear the square of the checker piece we want to move.
				strCheckersBoard[parentPrevX][parentPrevY] = "0";
				// Clear the image of that square.
				imageOfSquares[parentPrevX][parentPrevY].setImageResource(0);
				// Occupies new space in the helper array.
				strCheckersBoard[x][y] = strDest;
				// Move the checkers piece into the new location.
				imageOfSquares[x][y].setImageResource(destImg);
				// Now, I need to get rid of the highlights, etc.
				removeHighlights();
				// Clear the row/column values of the highlighted squares.
				xPrevAxis = new ArrayList<Integer>(); //- This causes the app to crash when I try to move another piece after successfully moving one beforehand.
				yPrevAxis = new ArrayList<Integer>();
				// - This stops the app crashing from happening because on on line 247, it will try to loop through the old value of
				// sizeOfPrev, where the size of the 'sizeOfPrev' would be larger the size of elements in the array causing an
				// IndexOutOfBounds exception ;)
				sizeOfPrev = 0; 
				
				// --- Debug Purposes --- //
				for(int c = 0;c <8;c++)
				{
					for(int d=0;d<8;d++)
					{
						System.out.print(strCheckersBoard[c][d]);
					}
					System.out.println("");
				}
				System.out.println("|----------|");
				// --- Debug Purposes --- //
				
				
				/*if(strCheckersBoard[parentPrevX][parentPrevY] == "1")	// Determines what colour piece we move ;)
				{
					
				}else if(strCheckersBoard[parentPrevX][parentPrevY] == "2")		// Determines what colour piece we move ;)
				{
					System.out.println("We successfully moved the piece :)");
					// Clear the square of the checker piece we want to move.
					strCheckersBoard[parentPrevX][parentPrevY] = "0";
					// Clear the image of that square.
					imageOfSquares[parentPrevX][parentPrevY].setImageResource(0);
					// Occupies new space in the helper array.
					strCheckersBoard[x][y] = "2";
					// Move the checkers piece into the new location.
					imageOfSquares[x][y].setImageResource(R.drawable.light_brown_piece);
					// Now, I need to get rid of the highlights, etc.
					removeHighlights();
					// Clear the row/column values of the highlighted squares.
					xPrevAxis = new ArrayList<Integer>(); //- This causes the app to crash when I try to move another piece after successfully moving one beforehand.
					yPrevAxis = new ArrayList<Integer>();
					// - This stops the app crashing from happening because on on line 247, it will try to loop through the old value of
					// sizeOfPrev, where the size of the 'sizeOfPrev' would be larger the size of elements in the array causing an
					// IndexOutOfBounds exception ;)
					sizeOfPrev = 0; 
					
					// --- Debug Purposes --- //
					for(int c = 0;c <8;c++)
					{
						for(int d=0;d<8;d++)
						{
							System.out.print(strCheckersBoard[c][d]);
						}
						System.out.println("");
					}
					System.out.println("|----------|");
					// --- Debug Purposes --- //
				}*/
			}
		}							
	}
	public void playerTurn(String playerNo, View v, boolean passCondition, int passX, int passY, int upOrDown, int passImgId, boolean playerTurn)
	{
		// The coordinates of the currently selected square.
		int x = passX, y = passY;
		// The coordinates of the parent (root) square of highlight squares.
		int rootX, rootY;
		
		// If there are no highlighted squares.
		sizeOfPrev = xPrevAxis.size();    // Initially, the size will be zero.
		// WORKS :) - It will check whether all the squares are highlighted, and assign the result of the operation into 'isHighlighted'
		isHighlighted = checkHighlights(v);
				
		if(sizeOfPrev <= 0)
		{
			if(strCheckersBoard[x][y] == playerNo)
			{
				// Add new highlights for the newly selected square.
				highlightSquares(passCondition, x, y, upOrDown); // upOrDown = 1 so, x-1 i.e, go from bottom to the top of the checkersboard.
			}			
		}
		else
		{
			if(isHighlighted == false)
			{
				// Gets rid of the highlights, and wipes the array that stores the coordinates of the highlighted squares.
				removeHighlights();
				// Now, if the newly selected square has checkers piece which belongs to playerX, then highlight it, and its neighbouring squares.
				// This prevents us from highlighting empty parent squares ;)
				if(strCheckersBoard[x][y] == playerNo)
				{
					// Add new highlights for the newly selected square.
					highlightSquares(passCondition, x, y, upOrDown); // upOrDown = 1 so, x-1 i.e, go from bottom to the top of the checkersboard.
					// Debug purposes.
					System.out.println("In isHighlighted==false The size of xPrevAxis = " + xPrevAxis.size() + " and yPrevAxis = " + yPrevAxis.size());
				}
			}
			else if(isHighlighted == true)
			{
				// Moves the checkers piece to the new location.
				movePiece(v, x, y, playerNo, passImgId);
				// The next turn will be player x ;)
				playerOneTurn = playerTurn;
			}	
		}	
	}
}