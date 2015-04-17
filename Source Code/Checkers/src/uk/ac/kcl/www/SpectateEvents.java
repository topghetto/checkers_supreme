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
import java.util.Random;

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

public class SpectateEvents extends Activity implements View.OnClickListener
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
	// Start button for spectating.
	public Button startBtn;
	
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
	public boolean isHighlighted, playerOneTurn, isEnemyAdjacent, isNewKing, adjacentToEnemy, isPaused;
	
	// Keeps track of the new location of the recently moved piece.
	public int xOfNewDest, yOfNewDest;
	
	// Will later hold the speed of each play in milliseconds.
	public static int speed;
	// A variable that will hold the count of how many times the bots should perform random moves. In this case,
	// each bot will initially perform their first move randomly...
	public int firstMoveCount;
	
	
	// Constructor
	public SpectateEvents(View[][] passSquares, ImageView[][] passImgSquares, String[][] passCheckersBoard, TextView passTextView, TextView passLoadingInfo, ProgressBar passLoadingWheel, ImageView passPlayerImage, Button passStartBtn)
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
		// Initialise...
		startBtn = passStartBtn;
		// Determines whether should start moving the bot pieces again.
		isPaused = false;
		
		// Make it so that both of the bot's first move.
		firstMoveCount = 0;
		
		// General Information.
		loadingInfo.setText("Press the Start \nto Begin");
		// Display the player's turn. REMEMBER TO CHANGE THIS PARTICULAR SECTION WHEN I AUTOMATICALLY MAKE THE CODE DECIDE WHO GOES FIRST!!!
		playerInfo.setText("Player " + 1 + "'s Turn");
		// Set the image of the player image.
		playerImage.setImageResource(R.drawable.dark_brown_piece);
		
		// Speed is initially 1 second per move.
		speed = 1000;
		// Keeps track of the number of pieces and...
		// ...Initially and dynamically determines the number of pieces for each player...
		updateNoOfPieces(strCheckersBoard);
	}
	public double evaluateNode(Tree<String[][]> passNode, String playerNo, String opponentNo)
	{
		// Evaluation will be done within this method...
		String[][] state = passNode.getValue();
		// The number of pieces on the board that player one (the human) has ...
		int noOfPlayerOne = 0;
		// The number of pieces on the board that player two (the computer) has...
		int noOfPlayerTwo = 0;
		// calculate how many pieces are close to becoming kings. The total number of pieces in 0,1,2 for player 1 and 5,6,&7 for player 2.
		// Although, it seems to do well without this so, I may delete this in the future.
		// double playerOneOffense = 0, playerTwoOffense = 0;
		
		// Perform the evaluation.
		for(int row = 0;row < 8; row++)
		{
			for(int column=((row+1)%2); column<8; column+=2)
			{
				// 1. Counts the number of player two's (CPU) pieces to player one's (Human) piece,
				// and also takes the number of kings into consideration.
				if(state[row][column].contains("1"))
				{
					// If it is a standard piece, increase the heuristic.
					noOfPlayerOne++;
					
					if(state[row][column].contains("K1"))
					{
						// If it is also a king, increase the heueristic even more...
						noOfPlayerOne++;
					}
					/*// If the piece is close to becoming a king.
					if(row >= 0 && row <= 2)
					{
						// Increase the heuristic value.
						playerOneOffense++;
					}*/
				}
				else if(state[row][column].contains("2"))
				{
					// If it is a standard piece, increase the heuristic.
					noOfPlayerTwo++;
					
					if(state[row][column].contains("K2"))
					{
						// If it is also a king, increase the heueristic even more...
						noOfPlayerTwo++;
					}
					/*// If the piece is close to becoming a king.
					if(row >= 5 && row <= 7)
					{
						// Increase the heuristic value.
						playerTwoOffense++;
					}*/
				}
			}
		}	
		// Evaluate the difference and store it. 
		// This will hold the result of the evaluation.
		double result;
		
		if(playerNo == "1"){
			
			// Subtract total of player two from player one
			result = noOfPlayerOne - noOfPlayerTwo;
		}
		else{ //if playerNo == "2"
			
			// Subtract total of player one from player two.
			result = noOfPlayerTwo - noOfPlayerOne;
		}
		return result;
	}
	// -- Alpha-Beta Experiment -- //	
	public double alphabeta(Tree<String[][]> passNode, int depth, double alpha, double beta, boolean maximisingPlayer, String playerNo, String opponentNo)
	{
		if(depth == 0) 
		{
			// Calculate and return the heuristic value.
			return evaluateNode(passNode, playerNo, opponentNo);
		}
		if(maximisingPlayer == true)
		{
			// Maximising player's turn... (i.e. it is a MAX node)...
			// Initially negative infinity.
			double bestValue = alpha;
			// Generate the children - This will correspond to the root MAX node which will create MIN nodes... This works okay.
			//createChildren(passNode, "2", "1");
			createChildren(passNode, playerNo, opponentNo);
			// Grab the children of the node passed in.
			ArrayList<Tree<String[][]>> children = passNode.children();
			
			// If the node did generate children after calling 'createChildren()'... We, will loop through...
			if(children.size() > 0)
			{
				// For each child of the (parent) node
				for(Tree<String[][]> child : children)
				{
					// Recursion call, y'all.
					double value = alphabeta(child, depth-1, alpha, beta, false, playerNo, opponentNo);
					
					// bestValue = Math.max(bestValue, value) is what the code below is technically doing...
					if(value > bestValue)
					{
						// Updates the bestValue with the current value.
						bestValue = value;
							
						if(passNode.isRoot())
						{
							// When we are at the root of the node passed in, store the actual node into a global Tree<String[][]> that we will later use...
							greatestMove = child;	
						}	
					}
					// We only update alpha if bestValue is greater than it...
					if(bestValue > alpha)
					{
						// Update alpha.
						alpha = bestValue;
					}
					// The pruning begins...
					if(beta <= alpha)
					{
						// Prune...
						break;
					}
				}
			}else
			{
				// This call wiill evaluate the node passed in, earlier than expected because it no longer has any children.
				bestValue = alphabeta(passNode, 0, alpha, beta, false, playerNo, opponentNo);
			}			
			// Return the overall result.
			return bestValue;
			
		}else //if(maximisingPlayer = false)
		{
			// If it is the Minising Player (i.e. a MIN node)...
			// beta will initially be positive infinity .
			double bestValue = beta;
			// Generate the children - the new states will be created in the method below. This corresponds to the MIN Nodes which will create MAX nodes.
			// createChildren(passNode, "1", "2");
			createChildren(passNode, opponentNo, playerNo);
			// Grab the children of the node passed in.
			ArrayList<Tree<String[][]>> children = passNode.children();
			
			// If the node did generate children after calling 'createChildren()'... We, will loop through...
			if(children.size() > 0)
			{
				// For each child of the (parent) node
				for(Tree<String[][]> child : children)
				{
					// A recursive call that will eventually assign the result of that call into 'value'.
					double value = alphabeta(child, depth-1, alpha, beta, true, playerNo, opponentNo);
					
					// bestValue = Math.min(bestValue, value) is what the code below is technically doing...
					if(value < bestValue)
					{
						// Update the bestValue with the new best value.
						bestValue = value;
						
						if(passNode.isRoot())
						{
							// When we are at the root of the node passed in, store the actual node into a global Tree<String[][]> that we will later use...
							greatestMove = child;	
						}	
					}
					// Only updates beta when bestValue is less than beta
					if(bestValue < beta)
					{
						// Updates beta.
						beta = bestValue;
					}
					// Prune...
					if(beta <= alpha)
					{
						// Pruning...
						break;
					}
				}
			}else
			{
				// Debug purposes. This is for the purpose of when the bot has only one piece left on the board, and it's cornered (about to be captured.)
				// When it is MIN's turn and assuming a final capture happened beforehand (at MAX), Min would have no pieces to move, resulting in no
				// children created so, because the game would be the won by the opponent, there are no more possible moves that can be made so,
				// we evaluate earlier, which seems to be working well.
				// System.out.println("We will evaluate passNode earlier than the cut-off depth because it has no children...");
				// This call wiill evaluate the node passed in, earlier than expected because it no longer has any children.
				bestValue = alphabeta(passNode, 0, alpha, beta, true, playerNo, opponentNo);
			}
			// Return the overall result.
			return bestValue;
		}
	}
	// --- End of Alpha-Beta Experiment -- //
	public double minimax(Tree<String[][]> passNode, int depth, boolean maximisingPlayer, String playerNo, String opponentNo)
	{
		// Base case...
		if(depth == 0) 
		{
			// Calculate and return the heuristic value.
			return evaluateNode(passNode, playerNo, opponentNo);
		}
		if(maximisingPlayer == true)
		{
			// Debug purposes.
			System.out.println("Maximising Player:"); 
			// If it is a MAX node...
			// An experiment
			Tree<String[][]> bestMove = null;
			// Initially negative infinity.
			double bestValue = Double.NEGATIVE_INFINITY;
			// Generate the children - This will correspond to the root MAX node which will create MIN nodes... This works okay.
			//createChildren(passNode, "2", "1");
			createChildren(passNode, playerNo, opponentNo);
			// Grab the children of the node passed in.
			ArrayList<Tree<String[][]>> children = passNode.children();
			
			// --- An experiment --- //
			
			if(children.size() > 0)
			{
				// For each child of the (parent) node
				for(Tree<String[][]> child : children)
				{
					// Recursion call, y'all.
					double value = minimax(child, depth-1, false, playerNo, opponentNo);
					// Debug purposes.
					System.out.println("The depth of the node ");
					System.out.print("max(" + bestValue + ", ");
					// If the new value obtained is larger than the previous bestValue, update the 'bestValue' with the new value.
					// bestValue = Math.max(bestValue, value);
					
					if(value > bestValue)
					{
						bestValue = value;
						// Store the best move... I hope. All this time I have been saving passNode not 'child'... Hopefully, it works now.	
						// An experiment. using 'passNode' always picks the last moveable piece in the tree.		
						if(passNode.isRoot())
						{
							// Well, it picks sometimes first child, and even second child. It may actually be working now. I'll run some more tests.
							// It cleverly avoided the pieces when the CPU had only one piece left. Yup, this works :)
							// Debug purposes.
							System.out.println("This is the root node within in the minimax recursive stack and here are the contents of one child from root:");
							// Print the board, yup.
							printCheckersBoard(child.getValue());
							// Store the greatest move.
							greatestMove = child;	
						}	
					}
					
					// Debug purposes.
					System.out.println(value + ") is " + bestValue);
				}
			}else
			{
				// Debug purposes. This is for the purpose of when the bot has only one piece left on the board, and it's cornered (about to be captured.)
				System.out.println("We will evaluate passNode earlier than the cut-off depth because it has no children...");
				// An attempt to evaluate the node early.
				bestValue = minimax(passNode, 0, false, playerNo, opponentNo);
			}
			// Return the overall result.
			return bestValue;
			
		}else //if(maximisingPlayer = false)
		{
			// If it is a MIN node...
			// Debug purposes.
			System.out.println("Minimising Player:"); 
			// An experiment
			Tree<String[][]> bestMove = null;
			// Initially positive infinity.
			double bestValue = Double.POSITIVE_INFINITY;
			// Generate the children - the new states will be created in the method below. This corresponds to the MIN Nodes which will create MAX nodes.
			// createChildren(passNode, "1", "2");
			createChildren(passNode, opponentNo, playerNo);
			// Grab the children of the node passed in.
			ArrayList<Tree<String[][]>> children = passNode.children();
			
			if(children.size() > 0)
			{
				// For each child of the (parent) node
				for(Tree<String[][]> child : children)
				{
					// A recursive call that will eventually assign the result of that call into 'value'.
					double value = minimax(child, depth-1, true, playerNo, opponentNo);
					// Debug purposes.
					System.out.print("min(" + bestValue + ", ");
					// If the new value obtained is smaller than the previous bestValue, update the 'bestValue' with the new value.
					// bestValue = Math.min(bestValue, value);
					
					if(value < bestValue)
					{
						bestValue = value;
						// Store the best move... I hope. Oh, shit, I think I am passing in the wrong node.
						// An experiment. using 'passNode' always picks the last moveable piece in the tree.		
						if(passNode.isRoot())
						{
							// Debug purposes.
							System.out.println("This is the root node within in the minimax recursive stack and here are the contents of one child from root:");
							// Print the board, yup.
							printCheckersBoard(child.getValue());
							// Store the greatest move.
							greatestMove = child;	
						}	
					}
					// Debug purposes.
					System.out.println(value + ") is " + bestValue);
				}
			}else
			{
				// Debug purposes. This is for the purpose of when the bot has only one piece left on the board, and it's cornered (about to be captured.)
				// When it is MIN's turn and assuming a final capture happened beforehand (at MAX), Min would have no pieces to move, resulting in no
				// children created so, because the game would be the won by the opponent, there are no more possible moves that can be made so,
				// we evaluate earlier, which seems to be working well.
				System.out.println("We will evaluate passNode earlier than the cut-off depth because it has no children...");
				// An attempt to evaluate the node early.
				bestValue = minimax(passNode, 0, true, playerNo, opponentNo);
			}
			// Return the overall result.
			return bestValue;
		}
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
	// The code for the AI will be written here...
	public void createChildren(Tree<String[][]> passNode, String playerNo, String opponentNo)
	{
		// Gets the checkersboard of the (current) state.
		String[][] theParentState = passNode.getValue();
		
		// Only run this when it is empty...
		if(arrayOfPrevCoordinatesX.size() <= 0)
		{	
			// Prepares the correct string for playerX.
			String strKing = "K" + playerNo;
			
			// Loop through each square of the board...
			for(int row = 0; row < 8;row++)
			{
				for(int column=((row+1)%2); column<8; column+=2)
				{
					// Checks whether there are any pieces neighbouring any enemy pieces.
					if(theParentState[row][column] == playerNo || theParentState[row][column].contains(strKing))
					{
						// checks which player's turn it is before calling the highlightSquares() method within in the method below.
						checkPlayerAndAdd(theParentState, row, column, opponentNo, playerNo);
						
						// Check for adjacent enemies...					
						if(xEnemyAxis.size() > 0)
						{
							// ...There is an adjacent enemy.
							
							// if no enemies have been seen yet, we clear the lists because we can only make captures from here on out...
							if(arrayOfEnemyCoordinatesX.size() <= 0)
							{
								// Clear the master ArrayList.
								clearMasterLists();
								// Add the coordinates to the master ArrayLists
								addToMasterLists(xPrevAxis, yPrevAxis, xEnemyAxis, yEnemyAxis);	
								// Then clear the standard ArrayLists and repeat.
								clearHelperArrays();
								
							}else if(arrayOfEnemyCoordinatesX.size() > 0)
							{
								// If there are exisiting enemies that other pieces are already adjacent to, we simply add our newly found coordinates to the list.
								// Appends the coordinates to the master ArrayLists
								addToMasterLists(xPrevAxis, yPrevAxis, xEnemyAxis, yEnemyAxis);	
								// Then clear the standard ArrayLists and repeat.
								clearHelperArrays();
							}
						}
						else if(xPrevAxis.size() > 0 && arrayOfEnemyCoordinatesX.size() <= 0) 
						{
							// if we have not seen any enemies adjacent prior to this point, and if we can legitmately move the piece, we add its coordinates...
							// ...To the master ArrayLists (i.e. a standard move).
							addToMasterLists(xPrevAxis, yPrevAxis, xEnemyAxis, yEnemyAxis);	
							// Clear the standard ArrayLists.
							clearHelperArrays();
						}	
					}// else - A piece has not been seen at this location...	
				}
			}
			
			// Now, the nodes will be created here...
			for(int m = 0;m < arrayOfPrevCoordinatesX.size();m++)
			{
				// Grab the coordinates of the highlighted squares and the enemy pieces, if there are any enemies. 
				ArrayList<Integer> autoPrevX = arrayOfPrevCoordinatesX.get(m);
				ArrayList<Integer> autoPrevY = arrayOfPrevCoordinatesY.get(m);
				// This will be dynamically initalised shortly...
				ArrayList<Integer> autoEnemyX;
				ArrayList<Integer> autoEnemyY;
				
				// Using '==' instead of 'greater than' works better. No IndexOutOfBoundExceptions 
				if(arrayOfEnemyCoordinatesX.size() == arrayOfPrevCoordinatesX.size())
				{
					autoEnemyX = arrayOfEnemyCoordinatesX.get(m);
					autoEnemyY = arrayOfEnemyCoordinatesY.get(m);
				}
				else
				{
					// We will create a autoEnemyX/Y ArrayList and pass it in later but, the movePiece() function will not use it.
					autoEnemyX = new ArrayList<Integer>();
					autoEnemyY = new ArrayList<Integer>();
				}
				
				for(int eachSquare = 1;eachSquare < autoPrevX.size();eachSquare++)
				{
					// Create a state for each move preserving the original state.
					String[][] newLocationState = new String[8][8];
					// Copy the contents of the parent state into 'newLocationState', preserving the parent state's original contents ;)
					duplicateArray(theParentState, newLocationState);
					
					// This section is where each move made, it will then create a new state (node).
					int xAxisOfDest = autoPrevX.get(eachSquare).intValue();
					int yAxisOfDest = autoPrevY.get(eachSquare).intValue();
					
					// This will create the state with the potential move.
					movePiece(newLocationState, xAxisOfDest, yAxisOfDest, autoPrevX, autoPrevY, autoEnemyX, autoEnemyY, playerNo, opponentNo, true);
					
					
					// We only check for an consecutive capture if the piece previously made a capture, and if it did, it should also not be a newly
					// transformed king... 
					if(autoEnemyX.size() > 0 && isNewKing == false)
					{
						// We will use this later to check whether the piece at the new location is adjacent to another enemy.
						highlightSquares(newLocationState, xAxisOfDest, yAxisOfDest, opponentNo, playerNo);
						// We will clone the result, and immediately clear the x/yPrevAxis ArrayLists. This will keep the global ArrayLists preserved.
						ArrayList<Integer> nextPrevAxisX = (ArrayList<Integer>) xPrevAxis.clone();
						ArrayList<Integer> nextPrevAxisY = (ArrayList<Integer>) yPrevAxis.clone();
						ArrayList<Integer> nextEnemyAxisX = (ArrayList<Integer>) xEnemyAxis.clone();
						ArrayList<Integer> nextEnemyAxisY = (ArrayList<Integer>) yEnemyAxis.clone();
						// clear the helper arrays.
						clearHelperArrays();
						
						// If there is an enemy adjacent to the piece at the new location, we will recursively check...
						// for consecutive captures from the current ocation...
						if(nextEnemyAxisX.size() > 0)
						{
							// Debug purposes...
							/*if(nextEnemyAxisX.size() > 1)
							{
								System.out.println("There are two options for the consecutive capture! Take 4!");
							}*/
							
							// Recursively check for consecutive captures, and adds the states to 'passNode'.
							consecutiveCaptures(passNode, newLocationState, true, playerNo, opponentNo, nextPrevAxisX, nextPrevAxisY, nextEnemyAxisX, nextEnemyAxisY);
							
						}else{
							
							// It was just a single capture we will add the state to the passNode...
							// We add the state to passNode, making it a child of 'passNode'
							passNode.addChild(new Tree(newLocationState));
							// Increment the size of the tree by 1.
							sizeOfTree++;	
						}
					}else{
						
						// It was just a standard move.
						// We add the state to passNode, making it a child of 'passNode'
						passNode.addChild(new Tree(newLocationState));
						// Increment the size of the tree by 1.
						sizeOfTree++;	
					}
				}
			}
			// Clears the Master ArrayLists.
			clearMasterLists();			
		}	// End of createChildren() method (except for consecutive captures)
	}
	public void consecutiveCaptures(Tree<String[][]> passNode, String[][] passState, boolean isAdjacent, String playerNo, String opponentNo,
																	ArrayList<Integer> passPrevX, ArrayList<Integer> passPrevY, ArrayList<Integer> passEnemyX, ArrayList<Integer> passEnemyY)
	{
		// A recursive method checks for consecutive captures.
		
		// Base case.
		if(isAdjacent == false){
			
			// Clear the helperArrays
			clearHelperArrays();
			// Add the state to the tree.
			passNode.addChild(new Tree(passState));
			// Increment the size of the tree too.
			sizeOfTree++;
		}
		else{ // technically if(isAdjacent == true) 
			
			// Holds the number of enemies adjacent for each function call...
			int noOfEnemies = passEnemyX.size();
			
			// Loop through...
			for(int e = 0; e < noOfEnemies; e++)
			{
				// This will shortly hold a copy of the currentState. 
				String[][] copyOfCurrentState = new String[8][8];
				// Copy the contents of the state passed into this function using the function below.
				duplicateArray(passState, copyOfCurrentState);
				
				// We obtain destinationX/Y from the copied ArrayLists.
				int destinationX = passPrevX.get(e+1).intValue(); int destinationY = passPrevY.get(e+1).intValue();
				// Move the piece (i.e. perform the capture).
				movePiece(copyOfCurrentState, destinationX, destinationY, passPrevX, passPrevY, passEnemyX, passEnemyY, playerNo, opponentNo, true);
				
				// Update the new positon of the piece - This is mainly for clarity in reading.
				int positionX = destinationX; int positionY = destinationY;
				// Clear the orginal helper arrays.
				clearHelperArrays();
				// Now, we will use this method to generate the helper ArrayLists that we will later use to check whether there any adjacent enemies
				// at the new location.
				highlightSquares(copyOfCurrentState, positionX, positionY, opponentNo, playerNo);
				// We will clone the result, and immediately clear the x/yPrevAxis ArrayLists.
				ArrayList<Integer> nextPrevAxisX = (ArrayList<Integer>) xPrevAxis.clone();
				ArrayList<Integer> nextPrevAxisY = (ArrayList<Integer>) yPrevAxis.clone();
				ArrayList<Integer> nextEnemyAxisX = (ArrayList<Integer>) xEnemyAxis.clone();
				ArrayList<Integer> nextEnemyAxisY = (ArrayList<Integer>) yEnemyAxis.clone();
				// clear the helper arrays.
				clearHelperArrays();
				
				// If the piece that moved to its new location just transformed to a king, we stop here...
				if(isNewKing == true){
				
					// stop here by calling the function again but, it will immediately stop isAdjacent == false.
					consecutiveCaptures(passNode, copyOfCurrentState, false, playerNo, opponentNo, nextPrevAxisX, nextPrevAxisY, nextEnemyAxisX, nextEnemyAxisY);
					// We will make this loop the last iteration, even though it is not needed. I say it is not needed because in order for
					// it to become a king, a square on the last row must be empty so, at most, it will be adjacent to only one enemy piece
					// upon its transformation ;)
					e = noOfEnemies; 
				}
				else if(nextEnemyAxisX.size() > 0){
					
					// Perform the recursive call to repeat this process again - 'true' because it is adjacent to an enemy at the new location.
					consecutiveCaptures(passNode, copyOfCurrentState, true, playerNo, opponentNo, nextPrevAxisX, nextPrevAxisY, nextEnemyAxisX, nextEnemyAxisY);
					
				}else{
					
					// to the tree... I hope. - 'false' because the piece at the new location is not adjacent to an enemy piece.
					consecutiveCaptures(passNode, copyOfCurrentState, false, playerNo, opponentNo, nextPrevAxisX, nextPrevAxisY, nextEnemyAxisX, nextEnemyAxisY);
				}
			}
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
	public class MinimaxThread extends Thread
	{
		// Member variables.
		public String playerNo, opponentNo;
		
		// Constructor
		public MinimaxThread(String passPlayerNo, String passOpponentNo)
		{
			// Initialise the member variables.
			playerNo = passPlayerNo;
			opponentNo = passOpponentNo;
		}
		public void run()
		{		
			// Run the Minimax algorithm within here.
			
			// This will later hold the node that yields the best value for alphabeta/minimax.
			greatestMove = new Tree(new String[8][8]);
			// Holds the result of the alphabeta/minimax operation...
			double heuristicValue = 0;
			
			// This ensure that the first initial move for each bot is randomly generated.
			if(firstMoveCount < 2){
				
				// Randomly move the piece...
				// Increment the count. This will run only twice (first move for each bot).
				firstMoveCount++;
				// Create the nodes...
				createChildren(decisionTree, playerNo, opponentNo);
				// Grab the children.
				ArrayList<Tree<String[][]>> children = decisionTree.children();
				// Create the random generator.
				Random randomGen = new Random();
				// Choose a number between 0 (inclusive) and the size of the decisionTree's children (exclusive). 
				int randomIndex = randomGen.nextInt(children.size());
				// Randomly grab a move and store it is as the greatestMove ;)
				greatestMove = children.get(randomIndex);
				// Debug purposes. Yup, this works, woop, woop.
				System.out.println("The index generated for player " + playerNo + " is " + randomIndex);
				
			}else{ //if - We will run alphabeta as normal.
				
				// Run the algorithm and store the value.
				//heuristicValue = minimax(decisionTree, 3, true, playerNo, opponentNo);
				heuristicValue = alphabeta(decisionTree, 3, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, true, playerNo, opponentNo);
			}
			
			// Grab the state of the greatestMove node.
			String[][] greatestMoveState = greatestMove.getValue();
			// Debug purposes - it prints out 292 nodes for a depth of 3, which is correct and I will assume that the correct states are being created.
			System.out.println("Player " + playerNo + " - the size of the decisionTree after the minimax operation is " + sizeOfTree + " and the greatest move is ");		
			// Debug purposes.
			printCheckersBoard(greatestMoveState); // So far, it grabs the states at depth 3 (which are the states at the cut-off depth). 
			System.out.println("The overall heuristic value of the minimax algorithm is: " + heuristicValue);
			
			// Debug.
			System.out.println("The contents of strCheckersBoard[][] before the Bot moved its piece is:");
			printCheckersBoard(strCheckersBoard);
			
			// Because we will be repainting the Views (i.e. the checkers pieces, checkersboard, etc.), we must do it on the UI thread.
			// All UI repainting must be done on the UI thread, which it will do now...
			
			runOnUiThread(new Runnable()
			{
				public void run()
				{
					// Debug purposes.
					System.out.println("Minimax is done. The bot will perform its move...");
					// Grabs the state of the greatestMove node.
					String[][] greatestMoveState = greatestMove.getValue();
					// Copy the contents of the greatest move into the current strCheckersboards (i.e. performs the move);
					duplicateArray(greatestMoveState, strCheckersBoard);
					// Re-paints the board based on the AI's greatest move... Which would visually appear as if the AI made his move.
					SpectateGame.populateBoard(strCheckersBoard);
					
					// hand over its turn to the opponent (i.e. the human).
					playerOneTurn = !playerOneTurn; 
					// Debug purposes.
					System.out.println("The contents of strCheckersBoard[][] after the Bot moved its piece is:");
					printCheckersBoard(strCheckersBoard);
					// Updates variables that hold the number of pieces each player has on the board.
					updateNoOfPieces(strCheckersBoard);
					
					// An experiment...
					
					// Get rid of the IF statement above if it does not work, and renable the code below :)
					// We pass in false because the turn we are handing over to, is a human so, yeah. This method will also print out the correct player
					// information, hides the loading wheel, and whatnot.
					switchBot(playerNo, opponentNo);
				}
			});
		}
	}
	public void computerTurn(String playerNo, String opponentNo)
	{
		// This will later hold a copy of the current state.
		String[][] currentState = new String[8][8];
		// Copy the contents of the checkers board into a temporary array which corresponds to the current state.
		duplicateArray(strCheckersBoard, currentState);
		// Using a different Tree data structure - I am assuming this initialises the tree with the current state as the root.
		decisionTree = new Tree(currentState);
		// This will hold the number of nodes.
		sizeOfTree = 1;

		// The tree will be generated within the MiniMaxThread	
		// Initialise the greatestMove node.
		greatestMove = new Tree(new String[8][8]);
		// Making the actual object final does not result in non-mutable variables used within the object ;)
		final MinimaxThread minimaxThread = new MinimaxThread(playerNo, opponentNo);
		// Load the minimax algorithm in a new thread.
		minimaxThread.start();
		
	}
	// I declared this method as synchronised hoping the code runs one at a time otherwise, if I clicked two buttons at the same time, I have a hunch
	// that it may cause a series of problems.
	public synchronized void onClick(View v) 
	{
		// Initially, isPaused will be false but, when we press the start button for the first time, it will change the value to true...
		if(isPaused != true)
		{
			// Rename the button to 'pause'.
			startBtn.setText("Pause");
			// Also make isPaused true so, when this button is clicked again, it will not run this statement ;)
			isPaused = true;
			
			if(playerOneTurn == true)
			{
				// Call computerTurn("1", "2") (i.e player one goes first.)
				computerTurn("1", "2");			
			}
			else // if it's player two's turn...
			{
				// Call computerTurn("2", "1") (i.e player two goes first.)
				computerTurn("2", "1");
				
			}	
		}else
		{
			// If the paused button is clicked...
			// Rename the button to resume...
			startBtn.setText("Resume");
			// We change isPaused back to false so, when we call this method (i.e. click the button again), it will run the code as normal :)
			isPaused = false;
			// Disable the loading wheel.
			loadingWheel.setVisibility(View.INVISIBLE);
			// Display some information.
			loadingInfo.setText("The game has \nbeen paused.");
		}
		
		
		
		/*System.out.println("Start spectating...");
		
		// We do not even need that playerOneTurn boolean variable anymore.
		// I need a better if statement though. Oh, I know, just disable the button after it is pressed, aha.
		if(true)
		{
			// The button will not be clickable throughout the entire match ;)
			startBtn.setClickable(false);
			// We will initially call computerTurn("1", "2") (i.e player one goes first.)
			computerTurn("1", "2");
			
			
		}*/
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
	// I probably should modify this section for spectate mode.
	public void switchBot(String playerNo, String opponentNo)
	{
		// If the opponent still has pieces on the board...
		if(getNoOfPieces(opponentNo) > 0)
		{
			// We could check if the opponent (in this case, the human) has trapped pieces.
			boolean isTrapped = isTrapped(strCheckersBoard, opponentNo, playerNo);
			
			if(isTrapped == true)
			{
				// The bot wins... Since, the opponent cannot make any legitimate moves. 16 characters per line break! Well, that did it and trapped code works
				loadingInfo.setText("Bot_" + playerNo + " says, \n\"Well, it looks \nlike you're \nstuck. Better \nluck next time.\"");
				// Disable the loading wheel.
				loadingWheel.setVisibility(View.INVISIBLE);
				// Display it too.
				playerInfo.setText("Game Over!\nPlayer " + playerNo + " is\nthe Winner!");
				// Display the image of the winner... blah de blah blah.
				setPlayerImage(playerNo);
			}
			else
			{
				// Game is still going...
				// Opponent info will go here...
				setPlayerImage(opponentNo);
				// Display's the opponent's information...
				playerInfo.setText("Player " + opponentNo + "'s Turn");
				// This is for clarity. This is all for not wanting to use more than one button, aha.
				boolean isNotPaused = isPaused;
				
				// The game is not paused so, we proceed as normal...
				if(isNotPaused == true){
					
					// -- computerTurn will be called here, using the delayForBot() method. --//
					// We will show the wheel to indicate it is the AI's turn.
					loadingWheel.setVisibility(View.VISIBLE);
					// Display the Bot's information...
					loadingInfo.setText("Bot_" + opponentNo + " is making \nits move...");
					// Adds a delay before we actually hand over the turn to the bot. This gives the computer time to repaint the UI in time...
					// In this case, opponentNo == "2" assuming the bot goes first.
					// Made for clarity.
					int duration = speed; int interval = speed;
					delayForBot(duration, interval, playerNo, opponentNo);
					//e.g. delayForBot(2000, 1000, playerNo, opponentNo);
					// ___ End of section where we hand over the turn to the bot. ___ //
				}
			}
		}
		else
		{
			// The bot wins because it captured all the opponent's pieces...
			loadingInfo.setText("Bot_" + playerNo + " says, \n\"Mission \nAccomplished.\"");
			// Disable the loading wheel.
			loadingWheel.setVisibility(View.INVISIBLE);
			// Display it on the player information too.
			playerInfo.setText("Game Over!\nBot_" + playerNo + " is\nthe Winner!");
			// Display the image of the winner... blah de blah blah.
			setPlayerImage(playerNo);	
		} 
	}
	public void delayForBot(long milliseconds, long interval, String passPlayerNo, String passOpponentNo)
	{
		// This method seems to be working fine.
		// We need these variables final because they will later be passed into an inner class.
		final String playerNo = passPlayerNo;
		final String opponentNo = passOpponentNo;
		// There are no trapped pieces so, we will let the opponent proceed as normal.
		// It will wait 200 milliseconds before the AI decides to move.
		new CountDownTimer(milliseconds, interval)
		{
			public void onTick(long millisUntilFinished)
			{
				// Debug purposes.
				// System.out.println("milliseconds remaining: " + millisUntilFinished);
			}
			public void onFinish()
			{
				// It's time to call computerTurn();
				computerTurn(opponentNo, playerNo);									
			}
		}.start();
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
}