package uk.ac.kcl.www;

import android.os.CountDownTimer;
import android.app.Activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import java.lang.Thread;
import java.lang.Runnable;

import android.app.Activity;
import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;

import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import android.widget.GridLayout;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.view.LayoutInflater;

import 	android.graphics.drawable.ColorDrawable;

import android.widget.ProgressBar;


public class SinglePlayerEvents extends Activity implements View.OnClickListener
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
	public SinglePlayerEvents(View[][] passSquares, ImageView[][] passImgSquares, String[][] passCheckersBoard, TextView passTextView, TextView passLoadingInfo, ProgressBar passLoadingWheel, ImageView passPlayerImage)
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
		
		// Keeps track of the number of pieces and...
		// ...Initially and dynamically determines the number of pieces for each player...
		updateNoOfPieces(strCheckersBoard);
	}
	
	public double evaluateNode(Tree<String[][]> passNode, String playerNo, String opponentNo)
	{
		// Debug purposes.
		System.out.println("Minimax got called at the cut-off so, we will perform the evaluation on this node."); // Well, the recursive call gets called.
		// Evaluation will go here.
		String[][] state = passNode.getValue();
		// The number of pieces on the board that player one (the human) has ...
		int noOfPlayerOne = 0;
		// The number of pieces on the board that player two (the computer) has...
		int noOfPlayerTwo = 0;
		// 3. calculate how many pieces are close to becoming kings. The total number of pieces in 0,1,2 for player 1 and 5,6,&7 for player 2.
		double playerOneOffense = 0, playerTwoOffense = 0;
		// 4. A sum total of protected pieces so, count number of pieces that have a neighbour of itself, and exclude the piece itself from the sum.
		double playerOneDefense = 0, playerTwoDefense = 0;
		
		
		// Determine whether it is an enemy capture (and also checks for consecutive captures).
		// In a sense of evaluating the states at the cut-off depth, it seems to be working well.
		// I will disable this for now.
		//determinePieceAndMove(passNode, state, playerNo, opponentNo, true);
		// After the CPU has performed consecutive captures, for even better accuracy, I should call this method again but, from the perspective of the opponent. I shall implement soon... I hope.
		
		// End of checking whether the state was a state where consecutive captures could be made.
		
		for(int row = 0;row < 8; row++)
		{
			for(int column=((row+1)%2); column<8; column+=2)
			{
				// 1. Counts the number of player two's (CPU) pieces to player one's (Human) piece,
				// and also takes the number of kings into consideration.
				// .equals("1") was what I had orginally but, that was wrong. contains("1") works for kings too ;)
				if(state[row][column].contains("1"))
				{
					// If it is a standard piece, increase the heuristic.
					noOfPlayerOne++;
					
					if(state[row][column].contains("K1"))
					{
						// If it is also a king, increase the heueristic even more...
						noOfPlayerOne++;
					}
					// If the piece is close to becoming a king.
					if(row >= 0 && row <= 2)
					{
						// Increase the heuristic value.
						playerOneOffense++;
					}
					
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
					// If the piece is close to becoming a king.
					if(row >= 5 && row <= 7)
					{
						// Increase the heuristic value.
						playerTwoOffense++;
					}
				}
			}
			// 4. A sum total of protected pieces so, count number of pieces that have a neighbour of itself, and exclude the piece itself from the sum.
			if(state[row][column].contains("1"))
			{
				// Player one...
				if(row <= 1 && row <= 7 && column >= 1 && column <= 6 && state[row-1][column-1].contains("1") && state[row-1][column+1].contains("1"))
				{
					// Increase the number of pieces that are well protected for player one.
					playerOneDefense = playerOneDefense + 0.20;
				}
				// 5. Defense against kings. I probably should change row >= 0 to 1 instead but, I will look into this.
				if(row >= 0 && row <= 6 && column >= 1 && column <= 6 && state[row+1][column-1].contains("1") && state[row+1][column+1].contains("1"))
				{
					// Increase the number of pieces that are well protected for player one.
					playerOneDefense = playerOneDefense + 0.10;
				}
				// 6. Defense - pieces on the side of the board.
				if(column == 0 || column == 7)
				{
					// Increase the number of pieces that are well protected for player one.
					playerOneDefense = playerOneDefense + 0.20;
				}
			}
			if(state[row][column].contains("2"))
			{
				// Player two...
				if(row >= 0 && row <= 6 && column >= 1 && column <= 6 && state[row+1][column-1].contains("2") && state[row+1][column+1].contains("2"))
				{
					// Increase the number of pieces that are well protected for player two.
					playerTwoDefense = playerTwoDefense + 0.20;
				}
				// 5. Defense against kings. 
				if(row >= 1 && row <= 7 && column >= 1 && column <= 6 && state[row-1][column-1].contains("2") && state[row-1][column+1].contains("2"))
				{
					// Increase the number of pieces that are well protected for player two.
					playerTwoDefense = playerTwoDefense + 0.10;
				}
				// 6. Defense - pieces on the side of the board.
				if(column == 0 || column == 7)
				{
					// Increase the number of pieces that are well protected for player one.
					playerTwoDefense = playerTwoDefense + 0.20;
				}
			}
		}
			
		System.out.println("The number of pieces left for player one is " + noOfPlayerOne);
		System.out.println("The number of pieces left for player two is " + noOfPlayerTwo);
		// Debug purposes.
		// printCheckersBoard(state);
		
		// Evaluate the difference and store it.
		double result = noOfPlayerTwo - noOfPlayerOne;
		// Debug purposes.
		System.out.println("The result of the leaf node is " + result);
		// Return the heuristic value.
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
			// Debug purposes.
			System.out.println("Maximising Player:"); 
			// If it is a MAX node...
			// An experiment
			Tree<String[][]> bestMove = null;
			// Initially negative infinity.
			double bestValue = alpha;
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
					double value = alphabeta(child, depth-1, alpha, beta, false, playerNo, opponentNo);
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
							System.out.println("This is the root node within in the alpha-beta recursive stack and here are the contents of one child from root:");
							// Print the board, yup.
							printCheckersBoard(child.getValue());
							// Store the greatest move.
							greatestMove = child;	
						}	
					}
					// Debug purposes.
					System.out.println(value + ") is " + bestValue);
					if(bestValue > alpha)
					{
						// I also forgot to change it to alpha = bestValue from alpha = value
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
				// Debug purposes. This is for the purpose of when the bot has only one piece left on the board, and it's cornered (about to be captured.)
				System.out.println("We will evaluate passNode earlier than the cut-off depth because it has no children...");
				// An attempt to evaluate the node early.
				bestValue = alphabeta(passNode, 0, alpha, beta, false, playerNo, opponentNo);
			}
			// --- An experiment --- //
			
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
			double bestValue = beta;
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
					double value = alphabeta(child, depth-1, alpha, beta, true, playerNo, opponentNo);
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
							System.out.println("This is the root node within in the alpha-beta recursive stack and here are the contents of one child from root:");
							// Print the board, yup.
							printCheckersBoard(child.getValue());
							// Store the greatest move.
							greatestMove = child;	
						}	
					}
					if(bestValue < beta)
					{
						beta = bestValue;
					}
					// Prune...
					if(beta <= alpha)
					{
						break;
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
				bestValue = alphabeta(passNode, 0, alpha, beta, true, playerNo, opponentNo);
			}
			// Return the overall result.
			return bestValue;
		}
	}
	// --- End of Alpha-Beta Experiment -- //
	public double minimax(Tree<String[][]> passNode, int depth, boolean maximisingPlayer, String playerNo, String opponentNo)
	{
		// I don't know whether I declare bestValue here or within the if statement. I will try doing it within in the if statements.
		// Debug purposes.
		
		// I got rid of the || passNode.isLeaf() part because we are now generating the states within the method instead.
		// Removing it caused problems so, I added it back in with an extra condition that checks if passNode.isRoot();
		// Adding it back in does not create the right number of state nodes, I almost had a panic attack man because the AI bot
		// was not making logical decisions. It was literally picking the first piece that can move. i.e. the first immediate child of the root.
		// NEXT TIME, I SHOULD TRY || (passNode.isLeaf() && passNode != decisionTree)
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
			
			// --- An experiment --- //
			
			/*
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
			}*/
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
			
			/*// For each child of the (parent) node
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
			}*/
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
			// Debug purposes.
			// System.out.println("I crashed at row=" + row + "/column=" + column + " and the current player is player " + playerNo);
			highlightSquares(passStrCheckersBoard, row, column, opponentNo, playerNo);
		}
		else if(playerNo == "2")
		{
			// Debug purposes.
			// System.out.println("I crashed at row=" + row + "/column=" + column + " and the current player is player " + playerNo);
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
			// Debug purposes.
			int notSeenCount = 0, seenCount = 0;
			// Prepares the correct string for playerX.
			String strKing = "K" + playerNo;
			
			
			for(int row = 0; row < 8;row++)
			{
				for(int column=((row+1)%2); column<8; column+=2)
				{
					// Checks whether there are any pieces neighbouring any enemy pieces.
					if(theParentState[row][column] == playerNo || theParentState[row][column].contains(strKing))
					{
						
						// ---- I probably should enclose this in a method later on. Did it. Seems okay :) ----- //
						// checks which player's turn it is before calling the highlightSquares() method within in the method below.
						checkPlayerAndAdd(theParentState, row, column, opponentNo, playerNo);
						// Debug purposes.
						seenCount++;
						
						if(xEnemyAxis.size() > 0)
						{
							// There is an adjacent enemy.
							// isEnemyAdjacent = true;
							
							// I modifed the addToMasterLists() method so, that it only adds x/yEnemyAxis to the corresponding ArrayList only when...
							// the ArrayList x/yEnemyAxis has data - i.e. an enemy. And everything seems to be working fine with the new modification...
							// of the method.
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
								// Debug purposes.
								// System.out.println("else if(arrayOfEnemyCoordinatesX.size() > 0) is true");
								// Appends the coordinates to the master ArrayLists
								addToMasterLists(xPrevAxis, yPrevAxis, xEnemyAxis, yEnemyAxis);	
								// Then clear the standard ArrayLists and repeat.
								clearHelperArrays();
							}
						}
						else if(xPrevAxis.size() > 0 && arrayOfEnemyCoordinatesX.size() <= 0) 
						{
							// Missing the arrayOfEnemyCoordinatesX.size() <= 0 condition so, if we didn't see another enemy, this would get run anyway, which we don't.
							// Debug purposes.
							// System.out.println("else if(xPrevAxis.size() >0) is true");
							// if no enemies have been seen yet, then this section corresponds to a normal move that will be made later on.
							// Also, it also needs to be a piece that can actually make a move whereas before, I forgot to add that validation.
							// Add the coordinates to the master ArrayLists
							addToMasterLists(xPrevAxis, yPrevAxis, xEnemyAxis, yEnemyAxis);	
							// Clear the standard ArrayLists.
							clearHelperArrays();
						}	
					}else
					{
						// Debug purposes.
						notSeenCount++;
					}
				}
			}
			
			// Debug purposes.
			// System.out.println("The number of pieces that we have not seen for player " + playerNo + " is " + notSeenCount);
			// Debug purposes.
			// System.out.println("The number of pieces that we have seen for player " + playerNo + " is " + seenCount);
			
			// Now, we can create the nodes here, I think.
		
			for(int e = 0;e < arrayOfPrevCoordinatesX.size();e++)
			{
				// Grab the coordinates of the highlighted squares and the enemy pieces, if there are any enemies. 
				ArrayList<Integer> autoPrevX = arrayOfPrevCoordinatesX.get(e);
				ArrayList<Integer> autoPrevY = arrayOfPrevCoordinatesY.get(e);
				ArrayList<Integer> autoEnemyX;
				ArrayList<Integer> autoEnemyY;
				
				// Using '==' instead of 'greater than' works better. No IndexOutOfBoundExceptions 
				if(arrayOfEnemyCoordinatesX.size() == arrayOfPrevCoordinatesX.size())
				{
					autoEnemyX = arrayOfEnemyCoordinatesX.get(e);
					autoEnemyY = arrayOfEnemyCoordinatesY.get(e);
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
					// Grabs the String[][] representation of the parent state.
					// String[][] theParentState = passNode.getValue();
					// Copy the contents of the state (possibly the current state, or a level up) into the new array, which we will modify to
					// ...Create a new state. Okay, this works fine. Yay :)
					duplicateArray(theParentState, newLocationState);
					
					// Debug purposes. I got an output of 5 or something. I think I need to immediately duplicate x/yEnemyAxis array lists or something...
					// System.out.println("The piece that caused this error is " + newLocationState[autoPrevX.get(0).intValue()][autoPrevY.get(0).intValue()]);
					
					// This section is where each move made, it will then create a new state (node).
					int xAxisOfDest = autoPrevX.get(eachSquare).intValue();
					int yAxisOfDest = autoPrevY.get(eachSquare).intValue();
					
					// The experiment was a success, me thinks...
					// Now, when I initially move the leftmost black piece to the right, the AI then generates 130 nodes for its turn, which is correct.
					// When I had the error with the consecutiveCaptures() method, it would give 160 nodes for the AI's turn if I made the same move so, it seems okay now.
					// I shall test this section rigoursly.	
					
					// This will create the state with the potential move.
					movePiece(newLocationState, xAxisOfDest, yAxisOfDest, autoPrevX, autoPrevY, autoEnemyX, autoEnemyY, playerNo, opponentNo, true);
					
					
					// We only check for an consecutive capture if the piece previously made a capture, and if it did, it also should not be a newly
					// transformed king... This new version seems to be working fine, I have also tested for consecutive captures for when the piece
					// has more than one option. Instead of cloning these ArrayLists regardless of a capture or not, I have now made it so, that
					// it only does when it has previously captured an enemy. This should theorectically speed things up too :)
					if(autoEnemyX.size() > 0 && isNewKing == false)
					{
						// We will use this later to check whether the piece at the new location is adjacent to another enemy.
						highlightSquares(newLocationState, xAxisOfDest, yAxisOfDest, opponentNo, playerNo);
						// We will clone the result, and immediately clear the x/yPrevAxis ArrayLists. So, hopefully, everything is preserved.
						ArrayList<Integer> nextPrevAxisX = (ArrayList<Integer>) xPrevAxis.clone();
						ArrayList<Integer> nextPrevAxisY = (ArrayList<Integer>) yPrevAxis.clone();
						ArrayList<Integer> nextEnemyAxisX = (ArrayList<Integer>) xEnemyAxis.clone();
						ArrayList<Integer> nextEnemyAxisY = (ArrayList<Integer>) yEnemyAxis.clone();
						// clear the helper arrays.
						clearHelperArrays();
						
						// If there is an enemy adjacent to the piece at the new location, we will recursively check for consecutive captures from the...
						// ...Current ocation...
						if(nextEnemyAxisX.size() > 0)
						{
							// Debug purposes...
							if(nextEnemyAxisX.size() > 1)
							{
								System.out.println("There are two options for the consecutive capture! Take 4!");
							}
							
							// Debug purposes.
							System.out.println("Here is the root state before any consecutive (or standard) capture were made (and its coordinates:");
							printCheckersBoard(passNode.getValue());
							// Debug purposes.
							System.out.println("...Followed by the state of the first capture performed by player " + playerNo + " and its coordinates");
							printCheckersBoard(newLocationState);
							// This means there is an adjacent enemy at the new location so, we will recursively check for consecutive captures after the capture
							ArrayList<Tree<String[][]>> successors = passNode.children();
							System.out.println("Initial consecutiveCaptures() method call and the size of passNode before call is " + successors.size());
							// which will initially be done in the function below. It will also add the state(s) to 'passNode' :)
							consecutiveCaptures(passNode, newLocationState, true, playerNo, opponentNo, nextPrevAxisX, nextPrevAxisY, nextEnemyAxisX, nextEnemyAxisY);
							// Debug purposes. For some reason newLocationState has the contents of the root node (not the move peformed)
							System.out.println("... Followed by each consecutive capture performed by player " + playerNo);
							ArrayList<Tree<String[][]>> children = passNode.children();
							for(int c = 0; c < children.size();c++)
							{
								System.out.println("Take 4 - Consecutive capture - option " + c + ":");
								printCheckersBoard(children.get(c).getValue());
							}
							
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
					
					/*
					// Basically means, if there was a previous capture, and its adjacent to another enemy piece at its new location, run this code...
					if(nextEnemyAxisX.size() > 0 && autoEnemyX.size() > 0 && isNewKing == false){
					
						if(nextEnemyAxisX.size() > 1)
						{
							System.out.println("There are two options for the consecutive capture! Take 3!");
						}
						
						// Debug purposes.
						System.out.println("Here is the root state before any consecutive (or standard) capture were made (and its coordinates:");
						printCheckersBoard(passNode.getValue());
						// Debug purposes.
						System.out.println("...Followed by the state of the first capture performed by player " + playerNo + " and its coordinates");
						printCheckersBoard(newLocationState);
						// This means there is an adjacent enemy at the new location so, we will recursively check for consecutive captures after the capture
						ArrayList<Tree<String[][]>> successors = passNode.children();
						System.out.println("Initial consecutiveCaptures() method call and the size of passNode before call is " + successors.size());
						// which will initially be done in the function below. It will also add the state(s) to 'passNode' :)
						consecutiveCaptures(passNode, newLocationState, true, playerNo, opponentNo, nextPrevAxisX, nextPrevAxisY, nextEnemyAxisX, nextEnemyAxisY);
						// Debug purposes. For some reason newLocationState has the contents of the root node (not the move peformed)
						System.out.println("... Followed by each consecutive capture performed by player " + playerNo);
						ArrayList<Tree<String[][]>> children = passNode.children();
						for(int c = 0; c < children.size();c++)
						{
							System.out.println("Take 3 - Consecutive capture - option " + c + ":");
							printCheckersBoard(children.get(c).getValue());
						}
						
					}
					else{
						
						// Otherwise, this was either a single capture, or just a standard move played.
						// We add the state to passNode, making it a child of 'passNode'
						passNode.addChild(new Tree(newLocationState));
						// Increment the size of the tree by 1.
						sizeOfTree++;	
					}*/
				}
			}
			// Clears the Master ArrayLists.
			clearMasterLists();
			
			// ------- Yup, here dawg ----- //
			
		}	// End of createChildren() method (except for consecutive captures)
	}
	public void consecutiveCaptures(Tree<String[][]> passNode, String[][] passState, boolean isAdjacent, String playerNo, String opponentNo,
																	ArrayList<Integer> passPrevX, ArrayList<Integer> passPrevY, ArrayList<Integer> passEnemyX, ArrayList<Integer> passEnemyY)
	{
		// I realised this section would need to be recursive, which is so long. Okay, after 5 hours of coding, I have finally written the function.
		// Now, I need to test it, and see if it works. Inshallah, it goes okay, ameen.
		
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
			
			// This will not get mutated, whoop... I hope. I might need to move this elsewhere.
			int noOfEnemies = passEnemyX.size();
			// Or, what if I needed to copy the ArrayLists here. Actually, it would make more sense to do it here. Well, commenting this has caused
			// no problems. 
			/*ArrayList<Integer> copyPrevAxisX = (ArrayList<Integer>) passPrevX.clone();
			ArrayList<Integer> copyPrevAxisY = (ArrayList<Integer>) passPrevY.clone();
			ArrayList<Integer> copyEnemyAxisX = (ArrayList<Integer>) passEnemyX.clone();
			ArrayList<Integer> copyEnemyAxisY = (ArrayList<Integer>) passEnemyY.clone();*/
			
			// This will shortly hold a copy of the currentState. I need to PAY CLOSE ATTENTION TO THIS SECTION, MAINLY BECAUSE OF WHERE...
			// DUPLICATEARRAY IS BEING CALLED... ALTHOUGH, IT DOES SEEM TO BE OKAY. Okay,it did cause problems leaving the variable here after
			// getting rid of the copyEnemyAxisY ArrayLists. I moved the declaration into the for-loop, and works okay.
			//String[][] copyOfCurrentState = new String[8][8];
			
			// The number of enemies...
			for(int e = 0; e < noOfEnemies; e++)
			{
				// This will shortly hold a copy of the currentState. I need to PAY CLOSE ATTENTION TO THIS SECTION, MAINLY BECAUSE OF WHERE...
				// DUPLICATEARRAY IS BEING CALLED... ALTHOUGH, IT DOES SEEM TO BE OKAY.
				String[][] copyOfCurrentState = new String[8][8];
				// Copy the contents of the state passed into this function using the function below.
				duplicateArray(passState, copyOfCurrentState);
				// After that, we make a copy of x/yEnemyAxis/PrevAxis ArrayLists...
				System.out.println("Within the consecutiveCaptures() method...");
				System.out.println("The contents of copyOfCurrentState at iteration e = " + e);
				printCheckersBoard(copyOfCurrentState);
				
				// We obtain destinationX/Y from the copied ArrayLists.
				int destinationX = passPrevX.get(e+1).intValue(); int destinationY = passPrevY.get(e+1).intValue();
				// Move the piece (i.e. perform the capture. Shit, I forgot to change 'passState' here. Now, it makes the consecutive moves but, no captures.
				movePiece(copyOfCurrentState, destinationX, destinationY, passPrevX, passPrevY, passEnemyX, passEnemyY, playerNo, opponentNo, true);
				
				System.out.println("The contents of copyOfCurrentState at iteration e= " + e + " after we used the movePiece() method.");
				printCheckersBoard(copyOfCurrentState);
				
				// Update the new positon of the piece
				int positionX = destinationX; int positionY = destinationY;
				// Clear the orginal helper arrays.
				clearHelperArrays();
				// Now, we will use this method to later check if the piece at the new location is adjacent to an enemy piece.
				highlightSquares(copyOfCurrentState, positionX, positionY, opponentNo, playerNo);
				// We will clone the result, and immediately clear the x/yPrevAxis ArrayLists.
				ArrayList<Integer> nextPrevAxisX = (ArrayList<Integer>) xPrevAxis.clone();
				ArrayList<Integer> nextPrevAxisY = (ArrayList<Integer>) yPrevAxis.clone();
				ArrayList<Integer> nextEnemyAxisX = (ArrayList<Integer>) xEnemyAxis.clone();
				ArrayList<Integer> nextEnemyAxisY = (ArrayList<Integer>) yEnemyAxis.clone();
				// clear the helper arrays.
				clearHelperArrays();
				
				if(isNewKing == true){
					
					// Since the piece has been recently transformed into a king, there are no more moves it should make so,
					// stop here by calling the function again but, it will immediately stop isAdjacent == false.
					consecutiveCaptures(passNode, copyOfCurrentState, false, playerNo, opponentNo, nextPrevAxisX, nextPrevAxisY, nextEnemyAxisX, nextEnemyAxisY);
					// We will make this loop the last iteration, even though it is not needed. I say it is not needed because in order for
					//it to become a king, a square on the last row must be empty so, at most, it will be adjacent to only one enemy piece
					//upon its transformation ;)
					e = noOfEnemies; 
				}
				else if(nextEnemyAxisX.size() > 0){
					
					System.out.println("We will perform the recursive call with the 'true' value passed in - isAdjacent = true");
					System.out.println("Here are some details about the ArrayLists we are passing in!");
					System.out.println("The size of nextPrevAxis.size() is " + nextPrevAxisX.size() + " and has " + nextEnemyAxisX.size() + " enemies.");
					
					// Perform the recursive call to repeat this process again - 'true' because it is adjacent to an enemy at the new location.
					consecutiveCaptures(passNode, copyOfCurrentState, true, playerNo, opponentNo, nextPrevAxisX, nextPrevAxisY, nextEnemyAxisX, nextEnemyAxisY);
					
				}else{
					
					System.out.println("We will perform the recursive call with the 'false' value passed in - isAdjacent = false");
					System.out.println("Here are some details about the ArrayLists we are passing in!");
					System.out.println("The size of nextPrevAxis.size() is " + nextPrevAxisX.size() + " and has " + nextEnemyAxisX.size() + " enemies.");
					
					// We will perform the recursive call but, when we call it, the if(isAdjacent == false) will be ran, and then it will add the state
					// to the tree... I hope. - 'false' because the piece at the new location is not adjacent to an enemy piece.
					consecutiveCaptures(passNode, copyOfCurrentState, false, playerNo, opponentNo, nextPrevAxisX, nextPrevAxisY, nextEnemyAxisX, nextEnemyAxisY);
				}
			}
		}
	}
	// --- The working one (well, you know what I mean ) -- //
	/*public void consecutiveCaptures(Tree<String[][]> passNode, String[][] passState, boolean isAdjacent, String playerNo, String opponentNo)
	{
		// I realised this section would need to be recursive, which is so long. Okay, after 5 hours of coding, I have finally written the function.
		// Now, I need to test it, and see if it works. Inshallah, it goes okay, ameen.
		
		// Base case.
		if(isAdjacent == false){
			
			// Clear the helperArrays
			clearHelperArrays();
			// Add the state to the tree.
			passNode.addChild(new Tree(passState));
			// Increment the size of the tree too.
			sizeOfTree++;
		}
		else{ // technically if(xEnemyAxis.size() > 0) 
			
			// I may not even need this condition here but, hey, we shall see. Seems to work well, without it
			//if(xEnemyAxis.size() > 0){
				
				// xEnemyAxis.size() >= 2 will cause some problems. I need to work around this.
				// Actually, we may not even need two if statements but, we shall see...
				
				// This will not get mutated, whoop... I hope. I might need to move this elsewhere.
				int noOfEnemies = xEnemyAxis.size();
				// Or, what if I needed to copy the ArrayLists here. Actually, it would make more sense to do it here.
				ArrayList<Integer> copyPrevAxisX = (ArrayList<Integer>) xPrevAxis.clone();
				ArrayList<Integer> copyPrevAxisY = (ArrayList<Integer>) yPrevAxis.clone();
				ArrayList<Integer> copyEnemyAxisX = (ArrayList<Integer>) xEnemyAxis.clone();
				// The reason behind the recurision method not working, is that I forgot to change the variable name
				// to yEnemyAxis.clone() from xEnemyAxis.clone(). Copy and paste is a bi***, aha.
				ArrayList<Integer> copyEnemyAxisY = (ArrayList<Integer>) yEnemyAxis.clone(); 
				
				// The number of enemies...
				for(int e = 0; e < noOfEnemies; e++)
				{
					// This will hold a copy of the current state.
					String[][] currentState; 	
					// We duplicate the currentState... Somehow. Oh, we duplicate it, and then switch the address or something.
					// we don't even have to switch the address or anything because through the recursion call, the desired state will be added
					// to 'passNode' correctly. Now, it automatically preserves the state regardless of whether
					// there is an option for more than one enemy piece to be captured.
					String[][] copyOfCurrentState = new String[8][8];
					// Copy the contents of the state passed into this function
					duplicateArray(passState, copyOfCurrentState);
					// Now the address of the currentState will point to the duplicate, (hopefully) keeping 'passState' intact.
					currentState = copyOfCurrentState;
					// After that, we make a copy of x/yEnemyAxis/PrevAxis ArrayLists...
					
					// We obtain destinationX/Y from the copied ArrayLists.
					int destinationX = copyPrevAxisX.get(e+1).intValue(); int destinationY = copyPrevAxisY.get(e+1).intValue();
					// Move the piece (i.e. perform the capture. Shit, I forgot to change 'passState' here. Now, it makes the consecutive moves but, no captures.
					movePiece(currentState, destinationX, destinationY, copyPrevAxisX, copyPrevAxisY, copyEnemyAxisX, copyEnemyAxisY, playerNo, opponentNo, true);
					
					// Debug purposes.
					// System.out.println("coordinates of copyEnemyAxisX.get(" + e +").intValue() = " + copyEnemyAxisX.get(e).intValue());
					// System.out.println("coordinates of copyEnemyAxisY.get(" + e +").intValue() = " + copyEnemyAxisY.get(e).intValue());
					
					// Update the new positon of the piece
					int positionX = destinationX; int positionY = destinationY;
					// Clear the orginal helper arrays.
					clearHelperArrays();
					// Now, we will use this method to later check if the piece at the new location is adjacent to an enemy piece.
					highlightSquares(currentState, positionX, positionY, opponentNo, playerNo);	
					
					if(isNewKing == true){
						
						// Since the piece has been recently transformed into a king, there are no more moves it should make so,
						// stop here by calling the function again but, it will immediately stop isAdjacent == false.
						consecutiveCaptures(passNode, currentState, false, playerNo, opponentNo);
						// We will make this loop the last iteration, even though it is not needed. I say it is not needed because in order for
						//it to become a king, a square on the last row must be empty so, at most, it will be adjacent to only one enemy piece
						//upon its transformation ;)
						e = noOfEnemies; 
					}
					else if(xEnemyAxis.size() > 0){
						
						// Perform the recursive call to repeat this process again - 'true' because it is adjacent to an enemy at the new location.
						consecutiveCaptures(passNode, currentState, true, playerNo, opponentNo);
						
					}else{
						
						// We will perform the recursive call but, when we call it, the if(isAdjacent == false) will be ran, and then it will add the state
						// to the tree... I hope. - 'false' because the piece at the new location is not adjacent to an enemy piece.
						consecutiveCaptures(passNode, currentState, false, playerNo, opponentNo);
					}
				}
			//}
		}
	}*/
	// --- End of work consecutiveCaptures() --- //
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
			// Debug purposes.
			greatestMove = new Tree(new String[8][8]);
			
			System.out.println("The fact that the decisionTree is a leaf is " + decisionTree.isLeaf());
			// A huge take on generating the states as we go along.
			//double heuristicValue = minimax(decisionTree, 3, true, playerNo, opponentNo);
			double heuristicValue = alphabeta(decisionTree, 3, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, true, playerNo, opponentNo);
			// Clarity
			String[][] greatestMoveState = greatestMove.getValue();
			// Debug purposes - it prints out 292 nodes for a depth of 3, which is correct and I will assume that the correct states are being created.
			System.out.println("The size of the decisionTree after the minimax operation is " + sizeOfTree + " and the greatest move is ");		
			// Debug purposes.
			printCheckersBoard(greatestMoveState); // So far, it grabs the states at depth 3 (which are the states at the cut-off depth). 
			System.out.println("The overall heuristic value of the minimax algorithm is: " + heuristicValue);
			
			// Debug.
			System.out.println("The contents of strCheckersBoard[][] before the Bot moved its piece is:");
			printCheckersBoard(strCheckersBoard);
			
			// ---- The experiment will be from here...
			
			// Because we will be repainting the Views (i.e. the checkers pieces, checkersboard, etc.), we must do it on the UI thread.
			// All UI repainting must be done on the UI thread. Seems to be working. Needs further testing...
			// We will disable this for now...
			
			runOnUiThread(new Runnable()
			{
				public void run()
				{
					System.out.println("Minimax is done. The bot will perform its move...");
					// This print out gets printed out before the 'mt' thread starts running, aha.
					// Whether, aha. Well, it is false, which makes sense because one the minimax() has finished performing, there would be...
					// nothing left for 'mt' to do so, it gets garbage collected.
					// Yup, a crap experiment.
					String[][] greatestMoveState = greatestMove.getValue();
					// This is where the actual move is made by the bot... so, I need that runOnUiThread here, I think, aha.		
					//determinePieceAndMove(greatestMove, greatestMoveState, playerNo, opponentNo, false);
					
					// An experiment
					// Copy the contents of the greatest move into the current strCheckersboards (i.e. performs the move);
					duplicateArray(greatestMoveState, strCheckersBoard);
					// Re-paints the board based on the AI's greatest move...
					SinglePlayerGame.populateBoard(strCheckersBoard);
					
					// An experiment - Success - I should hand over the player's turn here instead of within the determinePieceAndPiece() method.
					// Works as I hoped it would.
					// hand over its turn to the opponent (i.e. the human).
					playerOneTurn = !playerOneTurn; 
					// playerInfo.setText("Player " + opponentNo + "'s Turn") or game over!;
					// displayTurn(playerOneTurn, opponentNo); // for player one is playerTurn = false and player two is playerTurn = true;
				
					// Debug.
					System.out.println("The contents of strCheckersBoard[][] after the Bot moved its piece is:");
					printCheckersBoard(strCheckersBoard);
					// We will hide the wheel on startup.
					loadingWheel.setVisibility(View.INVISIBLE);
					// Update the message of the AI bot.
					
					// Oh, I know why noOfPieces does not get decreased... Because we don't use movePiece() which decrements the noOfPieces values.
					// I probably should write a function that counts the number of pieces left each time, blah de blah blah.
					// Well, my hunch was correct. It display's the right information now.
					updateNoOfPieces(strCheckersBoard);
					// We pass in false because the turn we are handing over to, is a human so, yeah.
					switchToHumanFromBot(playerNo, opponentNo, false);
					
					/*
					// If the opponent still has pieces on the board...
					if(getNoOfPieces(opponentNo) > 0)
					{
						// Game is still going...
						loadingInfo.setText("A.I.mee is waiting \nfor you to make \nyour move...");
						
						// We could check if the opponent (in this case, the human) has trapped pieces.
						boolean isTrapped = isTrapped(strCheckersBoard, opponentNo, playerNo);
						
						if(isTrapped == true)
						{
							// The bot wins... Since, the opponent cannot make any legitimate moves.
							loadingInfo.setText("A.I.mee says \n\"Well, looks like you're stuck.\"\nBetter luck next time.");
							// Display it too.
							playerInfo.setText("Game Over!\nPlayer " + playerNo + " is\nthe Winner!");
							// Display the image of the winner... blah de blah blah.
							// playerImage.setImageResource(R.drawable.light_brown_piece);
							setPlayerImage(playerNo);
						}
						else
						{
							// Opponent info will go here...
							setPlayerImage(opponentNo);
							// Display's the opponent's information...
							playerInfo.setText("Player " + opponentNo + "'s Turn");
							// I think computerTurn("Will be called here, using the delayForBot() method.
							// ___ Insert code here ___ //
						}
					}
					else
					{
						// The bot wins because it captured all the opponent's pieces...
						loadingInfo.setText("A.I.mee says \n\"Mission \nAccomplished.\"");
						// Display it too.
						playerInfo.setText("Game Over!\nPlayer " + playerNo + " is\nthe Winner!");
						// Display the image of the winner... blah de blah blah.
						// playerImage.setImageResource(R.drawable.light_brown_piece);
						setPlayerImage(playerNo);	
					}*/			
				}
			});
			
			// ...Until here.		
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
		}else
		{
			// We do not really need this here as we will be calling the computerTurn() method from within the playerTurn() method.
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
	public void switchToHumanFromBot(String playerNo, String opponentNo, boolean isBotNext)
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
				// Display it too.
				playerInfo.setText("Game Over!\nPlayer " + playerNo + " is\nthe Winner!");
				// Display the image of the winner... blah de blah blah.
				setPlayerImage(playerNo);
			}
			else
			{
				// Game is still going...
				loadingInfo.setText("Bot_" + playerNo + " is waiting \nfor you to make \nyour move...");
				// Opponent info will go here...
				setPlayerImage(opponentNo);
				// Display's the opponent's information...
				playerInfo.setText("Player " + opponentNo + "'s Turn");
				// I think computerTurn("Will be called here, using the delayForBot() method.
				if(isBotNext == true)
				{
					// If we are not playing against an human, we will call delayForBot() here...
				}
				// ___ Insert code here ___ //
			}
		}
		else
		{
			// The bot wins because it captured all the opponent's pieces...
			loadingInfo.setText("Bot_" + playerNo + " says, \n\"Mission \nAccomplished.\"");
			// Display it too.
			playerInfo.setText("Game Over!\nBot_" + playerNo + " is\nthe Winner!");
			// Display the image of the winner... blah de blah blah.
			setPlayerImage(playerNo);	
		} 
	}
	public void switchToBotFromHuman(String playerNo, String opponentNo)
	{
		// If the opponent still has pieces on the board...
		if(getNoOfPieces(opponentNo) > 0)
		{
			// We could check if the opponent (in this case, the human) has trapped pieces.
			boolean isTrapped = isTrapped(strCheckersBoard, opponentNo, playerNo);
			
			if(isTrapped == true)
			{
				// The bot wins... Since, the opponent cannot make any legitimate moves. 16 characters per line break! Well, this works too and trapped code works too.
				loadingInfo.setText("Bot_" + opponentNo + " says, \"I \nam stuck. How \ncould have this \nhappened?!\"");
				// Display it too.
				playerInfo.setText("Game Over!\nPlayer " + playerNo + " is\nthe Winner!");
				// Display the image of the winner... blah de blah blah.
				setPlayerImage(playerNo);
			}
			else
			{
				// Game is still going... so, we will make it blaaah
				// Opponent info will go here...
				setPlayerImage(opponentNo);
				// Display's the opponent's information...
				playerInfo.setText("Player " + opponentNo + "'s Turn");
				
				// -- computerTurn will be called here, using the delayForBot() method. --//
				// We will show the wheel to indicate it is the AI's turn.
				loadingWheel.setVisibility(View.VISIBLE);
				// Display the Bot's information...
				loadingInfo.setText("Bot_" + opponentNo + " is making \nits move...");
				// Adds a delay before we actually hand over the turn to the bot. This gives the computer time to repaint the UI in time...
				// In this case, opponentNo == "2" ;)
				delayForBot(200, 100, playerNo, opponentNo);
				// ___ End of section where we hand over the turn to the bot. ___ //
			}
		}
		else
		{
			// The bot wins because it captured all the opponent's pieces...
			loadingInfo.setText("Bot_" + opponentNo + " says \n\"Mission \nFailed.\"");
			// Display it too.
			playerInfo.setText("Game Over!\nPlayer " + playerNo + " is\nthe Winner!");
			// Display the image of the winner... blah de blah blah.
			setPlayerImage(playerNo);	
		} 
	}
	public void validateAndSwitchPlayer(String[][] passStrCheckersBoard, String playerNo, String opponentNo)
	{
		// This will check whether the opponent has trapped pieces...
		if(isTrapped(passStrCheckersBoard, opponentNo, playerNo) == false)
		{
			// Adds a delay of 200 milliseconds before the AI (the opponent) is handed its turn...
			delayForBot(200, 100, playerNo, opponentNo);		
		}
		else
		{
			// Display Game Over message...
			playerInfo.setText("Game Over!\nPlayer " + playerNo + " is\nthe Winner!");
			// Set the image of the winning player.
			if(playerNo == "1")
			{
				playerImage.setImageResource(R.drawable.dark_brown_piece);
			}
			else
			{
				playerImage.setImageResource(R.drawable.light_brown_piece);
			}			
			
			// Grabs the number of pieces the opponent has left...
			// If there are still pieces of player x, that it safe to assume this pieces are trapped pieces for the opponent so, we win, aha.
			if(getNoOfPieces(opponentNo) > 0)
			{
				// Yup, A.I.mee is trapped.
				loadingInfo.setText("A.I.mee is\ntrapped.");
				// Debug purposes.
				System.out.println("There are trapped pieces for the opponent so, player " + playerNo + " wins the game.");
				// Display it too.
			}
			else
			{
				// This may not be needed... Okay, it is, aha.
				loadingInfo.setText("A.I.mee says, she \nhas failed the \nones and zeros");
			}
		}
	}
	public void delayForBot(long milliseconds, long interval, String passPlayerNo, String passOpponentNo)
	{
		// This method seems to be working fine.
		// We need these variables final because they will later be passed into an inner class.
		final String playerNo = passPlayerNo;
		final String opponentNo = passOpponentNo;
		// Debug purposes.
		System.out.println("There are no trapped pieces for the opponent so, the opponent can take his turn...");
		// There are no trapped pieces so, we will let the opponent proceed as normal.
		// Waits 200 milliseconds before the AI decides to move.
		new CountDownTimer(milliseconds, interval)
		{
			public void onTick(long millisUntilFinished)
			{
				// Debug purposes.
				System.out.println("milliseconds remaining: " + millisUntilFinished);
			}
			public void onFinish()
			{
				// System.out.println("Now, it is time for Bot_" + playerNo + " to make its move...");
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
						switchToBotFromHuman(playerNo, opponentNo);
						
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
					switchToBotFromHuman(playerNo, opponentNo);
								
				}						
			}else
			{
				// Debug purposes.
				// System.out.println("We have not moved the piece to the new location. (from auto-generated highlights section) so, we do nothing until then.");
			}					
		}
	}
}