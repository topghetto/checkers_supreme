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
	public boolean isHighlighted;
	public int xParentPrev, yParentPrev;
	
	// Constructor
	public PlayerMoves(View[][] passSquares, ImageView[][] passImgSquares, String[][] passCheckersBoard)
	{
		strCheckersBoard = passCheckersBoard;
		squaresOfBoard = passSquares;
		imageOfSquares = passImgSquares;
		prevSquares = new ArrayList<View>();
		imgOfPrevSquares = null;
		sizeOfPrev = 0;
		xPrevAxis = new ArrayList<Integer>();
		yPrevAxis = new ArrayList<Integer>();
		
		
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
					
					// Used to hold the value of whether the squares are highlighted or not.
					isHighlighted = false;
					// And then check whether the neighbouring square(s) are empty...	AND THIS ENTIRE ELSE IF STATEMENT WORKS :)
					sizeOfPrev = xPrevAxis.size();    // xPrevAxis.size() causes the program to crash when I click a checkers piece!!! - SORTED ;)
					
					if(sizeOfPrev <= 0 && (strCheckersBoard[x][y] == "1" || strCheckersBoard[x][y] == "2"))
					{
						// First param variable is a condition
						// Last param variable 'upOrDown' = 1 so, [x+upOrDown] = [x+1] i.e, go from top to bottom of the checkersboard.
						highlightSquares(x >= 0 && x <= 6, x, y, 1);
						
						// If this section is enabled, then the app crashes when I select checkers pieces that have neighbouring checkers pieces
						//xParentPrev = xPrevAxis.get(0).intValue();
						//yParentPrev = yPrevAxis.get(0).intValue();
					}
					else // WE WILL ADD A CONDITION FOR "1" IF THIS WORKS... INSHALLAH! - The problem is here!
					{
						// ---
						// For all highlighted squares, check whether the selected square is part of the highlighted squares.
							for(int h = 0;h < sizeOfPrev;h++)
							{
								// Remove the highlights from the highlighted squares.
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
								
								System.out.println("I ran " + h + " times!");
							}
							
							if(isHighlighted == false)
							{
								// Works as expected :)
								removeHighlights(); 
								/*// If the square is not part of the highlighted squares, then remove the highlights of the existing square...
								for(int rm = 0;rm < sizeOfPrev;rm++)
								{
									// Remove the highlights from the highlighted squares.
									int xHighlighted = xPrevAxis.get(rm).intValue();
									int yHighlighted = yPrevAxis.get(rm).intValue();
									//squaresOfBoard[xHighlighted][yHighlighted].setBackground(null);
									prevSquares.get(rm).setBackground(null);
								}
								*/
								// Add new highlights for the newly selected square.
								highlightSquares(x >= 0 && x <= 6, x, y, 1); // upOrDown = 1 so, x+1 i.e, go from top to bottom of the checkersboard.
								// Debug purposes.
								System.out.println("In isHighlighted==false The size of xPrevAxis = " + xPrevAxis.size() + " and yPrevAxis = " + yPrevAxis.size());
								
							}
							else if(isHighlighted == true)
							{
								// The coordinates of the root square of highlighted squares
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
										if(strCheckersBoard[parentPrevX][parentPrevY] == "1")	// Determines what colour piece we move ;)
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
			
											for(int c = 0;c <8;c++)
											{
												for(int d=0;d<8;d++)
												{
													System.out.print(strCheckersBoard[c][d]);
												}
												System.out.println("");
											}
											
											System.out.println("|----------|");
											// Ends the for loop.
											//z = xPrevAxis.size();	
										}
									}
								}							
							}					
						// ---End of paste
					}
					
					
					
					
					/*
					isHighlighted = true;
					
					// We need to check if the selected square has a checkers piece...
					// I need a better condition because when I select a highlighted square, it will be 0 in the array and therefore, the following
					// statement will not run.
					//if(strCheckersBoard[x][y] == "1" && strCheckersBoard[x][y] == "2") // PROBLEM IS HERE!
					//{
						// And then check whether the neighbouring square(s) are empty...	AND THIS ENTIRE ELSE IF STATEMENT WORKS :)
						sizeOfPrev = xPrevAxis.size();    // xPrevAxis.size() causes the program to crash when I click a checkers piece!!! - SORTED ;)
						
						if(sizeOfPrev == 0)
						{
							// First param variable is a condition
							// Last param variable 'upOrDown' = 1 so, [x+upOrDown] = [x+1] i.e, go from top to bottom of the checkersboard.
							highlightSquares(x >= 0 && x <= 6, x, y, 1);	
						}
						else
						{
							
							
							// For all highlighted squares, check whether the selected square is part of the highlighted squares.
							for(int h = 0;h < sizeOfPrev;h++)
							{
								if(!prevSquares.get(h).equals(v))
								{
									// The selected square is not part of the highlighted squares.
									isHighlighted = false;
								}else
								{
									// The selected square is part of the highlighted squares.
									isHighlighted = true;
									// Close the loop early.
									//h = sizeOfPrev;
								}
							}
							
							if(isHighlighted == false)
							{
								// If the square is not part of the highlighted squares, then remove the highlights of the existing square...
								for(int rm = 0;rm < sizeOfPrev;rm++)
								{
									// Remove the highlights from the highlighted squares.
									int xHighlighted = xPrevAxis.get(rm).intValue();
									int yHighlighted = yPrevAxis.get(rm).intValue();
									squaresOfBoard[xHighlighted][yHighlighted].setBackground(null);
								}	
								// Add new highlights for the newly selected square.
								highlightSquares(x >= 0 && x <= 6, x, y, 1); // upOrDown = 1 so, x+1 i.e, go from top to bottom of the checkersboard.
								// Debug purposes.
								System.out.println("The size of xPrevAxis = " + xPrevAxis.size() + " and yPrevAxis = " + yPrevAxis.size());
							}
							else
							{
								// The coordinates of the root square of highlighted squares
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
										if(strCheckersBoard[parentPrevX][parentPrevY] == "1")	// Determines what colour piece we move ;)
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
											// Purpose of debug.
											System.out.println("|----------|");
			
											for(int c = 0;c <8;c++)
											{
												for(int d=0;d<8;d++)
												{
													System.out.print(strCheckersBoard[c][d]);
												}
												System.out.println("");
											}
											
											System.out.println("|----------|");
											// Ends the for loop.
											//z = xPrevAxis.size();	
										}
									}
								}
								
							}
							
										
						}*/
					//}
					
					
					/*
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
							
						}else{
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
								System.out.println("The size of xPrevAxis = " + xPrevAxis.size() + " and yPrevAxis = " + yPrevAxis.size());
												
							}else if(sizeOfPrev == 3 && !prevSquares.get(0).equals(v) && !prevSquares.get(1).equals(v) && !prevSquares.get(2).equals(v))
							{
								prevSquares.get(0).setBackground(null);
								prevSquares.get(1).setBackground(null);
								prevSquares.get(2).setBackground(null);
								
								// Condition = constraints and whatnot.
								// upOrDown = 1 so, [x+1] i.e, go from top to bottom of the checkersboard.
								highlightSquares(x >= 0 && x <= 6, x, y, 1);
								// Debug purposes.
								System.out.println("The size of xPrevAxis = " + xPrevAxis.size() + " and yPrevAxis = " + yPrevAxis.size());
								
								
								
							}
							
							// THIS SECTION DOES NOT WORK PROPERLY!!!! - NEEDS FIXING - I will come back to this.
							
							for(int z = 1;z < xPrevAxis.size();z++)
							{
								int prevX = xPrevAxis.get(z).intValue();
								int prevY = yPrevAxis.get(z).intValue();
								
								int parentPrevX = xPrevAxis.get(0).intValue();
								int parentPrevY = yPrevAxis.get(0).intValue();
								
								// If the new selected square is part of the highlighted neighbouring squares of squaresOfBoard[x][y] then...
								//
								System.out.println("The clicked square is x=" + x + " and y=" + y);
								System.out.println("The square of highlighted square x=" + parentPrevX + " and y="+parentPrevY);
								
								if(v.equals(squaresOfBoard[prevX][prevY]))
								{
									if(strCheckersBoard[parentPrevX][parentPrevY] == "1")
									{
										
									}else if(strCheckersBoard[parentPrevX][parentPrevY] == "2")
									{
										System.out.println("This did run :)");
										// Clear the square of the checker piece we want to move.
										strCheckersBoard[parentPrevX][parentPrevY] = "0";
										// Clear the image of that square.
										imageOfSquares[parentPrevX][parentPrevY].setImageResource(0);
										// Occupies new space in the helper array.
										strCheckersBoard[x][y] = "2";
										// Move the checkers piece into the new location.
										imageOfSquares[x][y].setImageResource(R.drawable.light_brown_piece);
										// Get rid of the highlights, etc.
										
										
										
										// Ends the for loop.
										//z = 3;	
									}
								}else
								{
									// Because this gets callled twice, the problem lies in the condition of the IF statement related to this else statement!
									System.out.println("This did not run :(");
								}
							}
							// THIS SECTION DOES NOT WORK PROPERLY!!!! - NEEDS FIXING 
						}
						
						
					}else{
						
						// Do nothing
					}*/
					
					
					
					
			}// if(squaresOfBoard[x][y].equals(v))	
		}		
	}	
	}// End of 'onClick'
	
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
				// Also highlights the neighbouring square to left of it.
				squaresOfBoard[x+upOrDown][y-1].setBackground(new ColorDrawable(0xFF999966));
				// Adds the neighbouring square to the prevSquares ArrayList ;)
				prevSquares.add(squaresOfBoard[x+upOrDown][y-1]);
				
				// Stores the coordinates for later use ;)
				xPrevAxis.add(new Integer(x+upOrDown));
				yPrevAxis.add(new Integer(y-1));
				
			}
			if(y >= 0 && y <= 6 && strCheckersBoard[x+upOrDown][y+1] == "0")
			{
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
				// Also highlights the neighbouring square to the right of it.
				squaresOfBoard[x+upOrDown][y+1].setBackground(new ColorDrawable(0xFF999966));
				// Adds the neighbouring square to the prevSquares ArrayList ;)
				prevSquares.add(squaresOfBoard[x+upOrDown][y+1]);
				
				
				// Stores the coordinates for later use ;)
				xPrevAxis.add(new Integer(x+upOrDown));
				yPrevAxis.add(new Integer(y+1));
			}
			
			// Debug purposes - It works in the way I hoped it would :)
			for(int a = 0;a<xPrevAxis.size();a++)
			{
				System.out.print("xPrevAxis.get(" + a + ").intValue() = " + xPrevAxis.get(a).intValue() + " and ");
				System.out.println("yPrevAxis.get(" + a + ").intValue() = " + yPrevAxis.get(a).intValue());
			}
			
			// I will need to add an else if statement to each of the previous if statements so, if the neighbouring square is an enemy piece,
			// I must also check if the enemy square's neighbour is an empty square, in other words, this will take the opponent's piece.	
			// Debug purposes.
			System.out.println("Printed from the if(prevSquares.size() == " + sizeOfPrev + " -- The size of prevSquares is now " + prevSquares.size());
			
		}
		
		// NEEDS CONFIRMATION - I cannot confirm whether this code works because I have not implemented the code for the pieces to move.
		/*if(x >= 0 && x <= 5) // I NEED A MUCH BETTER CONDITION. I'M DONE, I CBA ANY MORE. I'LL CONTINUE TOMORROW.
		{
			// If the neighbouring piece is an enemy piece, do the following...
			// I would need to change this later so, that the other part (operand) of the && operator is a param variable ;)
			
			// Only highlight the squares if the neighbbouring squares are vacant. "1" is the opposing piece.
			if(y >= 1 && strCheckersBoard[x+upOrDown][y-1] == "1")
			{
				// If neighbouring square is an enemy piece, then...
				if(y >= 2 && strCheckersBoard[x+(upOrDown+1)][y-2] == "0")
				{
					// if it is an enemy piece, check that the enemy piece's neighbouring square is an empty, if so...
					// Remove possible duplicate.
					prevSquares.remove(squaresOfBoard[x][y]);
					// Highlights the selected square.
					squaresOfBoard[x][y].setBackground(new ColorDrawable(0xFF999966));
					// Add the selected square to the prevSquares ArrayList
					prevSquares.add(squaresOfBoard[x][y]);
					// Also highlights the neighbouring square of the enemy square to the right of it.
					squaresOfBoard[x+(upOrDown+1)][y-2].setBackground(new ColorDrawable(0xFF999966));
					// Adds the neighbouring square of the enemy square to the prevSquares ArrayList ;)
					prevSquares.add(squaresOfBoard[x+upOrDown][y-1]);
				}
			}else if((y >= 1 && strCheckersBoard[x+upOrDown][y+1] == "1"))
			{
				// If neighbouring square is an enemy piece, then...
				if(y >= 2 && strCheckersBoard[x+(upOrDown+1)][y+2] == "0")
				{
					// if it is an enemy piece, check that the enemy piece's neighbouring square is an empty, if so...
					// Remove possible duplicate.
					prevSquares.remove(squaresOfBoard[x][y]);
					// Highlights the selected square.
					squaresOfBoard[x][y].setBackground(new ColorDrawable(0xFF999966));
					// Add the selected square to the prevSquares ArrayList
					prevSquares.add(squaresOfBoard[x][y]);
					// Also highlights the neighbouring square of the enemy square to the right of it.
					squaresOfBoard[x+(upOrDown+1)][y+2].setBackground(new ColorDrawable(0xFF999966));
					// Adds the neighbouring square of the enemy square to the prevSquares ArrayList ;)
					prevSquares.add(squaresOfBoard[x+upOrDown][y+2]);
				}	
			}
			
		}*/// NEEDS CONFIRMATION! -------- ///
	}
	public void removeHighlights()
	{
			// If the square is not part of the highlighted squares, then remove the highlights of the existing square...
			for(int rm = 0;rm < sizeOfPrev;rm++)
			{
				// Remove the highlights from the highlighted squares.
				int xHighlighted = xPrevAxis.get(rm).intValue();
				int yHighlighted = yPrevAxis.get(rm).intValue();
				//squaresOfBoard[xHighlighted][yHighlighted].setBackground(null);
				prevSquares.get(rm).setBackground(null);
			}			
	}
}