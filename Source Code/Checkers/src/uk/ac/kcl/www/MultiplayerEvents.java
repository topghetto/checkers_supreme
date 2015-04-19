package uk.ac.kcl.www;

import android.os.CountDownTimer;
import android.content.Context;
import android.os.Bundle;

import android.app.Activity;
import android.app.Activity;
import android.app.ActionBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import java.lang.Thread;
import java.lang.Runnable;

import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;

import android.widget.Button;
import android.widget.TextView;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import 	android.graphics.drawable.ColorDrawable;

public class MultiplayerEvents extends Activity implements View.OnClickListener
{
	// Member Variables
	public int row, column;
	public View[][] squaresOfBoard;
	public ImageView[][] imageOfSquares;
	public String[][] strCheckersBoard;
	// Player information.
	public TextView playerInfo;
	// Image of the player's piece.
	public ImageView playerImage;
	// Information about the Bot.
	public TextView loadingInfo;
	// Loading wheel for AI.
	public ProgressBar loadingWheel;
	
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
	
	// The Tree of possible states that the AI can go to.
	public Tree<String[][]> decisionTree;
	// Keeps track of the number of nodes in the tree
	public int sizeOfTree;
	// An experiment to return the move we should take.
	public Tree<String[][]> greatestMove;
	// Boolean helper variables... Which does what it says on the tin.	
	public boolean isHighlighted, playerOneTurn, isEnemyAdjacent, isNewKing, adjacentToEnemy;
	
	// Keeps track of the new location of the recently moved piece.
	public int xOfNewDest, yOfNewDest;
	
