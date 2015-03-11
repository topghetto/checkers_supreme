package uk.ac.kcl.www;

import java.util.ArrayList;

import android.util.Log;
import android.app.Activity;
import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;

import android.widget.Button;
import android.widget.TextView;
import android.view.View;

// These are all the GridView dependencies

import android.widget.GridLayout;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.view.Gravity;
import android.view.LayoutInflater;

import 	android.graphics.drawable.ColorDrawable;

public class MultiplayerEvents implements View.OnClickListener
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
	
	public boolean isHighlighted, playerOneTurn, isEnemyAdjacent, isNewKing;
	
	public int highlightParentX, highlightParentY, xOfNewDest, yOfNewDest, erm;
	
	// Constructor
	public MultiplayerEvents(View[][] passSquares, ImageView[][] passImgSquares, String[][] passCheckersBoard, TextView passTextView)
	{
		// The first turn goes to player two.
		// After each turn a player makes, a boolean variable will determine when it is the others players turn.
		// e.g. playerOne = true, playerTwo = false. Then after player one's turn, the variable will
		// become playerOne = false, and playerTwo = true, and this will keep switching back-and-forth. I just realised,
		// the computer will never ever be able to click this, lol.
		playerOneTurn = true;
		isNewKing = false;
		
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
		playerInfo.setText("Player " + 1 + "'s \n Turn.");
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
						// --------
						if(playerOneTurn == true)
						{
							// We move our pieces as normal.
							playerTurn("1", v, x >= 1 && x <= 7, x, y, -1, R.drawable.dark_brown_piece, R.drawable.king_dark_brown_piece, false, x >= 2, "2", noOfPiecesPlayerTwo);		// Nice, it works.
						}
						else
						{
							// We move our pieces as normal.
							playerTurn("2", v, x >= 0 && x <= 6, x, y, 1, R.drawable.light_brown_piece, R.drawable.king_light_brown_piece, true, x <= 5, "1", noOfPiecesPlayerOne);	// Nice, it works.		
						}
				}// if(squaresOfBoard[x][y].equals(v))
		}		
	}	
	}// End of 'onClick'
	public void addCoordinatesToLists(int passX, int passY, int upOrDown, int leftOrRight)
	{
				// Store in the variables passed in.
				int x = passX, y = passY;
				
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
	public void prepareHighlight(boolean firstCondition, boolean secondCondition, int passX, int passY, int upOrDown, int leftOrRight)
	{
		// This a method that will handle any number of neighbouring squares that need to be highlighted, or
		// only highlights enemy square, should it be the case.
		
		int x = passX;
		int y = passY;
		
		// If neighbouring square contains an enemy piece...
		if(firstCondition && strCheckersBoard[x+(upOrDown+upOrDown)][y+(leftOrRight+leftOrRight)] == "0") 
		{	
			// And there were no neighbouring enemy pieces prior to this one, then...
			if(xEnemyAxis.size() == 0)
			{
				// Clear the x/yPrevAxis ArrayList i.e. gets rid of the previous highlights.
				xPrevAxis = new ArrayList<Integer>();
				yPrevAxis = new ArrayList<Integer>();
				// Experiment - Adds the coordinates to the correct ArrayLists.
				addCoordinatesToLists(x, y, upOrDown, leftOrRight);
			}
			else
			{
				// If there was already a neighbouring enemy piece prior to this one, then...
				// Remove duplicate parent root.
				xPrevAxis.remove(new Integer(x));
				yPrevAxis.remove(new Integer(y));
				// Experiment - DELELTE IF NECESSARY AND ENABLE CODE ABOVE!!
				addCoordinatesToLists(x, y, upOrDown, leftOrRight);
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
		
		// Dynamically creates the correct string that will hold the value K1 or K2, depending on the player number.
		String strKing = "K" + playerNo;
		// If the selected piece is a king...
		if(strCheckersBoard[x][y].contains(strKing))
		{
			// Debug purposes.
			// System.out.println("Well, there is indeed a king!");
			// If the piece selected is a king piece...
			if(x >= 1 && x <= 7)
			{
				// When adding highlights to pieces above it...
				// Debug purposes.
				// System.out.println("if(x >= 1 && x <= 7) and strKing =" + strKing);
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
				// When adding highlights to pieces beneath it...
				// Debug purposes.
				// System.out.println("if(x >= 0 && x <= 6) and strKing =" + strKing);
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
			// If it is just a standard piece then...
			// Debug purposes.
			// System.out.println("else if(strCheckersBoard[x][y] == playerNo && passCondition) AND strCheckersBoard[" + x + "][" + y + "] = " + strCheckersBoard[x][y]);
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
	}
	//public void addHighlight(int passX, int passY, int upOrDown, int leftOrRight)
	public void addHighlight(ArrayList<Integer> passCoordinatesX, ArrayList<Integer> passCoordinatesY)
	{
		// Dynamicallly adds the visual highlights to the correct squares.
		
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
			// Debug purposes.
			// System.out.println("The coordinates of the square(s) that we wish to remove the highlights from, is row/column is row=" + xHighlighted + "and column=" + yHighlighted);
		}
	}
	public void clearHelperArrays()
	{
		// If the square is not part of the highlighted squares, then remove the highlights of the existing square...

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
				if(x == xHighlighted && y == yHighlighted) // This is safer to use, as in terms of an IndexOutOfBoundException, and less expensive.
				{
					// The selected square is part of the highlighted squares.
					isHighlighted = true;
					// Debug purposes.
					// System.out.println("It is highlighted and xHighlighted=" + xHighlighted + " and yHighlighted=" + yHighlighted);
					// Close the loop as soon as we speak the selected square (if) it is part of highlighted squares. 
					h = sizeOfPrev;
					
				}else
				{
					// Closing the loop early fixed the problem because if x = 0 true, then x =1 is false, then isHighlighted == false will run.
					// All we need is at least one x to be false in order for x1 && x2 && x3 to be false so, any other checks would cause harm.
					// The selected square is not part of the highlighted squares.
					isHighlighted = false;
					// Debug purposes.
					// System.out.println("It is not highlighted and xHighlighted=" + xHighlighted + " and yHighlighted=" + yHighlighted);
				}
			}
		}else
		{
			// Since there are no highlighted squares to even check, we just say it is false.
			isHighlighted = false;
			// Debug purposes.
			// System.out.println("The size of highlight squares equals " + sizeOfPrev);
		}
		
		// This will later be assigned to the global variable 'isHighlighted'
		return isHighlighted;		
	}
	public void movePiece(int passX, int passY, int upOrDown, ArrayList<Integer> passListOfRows, ArrayList<Integer> passListOfColumns,
												ArrayList<Integer> passEnemyX, ArrayList<Integer> passEnemyY, String strDest, String opponentNo, int destImg,
												int passImgOfKing)
	{
		// Clear the old values.
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
			// x == prevX && y == prevY would be a better/safer condition.
			// if(squaresOfBoard[x][y].equals(squaresOfBoard[prevX][prevY]) && strCheckersBoard[x][y] != opponentNo)
			if((x == prevX && y == prevY) && strCheckersBoard[x][y] != opponentNo)
			{
				// Close the loop after this iteration.
				mv = sizeOfPrev;
				// Debug purposes.
				// System.out.println("We successfully moved the piece :)");
				// Temporary variable that will hold the value at the old location (i.e. the piece we wish to move).
				strSource = strCheckersBoard[parentPrevX][parentPrevY];
	
				// If the parent square has a neighbouring enemy piece, then we determine which enemy piece it is.
				if(passEnemyX.size() > 0)
				{
					// Assuming that the parent highlighted sqaure was neighbouring more than one enemy piece, we would need to find out the right enemy...
					// Piece to get rid of.
					for(int e = 0; e < passEnemyX.size(); e++)
					{
						int enemyCoordinateX = passEnemyX.get(e).intValue();
						int enemyCoordinateY = passEnemyY.get(e).intValue();
						
						int checkBelow = parentPrevX + 1;
						int checkAbove = parentPrevX - 1;

						// We check the square below...
						if(checkBelow == enemyCoordinateX && (prevY+1) == enemyCoordinateY)
						{
							// The enemy square is here. This works. Nice.
							// Debug purposes.
							// System.out.println("These are the correct coordinates of the enemy square checkBelow=" + checkBelow + " and checkY=" + (prevY+1));
							// We clear the space i.e. take the piece.
							strCheckersBoard[enemyCoordinateX][enemyCoordinateY] = "0";
							// We clear the space (visually) i.e. take the piece
							imageOfSquares[enemyCoordinateX][enemyCoordinateY].setImageResource(0);
							// Makes this the last 'e' iteration.
							e = passEnemyX.size();
							// Will decrease the number of pieces the opponent has by 1.
							if(strDest.contains("1")){--noOfPiecesPlayerTwo;}else if(strDest.contains("2")){--noOfPiecesPlayerOne;}
							
						}
						else if(checkBelow == enemyCoordinateX && (prevY-1) == enemyCoordinateY)
						{
							// Check the square below...
							// The enemy square is here. This works. Nice.
							// Debug purposes.
							// System.out.println("These are the correct coordinates of the enemy square checkBelow=" + checkBelow + (prevY+1));
							// We clear the space i.e. take the piece.
							strCheckersBoard[enemyCoordinateX][enemyCoordinateY] = "0";
							// We clear the space (visually) i.e. take the piece
							imageOfSquares[enemyCoordinateX][enemyCoordinateY].setImageResource(0);
							// Makes this the last 'e' iteration.
							e = passEnemyX.size();
							// Will decrease the number of pieces the opponent has by 1.
							if(strDest.contains("1")){--noOfPiecesPlayerTwo;}else if(strDest.contains("2")){--noOfPiecesPlayerOne;}
						}
						else if(checkAbove == enemyCoordinateX && (prevY+1) == enemyCoordinateY)
						{
							// Check the square above...
							// The enemy square is here. This works. Nice.
							// Debug purposes.
							// System.out.println("These are the correct coordinates of the enemy square checkAbove=" + checkAbove + " and checkY=" + (prevY+1));
							// We clear the space i.e. take the piece.
							strCheckersBoard[enemyCoordinateX][enemyCoordinateY] = "0";
							// We clear the space (visually) i.e. take the piece
							imageOfSquares[enemyCoordinateX][enemyCoordinateY].setImageResource(0);
							// Makes this the last 'e' iteration.
							e = passEnemyX.size();
							// Will decrease the number of pieces the opponent has by 1.
							if(strDest.contains("1")){--noOfPiecesPlayerTwo;}else if(strDest.contains("2")){--noOfPiecesPlayerOne;}
						}
						else if(checkAbove == enemyCoordinateX && (prevY-1) == enemyCoordinateY)
						{
							// Check the square above...
							// The enemy square is here. This works. Nice.
							// Debug purposes.
							// System.out.println("These are the correct coordinates of the enemy square checkAbove=" + checkAbove + (prevY+1));
							// We clear the space i.e. take the piece.
							strCheckersBoard[enemyCoordinateX][enemyCoordinateY] = "0";
							// We clear the space (visually) i.e. take the piece
							imageOfSquares[enemyCoordinateX][enemyCoordinateY].setImageResource(0);
							// Makes this the last 'e' iteration.
							e = passEnemyX.size();
							// Will decrease the number of pieces the opponent has by 1.
							if(strDest.contains("1")){--noOfPiecesPlayerTwo;}else if(strDest.contains("2")){--noOfPiecesPlayerOne;}
						}					
					}
				}
				
				// If a piece of player one reaches the last row, transform the piece into a king...
				
				// Creates the king string corresponding to the player number.
				String strKing = "K" + strDest;
				
				if(x == 0 && strCheckersBoard[parentPrevX][parentPrevY] == "1")
				{
					// Debug purposes.
					// System.out.println("strSource does equal player one and let's output strKing=" + strKing);
					// Now, the piece will now become a king at the new location.
					strCheckersBoard[parentPrevX][parentPrevY] = strKing;
					// This stops a new king from making a consecutive attack upon transformation.
					isNewKing = true;
					
				}else if(x == 7 && strCheckersBoard[parentPrevX][parentPrevY] == "2")
				{
					// Debug purposes.
					// System.out.println("strSource does equal player two and let's output strKing=" + strKing);
					// If a piece of player two reaches the last row, transform the piece into a king.
					// Now, the piece will now become a king at the new location.
					strCheckersBoard[parentPrevX][parentPrevY] = strKing;
					// This stops a new king from making a consecutive attack upon transformation.
					isNewKing = true;
				}
				else
				{
						// This stops a new king from making a consecutive attack upon transformation.
						isNewKing = false;
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
				
				// Store the new location in our global variables to work with later on.
				xOfNewDest = x;
				yOfNewDest = y;
				// Clear the row/column values of the highlighted squares.
				clearHelperArrays();
				// Remove the highlights.
				removeHighlights(passListOfRows, passListOfColumns);
				
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
				
			}else
			{
				// Debug purposes.
				// System.out.println("We do nothing because an opposing piece was clicked or it was an invalid move.");
			}
		}							
	}
	public void playerTurn(String playerNo, View v, boolean passCondition, int passX, int passY, int upOrDown, int passImgId, int passImgOfKing, boolean playerTurn, boolean attackConstraint, String opponentNo, int passNoOfPieces)
	{	
		// The coordinates of the currently selected square.
		int x = passX, y = passY;
		// The coordinates of the parent (root) square of highlight squares.
		int rootX, rootY;
		// Holds the value on how many times the program it saw a piece of 'playerNo'
		int noOfTimes = 0;
				
		
		/*if(isGameOver == true)
		{
				
		}*/
		
		//if(true)
		// This works as a better condition because if(true) might as well not be a condition at all.
		// Since, true will always be true. Now, consecutive attacks work with our new condition.
		if(arrayOfPrevCoordinatesX.size() <= 0)
		{	
			// Debug purposes.
			// System.out.println("The initial statement that checks for adjacent enemies has just started.");
			// Prepares the correct string for playerX.
			String strKing = "K" + playerNo;
			
			for(int row = 0; row < 8;row++)
			{
				for(int column =((row+1)%2);column<8;column+=2)
				{
					// Checks whether there are any pieces neighbouring any enemy pieces.
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
							// Add the ArrayLists to the master ArrayList... Aha.
							// THIS IS A TEST - This should add the ArrayLists to the master ArrayLists
							addToMasterLists(xPrevAxis, yPrevAxis, xEnemyAxis, yEnemyAxis);
							
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
					// System.out.println("After checking through arrayOfPrevCoordinates, the square is part of the highlighted squares.");
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
			// System.out.println("if(isEnemyAdjacent == true) just ran so, a capture needs to be performed.");
			// This will determine what type of move (standard or a capture) it should make, and also make the move.
			performMoveAndCheckAdjacent(passX, passY, attackConstraint, passCondition, upOrDown, playerNo, opponentNo, passImgId, passImgOfKing, playerTurn, passNoOfPieces);
			
		}else
		{
			//...Otherwise, we check if the selected square corresponds to the player's number and if it is part of the highlighted squares.
			if(strCheckersBoard[x][y].contains(playerNo) && isHighlighted == false)
			{
				// Debug purposes.
				// System.out.println("the if(strCheckersBoard[x][y] == playerNo && isHighlighted == false) statement has just been run.");
				// Debug purposes.
				// System.out.println("The square we selected is not part of the highlighted squares so, we will get rid of the old highlights");
				
				for(int rm = 0;rm < arrayOfPrevCoordinatesX.size();rm++)
				{
					// Gets rid of the highlights, and clears the helper ArrayLists such as the x/yPrevAxis and x/yEnemyAxis ArrayLists.
					removeHighlights(arrayOfPrevCoordinatesX.get(rm), arrayOfPrevCoordinatesY.get(rm));
				}
				
				// Clear the standard ArrayLists.
				clearHelperArrays(); 
				// Now, if the newly selected square has checkers piece which belongs to playerX, then highlight it, and its neighbouring squares.
				// I forgot to clear the master ArrayLists too... Which, I will do now.
				// THIS IS A TEST - This should clear the master ArrayLists - ENABLE ABOVE CODE IF THIS DOESN'T WORK.
				clearMasterLists();
				
				// Debug purposes.
				// System.out.println("and the selected square does contain the player's checkers piece, then we highlight it and its neighbours.");
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
				// System.out.println("The size of arrayOfPrevCoordinatesX.size()" + arrayOfPrevCoordinatesX.size() + "--- from if(strCheckersBoard[x][y] == playerNo && isHighlighted == false)");
			}
			else if(strCheckersBoard[x][y] == "0" && isHighlighted == true)
			{	
				// Or if the selected square is part of the highlighted squares, and the square is empty, we will see if we can make a move...
				// Debug purposes.
				// System.out.println("else if(strCheckersBoard[x][y] == 0 && isHighlighted == true) just ran but, failed miserably.");
				// This will determine what type of move (standard or a capture) it should make, and also make the move.
				performMoveAndCheckAdjacent(passX, passY, attackConstraint, passCondition, upOrDown, playerNo, opponentNo, passImgId, passImgOfKing, playerTurn, passNoOfPieces);
			}
		}	
	}
	public void addToMasterLists(ArrayList<Integer> passPrevX, ArrayList<Integer> passPrevY, ArrayList<Integer> passEnemyX, ArrayList<Integer> passEnemyY)
	{
				// We apply the highligh// Add the ArrayLists to the master ArrayList... Aha.
				arrayOfPrevCoordinatesX.add(passPrevX);
				arrayOfPrevCoordinatesY.add(passPrevY);
				arrayOfEnemyCoordinatesX.add(passEnemyX);
				arrayOfEnemyCoordinatesY.add(passEnemyY);
	}			
	public void clearMasterLists()
	{
				// This will clear the ArrayLists that holds the ArrayLists :)
				arrayOfPrevCoordinatesX.clear();
				arrayOfPrevCoordinatesY.clear();
				arrayOfEnemyCoordinatesX.clear();
				arrayOfEnemyCoordinatesY.clear();
	}
	public void displayTurn(boolean playerOneTurn, String opponentNo)
	{
		// This needs major adjustments! I should sort this out, and look into the first few lines of the newly added code for the nest for-loop
		// at the very top!.
		// This method will display a TextView that states whether it is the opponent's turn or the winner of the game.
		// The reason why this did not work as intended because the statement originally was == true, which would be partially correct but,
		// this method is called after I change the player's turn so, it ends up calling the other if statement... Lol. I will make this better
		// by putting it back to true, and call this method before I change the value of 'playerOneTurn' to the opponent's turn :)
		
		if(playerOneTurn == false)
		{
			if(noOfPiecesPlayerTwo <= 0)
			{
				// If the opponent has no more pieces then player 1 is the winner.
				playerInfo.setText("Game Over!\nPlayer 1 is\n the Winner!");
				
			}
			else
			{
				// Display the player's turn.
				playerInfo.setText("Player " + opponentNo + "'s Turn");
				// Debug.
				System.out.println("The no of pieces left for Player 2 is " + noOfPiecesPlayerTwo);
			}				
		}
		else
		{
		  // If it is player 2's turn...
		  if(noOfPiecesPlayerOne <= 0)
			{
				// If the opponent has no more pieces then player 1 is the winner.
				playerInfo.setText("Game Over!\nPlayer 2 is\n the Winner!");	
			}
			else
			{
				// Display the player's turn.
				playerInfo.setText("Player " + opponentNo + "'s Turn");
				// Debug.
				System.out.println("The no of pieces left for Player 1 is " + noOfPiecesPlayerOne);
			}							
		}
	}
	public void performMoveAndCheckAdjacent(int passX, int passY, boolean attackConstraint, boolean passCondition, int upOrDown, String playerNo, String opponentNo, int passImgId, int passImgOfKing, boolean playerTurn, int passNoOfPieces)
	{
		// Debug purposes.
		// System.out.println("arrayOfPrevCoordinatesX.size() > 0 if statement just ran. (using our new function)");
		int x = passX, y = passY;

		for(int i = 0 ; i < arrayOfPrevCoordinatesX.size(); i++)
		{
			// Debug purposes.
			// System.out.println("The size of the arrayOfPrevCoordinates is equal to " + arrayOfPrevCoordinatesX.size());
			
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
				// If we have successfully moved the piece...
				
				// Debug purposes.
				// System.out.println("We have moved the piece to the new location (via the auto generated highlight.");
				
				// Makes sure this is the last iteration as we have found which piece made the recent move.
				i = arrayOfPrevCoordinatesX.size();
					
				// If a capture was made we check if there are neighbouring enemies at our new location...
				if(arrayOfEnemyCoordinatesX.size() > 0)
				{
					// I need to check if this piece has just been turned into a king... THIS NEEDS TO WORK SO, RESUME WHEN I COME BACK! 
					// in the movePiece method there are 3 areas in the code where 'isNewKing' gets mutated so, remember to keep an eye out on those.
					if(isNewKing == true)
					{
						// Debug purposes.
						System.out.println("Hello, this is a new king so, no consecutive attacks for you.");
						// This displays the message but, it still highlights a neighbouring enemy after we turn into a king :/
					}
					else
					{
						// We can use this method to later check whether the piece at the new location is neighbouring an enemy.
						highlightSquares(passCondition, xOfNewDest, yOfNewDest, upOrDown, attackConstraint, opponentNo, playerNo);
						// Debug purposes.
						System.out.println("Hello, no new king here so, yaaaah.");
					}
					
					// If the size of xEnemyAxis.size() == 0, then we can say no new enemies have been found using the 'highlightSquares()' method above.
					if(xEnemyAxis.size() <= 0)
					{
						// Debug purposes.
						// System.out.println("There are no potential captures that this piece can make from the new location so, we handover our turn to the opponent.");
						// We will remove the remaining highlights... i.e. if there were two different pieces each neighbouring an enemy.
						for(int rm = 0;rm<arrayOfPrevCoordinatesX.size();rm++)
						{
							// Gets rid of the rest of the highlights for the other pieces that had potential to capture enemy pieces.
							// It works the way I hoped it to.
							removeHighlights(arrayOfPrevCoordinatesX.get(rm), arrayOfPrevCoordinatesY.get(rm));
						}
						
						// THIS IS A TEST - This should clear the master ArrayLists - ENABLE ABOVE CODE IF THIS DOESN'T WORK.
						clearMasterLists();	
						// I have just realised that I have not cleared xPrevAxis and yPrevAis ArrayList so, I'll do that now.
						clearHelperArrays();
						// playerInfo.setText("Player " + opponentNo + "'s Turn") or game over!;
						displayTurn(playerTurn, opponentNo);
						// Handover the turn to opponent.
						playerOneTurn = playerTurn;
						// Since there are no adjacent enemies, we make it false.
						isEnemyAdjacent = false;
						// Display the player's turn.
					}
					else
					{
						// There is another neighbouring enemy at our new location so, next time this entire method is called again, thanks...
						// ...To the isEnemyAdjacent == true, if statement.
						// Debug purposes.
						// System.out.println("There is another neighbouring enemy at the new location. So, we don't stop until a move is made.");
						
						// Hopefully, this should sort out problem 3... Yup, it sorted problem 3 :)
						for(int rm = 0;rm<arrayOfPrevCoordinatesX.size();rm++)
						{
							// Gets rid of the rest of the highlights for the other pieces that had potential to capture enemy pieces.
							// It works the way I hoped it to.
							removeHighlights(arrayOfPrevCoordinatesX.get(rm), arrayOfPrevCoordinatesY.get(rm));
						}
						//...Well, hopefully.

						// This should clear the master ArrayLists
						clearMasterLists();
						// This should add the ArrayLists to the master ArrayLists 
						addToMasterLists(xPrevAxis, yPrevAxis, xEnemyAxis, yEnemyAxis);	
						// We apply the highlights to the eligable squares...
						addHighlight(arrayOfPrevCoordinatesX.get(0), arrayOfPrevCoordinatesY.get(0));
						// Clear the standard ArrayLists.
						clearHelperArrays();
						// Since there are adjacent enemies, we make it true. That way, if(isEnemyAdjacent == true) will call this entire if statement again.
						isEnemyAdjacent = true;
					}
					
				}else
				{
					// Just a standard move so, we  will clear all the ArrayLists and handover our turn to the opponent.	
					// Debug purposes.
					System.out.println("This is just a standard move.");	
					// THIS IS A TEST - This should clear the master ArrayLists - ENABLE ABOVE CODE IF THIS DOESN'T WORK.
					clearMasterLists();	
					// Clear the standard ArrayLists.
					clearHelperArrays();
					// playerInfo.setText("Player " + opponentNo + "'s Turn") or game over!;
					displayTurn(playerTurn, opponentNo);
					// Handover the turn to opponent.
					playerOneTurn = playerTurn;
					// Erm, I am so lost.
					isEnemyAdjacent = false;
					// I think I would need to remove the highlights too.
					// Display the player's turn.			
				}						
			}else
			{
				// Debug purposes.
				System.out.println("We have not moved the piece to the new location. (from auto-generated highlights section) so, we do nothing until then.");
			}					
		}
	}	
}