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
	
	public TextView playerInfo;
	
	
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
		// This will state whose turn it is to make a move.
		playerInfo = (TextView) findViewById(R.id.playerInfo);
		// Create instiate the event class
		playerEvents = new PlayerMoves(imageOfSquares, imageOfSquares, strCheckersBoard, playerInfo);
		
		// Grabs the ActionBar.
		ActionBar actionBar = getActionBar();
		// Hides the ActionBar.
		actionBar.hide();

		
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
	// Player information.
	public TextView playerInfo;
	
	// Keeps track of the selected square.
	public int sizeOfPrev;
	// Keeps track of the number of pieces left for each player.
	int noOfPiecesPlayerOne, noOfPiecesPlayerTwo;
	// This is a test.
	public ArrayList<ArrayList<Integer>> arrayOfPrevCoordinatesX;
	public ArrayList<ArrayList<Integer>> arrayOfPrevCoordinatesY;
	public ArrayList<ArrayList<Integer>> arrayOfEnemyCoordinatesX;
	public ArrayList<ArrayList<Integer>> arrayOfEnemyCoordinatesY;
	// Keeps track of the highlighted squares.
	public ArrayList<Integer> xPrevAxis, yPrevAxis;
	// Keeps track of the neighbouring enemies to the highlight checkers piece.
	public ArrayList<Integer> xEnemyAxis, yEnemyAxis;
	
	public boolean isHighlighted, playerOneTurn, isEnemyAdjacent;
	
	public int highlightParentX, highlightParentY, xOfNewDest, yOfNewDest, erm;
	
	// Constructor
	public PlayerMoves(View[][] passSquares, ImageView[][] passImgSquares, String[][] passCheckersBoard, TextView passTextView)
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
		sizeOfPrev = 0;
		xPrevAxis = new ArrayList<Integer>();
		yPrevAxis = new ArrayList<Integer>();
		xEnemyAxis = new ArrayList<Integer>();
		yEnemyAxis = new ArrayList<Integer>();
		xOfNewDest = 0;
		yOfNewDest = 0;
		erm = 0;
		
		// THIS IS A TEST
		isEnemyAdjacent = false;
		arrayOfPrevCoordinatesX = new ArrayList<ArrayList<Integer>>();
		arrayOfPrevCoordinatesY = new ArrayList<ArrayList<Integer>>();
		arrayOfEnemyCoordinatesX = new ArrayList<ArrayList<Integer>>();
		arrayOfEnemyCoordinatesY = new ArrayList<ArrayList<Integer>>();
		
		// Another test... Aha.
		playerInfo = passTextView;
		
		// Display the player's turn. REMEMBER TO CHANGE THIS PARTICULAR SECTION WHEN I AUTOMATICALLY MAKE THE CODE DECIDE WHO GOES FIRST!!!
		playerInfo.setText("Player " + 2 + "'s \n Turn.");
		// Keeps track of the number of pieces.
		noOfPiecesPlayerOne = 12;
		noOfPiecesPlayerTwo = 12;
		
	}
	// I declared this method as synchronised hoping the code runs one at a time otherwise,if I clicked ib two buttons at the same time, I have a hunch
	// that it may cause a series of problems.
	public synchronized void onClick(View v)
	{
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
						// If there are no more pieces for player one...
						if(noOfPiecesPlayerOne <= 0)
						{
							// End the game.
							playerInfo.setText("Game Over!\nPlayer " + 2 + ", Wins!");
						}
						else
						{
							// We move our pieces as normal.
							playerTurn("1", v, x >= 1 && x <= 7, x, y, -1, R.drawable.dark_brown_piece, R.drawable.king_dark_brown_piece, false, x >= 2, "2", noOfPiecesPlayerTwo);		// Nice, it works.
						}
					}
					else
					{
						// If there are no more pieces for player two...
						if(noOfPiecesPlayerTwo <= 0)
						{
							// End the game.
							playerInfo.setText("Game Over!\nPlayer " + 1 + ", Wins!");
						}
						else
						{
							System.out.println("Player Two's Turn.");
							// If it is Player two's turn...
							playerTurn("2", v, x >= 0 && x <= 6, x, y, 1, R.drawable.light_brown_piece, R.drawable.king_light_brown_piece, true, x <= 5, "1", noOfPiecesPlayerOne);	// Nice, it works.		
						}
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
	public void prepareHighlight(boolean firstCondition, boolean secondCondition, int passX, int passY, int upOrDown, int leftOrRight)
	{
		// Construct a method that will handle how many neighbouring squares that need to be highlighted, or only highlights enemy square, should...
		// it be the case.
		
		int x = passX;
		int y = passY;
		
		// Debug purposes.
		//System.out.println("x+(upOrDown+upOrDown) = " + x + "+(" + upOrDown + "+" + upOrDown + ") = " + (x+(upOrDown+upOrDown)));
		//System.out.println("y+(leftOrRight+leftOrRight) = " + y + "+(" + leftOrRight + "+" + leftOrRight + ") = " + (y+(leftOrRight+leftOrRight)));
		// Debug purposes.
		//System.out.println("x+upOrDown = " + x +"+" + upOrDown + " = " + (x+upOrDown));
		//System.out.println("y+leftOrRight = " + y +"+" + leftOrRight + " = " + (y+leftOrRight));
		
		// If neighbouring square contains an enemy piece...
		if(firstCondition && strCheckersBoard[x+(upOrDown+upOrDown)][y+(leftOrRight+leftOrRight)] == "0") 
		{	
			// And there were no neighbouring enemy pieces prior to this one, then...
			if(xEnemyAxis.size() == 0)
			{
				// Clear the x/yPrevAxis ArrayList i.e. gets rid of the previous highlights.
				xPrevAxis = new ArrayList<Integer>();
				yPrevAxis = new ArrayList<Integer>();
				// Stores the coordinates of parent for later use and makes sure these values are the first element in their ArrayLists ;)
				xPrevAxis.add(0, new Integer(x));
				yPrevAxis.add(0, new Integer(y));
				// Coordinates of the enemy square. 
				xEnemyAxis.add(x+upOrDown);
				yEnemyAxis.add(y+(leftOrRight));
				// Coordinates of the empty square neighbouring the enemy piece.
				xPrevAxis.add(x+(upOrDown+upOrDown));
				yPrevAxis.add(y+(leftOrRight+leftOrRight));
				// Experiment.
				//isAdjacentToEnemy = true;
			}
			else
			{
				// If there was already a neighbouring enemy piece prior to this one, then...
				// Remove duplicate parent root.
				xPrevAxis.remove(new Integer(x));
				yPrevAxis.remove(new Integer(y));
				// Stores the coordinates of parent for later use and makes sure these values are the first element in their ArrayLists ;)
				xPrevAxis.add(0, new Integer(x));
				yPrevAxis.add(0, new Integer(y));
				// Coordinates of the enemy square. 
				xEnemyAxis.add(x+upOrDown);
				yEnemyAxis.add(y+(leftOrRight));
				// Coordinates of the empty square neighbouring the enemy piece.
				xPrevAxis.add(x+(upOrDown+upOrDown));
				yPrevAxis.add(y+(leftOrRight+leftOrRight));	
			}
			// Debug purposes.
			System.out.println("First entire 'if' statment went through");
		}
		else if(secondCondition)	// If neighbouring square is an empty one, prepare an highlight.
		{
			if(xEnemyAxis.size() > 0)
			{
				// If we have already seen a neighbouring enemy, then we do not anything because the rule of checkers says the
				// the player must take an enemy piece should such an opportunity arises.
			}
			else // Okay, this could take a while as I need to somehow stop this from running whenever we try to see if there any adjacent enemies.
			{	
				// If there are no neighbouring enemy pieces, then we just highlight the neighbouring empty square.
				// Remove duplicate parent root.
				xPrevAxis.remove(new Integer(x));
				yPrevAxis.remove(new Integer(y));
				// Stores the coordinates of parent for later use and makes sure these values are the first element in their ArrayLists ;)
				xPrevAxis.add(0, new Integer(x));
				yPrevAxis.add(0, new Integer(y));
				// Coordinates of the empty square.
				xPrevAxis.add(x+upOrDown);
				yPrevAxis.add(y+leftOrRight);
			}	
			// Debug purposes.
			System.out.println("Second entire 'if' statment went through");				
		}		
	}
	public void highlightSquares(boolean passCondition, int passX, int passY, int upOrDown, boolean attackConstraint, String opponentNo, String playerNo)
	{
		int x = passX;
		int y = passY;
		// Initialises the x/yPrevAxis coordinates ArrayList.
		// Holds the co-ordinates of the 'x' and 'y' axis of the highlighted squares.
		/*xPrevAxis = new ArrayList<Integer>();
		yPrevAxis = new ArrayList<Integer>();
		// Keeps track of the neighbouring enemies to the highlight checkers piece.
		xEnemyAxis = new ArrayList<Integer>();
		yEnemyAxis = new ArrayList<Integer>();
		// I need to look into this.
		sizeOfPrev = xPrevAxis.size();*/
		
		// Gets rid of the highlights, and clears the helper ArrayLists such as the x/yPrevAxis and x/yEnemyAxis ArrayLists.
		//removeHighlights(); // I never actually needed this as I'd always call it just before calling this method (highlightSquares)
		
		// Our new code works fine, I basically mixed up the values for the 'attackConstraint' when passed into the 'playerTurn()' method... lol.
		// Now, I need it to make it so, that when an enemy is in the square, it only highlights the square for an attack, and nothing else.
		// After that, it must take the piece too. I think I got it.
		
		// Maybe, I need another if statement here... Somewhere. This section needs major work.
		String strKing = "K" + playerNo;
		
		if(strCheckersBoard[x][y].contains(strKing))
		{
			System.out.println("Well, there is indeed a king!");
			// If the piece selected is a king piece...
			if(x >= 1 && x <= 7)
			{
				// Debug purposes.
				System.out.println("if(x >= 1 && x <= 7) and strKing =" + strKing);
				// Only perform a move if we are on/within row 7 to row 1... x >= 2
				// Checks the left side and decides whether it should highlight the squre or not.
				// .contains() is more optimable than checking both, for example [x][y] == K2 || [x][y] == 2 where we know,
				// it will at least contain the number '2' ;)
				prepareHighlight(y >= 2 && x >= 2 && strCheckersBoard[x+(-1)][y-1].contains(opponentNo),
												 y >= 1 && strCheckersBoard[x+(-1)][y-1] == "0", x, y, -1, -1);
				// Checks the right side and decides whether it should highlight the square or not.
				prepareHighlight(y <= 5 && x >= 2 && strCheckersBoard[x+(-1)][y+1].contains(opponentNo),
												 y >= 0 && y <= 6 && strCheckersBoard[x+(-1)][y+1] == "0", x, y, -1 , 1);	
			}
			if(x >= 0 && x <= 6)
			{
				// Debug purposes.
				System.out.println("if(x >= 0 && x <= 6) and strKing =" + strKing);
				// Only perform a move if we are on/within row 6 to row 0... x <= 5
				// Checks the left side and decides whether it should highlight the squre or not.
				prepareHighlight(y >= 2 && x <= 5 && strCheckersBoard[x+(1)][y-1].contains(opponentNo),
												 y >= 1 && strCheckersBoard[x+(1)][y-1] == "0", x, y, 1, -1);
				// Checks the right side and decides whether it should highlight the square or not.
				prepareHighlight(y <= 5 && x <= 5 && strCheckersBoard[x+(1)][y+1].contains(opponentNo),
												 y >= 0 && y <= 6 && strCheckersBoard[x+(1)][y+1] == "0", x, y, 1, 1);	
			}		
		}
		else if(strCheckersBoard[x][y] == playerNo && passCondition)
		{
			System.out.println("else if(strCheckersBoard[x][y] == playerNo && passCondition) AND strCheckersBoard[" + x + "][" + y + "] = " + strCheckersBoard[x][y]);
			// If it is just a standard piece then...
			// I think it works fine. I accidentally put a [y+2] and a [y-2] for both of the second conditions of the 'prepareHighlight'
			// Checks the left side and decides whether it should highlight the squre or not.
			prepareHighlight(y >= 2 && attackConstraint && strCheckersBoard[x+(upOrDown)][y-1].contains(opponentNo),
											 y >= 1 && strCheckersBoard[x+(upOrDown)][y-1] == "0", x, y, upOrDown, -1);
			// Checks the right side and decides whether it should highlight the square or not.
			prepareHighlight(y <= 5 && attackConstraint && strCheckersBoard[x+(upOrDown)][y+1].contains(opponentNo),
											 y >= 0 && y <= 6 && strCheckersBoard[x+(upOrDown)][y+1] == "0", x, y, upOrDown, 1);		
			// Adds the highlights to the squares based on the result from the 'prepareHighlight' method.
			//addHighlight();		// Uncomment if it breaks the application... Lol.
		}
		
		/*
		if(passCondition)
		{
			// I think it works fine. I accidentally put a [y+2] and a [y-2] for both of the second conditions of the 'prepareHighlight'
			// Checks the left side and decides whether it should highlight the squre or not.
			prepareHighlight(y >= 2 && attackConstraint && strCheckersBoard[x+(upOrDown)][y-1] == opponentNo,
											 y >= 1 && strCheckersBoard[x+(upOrDown)][y-1] == "0", x, y, upOrDown, -1);
			// Checks the right side and decides whether it should highlight the square or not.
			prepareHighlight(y <= 5 && attackConstraint && strCheckersBoard[x+(upOrDown)][y+1] == opponentNo,
											 y >= 0 && y <= 6 && strCheckersBoard[x+(upOrDown)][y+1] == "0", x, y, upOrDown, 1);		
			// We will add the highlights to the squares based on the result from the 'prepareHighlight' method later on.
		}
		*/
		
		 //OLD CODE FOR HIGHLIGHT WHICH SHOULD STILL WORK... I HOPE. I DID CHANGE THE CODE FOR THE 'addHighlight' METHOD SO THAT MIGHT
		//	MIGHT BE A PROBLEM.
		/*
		if(passCondition)
		{
			// Only highlight the squares if the neighbbouring squares are vacant.
			if(y >= 2 && attackConstraint && strCheckersBoard[x+(upOrDown)][y-1] == opponentNo && strCheckersBoard[x+(upOrDown+upOrDown)][y-2] == "0")
			{	
				// WORKS - Adds an highlight to a neighbouring empty square or enemy piece (which needs to be implemented).
				addHighlight(x, y, upOrDown+upOrDown, -2);	// y-2
				System.out.println("First if statment went through");
			}
			else if(y >= 1 && strCheckersBoard[x+upOrDown][y-1] == "0")
			{
				// WORKS - Adds an highlight to a neighbouring empty square or enemy piece (which needs to be implemented).
				addHighlight(x, y, upOrDown, -1);	// y-1
				System.out.println("Second if statment went through");
			}
			
			if(y <= 5 && attackConstraint && strCheckersBoard[x+(upOrDown)][y+1] == opponentNo && strCheckersBoard[x+(upOrDown+upOrDown)][y+2] == "0")
			{
				// WORKS - Adds an highlight to a neighbouring empty square or enemy piece (which needs to be implemented).
				addHighlight(x, y, upOrDown+upOrDown, 2);	// y+2
				System.out.println("Third if statment went through");
			}
			else if(y <= 6 && strCheckersBoard[x+upOrDown][y+1] == "0")
			{
					// WORKS - Adds an highlight to a neighbouring empty square or enemy piece (which needs to be implemented).
					addHighlight(x, y, upOrDown, 1);	// y+1
					System.out.println("Fourth if statment went through");
			}
			
			// Debug purposes - It works in the way I hoped it would :)
			for(int a = 0;a<xPrevAxis.size();a++)
			{
				System.out.print("xPrevAxis.get(" + a + ").intValue() = " + xPrevAxis.get(a).intValue() + " and ");
				System.out.println("yPrevAxis.get(" + a + ").intValue() = " + yPrevAxis.get(a).intValue());
			}
			
			// Debug purposes.
			sizeOfPrev = xPrevAxis.size();
			System.out.println("Printed from the if(xPrevAxis.size() == " + sizeOfPrev + " -- The size of xPrevAxis is now " + sizeOfPrev);		
		}*/
	}
	//public void addHighlight(int passX, int passY, int upOrDown, int leftOrRight)
	public void addHighlight(ArrayList<Integer> passCoordinatesX, ArrayList<Integer> passCoordinatesY)
	{
		/*// leftOrRight would either be a negative value (go right), and a positive value (go left)
		int x = passX;
		int y = passY;
		
		// Remove duplicate.
		passCoordinatesX.remove(new Integer(x));
		yPrevAxis.remove(new Integer(y));
		// Stores the coordinates for later use and makes sure these values are the first element in their ArrayLists ;)
		xPrevAxis.add(0, new Integer(x));
		yPrevAxis.add(0, new Integer(y));
		
		// Highlights the selected square.
		squaresOfBoard[x][y].setBackground(new ColorDrawable(0xFF999966));
		// Also highlights the neighbouring square to left/right of it.
		squaresOfBoard[x+upOrDown][y+(leftOrRight)].setBackground(new ColorDrawable(0xFF999966));
		// Adds the coordinates of the neighbouring square to the x/yPrevAxis ArrayList ;)
		xPrevAxis.add(new Integer(x+upOrDown));
		yPrevAxis.add(new Integer(y+(leftOrRight)));
		
		*/
		// Attempt an Optimal Version.
		
		int parentX;
		int parentY;
		int highlightX, highlightY;
		
		// If the constraints are satisfied, then add the highlights...
	
		if(passCoordinatesX.size() > 0)
		{
			for(int count = 1; count < passCoordinatesX.size();count++)
			{
				// Coordinates of the parent of the highlighted squares.
				parentX = passCoordinatesX.get(0);
				parentY = passCoordinatesY.get(0);
				highlightX = passCoordinatesX.get(count);
				highlightY = passCoordinatesY.get(count);
				
				// Highlights the selected square.
				squaresOfBoard[parentX][parentY].setBackground(new ColorDrawable(0xFF999966));
				// Also highlights the neighbouring square to left/right of it.
				squaresOfBoard[highlightX][highlightY].setBackground(new ColorDrawable(0xFF999966));		
			}	
		}
		
				
		//}
		// Highlights the selected square.
		//squaresOfBoard[parentX][parentY].setBackground(new ColorDrawable(0xFF999966));
		// Also highlights the neighbouring square to left/right of it.
		//squaresOfBoard[highlightX][highlightY].setBackground(new ColorDrawable(0xFF999966));
		
		//
	}
	public void removeHighlights(ArrayList<Integer> passCoordinatesX, ArrayList<Integer> passCoordinatesY)
	{
		// If the square is not part of the highlighted squares, then remove the highlights of the existing square...
		for(int rm = 0;rm < passCoordinatesX.size();rm++)
		{
			// Remove the highlights from the highlighted squares.
			int xHighlighted = passCoordinatesX.get(rm).intValue();
			int yHighlighted = passCoordinatesY.get(rm).intValue();
			squaresOfBoard[xHighlighted][yHighlighted].setBackground(null);
			System.out.println("The coordinates of the square(s) that we wish to remove the highlights from, is row/column is row=" + xHighlighted + "and column=" + yHighlighted);
		}
	}
	public void clearHelperArrays()
	{
			// If the square is not part of the highlighted squares, then remove the highlights of the existing square...
			/*for(int rm = 0;rm < sizeOfPrev;rm++)
			{
				// Remove the highlights from the highlighted squares.
				int xHighlighted = xPrevAxis.get(rm).intValue();
				int yHighlighted = yPrevAxis.get(rm).intValue();
				squaresOfBoard[xHighlighted][yHighlighted].setBackground(null);
			}*/
			// When I highlight a checkers piece, and then click on another square that is not part of the highlighted squares, it removes the...
			// highlights as it should do but, when I try to click on the checkers piece that was once highlighted, it does not highlight again.
			// The following code may solve this, inshallah... Well, that sorted it :) alhamdullah.
			// Holds the co-ordinates of the 'x' and 'y' axis of the highlighted squares.
			xPrevAxis = new ArrayList<Integer>();
			yPrevAxis = new ArrayList<Integer>();
			// I will need to also re-intialise the x/yEnemyAxis ArrayLists.
			xEnemyAxis = new ArrayList<Integer>();
			yEnemyAxis = new ArrayList<Integer>();
			// I need to look into this.
			sizeOfPrev = xPrevAxis.size();
			
			// If I use .clear() instead of reinitialising the ArrayList, when I want to perform a capture, it does not work.
			
	}
	public boolean checkHighlights(int passX, int passY, ArrayList<Integer> passCoordinatesX, ArrayList<Integer> passCoordinatesY)
	{
		// Coordinates of the selected view.
		int x = passX, y = passY;
		// Size of the highlighted squares.
		sizeOfPrev = passCoordinatesX.size();
		
		boolean isHighlighted = true;
		// For all highlighted squares, check whether the selected square is part of the highlighted squares.
			
		if(sizeOfPrev > 0)
		{	
			for(int h = 0;h < sizeOfPrev;h++)
			{
				// The coordinates of the possibly highlighted square.
				int xHighlighted = passCoordinatesX.get(h).intValue();
				int yHighlighted = passCoordinatesY.get(h).intValue();
				
				// Actually, it may be better to base these conditions on Strings instead of Views. That way the computer can work with the code a bit easier.
				//if(squaresOfBoard[xHighlighted][yHighlighted].equals(v))
				//if(squaresOfBoard[x][y].equals(squaresOfBoard[xHighlighted][yHighlighted]))
				if(x == xHighlighted && y == yHighlighted) // This is safer to use, in terms of a IndexOutOfBoundException, and less expensive.
				{
					// The selected square is part of the highlighted squares.
					isHighlighted = true;
					System.out.println("It is highlighted and xHighlighted=" + xHighlighted + " and yHighlighted=" + yHighlighted);
					// Close the loop as soon as we speak the selected square (if) it is part of highlighted squares. 
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
		}else
		{
			// Since there are no highlighted squares to even check, we just say it is false.
			isHighlighted = false;
			// Debug purposes.
			System.out.println("The size of highlight squares equals " + sizeOfPrev);
		}
		
		// This will later be assigned to the global variable 'isHighlighted'
		return isHighlighted;		
	}
	public void movePiece(int passX, int passY, int upOrDown, ArrayList<Integer> passListOfRows, ArrayList<Integer> passListOfColumns,
												ArrayList<Integer> passEnemyX, ArrayList<Integer> passEnemyY, String strDest, String opponentNo, int destImg,
												int passImgOfKing)
	{
		// Another test.
		xOfNewDest = 0;
		yOfNewDest = 0;
		
		// I need to implement this in a bit.
		int sizeOfPrev = passListOfRows.size();
		int x = passX, y = passY;
		String strSource;
		
		for(int mv = 1;mv < sizeOfPrev;mv++)
		{	
			// The coordinates of the root square of highlighted squares
			int parentPrevX = passListOfRows.get(0).intValue();
			int parentPrevY = passListOfColumns.get(0).intValue();
			// The coordinates of the neighbouring squares of the highlighted squares.
			int prevX = passListOfRows.get(mv).intValue();
			int prevY = passListOfColumns.get(mv).intValue();

			// If the selected piece is part of the highlighted neighbouring squares, then perform blah de blah blah.
			//if(v.equals(squaresOfBoard[prevX][prevY]))
			//if(squaresOfBoard[x][y].equals(squaresOfBoard[prevX][prevY]))
			// x == prevX && y == prevY would be a much better/safer condition.
			if(squaresOfBoard[x][y].equals(squaresOfBoard[prevX][prevY]) && strCheckersBoard[x][y] != opponentNo)
			{
				// Close the loop after this iteration.
				mv = sizeOfPrev;
				// Debug purposes.
				System.out.println("We successfully moved the piece :)");
				// Temporary variable that will hold the value at the old location (i.e. the piece we wish to move).
				strSource = strCheckersBoard[parentPrevX][parentPrevY];
				
				// THIS IS A HUGE TEST. The test succeeded :)
				// If the parent square has a neighbouring enemy piece, then we determine which enemy piece it is.
				if(passEnemyX.size() > 0)
				{
					// Assuming that the parent highlighted sqaure was neighbouring more than one enemy piece, we would need to find out the right enemy...
					// Piece to get rid of.
					for(int e = 0; e < passEnemyX.size(); e++)
					{
						int enemyCoordinateX = passEnemyX.get(e).intValue();
						int enemyCoordinateY = passEnemyY.get(e).intValue();
						
						// Tries to go up/down depending on whose player turn is it. If it is player two's, then it will be x+1.
						/*int checkX = parentPrevX + upOrDown;

						// We check the square below...
						if(checkX == enemyCoordinateX && (prevY+1) == enemyCoordinateY)
						{
							// The enemy square is here. This works. Nice.
							System.out.println("These are the correct coordinates of the enemy square checkX=" + checkX + " and checkY=" + (prevY+1));
							// We clear the space i.e. take the piece.
							strCheckersBoard[enemyCoordinateX][enemyCoordinateY] = "0";
							// We clear the space (visually) i.e. take the piece
							imageOfSquares[enemyCoordinateX][enemyCoordinateY].setImageResource(0);
							// Makes this the last 'e' iteration.
							e = passEnemyX.size();
						}
						else if(checkX == enemyCoordinateX && (prevY-1) == enemyCoordinateY)
						{
							// Check the square below...
							// The enemy square is here. This works. Nice.
							System.out.println("These are the correct coordinates of the enemy square checkX=" + checkX + (prevY+1));
							// We clear the space i.e. take the piece.
							strCheckersBoard[enemyCoordinateX][enemyCoordinateY] = "0";
							// We clear the space (visually) i.e. take the piece
							imageOfSquares[enemyCoordinateX][enemyCoordinateY].setImageResource(0);
							// Makes this the last 'e' iteration.
							e = passEnemyX.size();
						}*/
						
						int checkBelow = parentPrevX + 1;
						int checkAbove = parentPrevX - 1;

						// We check the square below...
						if(checkBelow == enemyCoordinateX && (prevY+1) == enemyCoordinateY)
						{
							// The enemy square is here. This works. Nice.
							System.out.println("These are the correct coordinates of the enemy square checkBelow=" + checkBelow + " and checkY=" + (prevY+1));
							// We clear the space i.e. take the piece.
							strCheckersBoard[enemyCoordinateX][enemyCoordinateY] = "0";
							// We clear the space (visually) i.e. take the piece
							imageOfSquares[enemyCoordinateX][enemyCoordinateY].setImageResource(0);
							// Makes this the last 'e' iteration.
							e = passEnemyX.size();
							// Will decrease the number of pieces the opponent has by 1.
							if(strDest.contains("1")){noOfPiecesPlayerTwo--;}else{noOfPiecesPlayerOne--;}
							
						}
						else if(checkBelow == enemyCoordinateX && (prevY-1) == enemyCoordinateY)
						{
							// Check the square below...
							// The enemy square is here. This works. Nice.
							System.out.println("These are the correct coordinates of the enemy square checkBelow=" + checkBelow + (prevY+1));
							// We clear the space i.e. take the piece.
							strCheckersBoard[enemyCoordinateX][enemyCoordinateY] = "0";
							// We clear the space (visually) i.e. take the piece
							imageOfSquares[enemyCoordinateX][enemyCoordinateY].setImageResource(0);
							// Makes this the last 'e' iteration.
							e = passEnemyX.size();
							// Will decrease the number of pieces the opponent has by 1.
							if(strDest.contains("1")){noOfPiecesPlayerTwo--;}else{noOfPiecesPlayerOne--;}
						}
						else if(checkAbove == enemyCoordinateX && (prevY+1) == enemyCoordinateY)
						{
							// Check the square above...
							// The enemy square is here. This works. Nice.
							System.out.println("These are the correct coordinates of the enemy square checkAbove=" + checkAbove + " and checkY=" + (prevY+1));
							// We clear the space i.e. take the piece.
							strCheckersBoard[enemyCoordinateX][enemyCoordinateY] = "0";
							// We clear the space (visually) i.e. take the piece
							imageOfSquares[enemyCoordinateX][enemyCoordinateY].setImageResource(0);
							// Makes this the last 'e' iteration.
							e = passEnemyX.size();
							// Will decrease the number of pieces the opponent has by 1.
							if(strDest.contains("1")){noOfPiecesPlayerTwo--;}else{noOfPiecesPlayerOne--;}
						}
						else if(checkAbove == enemyCoordinateX && (prevY-1) == enemyCoordinateY)
						{
							// Check the square above...
							// The enemy square is here. This works. Nice.
							System.out.println("These are the correct coordinates of the enemy square checkAbove=" + checkAbove + (prevY+1));
							// We clear the space i.e. take the piece.
							strCheckersBoard[enemyCoordinateX][enemyCoordinateY] = "0";
							// We clear the space (visually) i.e. take the piece
							imageOfSquares[enemyCoordinateX][enemyCoordinateY].setImageResource(0);
							// Makes this the last 'e' iteration.
							e = passEnemyX.size();
							// Will decrease the number of pieces the opponent has by 1.
							if(strDest.contains("1")){noOfPiecesPlayerTwo--;}else{noOfPiecesPlayerOne--;}
						}
						
					}
				}
				
				// If a piece of player one reaches the last row, transform the piece into a king...
				
				// Creates the king string corresponding to the player number.
				String strKing = "K" + strDest;
				
				if(x == 0 && strCheckersBoard[parentPrevX][parentPrevY] == "1")
				{
					// Debug purposes.
					System.out.println("strSource does equal player one and let's output strKing=" + strKing);
					// Now, the piece will now become a king at the new location.
					strCheckersBoard[parentPrevX][parentPrevY] = strKing;
					
				}else if(x == 7 && strCheckersBoard[parentPrevX][parentPrevY] == "2")
				{
					// Debug purposes.
					System.out.println("strSource does equal player two and let's output strKing=" + strKing);
					// If a piece of player two reaches the last row, transform the piece into a king.
					// Now, the piece will now become a king at the new location.
					strCheckersBoard[parentPrevX][parentPrevY] = strKing;
				}
				
				// If the piece that we wish to move/perform capture is a king then...
				// if(strCheckersBoard[parentPrevX][parentPrevY] == strKing) never satisfies, regardless. == and mutated strings seem to cause problems.
				if(strCheckersBoard[parentPrevX][parentPrevY].contains(strKing))
				{
					// Finally, we will clear the square of the checker piece's old location
					strCheckersBoard[parentPrevX][parentPrevY] = "0";
					// Clear the image of that square.
					imageOfSquares[parentPrevX][parentPrevY].setImageResource(0);
					// Moves it to the new location...
					strCheckersBoard[x][y] = strKing;
					// (Visually) Moves the checkers piece into the new location
					imageOfSquares[x][y].setImageResource(passImgOfKing);
				}
				else //if(strCheckersBoard[parentPrevX][parentPrevY] == strDest)
				{
					// Finally, we will clear the square of the checker piece's old location
					strCheckersBoard[parentPrevX][parentPrevY] = "0";
					// Clear the image of that square.
					imageOfSquares[parentPrevX][parentPrevY].setImageResource(0);
					// If it is an ordinary piece, then it will just be a simple capture/move.
					strCheckersBoard[x][y] = strSource;
					// Move the checkers piece into the new location.
					imageOfSquares[x][y].setImageResource(destImg);
				}
				
				/*// Finally, we will clear the square of the checker piece's old location
				strCheckersBoard[parentPrevX][parentPrevY] = "0";
				// Clear the image of that square.
				imageOfSquares[parentPrevX][parentPrevY].setImageResource(0);
				// Occupies new space in the helper array.*/
				
				//strCheckersBoard[x][y] = strDest;
				// Move the checkers piece into the new location.
				//imageOfSquares[x][y].setImageResource(destImg);
				// THIS IS A TEST
				xOfNewDest = x;
				yOfNewDest = y;
				// Clear the row/column values of the highlighted squares.
				/*xPrevAxis = new ArrayList<Integer>(); //- This causes the app to crash when I try to move another piece after successfully moving one beforehand.
				yPrevAxis = new ArrayList<Integer>();
				xEnemyAxis = new ArrayList<Integer>();
				yEnemyAxis = new ArrayList<Integer>();
				// - This stops the app crashing from happening because on on line 247, it will try to loop through the old value of
				// sizeOfPrev, where the size of the 'sizeOfPrev' would be larger the size of elements in the array causing an
				// IndexOutOfBounds exception ;)
				sizeOfPrev = 0; */
				
				
				
				// Probably need to put an IF statment round this because this could possibly be a consecutive move...
				// Now, I need to get rid of the highlights, etc.
				removeHighlights(passListOfRows, passListOfColumns);
				clearHelperArrays();
				
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
					removeHighlights(passListOfRows, passListOfColumns);
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
			}else
			{
				// Debug purposes.
				System.out.println("We do nothing because an opposing piece was clicked or it was an invalid move.");
			}
		}							
	}
	
	// -----------------THIS IS A TEST, IF ALL FAILS, WE DELETE THIS CODE AND UNCOMMENT THE CODE BELOW ----------------- \\
	
	public void playerTurn(String playerNo, View v, boolean passCondition, int passX, int passY, int upOrDown, int passImgId, int passImgOfKing, boolean playerTurn, boolean attackConstraint, String opponentNo, int passNoOfPieces)
	{

		// I can try write the sample code here. At least I will have all the variables ready to be used. :)		
		// The coordinates of the currently selected square.
		int x = passX, y = passY;
		// The coordinates of the parent (root) square of highlight squares.
		int rootX, rootY;
		
		// If there are no highlighted squares.
		// sizeOfPrev = xPrevAxis.size();    // Initially, the size will be zero.
		// WORKS :) - It will check whether all the squares are highlighted, and assign the result of the operation into 'isHighlighted'
		// isHighlighted = checkHighlights(x, y, xPrevAxis, yPrevAxis);
		// Holds the value on how many times the program it saw a piece of 'playerNo'
		int noOfTimes = 0;
				
		// This app is so f***ed, that is not even a joke.
		// I NEED TO COME UP WITH A BETTER CONDITION FOR RUNNING THE INITIAL CHECK FOR ADJACENT ENEMIES.
		// OOOOOOOOH, I know. I can put it in isHighlighted == false statement... Maybe, that might work. Inshallah it does.
		//if(true)
		// This works as a better condition. Consecutive attacks work with this condition too.
		if(arrayOfPrevCoordinatesX.size() <= 0)
		{	
			// Debug purposes.
			System.out.println("The initial statement that checks for adjacent enemies has just started.");
			// Erm, yeah.
			String strKing = "K" + playerNo;
			
			for(int row = 0; row < 8;row++)
			{
				for(int column =((row+1)%2);column<8;column+=2)
				{
					if(strCheckersBoard[row][column] == playerNo || strCheckersBoard[row][column].contains(strKing))
					{
						// Debug purposes.
						// System.out.println("Does this section ever get executed?");
						
						if(playerNo == "1")
						{
							// Debug purposes.
							// System.out.println("I crashed at row=" + row + "/column=" + column + " and the current player is player " + playerNo);
							highlightSquares(row >= 1 && row <=7, row, column, upOrDown, row >=2, opponentNo, playerNo);
						}
						else if(playerNo == "2")
						{
							// Debug purposes.
							// System.out.println("I crashed at row=" + row + "/column=" + column + " and the current player is player " + playerNo);
							highlightSquares(row >= 0 && row <= 6, row, column, upOrDown, row <=5, opponentNo, playerNo);
						}		
						noOfTimes++;
						// Debug purposes. - This does not even run... I guess that's why the code does not break any more... Lol
						// System.out.println("Total number of pieces seen, is " + noOfTimes + " time(s).");
						
						if(xEnemyAxis.size() > 0)
						{
							// There is an adjacent enemy.
							isEnemyAdjacent = true;
							// We apply the highligh// Add the ArrayLists to the master ArrayList... Aha.
							arrayOfPrevCoordinatesX.add(xPrevAxis);
							arrayOfPrevCoordinatesY.add(yPrevAxis);
							arrayOfEnemyCoordinatesX.add(xEnemyAxis);
							arrayOfEnemyCoordinatesY.add(yEnemyAxis);
							// Debug purposes.
							// System.out.println("row=" + row + ", column=" + column + " is neighbouring an enemy.");
							// Then clear the standard ArrayLists and repeat.
							clearHelperArrays();
						}
						else
						{
							// Clear the standard ArrayLists.
							clearHelperArrays();
						}	
					}
				}
			}
			
			// Then we apply the highlights, and blah de blah.
			for(int h = 0; h < arrayOfPrevCoordinatesX.size();h++)
			{
				// Yeah, we add it... Init.
				addHighlight(arrayOfPrevCoordinatesX.get(h), arrayOfPrevCoordinatesY.get(h));
			}
		}
		// End of initial check before player makes a move (except for consecutive captures)
		
		// From this section...
		
		// Checks whether the selected square is part of the highlighted squares.
		if(arrayOfPrevCoordinatesX.size() > 0)
		{
			// Checks whether it is highlighted...
			for(int check = 0;check < arrayOfPrevCoordinatesX.size();check++)
			{
				// There is a loop within the following 'checkHighlights()' function.
				isHighlighted = checkHighlights(x, y, arrayOfPrevCoordinatesX.get(check), arrayOfPrevCoordinatesY.get(check));
				
				if(isHighlighted == true)
				{
					// We break the loop.
					check = arrayOfPrevCoordinatesX.size();
					// Debug purposes.
					System.out.println("After checking through arrayOfPrevCoordinates, the square is part of the highlighted squares.");
				}else
				{
					// We continue... I guess.
				}
			}
		}else
		{
			// The for-loop may not run at all so, we assign a value to isHighlighted.
			isHighlighted = false;
		}
		
		// ...To this section works-ish.	
		
		// Checks whether the highlighted piece is adjacent to an enemy (and there could be more than one.)
		if(isEnemyAdjacent == true)
		{
			// We lock the game until the player makes a capture.
			// Debug purposes.
			System.out.println("if(isEnemyAdjacent == true) just ran so, a capture needs to be performed.");
			// This will determine what type of move (standard or a capture) it should make, and also make the move.
			performMoveAndCheckAdjacent(passX, passY, attackConstraint, passCondition, upOrDown, playerNo, opponentNo, passImgId, passImgOfKing, playerTurn, passNoOfPieces);
			
		}else
		{
			//...Otherwise, we check if the selected square corresponds to the player's number and if it is part of the highlighted squares.
			if(strCheckersBoard[x][y].contains(playerNo) && isHighlighted == false)
			{
				// Debug purposes.
				System.out.println("the if(strCheckersBoard[x][y] == playerNo && isHighlighted == false) statement has just been run.");
				// Debug purposes.
				System.out.println("The square we selected is not part of the highlighted squares so, we will get rid of the old highlights");
				
				for(int rm = 0;rm < arrayOfPrevCoordinatesX.size();rm++)
				{
					// Gets rid of the highlights, and clears the helper ArrayLists such as the x/yPrevAxis and x/yEnemyAxis ArrayLists.
					removeHighlights(arrayOfPrevCoordinatesX.get(rm), arrayOfPrevCoordinatesY.get(rm));
				}
				
				clearHelperArrays(); 
				// Now, if the newly selected square has checkers piece which belongs to playerX, then highlight it, and its neighbouring squares.
				// I forgot to clear the master ArrayLists too... Which, I will do now.
				arrayOfPrevCoordinatesX.clear();
				arrayOfPrevCoordinatesY.clear();
				arrayOfEnemyCoordinatesX.clear();
				arrayOfEnemyCoordinatesY.clear();
				// Debug purposes.
				System.out.println("and the selected square does contain the player's checkers piece, then we highlight it and its neighbours.");
				// Add new highlights for the newly selected square.
				highlightSquares(passCondition, x, y, upOrDown, attackConstraint, opponentNo, playerNo); // upOrDown = 1 so, x-1 i.e, go from bottom to the top of the checkersboard.
				// Add the ArrayLists to the master ArrayList... Aha.
				arrayOfPrevCoordinatesX.add(xPrevAxis);
				arrayOfPrevCoordinatesY.add(yPrevAxis);
				// Clear the x/yPrevAxis and x/yEnemyAxis ArrayLists
				clearHelperArrays();
				// Adds the visual highlights to the squares based on the result from the 'prepareHighlight' method.
				addHighlight(arrayOfPrevCoordinatesX.get(0), arrayOfPrevCoordinatesY.get(0));
				// Debug purposes.
				System.out.println("The size of arrayOfPrevCoordinatesX.size()" + arrayOfPrevCoordinatesX.size() + "--- from if(strCheckersBoard[x][y] == playerNo && isHighlighted == false)");
			}
			else if(strCheckersBoard[x][y] == "0" && isHighlighted == true)
			{	
				// Or if the selected square is part of the highlighted squares, and the square is empty, we will see if we can make a move...
				// Debug purposes.
				System.out.println("else if(strCheckersBoard[x][y] == 0 && isHighlighted == true) just ran but, failed miserably.");
				// This will determine what type of move (standard or a capture) it should make, and also make the move.
				performMoveAndCheckAdjacent(passX, passY, attackConstraint, passCondition, upOrDown, playerNo, opponentNo, passImgId, passImgOfKing, playerTurn, passNoOfPieces);
			}
		}
		
		
			
			
			
			
			
			// OK, the copy-and-paste ends here.
			
			//performMoveAndCheckAdjacent(passX, passY, attackConstraint, passCondition, upOrDown, playerNo, opponentNo, passImgId, playerTurn);
			/*
			// Debug purposes.
			System.out.println("arrayOfPrevCoordinatesX.size() > 0 if statement just ran.");
			
			// May need to add code here so, it re-highlights the squares, blah blah.
			// If there are neighbouring enemies already highlighted... We check if a move has been made on the 
			// isEnemyAdjacent = true;
			// Then we loop through until each move has been made.
			// We will get the destination squares of each piece.
			
			// So far, so good.
			System.out.println("Player "+ playerNo + " You must make an attack... Dawg. and erm is equal to: " + erm);
			
			for(int i = 0 ; i < arrayOfPrevCoordinatesX.size(); i++)
			{
				//performMoveAndCheckAdjacent(passX, passY, attackConstraint, passCondition, upOrDown, playerNo, opponentNo, passImgId);
				
				// Debug purposes.
				System.out.println("The size of the arrayOfPrevCoordinates is equal to " + arrayOfPrevCoordinatesX.size());
				
				//arrayOfPrevCoordinatesX.get(i);
				ArrayList<Integer> autoPrevX = arrayOfPrevCoordinatesX.get(i);
				ArrayList<Integer> autoPrevY = arrayOfPrevCoordinatesY.get(i);
				ArrayList<Integer> autoEnemyX = arrayOfEnemyCoordinatesX.get(i);
				ArrayList<Integer> autoEnemyY = arrayOfEnemyCoordinatesY.get(i);
				// Moves the checkers piece to the new location.
				// Continue from here on out. It captures the piece but, the highlights stay, and it also captures some undesired pieces. Needs work!
				// CONTINUE FROM AROUND HERE!
				movePiece(x, y, upOrDown, autoPrevX, autoPrevY, autoEnemyX, autoEnemyY, playerNo, opponentNo, passImgId);
				
				// We need a way to check if it is at the new location.
				
				if(x == xOfNewDest && y == yOfNewDest)
				{
					// Debug purposes.
					System.out.println("We have moved the piece to the new location (via the auto generated highlight.");
					// If we have successfully moved the piece...
					// Makes sure this is the last iteration.
					i = arrayOfPrevCoordinatesX.size();
					// Then we check whether the new location is neighbouring an enemy...
					highlightSquares(passCondition, xOfNewDest, yOfNewDest, upOrDown, attackConstraint, opponentNo);
					// If xEnemyAxis.size() returns 0 then, we can hand over the player's turn
					
					if(xEnemyAxis.size() <= 0)
					{
						// Debug purposes.
						System.out.println("There are no potential captures that this piece can make from the new location so, we handover our turn to the opponent.");
						
						// There are no new neighbouring enemies in the new location.
						// We hand over our turn to the opponent.
						//playerOneTurn = playerTurn; // This was cause part of the problem for problem 2. I need to find a better place to place this.
						// Make erm equal to 0 so, this statment does not run again.
						
						// We will remove the remaining highlights... i.e. if there were two different pieces each neighbouring an enemy.
						for(int rm = 0;rm<arrayOfPrevCoordinatesX.size();rm++)
						{
							// Gets rid of the rest of the highlights for the other pieces that had potential to capture enemy pieces.
							// It works the way I hoped it to.
							removeHighlights(arrayOfPrevCoordinatesX.get(rm), arrayOfPrevCoordinatesY.get(rm));
						}
						
						erm = 0;
						// clear 'arrayOfPrev... arrays
						arrayOfPrevCoordinatesX.clear();
						arrayOfPrevCoordinatesY.clear();
						arrayOfEnemyCoordinatesX.clear();
						arrayOfEnemyCoordinatesY.clear();
						// I should also try clearing the xOfNewDest and yOfNewDest values too.
					}
					else
					{
						// Debug purposes.
						System.out.println("There is another neighbouring enemy at the new location. So, we don't stop until a move is made.");
						// THIS PART IS FUCKED. WHEN I TRY TO GET CAPTURE ANOTHER PIECE CONSECUTIVELY, IT CHANGES THE COLOR OF MY PIECE TO THE OPPONENT'S COLOR. NEEDS FIXING!
						// OK, this section is fine. It can now do consecutive attacks. 
						// clear 'arrayOfPrev... arrays... I might not need to clear this.
						arrayOfPrevCoordinatesX.clear();
						arrayOfPrevCoordinatesY.clear();
						arrayOfEnemyCoordinatesX.clear();
						arrayOfEnemyCoordinatesY.clear();
						// Add the ArrayLists to the master ArrayList... Aha.
						arrayOfPrevCoordinatesX.add(xPrevAxis);
						arrayOfPrevCoordinatesY.add(yPrevAxis);
						arrayOfEnemyCoordinatesX.add(xEnemyAxis);
						arrayOfEnemyCoordinatesY.add(yEnemyAxis);
						// We apply the highlights to the eligable squares...
						addHighlight(xPrevAxis, yPrevAxis);
						// Clear the standard ArrayLists.
						clearHelperArrays();
						
						
					}				
				}else
				{
					// Debug purposes.
					System.out.println("We have not moved the piece to the new location. (from auto-generated highlights section)");
				}					
			}*/
			
		//}
			
			
		// -- Blah de blah blah			
							
		
		//------
		
		/*// If the above f*** UP THE GAME. DELETE AND UNCOMMENT THE CODE BELOW :)
		int noOfTimes = 0;
		
		for(int row = 0; row < 8;row++)
		{
			for(int column = ((row+1)%2);column<8;column+=2)
			{
				if(strCheckersBoard[row][column] == playerNo)
				{
					// Gets rid of the highlights, and clears the helper ArrayLists such as the x/yPrevAxis and x/yEnemyAxis ArrayLists.
					//removeHighlights();
					//clearHelperArrays(); 
					// Creates an ArrayList that holds the coordinates for what square needs to be highlighted.
					// The problem seems to be coming from 'highlightSquares'
					//highlightSquares(passCondition, row, column, upOrDown, attackConstraint, opponentNo);
					// Add the ArrayLists to the master ArrayList... Aha.
					
					
					if(xEnemyAxis.size() > 0)
					{
						noOfTimes++;
						// Debug purposes.
						System.out.println("An enemy was encountered at " + noOfTimes + " different times.");
						// We apply the highlights to the eligable squares...
						//addHighlight(xPrevAxis, yPrevAxis);	
						// Add the ArrayLists to the master ArrayList... Aha.
						// I NEED TO ADD CODE!
						// Then clear the standard ArrayLists and repeat.
						//clearHelperArrays();
					}
					else
					{
						// Clear the standard ArrayLists.
						//clearHelperArrays();
					}	
				}
			}
		}
		
		// removeHighlights();
		
		
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
				highlightSquares(passCondition, x, y, upOrDown, attackConstraint, opponentNo); // upOrDown = 1 so, x-1 i.e, go from bottom to the top of the checkersboard.
				// THIS IS A TEST
				if(passCondition)
				{
					// Adds the highlights to the squares based on the result from the 'prepareHighlight' method.
					addHighlight(xPrevAxis, yPrevAxis);
				}				
			}			
		}
		else
		{
			if(isHighlighted == false)
			{
				// Gets rid of the highlights, and clears the helper ArrayLists such as the x/yPrevAxis and x/yEnemyAxis ArrayLists.
				removeHighlights();
				clearHelperArrays(); 
				// Now, if the newly selected square has checkers piece which belongs to playerX, then highlight it, and its neighbouring squares.
				// This prevents us from highlighting empty parent squares ;)
				if(strCheckersBoard[x][y] == playerNo)
				{
					// Add new highlights for the newly selected square.
					highlightSquares(passCondition, x, y, upOrDown, attackConstraint, opponentNo); // upOrDown = 1 so, x-1 i.e, go from bottom to the top of the checkersboard.
					// Debug purposes.
					// THIS IS A TEST
					if(passCondition)
					{
						// Adds the highlights to the squares based on the result from the 'prepareHighlight' method.
						addHighlight(xPrevAxis, yPrevAxis);
					}					
					System.out.println("In isHighlighted==false The size of xPrevAxis = " + xPrevAxis.size() + " and yPrevAxis = " + yPrevAxis.size());
				}
			}
			else if(isHighlighted == true)
			{
				// Moves the checkers piece to the new location.
				movePiece(v, x, y, upOrDown, xPrevAxis, yPrevAxis, playerNo, passImgId);
				// The next turn will be player x ;)
				playerOneTurn = playerTurn;
			}	
		}*/	
	}
	
	public void performMoveAndCheckAdjacent(int passX, int passY, boolean attackConstraint, boolean passCondition, int upOrDown, String playerNo, String opponentNo, int passImgId, int passImgOfKing, boolean playerTurn, int passNoOfPieces)
	{
			// Debug purposes.
			System.out.println("arrayOfPrevCoordinatesX.size() > 0 if statement just ran. (using our new function)");
			int x = passX, y = passY;

			for(int i = 0 ; i < arrayOfPrevCoordinatesX.size(); i++)
			{
				// Debug purposes.
				System.out.println("The size of the arrayOfPrevCoordinates is equal to " + arrayOfPrevCoordinatesX.size());
				
				// Grab the coordinates of the highlighted squares and the enemy pieces, if there are any enemies. 
				ArrayList<Integer> autoPrevX = arrayOfPrevCoordinatesX.get(i);
				ArrayList<Integer> autoPrevY = arrayOfPrevCoordinatesY.get(i);
				ArrayList<Integer> autoEnemyX;
				ArrayList<Integer> autoEnemyY;
				
				// Using '==' instead of 'greater than' works better. No IndexOutOfBoundExceptions 
				if(arrayOfEnemyCoordinatesX.size() == arrayOfPrevCoordinatesX.size())
				{
					autoEnemyX = arrayOfEnemyCoordinatesX.get(i);
					autoEnemyY = arrayOfEnemyCoordinatesY.get(i);
				}
				else
				{
					// We will create a autoEnemyX/Y ArrayList and pass it in later but, the movePiece() function will not use it.
					autoEnemyX = new ArrayList<Integer>();
					autoEnemyY = new ArrayList<Integer>();
				}
			
				// Moves the checkers piece to the new location.
				movePiece(x, y, upOrDown, autoPrevX, autoPrevY, autoEnemyX, autoEnemyY, playerNo, opponentNo, passImgId, passImgOfKing);
				
				// We need a way to check if it is at the new location.
				
				if(x == xOfNewDest && y == yOfNewDest)
				{
					// Debug purposes.
					System.out.println("We have moved the piece to the new location (via the auto generated highlight.");
					// If we have successfully moved the piece...
					// Makes sure this is the last iteration.
					i = arrayOfPrevCoordinatesX.size();
					// Then we check whether the new location is neighbouring an enemy...		
					highlightSquares(passCondition, xOfNewDest, yOfNewDest, upOrDown, attackConstraint, opponentNo, playerNo);
					// If xEnemyAxis.size() returns 0 then, we can hand over the player's turn
					
					// If a capture was made we check if there are neighbouring enemies at our new location...
					if(arrayOfEnemyCoordinatesX.size() > 0)
					{
						// Debug purposes.
						System.out.println("A capture was made prior to this move so, we will check if there any potential captures at our new location.");
						System.out.println("The size arrayOfEnemyCoordinatesX.size() == " + arrayOfEnemyCoordinatesX.size());
						// ...We check whether the new location is neighbouring an enemy.
						highlightSquares(passCondition, xOfNewDest, yOfNewDest, upOrDown, attackConstraint, opponentNo, playerNo);
						
						// If the size of xEnemyAxis.size() == 0, then we can say no new enemies have been found using the 'highlightSquares()' method above.
						if(xEnemyAxis.size() <= 0)
						{
							// Debug purposes.
							System.out.println("There are no potential captures that this piece can make from the new location so, we handover our turn to the opponent.");
							// We will remove the remaining highlights... i.e. if there were two different pieces each neighbouring an enemy.
							for(int rm = 0;rm<arrayOfPrevCoordinatesX.size();rm++)
							{
								// Gets rid of the rest of the highlights for the other pieces that had potential to capture enemy pieces.
								// It works the way I hoped it to.
								removeHighlights(arrayOfPrevCoordinatesX.get(rm), arrayOfPrevCoordinatesY.get(rm));
							}
							
							// clear the master ArrayLists.
							arrayOfPrevCoordinatesX.clear();
							arrayOfPrevCoordinatesY.clear();
							arrayOfEnemyCoordinatesX.clear();
							arrayOfEnemyCoordinatesY.clear();
							// I have just realised that I have not cleared xPrevAxis and yPrevAis ArrayList so, I'll do that now.
							clearHelperArrays();
							// I should also try clearing the xOfNewDest and yOfNewDest values too. Every time movePiece() is called, it initially clears
							// both 'xOfNewDest' and 'yOfNewDest'.
							// Handover the turn to opponent.
							playerOneTurn = playerTurn;
							// Since there are no adjacent enemies, we make it false.
							isEnemyAdjacent = false;
							// Display the player's turn.
							playerInfo.setText("Player " + opponentNo + "'s Turn");
						}
						else
						{
							// There is another neighbouring enemy at our new location so, next time this entire method is called again, thanks...
							// ...To the isEnemyAdjacent == true, if statement.
							// Debug purposes.
							System.out.println("There is another neighbouring enemy at the new location. So, we don't stop until a move is made.");
							
							// Hopefully, this should sort out problem 3... Yup, it sorted problem 3 :)
							for(int rm = 0;rm<arrayOfPrevCoordinatesX.size();rm++)
							{
								// Gets rid of the rest of the highlights for the other pieces that had potential to capture enemy pieces.
								// It works the way I hoped it to.
								removeHighlights(arrayOfPrevCoordinatesX.get(rm), arrayOfPrevCoordinatesY.get(rm));
							}
							//...Well, hopefully.
							
							
							// Clear the master ArrayLists
							arrayOfPrevCoordinatesX.clear();
							arrayOfPrevCoordinatesY.clear();
							arrayOfEnemyCoordinatesX.clear();
							arrayOfEnemyCoordinatesY.clear();
							// Add the ArrayLists to the master ArrayList... Aha.
							arrayOfPrevCoordinatesX.add(xPrevAxis);
							arrayOfPrevCoordinatesY.add(yPrevAxis);
							arrayOfEnemyCoordinatesX.add(xEnemyAxis);
							arrayOfEnemyCoordinatesY.add(yEnemyAxis);
							// We apply the highlights to the eligable squares...
							addHighlight(arrayOfPrevCoordinatesX.get(0), arrayOfPrevCoordinatesY.get(0));
							// Clear the standard ArrayLists.
							clearHelperArrays();
							// Since there are adjacent enemies, we make it true. That way, if(isEnemyAdjacent == true) will call this entire if statement again.
							isEnemyAdjacent = true;
						}
						
					}else
					{
						// Just a standard move.
						// Debug purposes.
						System.out.println("This is just a standard move.");
						// If it is just a standard move, we clear all the ArrayLists and handover our turn to the opponent.
						// clear 'arrayOfPrev... arrays... I might not need to clear this.
						arrayOfPrevCoordinatesX.clear();
						arrayOfPrevCoordinatesY.clear();
						arrayOfEnemyCoordinatesX.clear();
						arrayOfEnemyCoordinatesY.clear();
						// Clear the standard ArrayLists.
						clearHelperArrays();
						// Handover the turn to opponent.
						playerOneTurn = playerTurn;
						// Erm, I am so lost.
						isEnemyAdjacent = false;
						// I think I would need to remove the highlights too.
						// Display the player's turn.
						playerInfo.setText("Player " + opponentNo + "'s Turn");
					}
									
				}else
				{
					// Debug purposes.
					System.out.println("We have not moved the piece to the new location. (from auto-generated highlights section) so, we do nothing until then.");
				}					
			}
	}
	
	// -----------------THIS IS A TEST, IF ALL FAILS, WE DELETE THE CODE ABOVE AND UNCOMMENT THE CODE BELOW ----------------- \\
	
	// -----------------THE CODE TO RENABLE :) ----------------- \\
	
	/*public void playerTurn(String playerNo, View v, boolean passCondition, int passX, int passY, int upOrDown, int passImgId, boolean playerTurn, boolean attackConstraint, String opponentNo)
	{
		//----- THIS IS THE TEST CODE - DELETE IF NECCESSARY AND ENABLE THE CODE BELOW, AHA.
		// I can try write the sample code here. At least I will have all the variables ready to be used. :)
		
		// removeHighlights();;
		
		
		// The coordinates of the currently selected square.
		int x = passX, y = passY;
		// The coordinates of the parent (root) square of highlight squares.
		int rootX, rootY;
		
		// If there are no highlighted squares.
		sizeOfPrev = xPrevAxis.size();    // Initially, the size will be zero.
		// WORKS :) - It will check whether all the squares are highlighted, and assign the result of the operation into 'isHighlighted'
		isHighlighted = checkHighlights(x, y);
				
		if(sizeOfPrev <= 0)
		{
			// Maybe the code could go here...
			// ----	
			int noOfTimes = 0;
			// If we have not found adjacent enemies yet, we look again.
			if(arrayOfPrevCoordinatesX.size() <= 0)
			{	
				for(int row = 0; row < 8;row++)
				{
					for(int column =((row+1)%2);column<8;column+=2)
					{
						if(strCheckersBoard[row][column] == playerNo)
						{
							// Debug purposes.
							System.out.println("Does this section ever get executed?");
							
							if(playerNo == "1")
							{
								// Debug purposes.
								System.out.println("I crashed at row=" + row + "/column=" + column + " and the current player is player " + playerNo);
								highlightSquares(row >= 1 && row <=7, row, column, upOrDown, row >=2, opponentNo);
							}
							else if(playerNo == "2")
							{
								// Debug purposes.
								System.out.println("I crashed at row=" + row + "/column=" + column + " and the current player is player " + playerNo);
								highlightSquares(row >= 0 && row <= 6, row, column, upOrDown, row <=5, opponentNo);
							}
							
							
							noOfTimes++;
							// Debug purposes. - This does not even run... I guess that's why the code does not break any more... Lol
							System.out.println("Total number of pieces seen, is " + noOfTimes + " time(s).");
							
							if(xEnemyAxis.size() > 0)
							{
								erm++;
								// Add the ArrayLists to the master ArrayList... Aha.
								arrayOfPrevCoordinatesX.add(xPrevAxis);
								arrayOfPrevCoordinatesY.add(yPrevAxis);
								arrayOfEnemyCoordinatesX.add(xEnemyAxis);
								arrayOfEnemyCoordinatesY.add(yEnemyAxis);
								// We apply the highlights to the eligable squares...
								addHighlight(xPrevAxis, yPrevAxis);
								// Debug purposes.
								System.out.println("row=" + row + ", column=" + column + " is neighbouring an enemy.");
								// Then clear the standard ArrayLists and repeat.
								clearHelperArrays();
							}
							else
							{
								// Clear the standard ArrayLists.
								clearHelperArrays();
							}	
						}
					}
				}
			}
			// ...Let's see... Need a better condition.
			if(arrayOfPrevCoordinatesX.size() > 0)
			{
				// May need to add code here so, it re-highlights the squares, blah blah.
				// If there are neighbouring enemies already highlighted... We check if a move has been made on the 
				// isEnemyAdjacent = true;
				// Then we loop through until each move has been made.
				// We will get the destination squares of each piece.
				
				// So far, so good.
				System.out.println("Player "+ playerNo + " You must make an attack... Dawg. and erm is equal to: " + erm);
				
				for(int i = 0 ; i < arrayOfPrevCoordinatesX.size(); i++)
				{
					// Debug purposes.
					System.out.println("The size of the arrayOfPrevCoordinates is equal to " + arrayOfPrevCoordinatesX.size());
					
					//arrayOfPrevCoordinatesX.get(i);
					ArrayList<Integer> autoPrevX = arrayOfPrevCoordinatesX.get(i);
					ArrayList<Integer> autoPrevY = arrayOfPrevCoordinatesY.get(i);
					ArrayList<Integer> autoEnemyX = arrayOfEnemyCoordinatesX.get(i);
					ArrayList<Integer> autoEnemyY = arrayOfEnemyCoordinatesY.get(i);
					// Moves the checkers piece to the new location.
					// Continue from here on out. It captures the piece but, the highlights stay, and it also captures some undesired pieces. Needs work!
					// CONTINUE FROM AROUND HERE!
					movePiece(x, y, upOrDown, autoPrevX, autoPrevY, autoEnemyX, autoEnemyY, playerNo, opponentNo, passImgId);
					
					// We need a way to check if it is at the new location.
					
					if(x == xOfNewDest && y == yOfNewDest)
					{
						// Debug purposes.
						System.out.println("We have moved the piece to the new location (via the auto generated highlight.");
						// If we have successfully moved the piece...
						// Makes sure this is the last iteration.
						i = arrayOfPrevCoordinatesX.size();
						// Then we check whether the new location is neighbouring an enemy...
						highlightSquares(passCondition, xOfNewDest, yOfNewDest, upOrDown, attackConstraint, opponentNo);
						// If xEnemyAxis.size() returns 0 then, we can hand over the player's turn
						
						if(xEnemyAxis.size() <= 0)
						{
							// Debug purposes.
							System.out.println("There are no potential captures that this piece can make from the new location so, we handover our turn to the opponent.");
							
							// There are no new neighbouring enemies in the new location.
							// We hand over our turn to the opponent.
							//playerOneTurn = playerTurn; // This was cause part of the problem for problem 2. I need to find a better place to place this.
							// Make erm equal to 0 so, this statment does not run again.
							
							// We will remove the remaining highlights... i.e. if there were two different pieces each neighbouring an enemy.
							for(int rm = 0;rm<arrayOfPrevCoordinatesX.size();rm++)
							{
								// Gets rid of the rest of the highlights for the other pieces that had potential to capture enemy pieces.
								// It works the way I hoped it to.
								removeHighlights(arrayOfPrevCoordinatesX.get(rm), arrayOfPrevCoordinatesY.get(rm));
							}
							
							erm = 0;
							// clear 'arrayOfPrev... arrays
							arrayOfPrevCoordinatesX.clear();
							arrayOfPrevCoordinatesY.clear();
							arrayOfEnemyCoordinatesX.clear();
							arrayOfEnemyCoordinatesY.clear();
							// I should also try clearing the xOfNewDest and yOfNewDest values too.
						}
						else
						{
							// Debug purposes.
							System.out.println("There is another neighbouring enemy at the new location. So, we don't stop until a move is made.");
							// THIS PART IS FUCKED. WHEN I TRY TO GET CAPTURE ANOTHER PIECE CONSECUTIVELY, IT CHANGES THE COLOR OF MY PIECE TO THE OPPONENT'S COLOR. NEEDS FIXING!
							// OK, this section is fine. It can now do consecutive attacks. 
							// clear 'arrayOfPrev... arrays... I might not need to clear this.
							arrayOfPrevCoordinatesX.clear();
							arrayOfPrevCoordinatesY.clear();
							arrayOfEnemyCoordinatesX.clear();
							arrayOfEnemyCoordinatesY.clear();
							// Add the ArrayLists to the master ArrayList... Aha.
							arrayOfPrevCoordinatesX.add(xPrevAxis);
							arrayOfPrevCoordinatesY.add(yPrevAxis);
							arrayOfEnemyCoordinatesX.add(xEnemyAxis);
							arrayOfEnemyCoordinatesY.add(yEnemyAxis);
							// We apply the highlights to the eligable squares...
							addHighlight(xPrevAxis, yPrevAxis);
							// Clear the standard ArrayLists.
							clearHelperArrays();
							
							
						}				
					}else
					{
						// Debug purposes.
						System.out.println("We have not moved the piece to the new location. (from auto-generated highlights section)");
					}					
				}
				
			}
			else if(strCheckersBoard[x][y] == playerNo)
			{
				// I think I need 
				// Debug purposes.
				System.out.println("This piece was selected manually by the user as there were no prior neighbouring enemies.");
				// Add new highlights for the newly selected square.
				highlightSquares(passCondition, x, y, upOrDown, attackConstraint, opponentNo); // upOrDown = 1 so, x-1 i.e, go from bottom to the top of the checkersboard.
				// THIS IS A TEST
				
				// Adds the highlights to the squares based on the result from the 'prepareHighlight' method.
				addHighlight(xPrevAxis, yPrevAxis);
		
			}			
		}
		else
		{
			if(isHighlighted == false)
			{
				// Debug purposes.
				System.out.println("The square we selected is not part of the highlighted squares so, we will get rid of the old highlights, and add the highlights to the new square");
				// Gets rid of the highlights, and clears the helper ArrayLists such as the x/yPrevAxis and x/yEnemyAxis ArrayLists.
				removeHighlights(xPrevAxis, yPrevAxis);
				clearHelperArrays(); 
				// Now, if the newly selected square has checkers piece which belongs to playerX, then highlight it, and its neighbouring squares.
				// This prevents us from highlighting empty parent squares ;)
				if(strCheckersBoard[x][y] == playerNo)
				{
					// Debug purposes.
					System.out.println("If the selected square does contain the player's checkers piece, then we highlight it and its neighbours.");
					// Add new highlights for the newly selected square.
					highlightSquares(passCondition, x, y, upOrDown, attackConstraint, opponentNo); // upOrDown = 1 so, x-1 i.e, go from bottom to the top of the checkersboard.
					// Debug purposes.
					// THIS IS A TEST
					
					// Adds the highlights to the squares based on the result from the 'prepareHighlight' method.
					addHighlight(xPrevAxis, yPrevAxis);
					// Debug purposes.
					System.out.println("In isHighlighted==false The size of xPrevAxis = " + xPrevAxis.size() + " and yPrevAxis = " + yPrevAxis.size());
				}
			}
			else if(isHighlighted == true)
			{	
				// Let's say we click a checkers piece that is neighbouring an enemy piece. If we let the game automatically highlight the
				// ...Enemy pieces (by clicking any piece on the board ;)) then, the highlight will be locked until we make that particular capture, which is what we want.
				// ...But, if we manually select the piece that we want to use to perform the capture... It does not lock the highlight as I can still
				// ...select any other piece of mine. This is in fact, a glitch :) To overcome this, I can try setting erm to greater than 1 or something.
				// Debug purposes.
						
				System.out.println("The square selected is part of the highlighted squares so, we will make the move.");
				System.out.println("Also, the square is not an enemy square.");
				// Moves the checkers piece to the new location.
				movePiece(x, y, upOrDown, xPrevAxis, yPrevAxis, xEnemyAxis, yEnemyAxis, playerNo, opponentNo, passImgId);
				
				
				
				
				
				// Only if we have moved the piece to a new location, we make it so that it is the opponent's turn to make a move.
				if(x == xOfNewDest && y == yOfNewDest)
				{
					// The next turn will be player x ;)
					playerOneTurn = playerTurn;
				}
				// The next turn will be player x ;)
				//playerOneTurn = playerTurn;	
			}	
		}
		
		//------
		
		/*  //If the above f*** UP THE GAME. DELETE AND UNCOMMENT THE CODE BELOW :)
		int noOfTimes = 0;
		
		for(int row = 0; row < 8;row++)
		{
			for(int column = ((row+1)%2);column<8;column+=2)
			{
				if(strCheckersBoard[row][column] == playerNo)
				{
					// Gets rid of the highlights, and clears the helper ArrayLists such as the x/yPrevAxis and x/yEnemyAxis ArrayLists.
					//removeHighlights();
					//clearHelperArrays(); 
					// Creates an ArrayList that holds the coordinates for what square needs to be highlighted.
					// The problem seems to be coming from 'highlightSquares'
					//highlightSquares(passCondition, row, column, upOrDown, attackConstraint, opponentNo);
					// Add the ArrayLists to the master ArrayList... Aha.
					
					
					if(xEnemyAxis.size() > 0)
					{
						noOfTimes++;
						// Debug purposes.
						System.out.println("An enemy was encountered at " + noOfTimes + " different times.");
						// We apply the highlights to the eligable squares...
						//addHighlight(xPrevAxis, yPrevAxis);	
						// Add the ArrayLists to the master ArrayList... Aha.
						// I NEED TO ADD CODE!
						// Then clear the standard ArrayLists and repeat.
						//clearHelperArrays();
					}
					else
					{
						// Clear the standard ArrayLists.
						//clearHelperArrays();
					}	
				}
			}
		}
		
		// removeHighlights();
		
		
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
				highlightSquares(passCondition, x, y, upOrDown, attackConstraint, opponentNo); // upOrDown = 1 so, x-1 i.e, go from bottom to the top of the checkersboard.
				// THIS IS A TEST
				if(passCondition)
				{
					// Adds the highlights to the squares based on the result from the 'prepareHighlight' method.
					addHighlight(xPrevAxis, yPrevAxis);
				}				
			}			
		}
		else
		{
			if(isHighlighted == false)
			{
				// Gets rid of the highlights, and clears the helper ArrayLists such as the x/yPrevAxis and x/yEnemyAxis ArrayLists.
				removeHighlights();
				clearHelperArrays(); 
				// Now, if the newly selected square has checkers piece which belongs to playerX, then highlight it, and its neighbouring squares.
				// This prevents us from highlighting empty parent squares ;)
				if(strCheckersBoard[x][y] == playerNo)
				{
					// Add new highlights for the newly selected square.
					highlightSquares(passCondition, x, y, upOrDown, attackConstraint, opponentNo); // upOrDown = 1 so, x-1 i.e, go from bottom to the top of the checkersboard.
					// Debug purposes.
					// THIS IS A TEST
					if(passCondition)
					{
						// Adds the highlights to the squares based on the result from the 'prepareHighlight' method.
						addHighlight(xPrevAxis, yPrevAxis);
					}					
					System.out.println("In isHighlighted==false The size of xPrevAxis = " + xPrevAxis.size() + " and yPrevAxis = " + yPrevAxis.size());
				}
			}
			else if(isHighlighted == true)
			{
				// Moves the checkers piece to the new location.
				movePiece(v, x, y, upOrDown, xPrevAxis, yPrevAxis, playerNo, passImgId);
				// The next turn will be player x ;)
				playerOneTurn = playerTurn;
			}	
		}
	}*/
}