	// Constructor
	public MultiplayerEvents(View[][] passSquares, ImageView[][] passImgSquares, String[][] passCheckersBoard, TextView passTextView, TextView passLoadingInfo, ProgressBar passLoadingWheel, ImageView passPlayerImage)
	{
		// Player one will initially go first...
		playerOneTurn = true;
		isNewKing = false;
		adjacentToEnemy = false;
		
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
		
		// Helper ArrayLists that will hold the ArrayLists of coordinates.
		isEnemyAdjacent = false;
		arrayOfPrevCoordinatesX = new ArrayList<ArrayList<Integer>>();
		arrayOfPrevCoordinatesY = new ArrayList<ArrayList<Integer>>();
		arrayOfEnemyCoordinatesX = new ArrayList<ArrayList<Integer>>();
		arrayOfEnemyCoordinatesY = new ArrayList<ArrayList<Integer>>();
		
		// Initialise.
		playerInfo = passTextView;
		// Initialise the TextView for the display additional information with the loading wheel.
    loadingInfo = passLoadingInfo;
		// Initialise the ImageView
		playerImage = passPlayerImage;
		// Initialise the ProgressBar.		
		loadingWheel = passLoadingWheel;
		// We will hide the wheel on startup.
		
		// Display the player's turn. REMEMBER TO CHANGE THIS PARTICULAR SECTION WHEN I AUTOMATICALLY MAKE THE CODE DECIDE WHO GOES FIRST!!!
		playerInfo.setText("Player " + 1 + "'s Turn");
		// Set the image of the player image.
		playerImage.setImageResource(R.drawable.dark_brown_piece);
		// Loading information.
		loadingInfo.setText("Multiplayer Mode");
		
		// Keeps track of the number of pieces and...
		// ...Initially and dynamically determines the number of pieces for each player...
		updateNoOfPieces(strCheckersBoard);
	}
	public void printCheckersBoard(String[][] passCheckersBoard)
	{
		// Debug Purposes - This will print out a text representation of the checkers board. 
	
		System.out.println("|----------|");
		for(int c = 0;c <8;c++)
		{
			for(int d=0;d<8;d++)
			{
				System.out.print(passCheckersBoard[c][d]);
			}
			System.out.println("");
		}
		System.out.println("|----------|");		
	}
	public void checkPlayerAndAdd(String[][] passStrCheckersBoard, int row, int column, String opponentNo, String playerNo)
	{
		if(playerNo == "1")
		{
			// Create the helper ArrayLists for player 1.
			highlightSquares(passStrCheckersBoard, row, column, opponentNo, playerNo);
		}
		else if(playerNo == "2")
		{
			// Create the helper ArrayLists for player 2.
			highlightSquares(passStrCheckersBoard, row, column, opponentNo, playerNo);
		}		
	}
	public void duplicateArray(String[][] source, String[][] destination)
	{
		// When simply copying an array by re-assigning it to a new copy (theorectically, it would copy the contents) but, really it...
		// only copies the location of the array in memory so, if we modified array B, it would also modifiy array A. This method tackles this problem.
		
		for(int a = 0; a < 8;a++)
		{
			for(int b = 0; b < 8; b++)
			{
				// An actual copy. Okay, this did job. I will write a method.
				destination[a][b] = source[a][b];
			}
		}
	}
	// I declared this method as synchronised hoping the code runs one at a time otherwise,if I clicked ib two buttons at the same time, I have a hunch
	// that it may cause a series of problems.
	public synchronized void onClick(View v) 
	{
		// If it is player one's turn...
		if(playerOneTurn == true)
		{
			// The code for player one (the human) will go here, aha.
			for(int x = 0;x<8;x++)
			{
				// ((x+1)%2) will make it change back-and-forth from 1 to 0 after each 'x' iteration. This will allow a search for events through
				// the 32 squares that would call an event instead of searching through 64 squares (where 32 will never ever call a event).
				for(int y=((x+1)%2);y<8;y+=2)
				{
					// Firstly, we must find the row/column value of the square that initiated the event.
					if(squaresOfBoard[x][y].equals(v))
					{
						// We move our pieces as normal.
						playerTurn("1", strCheckersBoard, v, x, y, "2");		// Nice, it works.
					}
				}
			}
		}else // if playerOneTurn == false (i.e. player two's turn)
		{
			for(int x = 0;x<8;x++)
			{
				// Loop until we a find the piece that initiated the event.
				for(int y=((x+1)%2);y<8;y+=2)
				{
					// Firstly, we must find the row/column value of the square that initiated the event.
					if(squaresOfBoard[x][y].equals(v))
					{
						// We move our pieces as normal.
						playerTurn("2", strCheckersBoard, v, x, y, "1");		// Nice, it works.
					}
				}
			}
		}
		
	}// End of 'onClick'
	public boolean isTrapped(String[][] passStrCheckersBoard, String playerNo, String opponentNo)
	{
		// Initially true...
		boolean isTrapped = true;
		// Generate the king String.
		String strKing = "K" + playerNo;
		// This where the check will happen...
		for(int row = 0; row < 8;row++)
		{
			for(int column=((row+1)%2); column<8; column+=2)
			{
				// Checks whether the player can make it at least one move.
				if(passStrCheckersBoard[row][column] == playerNo || passStrCheckersBoard[row][column].contains(strKing))
				{
					// We then check (if we can add coordinates), and if so, add the coordinates of the possible moves that piece at[row][column] can make...
					highlightSquares(passStrCheckersBoard, row, column, opponentNo, playerNo);
					// We then check if it can make at least one move. If we do, we stop searching because we only need to find at least one.
					if(xPrevAxis.size() > 0) // arrayOfPrevCoordinatesX is not needed.
					{
						// We update the value.
						isTrapped = false;
						// Clear the ArrayLists.
						clearHelperArrays();
						//clearMasterLists();
						// Make it so, that this is the last iteration (i.e. break the loop)
						row = 8; column = 8;
					}
				}
			}
		}
		// Return the result.
		return isTrapped;	
	}
	public void updateNoOfPieces(String[][] passStrCheckersBoard)
	{
		// Clear the current count
		noOfPiecesPlayerOne = 0;
		noOfPiecesPlayerTwo = 0;
		
		// Update the number of pieces for each piece we see...
		for(int row = 0; row < 8;row++)
		{
			for(int column=((row+1)%2); column<8; column+=2)
			{	
				if(passStrCheckersBoard[row][column].contains("1")){
					
					noOfPiecesPlayerOne++;
				}
				else if(passStrCheckersBoard[row][column].contains("2")){
					
					noOfPiecesPlayerTwo++;
				}
			}
		}	
	}
	public void setPlayerImage(String playerNo)
	{
		// Update the player's image depending on the playerNo passed in.
		if(playerNo == "1"){
			
			playerImage.setImageResource(R.drawable.dark_brown_piece);
			
		}else{
			
			playerImage.setImageResource(R.drawable.light_brown_piece);	
		}
	}
	public void switchHuman(String playerNo, String opponentNo)
	{
		// If the opponent still has pieces on the board...
		if(getNoOfPieces(opponentNo) > 0)
		{
			// We could check if the opponent (in this case, the human) has trapped pieces.
			boolean isTrapped = isTrapped(strCheckersBoard, opponentNo, playerNo);
			
			if(isTrapped == true)
			{
				// The bot wins... Since, the opponent cannot make any legitimate moves. 16 characters per line break! Well, that did it and trapped code works
				loadingInfo.setText("Player " + playerNo + " says, \n\"Well, it looks \nlike you're \nstuck. Better \nluck next time.\"");
				// Display it too.
				playerInfo.setText("Game Over!\nPlayer " + playerNo + " is\nthe Winner!");
				// Display the image of the winner... blah de blah blah.
				setPlayerImage(playerNo);
			}
			else
			{
				// Opponent info will go here...
				setPlayerImage(opponentNo);
				// Display's the opponent's information...
				playerInfo.setText("Player " + opponentNo + "'s Turn");
				
				// Nothing will go here as we are playing against a human so, the human can simply perform his move by also clicking the screen :)
			}
		}
		else
		{
			// Display it on the player information too.
			playerInfo.setText("Game Over!\nPlayer " + playerNo + " is\nthe Winner!");
			// Display the image of the winner... blah de blah blah.
			setPlayerImage(playerNo);	
		} 
	}
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
	public void prepareHighlight(String[][] passStrCheckersBoard, boolean firstCondition, boolean secondCondition, int passX, int passY, int upOrDown, int leftOrRight)
	{
		// This a method that will handle any number of neighbouring squares that need to be highlighted, or
		// only highlights enemy square, should it be the case.
		
		int x = passX;
		int y = passY;
		
		// If neighbouring square contains an enemy piece...
		if(firstCondition && passStrCheckersBoard[x+(upOrDown+upOrDown)][y+(leftOrRight+leftOrRight)] == "0") 
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
		}		
	}
	public void highlightSquares(String[][] passStrCheckersBoard, int passX, int passY, String opponentNo, String playerNo)
	{
		// Store the coordinates, and whatnot.
		int x = passX;
		int y = passY;
		int upOrDown;
		
		// This will hold the generated conditions that we will compute shortly.
		boolean precondition = false, attackConstraint = false;
		// Dynamically, create the conditions based on the player number.
		if(playerNo.contains("1"))
		{
			// Goes down the checkersboard 
			upOrDown = -1;
			// Conditions for player one.
			precondition = (x >= 1 && x <= 7);
			attackConstraint = (x >= 2);
		}
		else //if(playerNo.contains("2"))
		{
			// Goes up the checkersboard 
			upOrDown = 1;
			// Conditions for player two.
			precondition = (x >= 0 && x <= 6);
			attackConstraint = (x <= 5);
		}
		
		// Dynamically creates the correct string that will hold the value K1 or K2, depending on the player number.
		String strKing = "K" + playerNo;
		// If the selected piece is a king...
		if(passStrCheckersBoard[x][y].contains(strKing))
		{
			// If the piece selected is a king piece...
			if(x >= 1 && x <= 7)
			{
				// When adding highlights to pieces above it...
				// Only perform a move if we are on/within row 7 to row 1... x >= 2
				// Checks the left side and decides whether it should highlight the squre or not.
				prepareHighlight(passStrCheckersBoard, y >= 2 && x >= 2 && passStrCheckersBoard[x+(-1)][y-1].contains(opponentNo),
												 y >= 1 && passStrCheckersBoard[x+(-1)][y-1] == "0", x, y, -1, -1);
				// Checks the right side and decides whether it should highlight the square or not.
				prepareHighlight(passStrCheckersBoard, y <= 5 && x >= 2 && passStrCheckersBoard[x+(-1)][y+1].contains(opponentNo),
												 y >= 0 && y <= 6 && passStrCheckersBoard[x+(-1)][y+1] == "0", x, y, -1 , 1);	
			}
			if(x >= 0 && x <= 6)
			{
				// When adding highlights to pieces beneath it...
				// Only perform a move if we are on/within row 6 to row 0... x <= 5
				// Checks the left side and decides whether it should highlight the squre or not.
				prepareHighlight(passStrCheckersBoard, y >= 2 && x <= 5 && passStrCheckersBoard[x+(1)][y-1].contains(opponentNo),
												 y >= 1 && passStrCheckersBoard[x+(1)][y-1] == "0", x, y, 1, -1);
				// Checks the right side and decides whether it should highlight the square or not.
				prepareHighlight(passStrCheckersBoard, y <= 5 && x <= 5 && passStrCheckersBoard[x+(1)][y+1].contains(opponentNo),
												 y >= 0 && y <= 6 && passStrCheckersBoard[x+(1)][y+1] == "0", x, y, 1, 1);	
			}		
		}
		else if(passStrCheckersBoard[x][y] == playerNo && precondition)
		{
			// If it is just a standard piece then...
			// Checks the left side and decides whether it should highlight the squre or not.
			prepareHighlight(passStrCheckersBoard, y >= 2 && attackConstraint && passStrCheckersBoard[x+(upOrDown)][y-1].contains(opponentNo),
											 y >= 1 && passStrCheckersBoard[x+(upOrDown)][y-1] == "0", x, y, upOrDown, -1);
			// Checks the right side and decides whether it should highlight the square or not.
			prepareHighlight(passStrCheckersBoard, y <= 5 && attackConstraint && passStrCheckersBoard[x+(upOrDown)][y+1].contains(opponentNo),
											 y >= 0 && y <= 6 && passStrCheckersBoard[x+(upOrDown)][y+1] == "0", x, y, upOrDown, 1);		
		}
	}
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
				
				// If the piece selected has been previously highlighted...
				// It is more efficent to compare the two sets of coordinates this way, rather than comparing through an array,
				// in terms of an IndexOutOfBoundException
				if(x == xHighlighted && y == yHighlighted) 
				{
					// The selected square is part of the highlighted squares.
					isHighlighted = true;
					// Close the loop as soon as we speak the selected square (if) it is part of highlighted squares. 
					h = sizeOfPrev;
					
				}else
				{
					// All we need is at least one x to be false in order for x1 && x2 && x3 to be false so, any other checks would cause harm.
					// The selected square is not part of the highlighted squares.
					isHighlighted = false;
				}
			}
		}else
		{
			// Since there are no highlighted squares to even check for, we simply assign it a value of 'false'
			isHighlighted = false;
		}
		// This will later be assigned to the global variable 'isHighlighted'
		return isHighlighted;		
	}
	public void movePiece(String[][] passStrCheckersBoard, int passX, int passY, ArrayList<Integer> passListOfRows, ArrayList<Integer> passListOfColumns,
												ArrayList<Integer> passEnemyX, ArrayList<Integer> passEnemyY, String strDest, String opponentNo, boolean forDecisionTree)
	{
		// Clear the old values.
		xOfNewDest = 0;
		yOfNewDest = 0;
		
		// This will hold the hex addresses of the images for the corresponding player, which would be dynamically obtained.
		int destinationImg = 0;
		int destinationImgKing = 0;
		// This will hold the value of either -1 or 1 determined by the playerNo, which will be dynamically obtained shortly...
		int upOrDown;
		
		// Dynamically determine whether the piece should go up or down on the checkers board.
		if(strDest.contains("1")){upOrDown = -1;}else{upOrDown = 1;}
		
		// If we are actually modify the checkers board...
		if(forDecisionTree == false)
		{
			// If this is for player one...
			if(strDest.contains("1"))
			{
				// Assign the hex addresses of the correct images for player one.
				destinationImg = R.drawable.dark_brown_piece;
				destinationImgKing = R.drawable.king_dark_brown_piece;
			}
			else // If this is for player two...
			{
				// Assign the hex addresses of the correct images for player two.
				destinationImg = R.drawable.light_brown_piece;
				destinationImgKing = R.drawable.king_light_brown_piece;
			}
		}
				
		// Grab the size of the selected coordinates.
		int sizeOfPrev = passListOfRows.size();
		int x = passX, y = passY;
		// a.k.a the playerNo
		String strSource;
		
		// This will hold the coordinates of the enemy that is about to be captured.
		int actualEnemyX = 0, actualEnemyY = 0;
		
		for(int mv = 1;mv < sizeOfPrev;mv++)
		{	
			// The coordinates of the root square of highlighted squares
			int parentPrevX = passListOfRows.get(0).intValue();
			int parentPrevY = passListOfColumns.get(0).intValue();
			// The coordinates of the neighbouring squares of the highlighted squares.
			int prevX = passListOfRows.get(mv).intValue();
			int prevY = passListOfColumns.get(mv).intValue();

			// If the selected piece is part of the highlighted neighbouring squares, then perform blah de blah blah.
			if((x == prevX && y == prevY) && passStrCheckersBoard[x][y] != opponentNo)
			{
				// Close the loop after this iteration.
				mv = sizeOfPrev;
				// Temporary variable that will hold the value at the old location (i.e. the piece we wish to move).
				strSource = passStrCheckersBoard[parentPrevX][parentPrevY];
	
				// If the parent square has a neighbouring enemy piece, then we need to determine which enemy piece it is from the list of enemy coordinates.
				if(passEnemyX.size() > 0)
				{
					// Assuming that the parent highlighted sqaure was neighbouring more than one enemy piece, we would need to find out the right enemy...
					// ...Piece to get rid of. We will loop until we find the correct enemy piece...
					for(int e = 0; e < passEnemyX.size(); e++)
					{
						int enemyCoordinateX = passEnemyX.get(e).intValue();
						int enemyCoordinateY = passEnemyY.get(e).intValue();
						
						int checkBelow = parentPrevX + 1;
						int checkAbove = parentPrevX - 1;

						// We check the square below...
						if(checkBelow == enemyCoordinateX && (prevY+1) == enemyCoordinateY)
						{
							// The enemy square is here...
							// We clear the space i.e. take the piece.
							passStrCheckersBoard[enemyCoordinateX][enemyCoordinateY] = "0";
							// We will store the coordinates of the recently found enemy to later set the image for the right ImageView.
							actualEnemyX = enemyCoordinateX;
							actualEnemyY = enemyCoordinateY;
							// Makes this the last 'e' iteration.
							e = passEnemyX.size();
							// Will decrease the number of pieces the opponent has by 1.
							if(strDest.contains("1") && forDecisionTree != true){--noOfPiecesPlayerTwo;}else if(strDest.contains("2") && forDecisionTree != true){--noOfPiecesPlayerOne;}
							
						}
						else if(checkBelow == enemyCoordinateX && (prevY-1) == enemyCoordinateY)
						{
							// Check the square below...
							// The enemy square is here. This works. Nice.
							// We clear the space i.e. take the piece.
							passStrCheckersBoard[enemyCoordinateX][enemyCoordinateY] = "0";
							// We will store the coordinates of the recently found enemy to later set the image for the right ImageView.
							actualEnemyX = enemyCoordinateX;
							actualEnemyY = enemyCoordinateY;
							// Makes this the last 'e' iteration.
							e = passEnemyX.size();
							// Will decrease the number of pieces the opponent has by 1.
							if(strDest.contains("1") && forDecisionTree != true){--noOfPiecesPlayerTwo;}else if(strDest.contains("2") && forDecisionTree != true){--noOfPiecesPlayerOne;}
						}
						else if(checkAbove == enemyCoordinateX && (prevY+1) == enemyCoordinateY)
						{
							// Check the square above...
							// The enemy square is here. This works. Nice.
							// We clear the space i.e. take the piece.
							passStrCheckersBoard[enemyCoordinateX][enemyCoordinateY] = "0";
							// We will store the coordinates of the recently found enemy to later set the image for the right ImageView.
							actualEnemyX = enemyCoordinateX;
							actualEnemyY = enemyCoordinateY;
							// Makes this the last 'e' iteration.
							e = passEnemyX.size();
							// Will decrease the number of pieces the opponent has by 1.
							if(strDest.contains("1") && forDecisionTree != true){--noOfPiecesPlayerTwo;}else if(strDest.contains("2") && forDecisionTree != true){--noOfPiecesPlayerOne;}
						}
						else if(checkAbove == enemyCoordinateX && (prevY-1) == enemyCoordinateY)
						{
							// Check the square above...
							// The enemy square is here. This works. Nice.
							// We clear the space i.e. take the piece.
							passStrCheckersBoard[enemyCoordinateX][enemyCoordinateY] = "0";
							// We will store the coordinates of the recently found enemy to later set the image for the right ImageView.
							actualEnemyX = enemyCoordinateX;
							actualEnemyY = enemyCoordinateY;
							// Makes this the last 'e' iteration.
							e = passEnemyX.size();
							// Will decrease the number of pieces the opponent has by 1.
							if(strDest.contains("1") && forDecisionTree != true){--noOfPiecesPlayerTwo;}else if(strDest.contains("2") && forDecisionTree != true){--noOfPiecesPlayerOne;}
						}					
					}
					// If this move is an actual move (not a potential move made by the minimax algorithm),
					// then we set the corresponding square's new image to be empty, which indicates a capture...
					if(forDecisionTree == false)
					{
						// We clear the space (visually) i.e. take the piece
						imageOfSquares[actualEnemyX][actualEnemyY].setImageResource(0);			
					}
					// else if this is for generating a state corresponding to a potential move, then there is no need to set the images since,
					// since we will not need at the moment.		
				}
				
				// Creates the king string corresponding to the player number.
				String strKing = "K" + strDest;
				// If a piece of player one reaches the last row (the topmost of the board), transform the piece into a king...
				if(x == 0 && passStrCheckersBoard[parentPrevX][parentPrevY] == "1")
				{
					// Now, the piece will now become a king at the new location.
					passStrCheckersBoard[parentPrevX][parentPrevY] = strKing;
					// This stops a new king from making a consecutive attack upon transformation.
					isNewKing = true;
					
				}else if(x == 7 && passStrCheckersBoard[parentPrevX][parentPrevY] == "2")
				{
					// If a piece of player two reaches the last row (bottommost of the board), transform the piece into a king.
					// Now, the piece will now become a king at the new location.
					passStrCheckersBoard[parentPrevX][parentPrevY] = strKing;
					// This stops a new king from making a consecutive attack upon transformation.
					isNewKing = true;
				}
				else
				{
					// This stops a new king from making a consecutive attack upon transformation.
					isNewKing = false;
				}
				
				// If the piece that we wish to move/or use to perform capture is a king, then we...
				// Using .contains() works better than using == on mutated Strings. Why? I don't know.
				if(passStrCheckersBoard[parentPrevX][parentPrevY].contains(strKing))
				{
					// Finally, we will clear the square of the checker piece's old location
					passStrCheckersBoard[parentPrevX][parentPrevY] = "0";
					// Moves it to the new location...
					passStrCheckersBoard[x][y] = strKing;
					// If this is for the purposes of an actual move (not a potential move for the AI state tree...
					if(forDecisionTree == false)
					{
						// Clear the image of the piece that occupied the square at the old location.
						imageOfSquares[parentPrevX][parentPrevY].setImageResource(0);
						// (Visually) Moves the checkers piece into the new location
						imageOfSquares[x][y].setImageResource(destinationImgKing);	
					}
						
				}
				else //if(passStrCheckersBoard[parentPrevX][parentPrevY] == strDest)
				{
					// Finally, we will clear the square of the checker piece's old location
					passStrCheckersBoard[parentPrevX][parentPrevY] = "0";
					// This is an ordinary piece, then it will just be a simple (only one direction) capture/move.
					passStrCheckersBoard[x][y] = strSource;
					// If this is for the purposes of an actual move (not a potential move for the AI state tree...
					if(forDecisionTree == false)
					{			
						// Clear the image of the piece that occupied the square at the old location.
						imageOfSquares[parentPrevX][parentPrevY].setImageResource(0);	
						// (Visually) Moves the checkers piece into the new location
						imageOfSquares[x][y].setImageResource(destinationImg);
					}
				}
				
				// Store the new location in our global variables to work with later on.
				xOfNewDest = x;
				yOfNewDest = y;
				// Clear the row/column values of the highlighted squares.
				clearHelperArrays();
				
				// Only clears the highlights if we are working with the actual checkers board.
				if(forDecisionTree == false)
				{
					// I forgot this modifies the UI, shizz. Whoops, hopefully, enclosing the following the code will help in creating a Thread.
					// Remove the highlights.
					removeHighlights(passListOfRows, passListOfColumns);	
				}
				
			}//else - We do nothing because an opposing piece was clicked or it was an invalid square clicked.
		}							
	}
	public void playerTurn(String playerNo, String[][] passStrCheckersBoard, View v, int passX, int passY, String opponentNo)
	{	
		// The coordinates of the currently selected square.
		int x = passX, y = passY;
		
		// It will initially check for any compuslory captures...
		if(arrayOfPrevCoordinatesX.size() <= 0)
		{	
			// Prepares the correct string for playerX.
			String strKing = "K" + playerNo;
			
			for(int row = 0; row < 8;row++)
			{
				for(int column =((row+1)%2);column<8;column+=2)
				{
					// Checks whether there are any pieces neighbouring any enemy pieces.
					if(passStrCheckersBoard[row][column] == playerNo || passStrCheckersBoard[row][column].contains(strKing))
					{
						// checks which player's turn it is before calling the highlightSquares() method within in the method below.
						checkPlayerAndAdd(passStrCheckersBoard, row, column, opponentNo, playerNo);
								
						// Check for adjacent enemy(s)...
						if(xEnemyAxis.size() > 0)
						{
							// There is an adjacent enemy.
							isEnemyAdjacent = true;
							// Add the ArrayLists to the master ArrayLists.
							addToMasterLists(xPrevAxis, yPrevAxis, xEnemyAxis, yEnemyAxis);
							// Then clear the standard ArrayLists and repeat.
							clearHelperArrays();
						}
						else
						{
							// No adjacent enemies were seen from this position so, we do nothing.
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
				}// else - we do nothing until the next iteration...
			}
		}else
		{
			// The for-loop may not run at all so, we assign a value to isHighlighted.
			isHighlighted = false;
		}
		
		// Checks whether the highlighted piece is adjacent to an enemy (and there could be more than one.)
		if(isEnemyAdjacent == true)
		{
			// We lock the game until the player makes a capture.
			// This will determine what type of move (either standard one or a capture) it should make, and also make the move.
			performMoveAndCheckAdjacent(passStrCheckersBoard, passX, passY, playerNo, opponentNo);
			
		}else
		{
			//...Otherwise, we check if the selected square corresponds to the player's number and if it is part of the highlighted squares.
			if(passStrCheckersBoard[x][y].contains(playerNo) && isHighlighted == false)
			{
				for(int rm = 0;rm < arrayOfPrevCoordinatesX.size();rm++)
				{
					// Gets rid of the highlights, and clears the helper ArrayLists such as the x/yPrevAxis and x/yEnemyAxis ArrayLists.
					removeHighlights(arrayOfPrevCoordinatesX.get(rm), arrayOfPrevCoordinatesY.get(rm));
				}
				
				// Clear the standard ArrayLists.
				clearHelperArrays(); 
				// Now, if the newly selected square has checkers piece which belongs to playerX, then highlight it, and its neighbouring squares.
				// Clear the master ArrayLists.
				clearMasterLists();
				
				// Add new highlights for the newly selected square.
				highlightSquares(passStrCheckersBoard, x, y, opponentNo, playerNo); 
				// Add the ArrayLists to the master ArrayList... Aha.
				arrayOfPrevCoordinatesX.add(xPrevAxis);
				arrayOfPrevCoordinatesY.add(yPrevAxis);
				// Clear the x/yPrevAxis and x/yEnemyAxis ArrayLists
				clearHelperArrays();
				// Adds the visual representation of the highlights to the squares based on the result from the 'prepareHighlight()' method.
				addHighlight(arrayOfPrevCoordinatesX.get(0), arrayOfPrevCoordinatesY.get(0));
			}
			else if(passStrCheckersBoard[x][y] == "0" && isHighlighted == true)
			{	
				// Or if the selected square is part of the highlighted squares, and the square is empty, we will see if we can make a move...
				// This will determine what type of move (standard or a capture) it should make, and also make the move.
				performMoveAndCheckAdjacent(passStrCheckersBoard, passX, passY, playerNo, opponentNo);
			}
		}	
	}
	public void addToMasterLists(ArrayList<Integer> passPrevX, ArrayList<Integer> passPrevY, ArrayList<Integer> passEnemyX, ArrayList<Integer> passEnemyY)
	{
				// Adds the standard ArrayLists passPrevX/Y to the master ArrayLists
				arrayOfPrevCoordinatesX.add(passPrevX);
				arrayOfPrevCoordinatesY.add(passPrevY);
				
				// Only, if there is exists an adjacent enemy, we add the ArrayList of its coordinates to the master ArrayList.
				if(passEnemyX.size() > 0)
				{
					arrayOfEnemyCoordinatesX.add(passEnemyX);
					arrayOfEnemyCoordinatesY.add(passEnemyY);
				}		
	}			
	public void clearMasterLists()
	{
				// This will clear the ArrayLists that holds the ArrayLists :)
				arrayOfPrevCoordinatesX.clear();
				arrayOfPrevCoordinatesY.clear();
				arrayOfEnemyCoordinatesX.clear();
				arrayOfEnemyCoordinatesY.clear();
	}
	public int getNoOfPieces(String playerNo)
	{
		// Automatically retrieve the number of pieces the player x has left...
		if(playerNo == "1")
		{
			return noOfPiecesPlayerOne;
		}else // if playerNo == "2"
		{
			return noOfPiecesPlayerTwo;
		}
	}
	public void performMoveAndCheckAdjacent(String[][] passStrCheckersBoard, int passX, int passY, String playerNo, String opponentNo)
	{
		// The coordinates of the destination.
		int x = passX, y = passY;

		for(int i = 0 ; i < arrayOfPrevCoordinatesX.size(); i++)
		{
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
		
			// Moves the checkers piece to the new location - passing imageOfSquares into the method has not caused any problems.
			movePiece(passStrCheckersBoard, x, y, autoPrevX, autoPrevY, autoEnemyX, autoEnemyY, playerNo, opponentNo, false);
			
			// Checks whether the piece is at the new location. x/yOfNewDest is generated only when a piece is moved within the movePiece() function.
			if(x == xOfNewDest && y == yOfNewDest)
			{
				// If we have successfully moved the piece...
				
				// Makes sure this is the last iteration as we have found which piece made the recent move.
				i = arrayOfPrevCoordinatesX.size();
					
				// If a capture was made, we check if there are neighbouring enemies at our new location...
				if(arrayOfEnemyCoordinatesX.size() > 0)
				{
					// This will check if it is has also been transformed into a king...
					// in the movePiece method there are 3 areas in the code where 'isNewKing' gets mutated so, remember to keep an eye out on those.
					if(isNewKing == true)
					{
						// It is true so, this will stop the piece from making any more captures during the remainder of its current turn.
					}
					else
					{
						// We will use this method to later check whether the piece at the new location is neighbouring an enemy.
						highlightSquares(passStrCheckersBoard, xOfNewDest, yOfNewDest, opponentNo, playerNo);
					}
					
					// If the size of xEnemyAxis.size() == 0, then we can say no new enemies have been found using the 'highlightSquares()' method above.
					if(xEnemyAxis.size() <= 0)
					{
						// We will remove the remaining highlights... i.e. if there were two different pieces each neighbouring an enemy.
						for(int rm = 0;rm<arrayOfPrevCoordinatesX.size();rm++)
						{
							// Gets rid of the rest of the highlights for the other pieces that had potential to capture enemy pieces.
							removeHighlights(arrayOfPrevCoordinatesX.get(rm), arrayOfPrevCoordinatesY.get(rm));
						}
						
						// Clears the master ArrayLists...
						clearMasterLists();	
						// Clears the standard helper ArrayLists.
						clearHelperArrays();
						// hand over its turn to the opponent (i.e. the human).
						playerOneTurn = !playerOneTurn; 
						// No more adjacent enemies so, yeah, it will now be false.
						isEnemyAdjacent = false;
						
						// It will check whether the opponent's pieces are trapped, and adds a delay of 200ms before handing over its turn to the bot.
						// It also displays the correct player information, and whatnot.
						switchHuman(playerNo, opponentNo);
						
					}
					else
					{
						// There is another neighbouring enemy at our new location so, when this method is called again (which was now), it will...
						// ...Run the code below...
						
						// Hopefully, this should sort out problem 3... Yup, it sorted problem 3 specified in crappy_notes.txt file :)
						for(int rm = 0;rm<arrayOfPrevCoordinatesX.size();rm++)
						{
							// Gets rid of the rest of the highlights for the other pieces that had potential to capture enemy pieces.
							removeHighlights(arrayOfPrevCoordinatesX.get(rm), arrayOfPrevCoordinatesY.get(rm));
						}
						
						// Clear the master ArrayLists
						clearMasterLists();
						// Adds the helper ArrayLists to the master ArrayLists 
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
					// Clear the master ArrayLists
					clearMasterLists();	
					// Clear the standard ArrayLists.
					clearHelperArrays();
					// hand over its turn to the opponent (i.e. the human).
					playerOneTurn = !playerOneTurn; 
					// No more adjacent enemies so, yeah, it will now be false.
					isEnemyAdjacent = false;
					// It will check whether the opponent's pieces are trapped, and adds a delay of 200ms before handing over its turn to the bot.
					// It also displays the correct player information, and whatnot.
					switchHuman(playerNo, opponentNo);
								
				}						
			}else
			{
				// Debug purposes.
				// System.out.println("We have not moved the piece to the new location. (from auto-generated highlights section) so, we do nothing until then.");
			}					
		}
	}
}