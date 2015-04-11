package uk.ac.kcl.www;

import android.os.CountDownTimer;
import android.app.Activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import java.lang.Thread;
import java.lang.Runnable;

import android.util.Log;
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
import android.view.Gravity;
import android.view.LayoutInflater;

import 	android.graphics.drawable.ColorDrawable;

import android.widget.ProgressBar;

// Third party libraries.
import com.gaurav.tree.ArrayListTree;
import com.gaurav.tree.NodeNotFoundException;


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
	
	// AI Tree of States
	public ArrayListTree<String[][]> stateTree;
	// The Tree of possible states that the AI can go to.
	public Tree<String[][]> decisionTree;
	// Keeps track of the number of nodes in the tree
	public int sizeOfTree;
	// An experiment to return the move we should take.
	public Tree<String[][]> greatestMove;
	
	// The state of the game represented as a multidimensional array of Strings.
	// public String[][] currentState;
	
	public boolean isHighlighted, playerOneTurn, isEnemyAdjacent, isNewKing, adjacentToEnemy;
	
	public int highlightParentX, highlightParentY, xOfNewDest, yOfNewDest, erm;
	
	// Constructor
	public SinglePlayerEvents(View[][] passSquares, ImageView[][] passImgSquares, String[][] passCheckersBoard, TextView passTextView, TextView passLoadingInfo, ProgressBar passLoadingWheel, ImageView passPlayerImage)
	{
		// The first turn goes to player two.
		// After each turn a player makes, a boolean variable will determine when it is the others players turn.
		// e.g. playerOne = true, playerTwo = false. Then after player one's turn, the variable will
		// become playerOne = false, and playerTwo = true, and this will keep switching back-and-forth. I just realised,
		// the computer will never ever be able to click this, lol.
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
		erm = 0;	
		
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
		// Keeps track of the number of pieces.
		noOfPiecesPlayerOne = 12;
		noOfPiecesPlayerTwo = 12;
		
	}
	public void performEnemyCapture(String[][] passState, int passX, int passY, String playerNo, String opponentNo, boolean forDecisionTree)
	{
		// Store the coordinates in a more convenient variable, yup. I need to delete this entire method when I have time.
		int destinationX = passX; int destinationY = passY;
	}
	// I need to get back to this.
	public class CountDown extends CountDownTimer
	{
		public int destinationX;
		public int destinationY;
		public String[][] state;
		public String playerNo, opponentNo;
		public int destImg, imgOfKing;
		public boolean forDecisionTree;
		
		public CountDown(long startTime, long interval, String[][] passState, int passX, int passY, String passPlayerNo, String passOpponentNo, boolean passForDecisionTree)
		{
			// Passes it into the base constructor of 'CountDownTimer'
			super(startTime, interval);
			// Properly store the coordinates.
			destinationX = passX;
			destinationY = passY;
			state= passState;
			playerNo = passPlayerNo; opponentNo = passOpponentNo;
			forDecisionTree = passForDecisionTree;
			
		}
		@Override
		public void onTick(long millisUntilFinished)
		{
			System.out.println("seconds remaining: " + millisUntilFinished / 1000);
		}
		@Override
		public void onFinish()
		{
		
		}
	}
	public void determinePieceAndMove(Tree<String[][]> passNode, String[][] state, String playerNo, String opponentNo, boolean forDecisionTree)
	{
		// This will find out which piece was actually moved.

		// We grab the state of the parent node.
		String[][] parentState;
		// An experiment for making isNewKing = false on startup. Sometimes it would stay true because it would keep the result of the last operation
		// using determinePieceAndMove().
		isNewKing = false;
		
		if(forDecisionTree == true)
		{
			// If this is for the purpose of evaluation the decision tree...
			// This is to check whether the actual root of the tree is passed into this method through minimax/alpha-beta algorithm. If not, we proceed...
			//if(passNode.parent() != null)
			//{
				// Grab the value of the parent node, and store it in a MD array.
				parentState = passNode.parent().getValue();
			//}	
		}
		else //if(forDecisionTree == false)
		{
			// If this is for the purpose of moving the actual checkers on the board, grab the actual strCheckersBoard 2D array.
			parentState = strCheckersBoard;
		}
		
		for(int row = 0;row < 8; row++)
		{
			for(int column=((row+1)%2); column<8; column+=2)
			{
				// If the coordinates of both the parentState and the current state do not match, it is safe to assume that was the piece that was moved.
				// I will need more conditions within this statement. Yeah, this might actually be long, aha.
				// I need this to work for both directions, and shit.
				if(parentState[row][column] != state[row][column])
				{
					// This should work for both directions... I hope. The OR operation is a shortcut to check the vice-versa instead of having to iterate until we reach the opposite side. I'll explain this in a bit. || (parentState[row][column].contains("0") && state[row][column].contains("2"))
					if((state[row][column].contains("0") && parentState[row][column].contains(playerNo)))
					{
						// This piece moved on its accord (i.e. it was not captured by the opponent). I think that should be okay for now.
						// Store the coordinates of the piece that was moved.
						int xOfPiece = row, yOfPiece = column;
						
						// Duplicate the parent state.
						String[][] copyParentState = new String[8][8];
						duplicateArray(parentState, copyParentState);
						// We need to check if it was a standard move or a capture, which we will do shortly.
						// We will use the method to generate the necessary coordinates ArrayLists based on the copy of our parent state. Leaving our...
						// main parentState intact.
						highlightSquares(copyParentState, xOfPiece, yOfPiece, opponentNo, playerNo);
						// These coordinates will later hold the destination coordinates of where the piece will be going...
						int destinationX = 0; int destinationY = 0;
						
						// Loop until we find the destination coordinates that match the piece at the child state (where we want to be)
						// of the parent state (where we are currently at).
						for(int l = 1; l < xPrevAxis.size();l++)
						{
							// The coordinates to test.
							destinationX = xPrevAxis.get(l).intValue();
							destinationY = yPrevAxis.get(l).intValue();
							
							// I think I will need better validations placed but, for now, I will use this. This seems to be fine.
							if(state[destinationX][destinationY].contains(playerNo))
							{
								// Make it the last iteration.
								l = xPrevAxis.size();
								// We got the coordinates. Well, I hope we did.
							}
						}
						// Make a copy of the child state and store it.
						String[][] copyState = new String[8][8];
						duplicateArray(state, copyState);
						
						// If this method is being called for the purpose of moving the actual piece on the checkersboard then we assign
						// ...Address of parentState to 'state' so, when we modify state, we are actually modifying 'parentState', which is also the actual...
						// checkersboard.
						if(forDecisionTree == false)
						{
							// The AI bot will be performing an actual move with the checkers piece on the 'parentState'.
							// Debug purposes.
							System.out.println("The parent state does get switched.");
							// The state now becomes the actual checkersboard we wish to work with.
							state = parentState;
							// If we enabled it here, it won't move the piece later on...
							//destinationX = xOfPiece; destinationY = yOfPiece;
						}						
						// At the duplicate state 'copyParentState' and simulate, in order to determine whether the 'state' was a capture or a standard move.
						// We will use the coordinates that we recently obtained.
						if(xEnemyAxis.size() > 0)
						{
							// If the AI bot is performing an actual move.
							if(forDecisionTree == false)
							{
								// The AI bot will be performing an actual move with the checkers piece on the 'parentState'. This means that the current value we...
								// have for destinationX/Y will not be suitable as the coordinates currently correspond to the location of the piece at the state.
								// Where the state is where the piece moved to but, if we are actually moving the real piece, we need to start from parentState
								// so, we make it that destinationX/Y is equal to the coordinates of the piece at the parentState (current).
								destinationX = xOfPiece; destinationY = yOfPiece;
							}
							else // Run this piece of code only when we are working with the minimax() algorithm.
							{
								// Check whether the destinationX/Y is a recently transformed King.
								if(parentState[xOfPiece][yOfPiece] == playerNo && state[destinationX][destinationY].equals("K"+playerNo))
								{
									// This will prevent us from doing any consecutive captures later on because a new king is not allowed to make a consecutive move.
									isNewKing = true;
									// Debug purposes.
									System.out.println("We do have a new king...");
								}
							}
							
							// Debug purposes.
							System.out.println("Computing on the copyParentState, a capture was performed by player x");
							printCheckersBoard(parentState);
							System.out.println("Now, we know the type of move made was a capture so, now we will actually perform the capture");
							System.out.println("Here is the state after the capture was performed by player x");
							printCheckersBoard(copyState);
							
							
							// This piece performed a capture so, we see if it is adjacent to an enemy at the new location.
							// boolean adjacentToEnemy = true;
							adjacentToEnemy = true;
							// Initially, on the first iteration, this will perform the capture that yields the state 'state' from 'parentState'
							// Then, at the 'state' it will check for consecutive captures.
							// It is more explicit for me if I use == true instead of just the variable name itself as the condition.
							while(adjacentToEnemy == true)
							{
								// Clear the helper ArrayLists.
								clearHelperArrays();
								// Creates the necessary coordinates, and their ArrayLists too.
								highlightSquares(state, destinationX, destinationY, opponentNo, playerNo);
								
								// Only when the piece has not immediately been transformed into a king, we continue to loop through until no captures can be made...
								if(isNewKing == false)
								{
									// Run the code as normal.
									if(xEnemyAxis.size() > 0)
									{	
										// Updates the destination coordinates with the newly obtained ones. Automatically, picks the first move-capture.
										destinationX = xPrevAxis.get(1).intValue();
										destinationY = yPrevAxis.get(1).intValue();
										
										// I should probably insert the time delay somewhere around here.
										/*if(forDecisionTree == false)
										{
											// It does not move the piece because the player's turn is handed over before it can make the move.
											// Ah, threads are pain the ass.
											System.out.println("Does the CountDown class get called?");
											new CountDown(2000, 1000, state, destinationX, destinationY, upOrDown, playerNo, opponentNo, forDecisionTree).start();
										}
										else
										{
											movePiece(state, imageOfSquares, destinationX, destinationY, upOrDown, xPrevAxis, yPrevAxis, xEnemyAxis, yEnemyAxis, playerNo, opponentNo, forDecisionTree);
										}
										*/
										// It is adjacent so, we perform the move, yada yada yada. THIS METHOD WILL UPDATE UI ELEMENTS WHEN THIS IS CALLED FOR THE PURPOSE OF MOVING THE BOT'S CHECKERS PIECE.
										movePiece(state, destinationX, destinationY, xPrevAxis, yPrevAxis, xEnemyAxis, yEnemyAxis, playerNo, opponentNo, forDecisionTree);
										// Debug purposes. Insert variable later on. This went into an infinite loop. I forgot to re-assign destinationX/Y
										
										System.out.println("Another capture was made, making this a consecutive capture performed by player " + playerNo);
										printCheckersBoard(state);
									}
									else // no more adjacent enemies.
									{
										// This does not need to be here... Okay, it does.
										// If this is for an actual checkers piece that was moved by the AI bot, then we also hand over our turn to the opponent.
										// No need to run this if it is for the purpose of evaluating the cut-off nodes.
										if(forDecisionTree == false)
										{
											// Display player information.
											
											// hand over its turn to the opponent (i.e. the human).
											//playerOneTurn = !playerOneTurn; 
											// playerInfo.setText("Player " + opponentNo + "'s Turn") or game over!;
											//displayTurn(playerOneTurn, opponentNo); // for player one is playerTurn = false and player two is playerTurn = true;
										}
										// Clear the helper ArrayLists.
										clearHelperArrays();
										// No more.
										adjacentToEnemy = false;
									}
								}
								else
								{
									// Handover our turn.
									if(forDecisionTree == false)
									{
										// Display player information.
										
										// hand over its turn to the opponent (i.e. the human).
										//playerOneTurn = !playerOneTurn; 
										// playerInfo.setText("Player " + opponentNo + "'s Turn") or game over!;
										//displayTurn(playerOneTurn, opponentNo); // for player one is playerTurn = false and player two is playerTurn = true;
									}
									// Clear the helper ArrayLists.
									clearHelperArrays();
									// No more.
									adjacentToEnemy = false;
								}
								
								/*
								if(xEnemyAxis.size() > 0)
								{	
									// Updates the destination coordinates with the newly obtained ones. Automatically, picks the first move-capture.
									destinationX = xPrevAxis.get(1).intValue();
									destinationY = yPrevAxis.get(1).intValue();
									// It is adjacent so, we perform the move, yada yada yada. THIS METHOD WILL UPDATE UI ELEMENTS WHEN THIS IS CALLED FOR THE PURPOSE OF MOVING THE BOT'S CHECKERS PIECE.
									movePiece(state, imageOfSquares, destinationX, destinationY, upOrDown, xPrevAxis, yPrevAxis, xEnemyAxis, yEnemyAxis, playerNo, opponentNo, destImg, passImgOfKing, forDecisionTree);
									// Debug purposes. Insert variable later on. This went into an infinite loop. I forgot to re-assign destinationX/Y
									
									System.out.println("Another capture was made, making this a consecutive capture performed by player " + playerNo);
									printCheckersBoard(state);
								}
								else // no more adjacent enemies.
								{
									// This does not need to be here... Okay, it does.
									// If this is for an actual checkers piece that was moved by the AI bot, then we also hand over our turn to the opponent.
									// No need to run this if it is for the purpose of evaluating the cut-off nodes.
									if(forDecisionTree == false)
									{
										// Display player information.
										
										// hand over its turn to the opponent (i.e. the human).
										playerOneTurn = !playerOneTurn; 
										// playerInfo.setText("Player " + opponentNo + "'s Turn") or game over!;
										displayTurn(playerOneTurn, opponentNo); // for player one is playerTurn = false and player two is playerTurn = true;
									}
									// Clear the helper ArrayLists.
									clearHelperArrays();
									// No more.
									adjacentToEnemy = false;
								}*/		
							}	
						}
						else
						{
							// If an actual checkers piece is being moved by the AI bot, then we run the following piece of code.
							if(forDecisionTree == false)
							{
								System.out.println("Computation on copyParentState resulted in a standard move.");
								// Which means this is an actual move the computer is about to make... We move the piece
								
								// THIS SECTION ALSO MODIFIES THE UI THREAD.
								movePiece(state, destinationX, destinationY, xPrevAxis, yPrevAxis, xEnemyAxis, yEnemyAxis, playerNo, opponentNo, forDecisionTree);
								// Display player information.
								// hand over its turn to the opponent (i.e. the human).
								//playerOneTurn = !playerOneTurn; 
								// playerInfo.setText("Player " + opponentNo + "'s Turn") or game over!;
								//displayTurn(playerOneTurn, opponentNo); // for player one is playerTurn = false and player two is playerTurn = true;
							}
							// It was just a standard move...
							clearHelperArrays();		
						}	
						// Makes sure this is the last iteration of the for-loop.
						row = 8; column = 8;
					}					
				}		
			}
		}	
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
		determinePieceAndMove(passNode, state, playerNo, opponentNo, true);
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
					// This section is where each move made, it will then create a new state (node).
					int xAxisOfDest = autoPrevX.get(eachSquare).intValue();
					int yAxisOfDest = autoPrevY.get(eachSquare).intValue();
					
					// Debug purposes.
					// System.out.println("State " + eachSquare + " of piece number " + e);
					// At the moment it makes the moves but, the states are not being preserved. In other words, it gets overwritten as we go along.
					// Debug purposes.
					// System.out.println("Master List " + e + ": autoPrevX.get(" +eachSquare + ")=" + xAxisOfDest + "and autoPrevY.get(" + eachSquare + ")=" + yAxisOfDest);
					// This will create the state with the potential move.
					movePiece(newLocationState, xAxisOfDest, yAxisOfDest, autoPrevX, autoPrevY, autoEnemyX, autoEnemyY, playerNo, opponentNo, true);
					
					// Our new tree thang.
					passNode.addChild(new Tree(newLocationState));
					// Increment the size of the tree by 1.
					sizeOfTree++;			
				}
			}
			// Debug purposes. - Just as I though, initally it says the size is 12 but, really it should be 4. 
			// System.out.println("The size of arrayOfPrevCoordinatesX = " + arrayOfPrevCoordinatesX.size());
			// Make sure I clear the Master ArrayLists.
			clearMasterLists();
			
			// ------- Yup, here dawg ----- //
			
		}	// End of initial check before player makes a move (except for consecutive captures)
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
					determinePieceAndMove(greatestMove, greatestMoveState, playerNo, opponentNo, false);
					
					// An experiment - Success - I should hand over the player's turn here instead of within the determinePieceAndPiece() method.
					// Works as I hoped it would.
					// hand over its turn to the opponent (i.e. the human).
					playerOneTurn = !playerOneTurn; 
					// playerInfo.setText("Player " + opponentNo + "'s Turn") or game over!;
					displayTurn(playerOneTurn, opponentNo); // for player one is playerTurn = false and player two is playerTurn = true;
				
					// Debug.
					System.out.println("The contents of strCheckersBoard[][] after the Bot moved its piece is:");
					printCheckersBoard(strCheckersBoard);
					// We will hide the wheel on startup.
					loadingWheel.setVisibility(View.INVISIBLE);
					// Update the message of the AI bot.
					//if(noOfPiecesPlayerOne > 0)
					if(getNoOfPieces(opponentNo) > 0)
					{
						// Game is still going...
						loadingInfo.setText("A.I.mee is waiting \nfor you to make \nyour move...");
						
						// We could check if the opponent (in this case, the human) has trapped pieces.
						boolean isTrapped = isTrapped(strCheckersBoard, opponentNo, playerNo);
						
						if(isTrapped == true)
						{
							// The bot wins...
							loadingInfo.setText("A.I.mee says \n\"Well, looks like you're stuck.\"\nBetter luck next time.");
							// Display it too.
							playerInfo.setText("Game Over!\nPlayer" + playerNo + " is\nthe Winner!");
							// Display the image of the winner... blah de blah blah.
							playerImage.setImageResource(R.drawable.light_brown_piece);
						}
						else
						{
							// I think computerTurn("Will be called here, using the switchPlayer() method.
						}
					}
					else
					{
						// The bot wins...
						loadingInfo.setText("A.I.mee says \n\"Mission \nAccomplished.\"");
					}
					
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
		// An attempt to track the total number of nodes.
		sizeOfTree = 1;

		// I will try to generate the tree within the minimax method instead.
		
		if(playerNo == "2")
		{
			// This will correspond to the root MAX node which will create MIN nodes... This works okay.
			/*createChildren(decisionTree, "2", "1", 1);
			// Debug purposes. - So far, so good.
			System.out.println("The decisionTree now has " + decisionTree.children().size() + " child nodes.");
			System.out.println("The decisionTree root a is at depth " + decisionTree.depth());
			// Now, I must pass in the children (which is 7 assuming that I moved the black the piece on the top-leftmost square to the left.)
			ArrayList<Tree<String[][]>> children = decisionTree.children();*/
			
			/*(new Thread(new Runnable(){
				public void run()
				{
					// Insert code here foo!
				}
				})).start();*/
			
			// Yup. I need to come back to this.
			//loadingInfo.setText("Well, A.I.mee is thinking...");
			// Erm.
			//loadingWheel.setVisibility(View.VISIBLE);
			
			// This will prevent minimax() from being called when the bot has no more pieces on the board.	
			if(noOfPiecesPlayerTwo > 0)
			{
				// Debug purposes.
				greatestMove = new Tree(new String[8][8]);
				// Making the actual object final does not result in non-mutable variables used within the object ;)
				final MinimaxThread minimaxThread = new MinimaxThread(playerNo, opponentNo);
				// Load the minimax algorithm in a new thread.
				minimaxThread.start();
				
			}
			else
			{
				// Lol.
				loadingInfo.setText("A.I.mee has accepted\n defeat.");
				// Stop showing the loading wheel.
				loadingWheel.setVisibility(View.INVISIBLE);
				// Debug purposes.
				System.out.println("AI has lost. No need to call minimax() any longer");
			}
			
				
			
			/*// Waits 700 milliseconds before the AI decides to move. I need a better approach. It does work and so does the loading wheel.
			new CountDownTimer(7000, 1000)
			{
				public void onTick(long millisUntilFinished)
				{
					System.out.println("seconds remaining: " + millisUntilFinished / 1000);
					// We will show the wheel to indicate it is the AI's turn.
				}
				public void onFinish()
				{
					System.out.println("Minimax is done. The bot will perform its move...");
					// This print out gets printed out before the 'mt' thread starts running, aha.
					// Whether, aha. Well, it is false, which makes sense because one the minimax() has finished performing, there would be...
					// nothing left for 'mt' to do so, it gets garbage collected.
					System.out.println("The 'mt' Thread.isAlive() = " + mt.isAlive());
					System.out.println("I wonder if minimax() is done yet?");
					// Yup, a crap experiment.
					String[][] greatestMoveState = greatestMove.getValue();
					// This is where the actual move is made by the bot... so, I need that runOnUiThread here, I think, aha.		
					determinePieceAndMove(greatestMove, greatestMoveState, "2", "1", false, 1, R.drawable.light_brown_piece, R.drawable.king_light_brown_piece);
					// Debug.
					System.out.println("The contents of strCheckersBoard[][] after the Bot moved its piece is:");
					printCheckersBoard(strCheckersBoard);
					
					// We will hide the wheel on startup.
					loadingWheel.setVisibility(View.INVISIBLE);
					// Update the message of the AI bot.
					loadingInfo.setText("A.I.mee is\nwaiting for you to\nmake your move...");
				}
			}.start();*/
			
			
			
			
			
			
			/*
			System.out.println("The fact that the decisionTree is a leaf is " + decisionTree.isLeaf());
			// A huge take on generating the states as we go along.
			double heuristicValue = minimax(decisionTree, 3, true);
			// Clarity.
			String[][] greatestMoveState = greatestMove.getValue();
			// Debug purposes - it prints out 292 nodes for a depth of 3, which is correct and I will assume that the correct states are being created.
			System.out.println("The size of the decisionTree after the minimax operation is " + sizeOfTree + " and the greatest move is ");		
			// Debug purposes.
			printCheckersBoard(greatestMoveState); // So far, it grabs the states at depth 3 (which are the states at the cut-off depth). 
			System.out.println("The overall heuristic value of the minimax algorithm is: " + heuristicValue);
			
			
			// An experiment.
			System.out.println("The contents of the greatestMove.parent() should match the current state:");
			printCheckersBoard(greatestMove.parent().getValue());
			System.out.println("The contents of the greatestMove.parent().setValue(strCheckersBoard) should match the current state:");
			greatestMove.parent().setValue(strCheckersBoard);
			printCheckersBoard(greatestMove.parent().getValue());
			
			// Debug.
			System.out.println("The contents of strCheckersBoard[][] before the Bot moved its piece is:");
			printCheckersBoard(strCheckersBoard);
			
			// I will finally try and get the AI to move the piece itself. Inshallah, it works well.
			// This did not work, probably because the root is only a copy of the current state so, I need to figure out how to
			// pass in the actual checkersboard, which is strCheckersBoard.
			// I also forgot to enable the movePiece() method within the section where it only makes a standard move... lol.
			// Well, it moves now but, sometimes a new pieces pop out of thin air. The strCheckersBoard never gets modified. That bit is sorted... I think
			// Erm, it seems to move by itself but, when it is adjacent to an enemy, it does not do anything so, the problem lies in the code of the enemy thang.
			
			// --- This section will be performed on the runOnUiThread, from here... --- //
			
			
			// This is where the actual move is made by the bot... so, I need that runOnUiThread here, I think, aha.		
			determinePieceAndMove(greatestMove, greatestMoveState, "2", "1", false, 1, R.drawable.light_brown_piece, R.drawable.king_light_brown_piece);
			*/
			
			
			// We put the code here, I think.
								/*new Activity().runOnUiThread(new Runnable() {
										@Override
										public void run() {
												// This code will always run on the UI thread, therefore is safe to modify UI elements.
												//movePiece(finalState, imageOfSquares, finalDestinationX, finalDestinationY, finalUpOrDown, xPrevAxis, yPrevAxis, xEnemyAxis, yEnemyAxis, finalPlayerNo, finalOpponentNo, finalDestImg, finalImgOfKing, finalForDecisionTree);
												determinePieceAndMove(greatestMove, greatestMoveState, "2", "1", false, 1, R.drawable.light_brown_piece, R.drawable.king_light_brown_piece);
										}
								});
			
			
			// Debug.
			System.out.println("The contents of strCheckersBoard[][] after the Bot moved its piece is:");
			printCheckersBoard(strCheckersBoard);
			
			// We will hide the wheel on startup.
			loadingWheel.setVisibility(View.INVISIBLE);
			// Update the message of the AI bot.
			loadingInfo.setText("A.I.mee is\nwaiting for you to\nmake your move...");
			*/
			// --- ...Up until here. --- //
			
			// Yup.
			//loadingInfo.setText("Well, A.I.mee is done thinking...");
			// Erm.
			//loadingWheel.setVisibility(View.INVISIBLE);
			
			// I HAVE ALSO NOTICED THAT WHEN THERE IS ONLY PIECE FOR THE CPU LEFT FOR THE CPU, AND ITS CORNERED
			// THE VALUE OF GREATESTMOVE IS ALL NULL AND THE MINIMAX ALGORITHM RETURNS -INFINITY AND LATELY
			// BECAUSE I'M ACTUALLY MOVING THE PIECES, IT CRASHES THE PROGRAM.
			
			/*
			The reason why minimax() returns negative infinity on the last piece of the AI pieces that is also about to be captured is
			because our cut-off depth only works with depth == 0 but, if we have a depth of 3 but, the piece gets captured at depth == 1,
			it won't cut off so, I need to also add a isLeaf() condition to it. I originally had that but, removed it because when creating
			the tree within the minimax algorithm, there is only one node when it is initially passed in, making it a leaf when passed in so,
			nothing would get created if(depth == 0 || isLeaf()) but,
			I think I can solve this by using if(depth == 0 || (isLeaf() && isRoot() != true)) :D
			*/ 
			
			//System.out.println("By using an experimental technique, the move that we obtain is...");
			// Prints the contents of the root node.
			//printCheckersBoard(decisionTree.getValue()); // Well, I need get back to this. Modify the node swap code within the minimax method.
			
			// Okay, it does look better.
			/*
			for(int n = 0; n < children.size(); n++)
			{
				// Each child of the previous node.
				// This will be used to create another set of states, where this (child) node will become the parent of the next new (nodes) states.
				Tree<String[][]> nextState = children.get(n);
				// This does not print out the total of levels in the tree but, what level the node is at :)
				int currentDepth = nextState.depth();
				// Debug purposes. Take note that the depth will change as soon as we iterate at least once through this for-loop
				System.out.println("Min's turn - The contents of the nextState-" + n + " MD-array at depth " + currentDepth + "...");
						
				// Prints out the text representation of the checkers board (depending on the state)
				//printCheckersBoard(nextState.getValue());
				
				// The new states will be created in the method below. This corresponds to the MIN Nodes which will create MAX nodes. This works okay :)
				createChildren(nextState, "1", "2", -1);
				
				// Children's children.
				ArrayList<Tree<String[][]>> newChildren = nextState.children();
				
				for(int ns = 0; ns < newChildren.size();ns++)
				{
					// This will be used to create another set of states based on the new children we recently acquired.
					Tree<String[][]> nextNextState = newChildren.get(ns);
					// This does not print out the total of levels in the tree but, what level the node is at :)
					currentDepth = nextNextState.depth();
					// Debug purposes. Take note that the depth will change as soon as we iterate at least once through this for-loop
					System.out.println("Max's turn - The contents of the nextNextState-" + ns + " MD-array at depth " + currentDepth + "...");
					
					// --- Print for-loop code goes here --- //
					
					// Prints out the text representation of the checkers board (depending on the state)
					printCheckersBoard(nextNextState.getValue());
					
					// This will correspond to the MAX nodes which lastly create MIN nodes... This works okay.
					createChildren(nextNextState, "2", "1", 1);
					
				}
			}*/
			
			// Debug purposes.
			// System.out.println("Let's start the recursive call...");
			// Recursion - Well, it outputs a size of 292 nodes, which is correct. I just need to check if the states are being printed correctly.
			// generateStates(children, 2);
			
			// I need to double check whether the output of the size is indeed correct. This returns 55 but, before using the new Tree class
			// I think I got value of 61. However, this might have been before implementing the state pruning if an enemy is adjacent to a piece.
			// Only called createChildren() twice so, this corresponds to a depth of 3.
			// Calling createChildren() three times yields 292 nodes which was the same value
			// I obtained when I used the other ArrayListTree data structure.
			// System.out.println("The size of the decisionTree is " + sizeOfTree);
			// Debug purposes.	
			// System.out.println("This is the actual strCheckersBoard[][] md array...");
			// Prints out the text representation of the checkers board 
			//printCheckersBoard(strCheckersBoard);
			
			// Debug purposes. - Oh, shit, I forgot to change the depth to 5...
			// double heuristicValue = minimax(decisionTree, 3, true);
			// Debug purposes.
			// System.out.println("The heuristic value of the minimax algorithm is " + heuristicValue);
			//int depthOfGreatestMove = greatestMove.depth();
			//System.out.println("The greatest move to make is and its depth is " + depthOfGreatestMove + " ...");
			
			// I guess this works in a sense that it returns the parent of the node but, the greatestMove node is the problem itsel
			// while(depthOfGreatestMove > 1)
			//{
			//	greatestMove = greatestMove.parent();
			//	depthOfGreatestMove--;
			//}
			
			//printCheckersBoard(greatestMove.getValue());
			
			// ArrayList<Tree<String[][]>> overAllChildren = decisionTree.children();
			// Returns 7, which is cool. Those 7 children have children of their own.
			// System.out.println("The decisionTree has " + overAllChildren.size() + " children");
		}
		
	}
	// I declared this method as synchronised hoping the code runs one at a time otherwise,if I clicked ib two buttons at the same time, I have a hunch
	// that it may cause a series of problems.
	public synchronized void onClick(View v) 
	{
		// This will no longer work if when we start using the AI bot. The AI cannot click so, if we ran [x][y].equals(View v)... Will not run because
		// like I said, it cannot click so, I will need to probably move the if(playerOneTurn == true) statement outside of the nested for-loop ;)
		
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
		
		/*
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
							playerTurn("1", strCheckersBoard, v, x, y, -1, R.drawable.dark_brown_piece, R.drawable.king_dark_brown_piece, "2");		// Nice, it works.
							//playerTurn("1", strCheckersBoard, v, x >= 1 && x <= 7, x, y, -1, R.drawable.dark_brown_piece, R.drawable.king_dark_brown_piece, false, x >= 2, "2");		// Nice, it works.
						}
						else
						{
							// The AI Code will go here... Now, where to begin.	
							//computerTurn("2");
									
							// We move our pieces as normal.
							playerTurn("2", strCheckersBoard, v, x, y, 1, R.drawable.light_brown_piece, R.drawable.king_light_brown_piece, "1");	// Nice, it works.
							//playerTurn("2", strCheckersBoard, v, x >= 0 && x <= 6, x, y, 1, R.drawable.light_brown_piece, R.drawable.king_light_brown_piece, true, x <= 5, "1");	// Nice, it works.		
						}
				}// if(squaresOfBoard[x][y].equals(v))
			}		
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
			// Debug purposes.
			// System.out.println("First entire 'if' statment went through");
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
			// System.out.println("Second entire 'if' statment went through");				
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
				prepareHighlight(passStrCheckersBoard, y >= 2 && x >= 2 && passStrCheckersBoard[x+(-1)][y-1].contains(opponentNo),
												 y >= 1 && passStrCheckersBoard[x+(-1)][y-1] == "0", x, y, -1, -1);
				// Checks the right side and decides whether it should highlight the square or not.
				prepareHighlight(passStrCheckersBoard, y <= 5 && x >= 2 && passStrCheckersBoard[x+(-1)][y+1].contains(opponentNo),
												 y >= 0 && y <= 6 && passStrCheckersBoard[x+(-1)][y+1] == "0", x, y, -1 , 1);	
			}
			if(x >= 0 && x <= 6)
			{
				// When adding highlights to pieces beneath it...
				// Debug purposes.
				// System.out.println("if(x >= 0 && x <= 6) and strKing =" + strKing);
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
			// Debug purposes.
			// System.out.println("else if(passStrCheckersBoard[x][y] == playerNo && precondition) AND passStrCheckersBoard[" + x + "][" + y + "] = " + passStrCheckersBoard[x][y]);
			// I think it works fine. I accidentally put a [y+2] and a [y-2] for both of the second conditions of the 'prepareHighlight'
			// Checks the left side and decides whether it should highlight the squre or not.
			prepareHighlight(passStrCheckersBoard, y >= 2 && attackConstraint && passStrCheckersBoard[x+(upOrDown)][y-1].contains(opponentNo),
											 y >= 1 && passStrCheckersBoard[x+(upOrDown)][y-1] == "0", x, y, upOrDown, -1);
			// Checks the right side and decides whether it should highlight the square or not.
			prepareHighlight(passStrCheckersBoard, y <= 5 && attackConstraint && passStrCheckersBoard[x+(upOrDown)][y+1].contains(opponentNo),
											 y >= 0 && y <= 6 && passStrCheckersBoard[x+(upOrDown)][y+1] == "0", x, y, upOrDown, 1);		
			// Adds the highlights to the squares based on the result from the 'prepareHighlight' method.
			//addHighlight();		// Uncomment if it breaks the application... Lol.
		}
		
		/*
		if(precondition)
		{
			// I think it works fine. I accidentally put a [y+2] and a [y-2] for both of the second conditions of the 'prepareHighlight'
			// Checks the left side and decides whether it should highlight the squre or not.
			prepareHighlight(passStrCheckersBoard, y >= 2 && attackConstraint && passStrCheckersBoard[x+(upOrDown)][y-1] == opponentNo,
											 y >= 1 && passStrCheckersBoard[x+(upOrDown)][y-1] == "0", x, y, upOrDown, -1);
			// Checks the right side and decides whether it should highlight the square or not.
			prepareHighlight(passStrCheckersBoard, y <= 5 && attackConstraint && passStrCheckersBoard[x+(upOrDown)][y+1] == opponentNo,
											 y >= 0 && y <= 6 && passStrCheckersBoard[x+(upOrDown)][y+1] == "0", x, y, upOrDown, 1);		
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
	public void movePiece(String[][] passStrCheckersBoard, int passX, int passY, ArrayList<Integer> passListOfRows, ArrayList<Integer> passListOfColumns,
												ArrayList<Integer> passEnemyX, ArrayList<Integer> passEnemyY, String strDest, String opponentNo, boolean forDecisionTree)
	{
		// Clear the old values.
		xOfNewDest = 0;
		yOfNewDest = 0;
		
		// An experiment...
		// This will hold the hex addresses of the images for the corresponding player, which would be dynamically obtained.
		int destinationImg = 0;
		int destinationImgKing = 0;
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
		
		// ...An experiment.
		
		// I need to implement this in a bit.
		int sizeOfPrev = passListOfRows.size();
		int x = passX, y = passY;
		String strSource;
		
		// An experiment...
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
			//if(v.equals(squaresOfBoard[prevX][prevY]))
			//if(squaresOfBoard[x][y].equals(squaresOfBoard[prevX][prevY]))
			// x == prevX && y == prevY would be a better/safer condition.
			// if(squaresOfBoard[x][y].equals(squaresOfBoard[prevX][prevY]) && passStrCheckersBoard[x][y] != opponentNo)
			if((x == prevX && y == prevY) && passStrCheckersBoard[x][y] != opponentNo)
			{
				// Close the loop after this iteration.
				mv = sizeOfPrev;
				// Debug purposes.
				// System.out.println("We successfully moved the piece :)");
				// Temporary variable that will hold the value at the old location (i.e. the piece we wish to move).
				strSource = passStrCheckersBoard[parentPrevX][parentPrevY];
	
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
							passStrCheckersBoard[enemyCoordinateX][enemyCoordinateY] = "0";
							// We will store the coordinates of the recently found enemy to later set the image for the right ImageView.
							actualEnemyX = enemyCoordinateX;
							actualEnemyY = enemyCoordinateY;
							// We clear the space (visually) i.e. take the piece
							// imageOfSquares[enemyCoordinateX][enemyCoordinateY].setImageResource(0);
							// Makes this the last 'e' iteration.
							e = passEnemyX.size();
							// Will decrease the number of pieces the opponent has by 1.
							if(strDest.contains("1") && forDecisionTree != true){--noOfPiecesPlayerTwo;}else if(strDest.contains("2") && forDecisionTree != true){--noOfPiecesPlayerOne;}
							
						}
						else if(checkBelow == enemyCoordinateX && (prevY-1) == enemyCoordinateY)
						{
							// Check the square below...
							// The enemy square is here. This works. Nice.
							// Debug purposes.
							// System.out.println("These are the correct coordinates of the enemy square checkBelow=" + checkBelow + (prevY+1));
							// We clear the space i.e. take the piece.
							passStrCheckersBoard[enemyCoordinateX][enemyCoordinateY] = "0";
							// We will store the coordinates of the recently found enemy to later set the image for the right ImageView.
							actualEnemyX = enemyCoordinateX;
							actualEnemyY = enemyCoordinateY;
							// We clear the space (visually) i.e. take the piece
							// imageOfSquares[enemyCoordinateX][enemyCoordinateY].setImageResource(0);
							// Makes this the last 'e' iteration.
							e = passEnemyX.size();
							// Will decrease the number of pieces the opponent has by 1.
							if(strDest.contains("1") && forDecisionTree != true){--noOfPiecesPlayerTwo;}else if(strDest.contains("2") && forDecisionTree != true){--noOfPiecesPlayerOne;}
						}
						else if(checkAbove == enemyCoordinateX && (prevY+1) == enemyCoordinateY)
						{
							// Check the square above...
							// The enemy square is here. This works. Nice.
							// Debug purposes.
							// System.out.println("These are the correct coordinates of the enemy square checkAbove=" + checkAbove + " and checkY=" + (prevY+1));
							// We clear the space i.e. take the piece.
							passStrCheckersBoard[enemyCoordinateX][enemyCoordinateY] = "0";
							// We will store the coordinates of the recently found enemy to later set the image for the right ImageView.
							actualEnemyX = enemyCoordinateX;
							actualEnemyY = enemyCoordinateY;
							// We clear the space (visually) i.e. take the piece
							// imageOfSquares[enemyCoordinateX][enemyCoordinateY].setImageResource(0);
							// Makes this the last 'e' iteration.
							e = passEnemyX.size();
							// Will decrease the number of pieces the opponent has by 1.
							if(strDest.contains("1") && forDecisionTree != true){--noOfPiecesPlayerTwo;}else if(strDest.contains("2") && forDecisionTree != true){--noOfPiecesPlayerOne;}
						}
						else if(checkAbove == enemyCoordinateX && (prevY-1) == enemyCoordinateY)
						{
							// Check the square above...
							// The enemy square is here. This works. Nice.
							// Debug purposes.
							// System.out.println("These are the correct coordinates of the enemy square checkAbove=" + checkAbove + (prevY+1));
							// We clear the space i.e. take the piece.
							passStrCheckersBoard[enemyCoordinateX][enemyCoordinateY] = "0";
							// We will store the coordinates of the recently found enemy to later set the image for the right ImageView.
							actualEnemyX = enemyCoordinateX;
							actualEnemyY = enemyCoordinateY;
							// We clear the space (visually) i.e. take the piece
							// imageOfSquares[enemyCoordinateX][enemyCoordinateY].setImageResource(0);
							// Makes this the last 'e' iteration.
							e = passEnemyX.size();
							// Will decrease the number of pieces the opponent has by 1.
							if(strDest.contains("1") && forDecisionTree != true){--noOfPiecesPlayerTwo;}else if(strDest.contains("2") && forDecisionTree != true){--noOfPiecesPlayerOne;}
						}					
					}
					// If this move is an actual move (not a potential move), then we set the corresponding square's new image.
					if(forDecisionTree == false)
					{
						// We clear the space (visually) i.e. take the piece
						imageOfSquares[actualEnemyX][actualEnemyY].setImageResource(0);			
					}
					else
					{
						// If this is for generating a state corresponding to a potential move, then there is no need to set the images since,
						// since we will not need at the moment.
					}
					
				}
				
				// If a piece of player one reaches the last row, transform the piece into a king...
				
				// Creates the king string corresponding to the player number.
				String strKing = "K" + strDest;
				
				if(x == 0 && passStrCheckersBoard[parentPrevX][parentPrevY] == "1")
				{
					// Debug purposes.
					// System.out.println("strSource does equal player one and let's output strKing=" + strKing);
					// Now, the piece will now become a king at the new location.
					passStrCheckersBoard[parentPrevX][parentPrevY] = strKing;
					// This stops a new king from making a consecutive attack upon transformation.
					isNewKing = true;
					
				}else if(x == 7 && passStrCheckersBoard[parentPrevX][parentPrevY] == "2")
				{
					// Debug purposes.
					// System.out.println("strSource does equal player two and let's output strKing=" + strKing);
					// If a piece of player two reaches the last row, transform the piece into a king.
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
				
				// If the piece that we wish to move/perform capture is a king then...
				// if(passStrCheckersBoard[parentPrevX][parentPrevY] == strKing) never satisfies, regardless. == and mutated strings seem to cause problems.
				if(passStrCheckersBoard[parentPrevX][parentPrevY].contains(strKing))
				{
					// Finally, we will clear the square of the checker piece's old location
					passStrCheckersBoard[parentPrevX][parentPrevY] = "0";
					// Moves it to the new location...
					passStrCheckersBoard[x][y] = strKing;
					// If this is for the purposes of an actual move (not a potential move for the AI state tree...
					if(forDecisionTree == false)
					{
						// Clear the image of the piece that occupied the location of the old square.
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
						// Clear the image of the piece that occupied the location of the old square.
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
				
				
				// --- Debug Purposes --- //
				/*System.out.println("We have succesfully moved the piece. The values of the successful move were...");		
				// Prints out the text representation of the checkers board 
				printCheckersBoard(passStrCheckersBoard);
				// --- Debug Purposes --- //
				System.out.println("x= " + x + "and y= " + y + ". prevX= " + prevX + "and prevY= " + prevY);
				System.out.println("===========================================================================================");*/
				
			}else
			{
				/*// Debug purposes.
				System.out.println("We did not make a move because x and y did not equal prevX and prevY. Their values were...");
				System.out.println("x= " + x + "and y= " + y + ". prevX= " + prevX + "and prevY= " + prevY);
				System.out.println("===========================================================================================");
				// Debug purposes.
				// System.out.println("We do nothing because an opposing piece was clicked or it was an invalid move.");*/
			}
		}							
	}
	public void playerTurn(String playerNo, String[][] passStrCheckersBoard, View v, int passX, int passY, String opponentNo)
	{	
		// The coordinates of the currently selected square.
		int x = passX, y = passY;
		// The coordinates of the parent (root) square of highlight squares.
		int rootX, rootY;
		// Holds the value on how many times the program it saw a piece of 'playerNo'
		int noOfTimes = 0;
		
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
					if(passStrCheckersBoard[row][column] == playerNo || passStrCheckersBoard[row][column].contains(strKing))
					{
						// Debug purposes.
						// System.out.println("Does this section ever get executed?");
						
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
		
		// ...To this section works-ish. Been a while since I wrote this section, and forgot to add that it works splendidly. 	
		
		// Checks whether the highlighted piece is adjacent to an enemy (and there could be more than one.)
		if(isEnemyAdjacent == true)
		{
			// We lock the game until the player makes a capture.
			// Debug purposes.
			// System.out.println("if(isEnemyAdjacent == true) just ran so, a capture needs to be performed.");
			// This will determine what type of move (standard or a capture) it should make, and also make the move.
			performMoveAndCheckAdjacent(passStrCheckersBoard, passX, passY, playerNo, opponentNo);
			
		}else
		{
			//...Otherwise, we check if the selected square corresponds to the player's number and if it is part of the highlighted squares.
			if(passStrCheckersBoard[x][y].contains(playerNo) && isHighlighted == false)
			{
				// Debug purposes.
				// System.out.println("the if(passStrCheckersBoard[x][y] == playerNo && isHighlighted == false) statement has just been run.");
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
				highlightSquares(passStrCheckersBoard, x, y, opponentNo, playerNo); // upOrDown = 1 so, x-1 i.e, go from bottom to the top of the checkersboard.
				// Add the ArrayLists to the master ArrayList... Aha.
				arrayOfPrevCoordinatesX.add(xPrevAxis);
				arrayOfPrevCoordinatesY.add(yPrevAxis);
				// Clear the x/yPrevAxis and x/yEnemyAxis ArrayLists
				clearHelperArrays();
				// Adds the visual highlights to the squares based on the result from the 'prepareHighlight' method.
				addHighlight(arrayOfPrevCoordinatesX.get(0), arrayOfPrevCoordinatesY.get(0));
				// Debug purposes.
				// System.out.println("The size of arrayOfPrevCoordinatesX.size()" + arrayOfPrevCoordinatesX.size() + "--- from if(passStrCheckersBoard[x][y] == playerNo && isHighlighted == false)");
			}
			else if(passStrCheckersBoard[x][y] == "0" && isHighlighted == true)
			{	
				// Or if the selected square is part of the highlighted squares, and the square is empty, we will see if we can make a move...
				// Debug purposes.
				// System.out.println("else if(passStrCheckersBoard[x][y] == 0 && isHighlighted == true) just ran but, failed miserably.");
				// This will determine what type of move (standard or a capture) it should make, and also make the move.
				performMoveAndCheckAdjacent(passStrCheckersBoard, passX, passY, playerNo, opponentNo);
			}
		}	
	}
	public void addToMasterLists(ArrayList<Integer> passPrevX, ArrayList<Integer> passPrevY, ArrayList<Integer> passEnemyX, ArrayList<Integer> passEnemyY)
	{
				// We apply the highligh// Add the ArrayLists to the master ArrayList... Aha.
				arrayOfPrevCoordinatesX.add(passPrevX);
				arrayOfPrevCoordinatesY.add(passPrevY);
				
				// THIS IS A TEST. DELETE THE IF STATEMENT IF ALL FAILS BUT, KEEP THE CONTENT FROM IT.
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
	public void displayTurn(boolean playerOneTurn, String opponentNo)
	{
		// This needs major adjustments! I should sort this out, and look into the first few lines of the newly added code for the nest for-loop
		// at the very top!.
		// This method will display a TextView that states whether it is the opponent's turn or the winner of the game.
		// The reason why this did not work as intended because the statement originally was == true, which would be partially correct but,
		// this method is called after I change the player's turn so, it ends up calling the other if statement... Lol. I will make this better
		// by putting it back to true, and call this method before I change the value of 'playerOneTurn' to the opponent's turn :)
		
		if(playerOneTurn == false) // Just before player two hands over its turn... 
		{
			if(noOfPiecesPlayerTwo <= 0)
			{
				// If the opponent has no more pieces then player 1 is the winner.
				playerInfo.setText("Game Over!\nPlayer 1 is\nthe Winner!");
				
			}
			else
			{
				// Display the player's turn.
				playerInfo.setText("Player " + opponentNo + "'s Turn");
				// Set the player image.
				playerImage.setImageResource(R.drawable.light_brown_piece);
				// Debug.
				System.out.println("The no of pieces left for Player 2 is " + noOfPiecesPlayerTwo);
			}				
		}
		else // Just before player one hands over its turn...
		{  
		  if(noOfPiecesPlayerOne <= 0)
			{
				// If the opponent has no more pieces then player 1 is the winner.
				playerInfo.setText("Game Over!\nPlayer 2 is\nthe Winner!");	
			}
			else
			{
				// Display the player's turn.
				playerInfo.setText("Player " + opponentNo + "'s Turn");
				// Set the image of the player image.
				playerImage.setImageResource(R.drawable.dark_brown_piece);
				// Debug.
				System.out.println("The no of pieces left for Player 1 is " + noOfPiecesPlayerOne);
			}							
		}
	}
	public int getNoOfPieces(String playerNo)
	{
		// Automatically retrieve the number of pieces the player x has left...
		if(playerNo == "1")
		{
			return noOfPiecesPlayerOne;
		}else // playerNo == "2"
		{
			return noOfPiecesPlayerTwo;
		}
	}
	public void performMoveAndCheckAdjacent(String[][] passStrCheckersBoard, int passX, int passY, String playerNo, String opponentNo)
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
		
			// Moves the checkers piece to the new location - passing imageOfSquares into the method has not caused any problems.
			movePiece(passStrCheckersBoard, x, y, autoPrevX, autoPrevY, autoEnemyX, autoEnemyY, playerNo, opponentNo, false);
			
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
					// I need to check if this piece has just been turned into a king... Well, this works.
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
						highlightSquares(passStrCheckersBoard, xOfNewDest, yOfNewDest, opponentNo, playerNo);
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
						//displayTurn(playerTurn, opponentNo); // for player one is playerTurn = false and player two is playerTurn = true;
						// Handover the turn to opponent.
						//playerOneTurn = playerTurn;
						
						// hand over its turn to the opponent (i.e. the human).
						playerOneTurn = !playerOneTurn; 
						// playerInfo.setText("Player " + opponentNo + "'s Turn") or game over!;
						// Display the player's turn.
						displayTurn(playerOneTurn, opponentNo); // for player one is playerTurn = false and player two is playerTurn = true;
						// Since there are no adjacent enemies, we make it false.
						isEnemyAdjacent = false;
						
						// A huge experiment... This will check if the opponent pieces is trapped, and if not, it will hand over its to the opponent
						// with a delay of 200ms, and it seems to be working okay. 
						validateAndSwitchPlayer(strCheckersBoard, playerNo, opponentNo);
						
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
					//displayTurn(playerTurn, opponentNo); // for player one is playerTurn = false and player two is playerTurn = true;
					// Handover the turn to opponent.
					// playerOneTurn = playerTurn;
					
					// hand over its turn to the opponent (i.e. the human).
					playerOneTurn = !playerOneTurn; 
					// Display the player's turn.
					// playerInfo.setText("Player " + opponentNo + "'s Turn") or game over!; 
					displayTurn(playerOneTurn, opponentNo); // for player one is playerTurn = false and player two is playerTurn = true;
					// Erm, I am so lost.
					isEnemyAdjacent = false;
					// I think I would need to remove the highlights too.
					
					// A huge experiment... This will check if the opponent pieces is trapped, and if not, it will hand over its to the opponent
					// with a delay of 200ms, and it seems to be working okay. 
					validateAndSwitchPlayer(strCheckersBoard, playerNo, opponentNo);
								
				}						
			}else
			{
				// Debug purposes.
				System.out.println("We have not moved the piece to the new location. (from auto-generated highlights section) so, we do nothing until then.");
			}					
		}
	}
	public void validateAndSwitchPlayer(String[][] passStrCheckersBoard, String playerNo, String opponentNo)
	{
		// This will check whether the opponent has trapped pieces...
		if(isTrapped(passStrCheckersBoard, opponentNo, playerNo) == false)
		{
			// Adds a delay of 200 milliseconds before the AI (the opponent) is handed its turn...
			switchPlayer(200, 100, playerNo, opponentNo);		
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
	public void switchPlayer(long milliseconds, long interval, String passPlayerNo, String passOpponentNo)
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
				System.out.println("milliseconds remaining: " + millisUntilFinished);
				// We will show the wheel to indicate it is the AI's turn.
				loadingWheel.setVisibility(View.VISIBLE);			
				// Update the message of the AI bot.
				loadingInfo.setText("A.I.mee is making \nher move...");
			}
			public void onFinish()
			{
				System.out.println("Now, it is time for A.I.mee to make her move...");
				computerTurn(opponentNo, playerNo);								
				
				// I tried using a Thread but, it caused some complexities because various sections within computerTurn() modifies
				// the UI but, if we did this within another thread besides the UI thread, the application crashes.
				// Problem sorted because I split the operations in computerTurn into two threads. minimax() runs on one thread,
				// and the UI repainting is on done on the actual UI thread ;)
			}
		}.start();
	}
}